package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class AbstractStabilizationStrategy extends AbstractParameterObject implements IStabilizationStrategy {

    protected ConstrainedMultiDimensionalParameter inflationIndices;
    protected StabilizationBasedOn stabilizationBasedOn;

    private List<Factors> factors;

    public Map getParameters() {
        Map<String, Object> params = new HashMap<String, Object>(2);
        params.put("inflationIndices", inflationIndices);
        params.put("stabilizationBasedOn", stabilizationBasedOn);
        return params;
    }

    public void mergeFactors(List<FactorsPacket> inFactors) {
        factors = IndexUtils.filterFactors(inFactors, inflationIndices);
    }

    public double indexFactor(ClaimCashflowPacket claim, IPeriodCounter periodCounter) {
        return IndexUtils.aggregateFactor(factors, claim.getUpdateDate(), periodCounter, claim.getOccurrenceDate());
    }

    public boolean basedOnPaid() {
        return stabilizationBasedOn.isPaid();
    }

    public boolean basedOnReported() {
        return stabilizationBasedOn.isReported();
    }
}
