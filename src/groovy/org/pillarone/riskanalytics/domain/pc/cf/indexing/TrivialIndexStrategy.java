package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;

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

    public FactorsPacket getFactors(PeriodScope periodScope, Component origin, List<EventDependenceStream> eventStreams) {
        return null;
    }
}
