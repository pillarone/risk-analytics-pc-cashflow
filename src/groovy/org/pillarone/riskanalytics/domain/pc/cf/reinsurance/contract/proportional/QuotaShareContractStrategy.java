package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.ILossParticipationStrategy;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    private double quotaShare;
    private ILossParticipationStrategy lossParticipation;
    private ILimitStrategy limit;
    private ICommissionStrategy commission;

    /**not a parameter but updated during calculateCommission() to avoid side effect for the parameter variable
     * required as we need to share the loss carried forward among different instances */
    private DoubleValuePerPeriod lossCarriedForward;

    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.QUOTASHARE;
    }

    public Map getParameters() {
        Map params = new HashMap(4);
        params.put(QUOTASHARE, quotaShare);
        params.put(LOSSPARTICIPATION, lossParticipation);
        params.put(LIMIT, limit);
        params.put(COMMISSION, commission);
        return params;
    }

    /**
     * This implementation ignores all provided parameters.
     *
     *
     * @param period ignored
     * @param underwritingInfoPackets ignored
     * @param base ignored
     * @param termDeductible ignored
     * @param termLimit ignored
     * @param claims
     * @return one contract
     */
    public List<IReinsuranceContract> getContracts(int period,
                                                   List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                                   IPeriodDependingThresholdStore termDeductible, IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims) {
        IReinsuranceContract contract;
        if (period == 0) {
            lossCarriedForward = commission.getInitialLossCarriedForward();
        }
        if (limit instanceof NoneLimitStrategy) {
            contract = new QuotaShareContract(quotaShare, commission.getCalculator(lossCarriedForward), lossParticipation.getLossParticpation());
        }
        else if (limit instanceof AalLimitStrategy || limit instanceof AalAadLimitStrategy || limit instanceof  AadLimitStrategy) {
            contract = new AALAADQuotaShareContract(quotaShare, commission.getCalculator(lossCarriedForward), limit, lossParticipation.getLossParticpation());
        }
        else if (limit instanceof EventLimitStrategy) {
            contract = new EventLimitQuotaShareContract(quotaShare, commission.getCalculator(lossCarriedForward), (EventLimitStrategy) limit, lossParticipation.getLossParticpation());
        }
        else {
            throw new NotImplementedException(limit + " not implemented.");
        }
        return new ArrayList<IReinsuranceContract>(Arrays.asList(contract));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public static final String QUOTASHARE = "quotaShare";
    public static final String LOSSPARTICIPATION = "lossParticipation";
    public static final String LIMIT = "limit";
    public static final String COMMISSION = "commission";
}
