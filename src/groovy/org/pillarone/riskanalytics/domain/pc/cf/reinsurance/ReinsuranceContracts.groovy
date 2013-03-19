package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.pillarone.riskanalytics.core.wiring.WireCategory as WC
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory as PRC

import com.google.common.collect.ListMultimap
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.graph.Graph
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.*
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker
import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.graph.Node
import com.google.common.collect.LinkedListMultimap

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): diamond cover, mixing contract and company cover allowed?
class ReinsuranceContracts extends DynamicComposedComponent {

    static Log LOG = LogFactory.getLog(ReinsuranceContracts)

    PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
//    PacketList<LegalEntityDefaultPacket> inReinsurersDefault = new PacketList<LegalEntityDefaultPacket>(LegalEntityDefaultPacket)
    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<LegalEntityDefault> inLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault)

    PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoInward = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<CommissionPacket> outCommission = new PacketList<CommissionPacket>(CommissionPacket)

    private List contractsBasedOnGrossClaims = []
    private Graph contractsBasedOnContracts = new Graph()
    /** key: covering contract, value: covered contract and based to be covered */
    private ListMultimap<ReinsuranceContract, ReinsuranceContractAndBase> coverForContracts = LinkedListMultimap.create()
    /** contains the relations between reinsurance contracts covering legal entities */
    private Graph contractsBasedOnCompanies = new Graph()
    private ListMultimap<ILegalEntityMarker, IReinsuranceContractMarker> coverForLegalEntity = LinkedListMultimap.create()
    /** contains for each legal entity the contract where it acts as a counter party */
    private ListMultimap<ILegalEntityMarker, IReinsuranceContractMarker> inwardLegalEntity = LinkedListMultimap.create()

    /** key: technical contract name, value: contract instance */
    private Map<String, ReinsuranceContract> reinsuranceContracts = new HashMap<String, ReinsuranceContract>()

    public ReinsuranceContract createDefaultSubComponent(){
        return new ReinsuranceContract(
                parmReinsurers : new ConstrainedMultiDimensionalParameter(
                    Collections.emptyList(), LegalEntityPortionConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(LegalEntityPortionConstraints.IDENTIFIER)),
                parmCover : CoverAttributeStrategyType.getDefault(),
                parmCoveredPeriod : PeriodStrategyType.getDefault(),
                parmContractStrategy : ReinsuranceContractType.getDefault());
    }

    @Override
    void wire() {
        if (noneTrivialContracts()) {
            init()
            replicateInChannels this, inFactors
            replicateInChannels this, inLegalEntityDefault
            replicateOutChannels this, outClaimsInward
            replicateOutChannels this, outUnderwritingInfoInward
            wireContractsBasedOnGross()
            wireContractsBaseOnContracts()
            wireContractsIncludingInwardBusiness()
            wireProgramIndependentReplications()
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(contractsBasedOnContracts)
            LOG.debug(contractsBasedOnCompanies)
        }
    }

    /**
     * in channels of contracts based on original (gross) claims can be wired directly with replicating in channels
     */
    private void wireContractsBasedOnGross() {
        for (ReinsuranceContract contract : contractsBasedOnGrossClaims) {
            doWire PRC, contract, 'inClaims', this, 'inClaims'
            doWire PRC, contract, 'inUnderwritingInfo', this, 'inUnderwritingInfo'
        }
    }

    /**
     * The key entries of coverForContracts contains the covering contracts, the value the contracts to be covered by a
     * contract and the base in order to select the correct channel. If net is covered the GNPI channel is wired.
     */
    private void wireContractsBaseOnContracts() {
        for (Map.Entry<ReinsuranceContract, ReinsuranceContractAndBase> contractCoveredByContracts: coverForContracts.entries()) {
            for (ReinsuranceContractAndBase contractAndBase : contractCoveredByContracts.value) {
                if (contractAndBase.contractBase.equals(ReinsuranceContractBase.CEDED)) {
                    doWire WC, contractCoveredByContracts.key, 'inClaims', contractAndBase.reinsuranceContract, 'outClaimsCeded'
                    doWire WC, contractCoveredByContracts.key, 'inUnderwritingInfo', contractAndBase.reinsuranceContract, 'outUnderwritingInfoCeded'
                }
                else {
                    doWire WC, contractCoveredByContracts.key, 'inClaims', contractAndBase.reinsuranceContract, 'outClaimsNet'
                    doWire WC, contractCoveredByContracts.key, 'inUnderwritingInfo', contractAndBase.reinsuranceContract, 'outUnderwritingInfoGNPI'
                }
            }
        }
    }

    private void wireContractsIncludingInwardBusiness() {
        for (Node node : contractsBasedOnCompanies.nodes) {
            ReinsuranceContract inContract = reinsuranceContracts[node.name]
            for (Node coveredContractNode : node.parents) {
                ReinsuranceContract precedingContract = reinsuranceContracts[coveredContractNode.name]
                doWire WC, inContract, 'inClaims', precedingContract, 'outClaimsInward'
                doWire WC, inContract, 'inUnderwritingInfo', precedingContract, 'outUnderwritingInfoInward'
            }
        }
        
//        if (contractsBasedOnCompanies.nodes.size() > 0) {
//            for (ReinsuranceContract contract : contractsBasedOnCompanies) {
//                if (contract.parmCover.getType().equals(CoverAttributeStrategyType.LEGALENTITIES)) {
//                    List<ILegalEntityMarker> coveredLegalEntities = ((InwardLegalEntitiesCoverAttributeStrategy) contract.parmCover).getCoveredLegalEntities();
//                    for (ILegalEntityMarker legalEntity : coveredLegalEntities) {
//                        for (ReinsuranceContract preceedingContract : inwardLegalEntity.get(legalEntity)) {
//                            doWire WC, contract, 'inClaims', preceedingContract, 'outClaimsInward'
//                            doWire WC, contract, 'inUnderwritingInfo', preceedingContract, 'outUnderwritingInfoInward'
//                        }
//                    }
//                }
//            }
//        }
    }

    private void init() {
        initContractMap()
        for (ReinsuranceContract contract : componentList) {
            List<LegalEntity> reinsurers = contract.parmReinsurers.getValuesAsObjects(LegalEntityPortionConstraints.COMPANY_COLUMN_INDEX)
            for (LegalEntity reinsurer : reinsurers) {
                inwardLegalEntity.put(reinsurer, contract)
            }
            if (isGrossCover(contract)) {
                contractsBasedOnGrossClaims << contract
            }
        }
        for (ReinsuranceContract contract : componentList) {
            if (isContractCover(contract)) {
                List<ReinsuranceContractAndBase> coveredContracts = ((IContractCover) contract.parmCover).getCoveredReinsuranceContractsAndBase(reinsuranceContracts)
                contractsBasedOnContracts.createNode(contract.name)
                for (ReinsuranceContractAndBase coveredContract : coveredContracts) {
                    contractsBasedOnContracts.addRelation(contract.name, coveredContract.reinsuranceContract.name)
                    coverForContracts.put(contract, coveredContract)
                }
            }
            else if (includesInwardCover(contract)) {
                // todo(sku): fix! need to collect preceding contracts covering LE and probably gross peril
                List<ILegalEntityMarker> coveredLegalEntities = ((ILegalEntityCover) contract.parmCover).getCoveredLegalEntities()
                contractsBasedOnCompanies.createNode(contract.name)
                for (ILegalEntityMarker coveredLegalEntity : coveredLegalEntities) {
                    List<IReinsuranceContractMarker> coveredContracts = inwardLegalEntity.get(coveredLegalEntity);
                    for (IReinsuranceContractMarker coveredContract : coveredContracts) {
                        contractsBasedOnCompanies.createNode(coveredContract.name)
                        contractsBasedOnCompanies.addRelation(contract.name, coveredContract.name);
                    }
                }
            }
        }
    }

    private void initContractMap() {
        for (ReinsuranceContract contract: componentList) {
            reinsuranceContracts.put(contract.name, contract)
        }
    }

    private boolean isGrossCover(ReinsuranceContract contract) {
        boolean isGrossLegalEntitiyCover = getLegalEntityCoverMode(contract).equals(LegalEntityCoverMode.ORIGINALCLAIMS)
        return (isGrossLegalEntitiyCover || contract.parmCover.getType().equals(CoverAttributeStrategyType.ORIGINALCLAIMS))
    }

    private boolean isContractCover(ReinsuranceContract contract) {
        return contract.parmCover instanceof IContractCover
    }
    
    private LegalEntityCoverMode getLegalEntityCoverMode(ReinsuranceContract contract) {
        if (!contract.parmCover.getType().equals(CoverAttributeStrategyType.LEGALENTITIES)) return null
        return ((InwardLegalEntitiesCoverAttributeStrategy) contract.parmCover).legalEntityCoverMode
    }

    private boolean includesInwardCover(ReinsuranceContract contract) {
        LegalEntityCoverMode legalEntityCoverMode = getLegalEntityCoverMode(contract)
        return legalEntityCoverMode && !legalEntityCoverMode.equals(LegalEntityCoverMode.ORIGINALCLAIMS)
    }

    private boolean isInwardLegalEntityCover(ReinsuranceContract contract) {
        return contract.parmCover instanceof ILegalEntityCover && contract.parmCover.getType().equals(CoverAttributeStrategyType)
    }

    /**
     * All ceded information is wired directly to a replicating channel independently of specific reinsurance program.
     * Includes all replicating wiring independent of a p14n.
     */
    private void wireProgramIndependentReplications () {
        replicateOutChannels this, 'outCommission'
        for (ReinsuranceContract contract : componentList) {
            if (!contract.getParmVirtual()) {
                doWire PRC, this, 'outUnderwritingInfoCeded', contract, 'outUnderwritingInfoCeded'
                doWire PRC, this, 'outClaimsCeded', contract, 'outClaimsCeded'
            }
        }
    }

    /**
     * @return true if at least one contract has a none trivial cover strategy
     */
    private boolean noneTrivialContracts() {
        List<ReinsuranceContract> contractsWithNoCover = new ArrayList<ReinsuranceContract>()
        for (ReinsuranceContract contract: componentList) {
            if (contract.parmCover.getType().equals(CoverAttributeStrategyType.NONE)) {
                contractsWithNoCover.add(contract)
            }
        }
        LOG.debug("removed contracts: ${contractsWithNoCover.collectAll { it.normalizedName }}");
        componentList.size() > contractsWithNoCover.size()
    }

    /**
     * Helper method for wiring when sender or receiver are determined dynamically
     */
    public static void doWire(category, receiver, inChannelName, sender, outChannelName) {
        LOG.debug "$receiver.$inChannelName <- $sender.$outChannelName ($category)"
        category.doSetProperty(receiver, inChannelName, category.doGetProperty(sender, outChannelName))
    }
}
