package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IStabilizationStrategy extends IParameterObject {

    void mergeFactors(List<FactorsPacket> inFactors);

    double indexFactor(DateTime date);
    boolean basedOnPaid();
    boolean basedOnReported();
}
