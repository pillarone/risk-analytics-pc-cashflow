package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DeterministicIndexStrategy extends AbstractParameterObject implements IIndexStrategy {

    public static final String INDICES = "indices";

    private ConstrainedMultiDimensionalParameter indices;
    private FactorsPacket factors;


    public IParameterObjectClassifier getType() {
        return IndexStrategyType.DETERMINISTICINDEXSERIES;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(INDICES, indices);
        return params;
    }

    public FactorsPacket getFactors(PeriodScope periodScope, Component origin, List<EventDependenceStream> eventStreams) {
        lazyInitFactors(origin);
        return factors;
    }

    protected void lazyInitFactors(Component origin) {
        if (factors == null) {
            factors = new FactorsPacket();
            int dateColumnIndex = indices.getColumnIndex(DeterministicIndexTableConstraints.DATE);
            int indexColumnIndex = indices.getColumnIndex(DeterministicIndexTableConstraints.INDEX);
            if (indices.getValues().size() > 0) {
                for (int row = indices.getTitleRowCount(); row < indices.getRowCount(); row++) {
                    DateTime indexDate = (DateTime) indices.getValueAt(row, dateColumnIndex);
                    double index = InputFormatConverter.getDouble(indices.getValueAt(row, indexColumnIndex));
                    factors.add(indexDate, index);
                    factors.origin = origin;
                }
            }
        }
    }
}
