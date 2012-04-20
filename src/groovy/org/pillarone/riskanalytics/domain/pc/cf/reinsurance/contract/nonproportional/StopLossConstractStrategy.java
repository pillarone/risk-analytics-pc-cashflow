package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class StopLossConstractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    /** Premium can be expressed as a fraction of a base quantity. */
    private StopLossBase stopLossContractBase;

    /** Premium as a percentage of the premium base */
    private double premium;

    /** Strategy to allocate the ceded premium to the different lines of business  */
    private IRIPremiumSplitStrategy premiumAllocation;
    private double attachmentPoint;
    private double limit;

    public Map getParameters() {
        Map params = new HashMap(5);
        params.put(CONTRACT_BASE, stopLossContractBase);
        params.put(PREMIUM, premium);
        params.put(PREMIUM_ALLOCATION, premiumAllocation);
        params.put(ATTACHMENT_POINT, attachmentPoint);
        params.put(LIMIT, limit);
        return params;
    }

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractType.STOPLOSS;
    }

    public List<IReinsuranceContract> getContracts(List<UnderwritingInfoPacket> underwritingInfoPackets,
                                                   ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit) {
        double cededPremiumFixed = premium;
        double scaledAttachmentPoint = attachmentPoint;
        double scaledLimit = limit;
        switch (stopLossContractBase) {
            case ABSOLUTE:
                break;
            case GNPI:
                double gnpi = UnderwritingInfoUtils.sumPremiumWritten(underwritingInfoPackets);
                cededPremiumFixed *= gnpi;
                scaledAttachmentPoint *= gnpi;
                scaledLimit *= gnpi;
                break;
        }
        return new ArrayList<IReinsuranceContract>(Arrays.asList(
                new StopLossContract(cededPremiumFixed, scaledAttachmentPoint, scaledLimit, premiumAllocation)));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public static final String CONTRACT_BASE = "stopLossContractBase";
    public static final String PREMIUM = "premium";
    public static final String PREMIUM_ALLOCATION = "riPremiumSplit";
    public static final String ATTACHMENT_POINT = "attachmentPoint";
    public static final String LIMIT = "limit";

}
