package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class StopLossConstractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    /** Premium can be expressed as a fraction of a base quantity. */
    private StopLossBase contractBase;

    /** Premium as a percentage of the premium base */
    private double premium;

    /** Strategy to allocate the ceded premium to the different lines of business  */
    private IRIPremiumSplitStrategy premiumAllocation;
    /** As a percentage of premium */
    private AbstractMultiDimensionalParameter reinstatementPremiums;
    private double attachmentPoint;
    private double limit;

    public Map getParameters() {
        Map params = new HashMap(6);
        params.put(CONTRACT_BASE, contractBase);
        params.put(PREMIUM, premium);
        params.put(PREMIUM_ALLOCATION, premiumAllocation);
        params.put(REINSTATEMENT_PREMIUMS, reinstatementPremiums);
        params.put(ATTACHMENT_POINT, attachmentPoint);
        params.put(LIMIT, limit);
        return params;
    }

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractType.STOPLOSS;
    }

    public IReinsuranceContract getContract(List<UnderwritingInfoPacket> underwritingInfoPackets) {
        double cededPremiumFixed = premium;
        double scaledAttachmentPoint = attachmentPoint;
        double scaledLimit = limit;
        switch (contractBase) {
            case ABSOLUTE:
                break;
            case GNPI:
                double gnpi = UnderwritingInfoUtils.sumPremiumWritten(underwritingInfoPackets);
                cededPremiumFixed *= gnpi;
                scaledAttachmentPoint *= gnpi;
                scaledLimit *= gnpi;
                break;
        }
        return new StopLossContract(cededPremiumFixed, scaledAttachmentPoint, scaledLimit, premiumAllocation);
    }

    public static final String CONTRACT_BASE = "contractBase";
    public static final String PREMIUM = "premium";
    public static final String PREMIUM_ALLOCATION = "riPremiumSplit";
    public static final String REINSTATEMENT_PREMIUMS = "reinstatementPremiums";
    public static final String ATTACHMENT_POINT = "attachmentPoint";
    public static final String LIMIT = "limit";

}
