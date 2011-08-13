package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.pillarone.riskanalytics.core.wiring.WireCategory as WC

import com.google.common.collect.ArrayListMultimap
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
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityDefaultPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.graph.Graph
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.*
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker
import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): diamond cover, GNPI, mixing contract and company cover allowed?
class ReinsuranceContracts extends DynamicComposedComponent {

    static Log LOG = LogFactory.getLog(ReinsuranceContracts)

    PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<LegalEntityDefaultPacket> inReinsurersDefault = new PacketList<LegalEntityDefaultPacket>(LegalEntityDefaultPacket)
    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<LegalEntityDefault> inLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault)

    PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
    PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoInward = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
    PacketList<CommissionPacket> outCommission = new PacketList<CommissionPacket>(CommissionPacket)

    private List contractsBasedOnGrossClaims = []
    private Graph contractsBasedOnContracts = new Graph()
    private ListMultimap<ReinsuranceContract, ReinsuranceContractAndBase> contractCoveredBy = ArrayListMultimap.create()
    private Graph contractsBasedOnCompanies = new Graph()
    private ListMultimap<ILegalEntityMarker, IReinsuranceContractMarker> coverForLegalEntity = ArrayListMultimap.create()
    private ListMultimap<ILegalEntityMarker, IReinsuranceContractMarker> inwardLegalEntity = ArrayListMultimap.create()

    /** key: normalized contract name, value: contract instance */
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
            replicateInChannels this, 'inUnderwritingInfo'
            replicateInChannels this, 'inFactors'
            replicateInChannels this, 'inLegalEntityDefault'
            replicateOutChannels this, 'outClaimsInward'
            replicateOutChannels this, 'outUnderwritingInfoInward'
            wireContractsBasedOnGross()
            wireContractsBaseOnContracts()
            wireContractsIncludingInwardBusiness()
            wireProgramIndependentReplications()
        }
        else {
            wireTrivialContractsOnly()
        }
        println contractsBasedOnContracts
        println contractsBasedOnCompanies
    }

    private void wireContractsBasedOnGross() {
        for (ReinsuranceContract contract : contractsBasedOnGrossClaims) {
            doWire PortReplicatorCategory, contract, 'inClaims', this, 'inClaims'
        }
    }

    private void wireContractsBaseOnContracts() {
        for (Map.Entry<ReinsuranceContract, ReinsuranceContractAndBase> contractCoveredByContracts: contractCoveredBy.entries()) {
            for (ReinsuranceContractAndBase contractAndBase : contractCoveredByContracts.value) {
                if (contractAndBase.contractBase.equals(ReinsuranceContractBase.CEDED)) {
                    doWire WC, contractCoveredByContracts.key, 'inClaims', contractAndBase.reinsuranceContract, 'outClaimsCeded'
                }
                else {
                    doWire WC, contractCoveredByContracts.key, 'inClaims', contractAndBase.reinsuranceContract, 'outClaimsNet'
                }
            }
        }
    }

    private void wireContractsIncludingInwardBusiness() {
        if (contractsBasedOnCompanies.nodes.size() > 0) {
            for (ReinsuranceContract contract : contractsBasedOnCompanies) {
                if (contract.parmCover.getType().equals(CoverAttributeStrategyType.INWARDLEGALENTITIES)) {
                    List<ILegalEntityMarker> coveredLegalEntities = ((InwardLegalEntitiesCoverAttributeStrategy) contract.parmCover).getCoveredLegalEntities();
                    for (ILegalEntityMarker legalEntity : coveredLegalEntities) {
                        for (ReinsuranceContract preceedingContract : inwardLegalEntity.get(legalEntity)) {
                            doWire WC, contract, 'inClaims', preceedingContract, 'outClaimsInward'
                        }
                    }
                }
            }
        }
    }

    private void init() {
        initContractMap()
        for (ReinsuranceContract contract : componentList) {
            if (isGrossCover(contract)) {
                contractsBasedOnGrossClaims << contract
            }
            else if (isContractCover(contract)) {
                List<ReinsuranceContractAndBase> coveredContracts = ((IContractCover) contract.parmCover).getCoveredReinsuranceContractsAndBase(reinsuranceContracts)
                contractsBasedOnContracts.createNode(contract.name)
                for (ReinsuranceContractAndBase coveredContract : coveredContracts) {
                    contractsBasedOnContracts.addRelation(contract.name, coveredContract.reinsuranceContract.name)
                    contractCoveredBy.put(contract, coveredContract)
                }
            }
            else if (isLegalEntityCover(contract)) {
                List<ILegalEntityMarker> coveredContracts = ((ILegalEntityCover) contract).getCoveredLegalEntities()
                contractsBasedOnCompanies.createNode(contract.name)
                for (IReinsuranceContractMarker coveredContract : coveredContracts) {
                    contractsBasedOnCompanies.addRelation(contract.name, coveredContract.name)
                }
            }
            List<LegalEntity> reinsurers = contract.parmReinsurers.getValuesAsObjects(LegalEntityPortionConstraints.COMPANY_COLUMN_INDEX)
            for (LegalEntity reinsurer : reinsurers) {
                inwardLegalEntity.put(reinsurer, contract)
            }
        }
    }

    private void initContractMap() {
        for (ReinsuranceContract contract: componentList) {
            reinsuranceContracts.put(contract.normalizedName, contract)
        }
    }

    private boolean isGrossCover(ReinsuranceContract contract) {
        return (contract.parmCover.getType().equals(CoverAttributeStrategyType.ALL)
                || contract.parmCover.getType().equals(CoverAttributeStrategyType.GROSSPERILS)
                || contract.parmCover.getType().equals(CoverAttributeStrategyType.GROSSSEGMENTS)
                || contract.parmCover.getType().equals(CoverAttributeStrategyType.GROSSPERILSSEGMENTS)
                || contract.parmCover.getType().equals(CoverAttributeStrategyType.GROSSLEGALENTITIES))
    }

    private boolean isContractCover(ReinsuranceContract contract) {
        return contract.parmCover instanceof IContractCover
    }

    private boolean isLegalEntityCover(ReinsuranceContract contract) {
        return contract.parmCover instanceof ILegalEntityCover
    }

    /**
     * includes all replicating wiring independent of a p14n
     */
    private void wireProgramIndependentReplications () {
        replicateInChannels this, 'inReinsurersDefault'
        replicateOutChannels this, 'outCommission'
        replicateOutChannels this, 'outUnderwritingInfoCeded'
        replicateOutChannels this, 'outClaimsCeded'
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
     * directly wire incoming claims and underwriting information to its corresponding net channel
     */
    // todo(sku): how to deal with missing/trivial reinsurance contracts as net channels have to be removed? -> model level?
    private void wireTrivialContractsOnly() {
        LOG.debug 'wireTrivialContractsOnly()'
//        doWire WC, this, 'outClaimsNet', this, 'inClaims'
//        doWire WC, this, 'outUnderwritingInfoNet', this, 'inUnderwritingInfo'
    }

    /**
     * Helper method for wiring when sender or receiver are determined dynamically
     */
    public static void doWire(category, receiver, inChannelName, sender, outChannelName) {
        LOG.debug "$receiver.$inChannelName <- $sender.$outChannelName ($category)"
        category.doSetProperty(receiver, inChannelName, category.doGetProperty(sender, outChannelName))
    }
}
