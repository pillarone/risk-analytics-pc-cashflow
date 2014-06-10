package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.RetrospectiveCoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints

/**
 * The current implementation does not allow any contract chains and does not check double cover.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RetrospectiveReinsuranceContracts extends DynamicComposedComponent {

    static Log LOG = LogFactory.getLog(RetrospectiveReinsuranceContracts)

    PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<LegalEntityDefault> inLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault)

    PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> outClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket)
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoInward = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
    PacketList<ContractFinancialsPacket> outContractFinancials = new PacketList<ContractFinancialsPacket>(ContractFinancialsPacket);

    private List contractsBasedOnGrossClaims = []
    /** contains the relations between reinsurance contracts covering legal entities */
    private ListMultimap<ILegalEntityMarker, IReinsuranceContractMarker> coverForLegalEntity = ArrayListMultimap.create()

    public RetrospectiveReinsuranceContract createDefaultSubComponent(){
        return new RetrospectiveReinsuranceContract(
                parmReinsurers : new ConstrainedMultiDimensionalParameter(
                    Collections.emptyList(), LegalEntityPortionConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(LegalEntityPortionConstraints.IDENTIFIER)),
                parmCover : RetrospectiveCoverAttributeStrategyType.getDefault(),
                parmContractStrategy : RetrospectiveReinsuranceContractType.getDefault());
    }

    @Override
    void wire() {
        if (noneTrivialContracts()) {
            replicateInChannels this, inLegalEntityDefault
            replicateInChannels this, inClaims
            replicateInChannels this, inUnderwritingInfo
            replicateOutChannels this, outClaimsInward
            replicateOutChannels this, outUnderwritingInfoInward
            replicateOutChannels this, outUnderwritingInfoCeded
            replicateOutChannels this, outClaimsCeded
            replicateOutChannels this, outContractFinancials
        }
    }

    /**
     * @return true if at least one contract has a none trivial cover strategy
     */
    private boolean noneTrivialContracts() {
        List<RetrospectiveReinsuranceContract> contractsWithNoCover = new ArrayList<RetrospectiveReinsuranceContract>()
        for (RetrospectiveReinsuranceContract contract: componentList) {
            if (contract.parmCover.getType().equals(RetrospectiveCoverAttributeStrategyType.NONE)) {
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
