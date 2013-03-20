package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoneCoverStrategy extends AbstractParameterObject implements ICoverStrategy {

    public IParameterObjectClassifier getType() {
        return CoverStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public void coveredClaims(List<ClaimCashflowPacket> source) {

    }
}
