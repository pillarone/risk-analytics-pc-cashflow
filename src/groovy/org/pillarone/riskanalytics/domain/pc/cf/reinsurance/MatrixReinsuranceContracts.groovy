package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.wiring.PortReplicatorCategory as PRC
import org.pillarone.riskanalytics.core.wiring.WireCategory as WC
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.NoneCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class MatrixReinsuranceContracts extends DynamicComposedComponent {

    static Log LOG = LogFactory.getLog(MatrixReinsuranceContracts)

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
    List<ClaimMerger> claimMergers = []

    public ReinsuranceContract createDefaultSubComponent() {
        return new ReinsuranceContract(
                parmReinsurers: new ConstrainedMultiDimensionalParameter(
                        Collections.emptyList(), LegalEntityPortionConstraints.COLUMN_TITLES,
                        ConstraintsFactory.getConstraints(LegalEntityPortionConstraints.IDENTIFIER)),
                parmCover: CoverAttributeStrategyType.getDefault(),
                parmCoveredPeriod: PeriodStrategyType.getDefault(),
                parmContractStrategy: ReinsuranceContractType.getDefault());
    }

    @Override
    void wire() {
        if (noneTrivialContracts()) {
            replicateInChannels this, inFactors
            replicateInChannels this, inLegalEntityDefault
            replicateOutChannels this, outClaimsInward
            replicateOutChannels this, outUnderwritingInfoInward
            wireContractsBasedOnGross()
            wireContractsBaseOnNetContracts()
            wireContractsBaseOnCededContracts()
            wireWithMerger()
            wireProgramIndependentReplications()
        }
    }

/**
 * in channels of contracts based on original (gross) claims can be wired directly with replicating in channels
 */
    private void wireContractsBasedOnGross() {
        for (ReinsuranceContract contract : componentList) {
            MatrixCoverAttributeStrategy strategy = getCoverStrategy(contract)
            if (strategy?.coverGrossClaimsOnly()) {
                doWire PRC, contract, 'inClaims', this, 'inClaims'
                doWire PRC, contract, 'inUnderwritingInfo', this, 'inUnderwritingInfo'
            }
        }
    }

    private void wireWithMerger() {
        // todo: wire uw info
        for (ReinsuranceContract contract : componentList) {
            MatrixCoverAttributeStrategy strategy = getCoverStrategy(contract)
            if (strategy?.mergerRequired()) {
                ClaimMerger claimMerger = new ClaimMerger(coverAttributeStrategy: strategy)
                claimMergers << claimMerger
                List<IReinsuranceContractMarker> benefitContracts = strategy.benefitContracts
                List<IReinsuranceContractMarker> coveredNetOfContracts = strategy.coveredNetOfContracts()
                List<IReinsuranceContractMarker> coveredCededOfContracts = strategy.coveredCededOfContracts()
                for (IReinsuranceContractMarker coveredContract : coveredCededOfContracts) {
                    doWire WC, claimMerger, 'inClaimsCeded', coveredContract, 'outClaimsCeded'
                }
                if (coveredNetOfContracts.size() > 0 || benefitContracts.size() > 0) {
                    for (IReinsuranceContractMarker coveredContract : coveredNetOfContracts) {
                        doWire WC, claimMerger, 'inClaimsNet', coveredContract, 'outClaimsNet'
                        doWire WC, claimMerger, 'inClaimsCededForNet', coveredContract, 'outClaimsCeded'
                    }
                    for (IReinsuranceContractMarker benefitContract : benefitContracts) {
                        doWire WC, claimMerger, 'inClaimsBenefit', benefitContract, 'outClaimsCeded'
                    }
                    doWire PRC, claimMerger, 'inClaimsGross', this, 'inClaims'
                }
                else if (strategy.hasGrossFilters()) {
                    doWire PRC, claimMerger, 'inClaimsGross', this, 'inClaims'
                }
                doWire WC, contract, 'inClaims', claimMerger, 'outClaims'
            }
        }
    }

    private MatrixCoverAttributeStrategy getCoverStrategy(ReinsuranceContract contract) {
        if (contract.parmCover instanceof MatrixCoverAttributeStrategy) {
            return (MatrixCoverAttributeStrategy) contract.parmCover
        } else if (contract.parmCover instanceof NoneCoverAttributeStrategy) {
            return null
        } else {
            throw new IllegalArgumentException('This Reinsurance Program allows only Matrix Cover. Developer has to restrict available cover types on model level.')
        }
    }

    private void wireContractsBaseOnNetContracts() {
        for (ReinsuranceContract contract : componentList) {
            MatrixCoverAttributeStrategy strategy = getCoverStrategy(contract)
            if (strategy && !strategy?.mergerRequired()) {
                for (IReinsuranceContractMarker coveredContract : strategy.coveredNetOfContracts()) {
                    doWire WC, contract, 'inClaims', coveredContract, 'outClaimsNet'
                    doWire WC, contract, 'inUnderwritingInfo', coveredContract, 'outUnderwritingInfoGNPI'
                }
            }
        }
    }

    private void wireContractsBaseOnCededContracts() {
        for (ReinsuranceContract contract : componentList) {
            MatrixCoverAttributeStrategy strategy = getCoverStrategy(contract)
            if (strategy && !strategy?.mergerRequired()) {
                for (IReinsuranceContractMarker coveredContract : strategy.coveredCededOfContracts()) {
                    doWire WC, contract, 'inClaims', coveredContract, 'outClaimsCeded'
                    doWire WC, contract, 'inUnderwritingInfo', coveredContract, 'outUnderwritingInfoCeded'
                }
            }
        }
    }

    /**
     * All ceded information is wired directly to a replicating channel independently of specific reinsurance program.
     * Includes all replicating wiring independent of a p14n.
     */
    private void wireProgramIndependentReplications() {
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
        for (ReinsuranceContract contract : componentList) {
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
        println "$receiver.$inChannelName <- $sender.$outChannelName ($category)"
        category.doSetProperty(receiver, inChannelName, category.doGetProperty(sender, outChannelName))
    }
}
