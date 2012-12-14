package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SurplusContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    /** line/maximum */
    private double retention;
    /** number of lines */
    private double lines;
    /** surplus share for claims without sum insured information */
    private double defaultCededLossShare;

    private ICommissionStrategy commission;


    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.SURPLUS;
    }

    public Map getParameters() {
        Map params = new HashMap(4);
        params.put(RETENTION, retention);
        params.put(LINES, lines);
        params.put(DEFAULTCEDEDLOSSSHARE, defaultCededLossShare);
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
        return new ArrayList<IReinsuranceContract>(Arrays.asList(
                new SurplusContract(retention, lines, defaultCededLossShare, commission.getCalculator(new DoubleValue()))));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public static final String RETENTION = "retention";
    public static final String LINES = "lines";
    public static final String DEFAULTCEDEDLOSSSHARE = "defaultCededLossShare";
    public static final String COMMISSION = "commission";
}
