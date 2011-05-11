package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TrivialIndexStrategy extends AbstractParameterObject implements IIndexStrategy {

    public IParameterObjectClassifier getType() {
        return IndexStrategyType.NONE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public List<FactorsPacket> getFactors(PeriodScope periodScope, Index origin) {
        return new ArrayList<FactorsPacket>();
    }
}
