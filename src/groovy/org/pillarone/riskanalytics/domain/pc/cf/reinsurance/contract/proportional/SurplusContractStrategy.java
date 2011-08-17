package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ProportionalPremiumBase premiumBase;


    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.SURPLUS;
    }

    public Map getParameters() {
        Map params = new HashMap(4);
        params.put(RETENTION, retention);
        params.put(LINES, lines);
        params.put(DEFAULTCEDEDLOSSSHARE, defaultCededLossShare);
        params.put(COMMISSION, commission);
        params.put(PREMIUM_BASE, premiumBase);
        return params;
    }

    public IReinsuranceContract getContract(List<UnderwritingInfoPacket> underwritingInfoPackets) {
        return new SurplusContract(retention, lines, defaultCededLossShare, commission.getCalculator(), premiumBase);
    }

    public static final String RETENTION = "retention";
    public static final String LINES = "lines";
    public static final String DEFAULTCEDEDLOSSSHARE = "default ceded loss share";
    public static final String COMMISSION = "commission";
    public static final String PREMIUM_BASE = "premiumBase";
}
