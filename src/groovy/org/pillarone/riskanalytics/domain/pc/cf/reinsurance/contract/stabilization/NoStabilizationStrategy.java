package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoStabilizationStrategy extends AbstractParameterObject implements IStabilizationStrategy {

    public IParameterObjectClassifier getType() {
        return StabilizationStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public void mergeFactors(List<FactorsPacket> inFactors) {
    }

    public double indexFactor(ClaimCashflowPacket claim, IPeriodCounter periodCounter) {
        return 1;
    }

    public boolean basedOnPaid() {
        return false;
    }

    public boolean basedOnReported() {
        return false;
    }
}
