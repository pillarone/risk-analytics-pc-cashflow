package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.EqualUsagePerPeriodThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ThresholdStore;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TrivialContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    public List<IReinsuranceContract> getContracts(List<UnderwritingInfoPacket> underwritingInfoPackets,
                                                   ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit) {
        return new ArrayList<IReinsuranceContract>(Arrays.asList(new TrivialContract()));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractType.TRIVIAL;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
