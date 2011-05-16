package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.pillarone.riskanalytics.core.packets.PacketList

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityDefaultPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceContracts extends DynamicComposedComponent {

    PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket);
    PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket);
    PacketList<LegalEntityDefaultPacket> inReinsurersDefault = new PacketList<LegalEntityDefaultPacket>(LegalEntityDefaultPacket);

    PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket);
    PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket);
    PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket);
    PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket);
    PacketList<ContractFinancialsPacket> outContractFinancials = new PacketList<ContractFinancialsPacket>(ContractFinancialsPacket);
    PacketList<CommissionPacket> outCommission = new PacketList<CommissionPacket>(CommissionPacket);

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
        replicateInChannels this, 'inClaims'
        replicateInChannels this, 'inUnderwritingInfo'
        replicateInChannels this, 'inReinsurersDefault'
        replicateOutChannels this, 'outClaimsNet'
        replicateOutChannels this, 'outClaimsCeded'
        replicateOutChannels this, 'outUnderwritingInfoNet'
        replicateOutChannels this, 'outUnderwritingInfoCeded'
        replicateOutChannels this, 'outContractFinancials'
        replicateOutChannels this, 'outCommission'
    }
}
