package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IPremiumAllocationStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.PremiumBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class WXLConstractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    /** Premium can be expressed as a fraction of a base quantity. */
    private PremiumBase premiumBase;

    /** Premium as a percentage of the premium base */
    private double premium;

    /** Strategy to allocate the ceded premium to the different lines of business  */
    private IPremiumAllocationStrategy premiumAllocation;
    /** As a percentage of premium */
    private AbstractMultiDimensionalParameter reinstatementPremiums;
    private double attachmentPoint;
    private double limit;
    private double aggregateDeductible;
    private double aggregateLimit;


    public Map getParameters() {
        Map params = new HashMap();
        params.put(PREMIUM_BASE, premiumBase);
        params.put(PREMIUM, premium);
        params.put(PREMIUM_ALLOCATION, premiumAllocation);
        params.put(REINSTATEMENT_PREMIUMS, reinstatementPremiums);
        params.put(ATTACHMENT_POINT, attachmentPoint);
        params.put(LIMIT, limit);
        params.put(AGGREGATE_DEDUCTIBLE, aggregateDeductible);
        params.put(AGGREGATE_LIMIT, aggregateLimit);
        return params;
    }

    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.WXL;
    }

    public IReinsuranceContract getContract(List<UnderwritingInfoPacket> underwritingInfoPackets) {
        double cededPremiumFixed = 0;
        switch (premiumBase) {
            case ABSOLUTE:
                cededPremiumFixed = premium;
                break;
            case GNPI:
                cededPremiumFixed = premium * UnderwritingInfoUtils.sumPremiumWritten(underwritingInfoPackets);
                break;
            case RATE_ON_LINE:
                cededPremiumFixed = premium * limit;
                break;
            case NUMBER_OF_POLICIES:
                cededPremiumFixed = premium * UnderwritingInfoUtils.sumNumberOfPolicies(underwritingInfoPackets);
                break;
        }
        List<Double> reinstatementPremiumFactors = reinstatementPremiums.getValues();
        return new WXLContract(cededPremiumFixed, attachmentPoint, limit, aggregateDeductible, aggregateLimit,
                reinstatementPremiumFactors, premiumAllocation);
    }

    public static final String PREMIUM_BASE = "premiumBase";
    public static final String PREMIUM = "premium";
    public static final String PREMIUM_ALLOCATION = "premiumAllocation";
    public static final String REINSTATEMENT_PREMIUMS = "reinstatementPremiums";
    public static final String ATTACHMENT_POINT = "attachmentPoint";
    public static final String LIMIT = "limit";
    public static final String AGGREGATE_DEDUCTIBLE = "aggregateDeductible";
    public static final String AGGREGATE_LIMIT = "aggregateLimit";
}
