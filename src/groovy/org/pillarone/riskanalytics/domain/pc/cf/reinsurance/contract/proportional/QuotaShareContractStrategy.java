package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    private double quotaShare;
    private ILimitStrategy limit;
    private ICommissionStrategy commission;
    private ProportionalPremiumBase premiumBase;


    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.QUOTASHARE;
    }

    public Map getParameters() {
        Map params = new HashMap(2);
        params.put(QUOTASHARE, quotaShare);
        params.put(LIMIT, limit);
        params.put(COMMISSION, commission);
        params.put(PREMIUM_BASE, premiumBase);
        return params;
    }

    public IReinsuranceContract getContract(List<UnderwritingInfoPacket> underwritingInfoPackets) {
        if (limit instanceof NoneLimitStrategy) {
            return new QuotaShareContract(quotaShare, commission.getCalculator(), premiumBase);
        }
        else if (limit instanceof AalLimitStrategy) {
            return new AALQuotaShareContract(quotaShare, commission.getCalculator(), (AalLimitStrategy) limit, premiumBase);
        }
        else if (limit instanceof AadLimitStrategy) {
            return new AADQuotaShareContract(quotaShare, commission.getCalculator(), (AadLimitStrategy) limit, premiumBase);
        }
        else if (limit instanceof AalAadLimitStrategy) {
            return new AALAADQuotaShareContract(quotaShare, commission.getCalculator(), (AalAadLimitStrategy) limit, premiumBase);
        }
        else {
            throw new NotImplementedException(limit + " not implemented.");
        }
    }

    public static final String QUOTASHARE = "quotaShare";
    public static final String LIMIT = "limit";
    public static final String COMMISSION = "commission";
    public static final String PREMIUM_BASE = "premiumBase";
}
