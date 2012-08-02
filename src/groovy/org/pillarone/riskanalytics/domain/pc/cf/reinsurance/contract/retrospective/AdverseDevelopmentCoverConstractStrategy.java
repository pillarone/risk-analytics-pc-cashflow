package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
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
public class AdverseDevelopmentCoverConstractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    private double attachmentPoint;
    private double limit;
    private double reinsurancePremium;

    public Map getParameters() {
        Map params = new HashMap(3);
        params.put(ATTACHMENT_POINT, attachmentPoint);
        params.put(LIMIT, limit);
        params.put(REINSURANCEPREMIUM, reinsurancePremium);
        return params;
    }

    public IParameterObjectClassifier getType() {
        return RetrospectiveReinsuranceContractType.ADVERSEDEVELOPMENTCOVER;
    }

    public List<IReinsuranceContract> getContracts(int period, List<UnderwritingInfoPacket> underwritingInfoPackets,
                                                   ExposureBase base, IPeriodDependingThresholdStore termDeductible,
                                                   IPeriodDependingThresholdStore termLimit) {
        return new ArrayList<IReinsuranceContract>(Arrays.asList(new AdverseDevelopmentCoverContract(reinsurancePremium, attachmentPoint, limit)));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public static final String REINSURANCEPREMIUM = "reinsurancePremium";
    public static final String ATTACHMENT_POINT = "attachmentPoint";
    public static final String LIMIT = "limit";

}
