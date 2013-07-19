package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NoneExclusionStrategy extends AbstractParameterObject implements IExclusionCoverStrategy {

    public IParameterObjectClassifier getType() {
        return ExclusionStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public void exclusionClaims(final List<ClaimCashflowPacket> source) {
        return;
    }
}
