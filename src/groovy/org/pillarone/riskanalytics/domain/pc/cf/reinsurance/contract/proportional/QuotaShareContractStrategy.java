package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class QuotaShareContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    private double quotaShare;
    private ILimitStrategy limit;
    private ICommissionStrategy commission;


    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.QUOTASHARE;
    }

    public Map getParameters() {
        Map params = new HashMap(2);
        params.put(QUOTASHARE, quotaShare);
        params.put(LIMIT, limit);
        params.put(COMMISSION, commission);
        return params;
    }

    /**
     * This implementation ignores all provided parameters.
     * @param period ignored
     * @param underwritingInfoPackets ignored
     * @param base ignored
     * @param termDeductible ignored
     * @param termLimit ignored
     * @return one contract
     */
    public List<IReinsuranceContract> getContracts(int period,
                                                   List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                                   IPeriodDependingThresholdStore termDeductible, IPeriodDependingThresholdStore termLimit) {
        IReinsuranceContract contract;
        if (limit instanceof NoneLimitStrategy) {
            contract = new QuotaShareContract(quotaShare, commission.getCalculator());
        }
        else if (limit instanceof AalLimitStrategy || limit instanceof AalAadLimitStrategy || limit instanceof  AadLimitStrategy) {
            contract = new AALAADQuotaShareContract(quotaShare, commission.getCalculator(), limit);
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
    public static final String LIMIT = "limit";
    public static final String COMMISSION = "commission";
}
