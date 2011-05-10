package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;

import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TrivialContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {
    public IReinsuranceContract getContract(List<UnderwritingInfoPacket> underwritingInfoPackets) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IParameterObjectClassifier getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Map getParameters() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
