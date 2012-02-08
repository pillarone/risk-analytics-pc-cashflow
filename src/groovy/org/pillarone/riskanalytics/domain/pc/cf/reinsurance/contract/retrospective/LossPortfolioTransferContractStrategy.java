package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.EqualUsagePerPeriodThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ThresholdStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LossPortfolioTransferContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    private double cededShare;
    private double limit;
    private double reinsurancePremium;

    public RetrospectiveReinsuranceContractType getType() {
        return RetrospectiveReinsuranceContractType.LOSSPORTFOLIOTRANSFER;
    }

    public Map getParameters() {
        Map params = new HashMap(3);
        params.put(CEDEDSHARE, cededShare);
        params.put(LIMIT, limit);
        params.put(REINSURANCEPREMIUM, reinsurancePremium);
        return params;
    }

    public IReinsuranceContract getContract(List<UnderwritingInfoPacket> underwritingInfoPackets,
                                            ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit) {
        return new LossPortfolioTransferContract(cededShare, limit, reinsurancePremium);
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public static final String CEDEDSHARE = "cededShare";
    public static final String LIMIT = "limit";
    public static final String REINSURANCEPREMIUM = "reinsurancePremium";
}
