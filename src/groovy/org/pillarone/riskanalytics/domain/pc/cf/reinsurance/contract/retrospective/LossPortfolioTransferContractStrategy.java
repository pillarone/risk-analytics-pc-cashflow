package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;

import java.util.*;

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

    /**
     * This implementation ignores all provided parameters.
     *
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
        return new ArrayList<IReinsuranceContract>(Arrays.asList(new LossPortfolioTransferContract(cededShare, limit, reinsurancePremium)));
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
