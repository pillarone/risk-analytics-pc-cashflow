package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
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
public class AgeToAgeIndexStrategy extends AbstractParameterObject implements IIndexStrategy {

    public static final String RATIOS = "ratios";

    private ConstrainedMultiDimensionalParameter ratios;
    private FactorsPacket factors;


    public IParameterObjectClassifier getType() {
        return IndexStrategyType.AGE_TO_AGE;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(RATIOS, ratios);
        return params;
    }

    public FactorsPacket getFactors(PeriodScope periodScope, Index origin, List<EventDependenceStream> eventStreams) {
        lazyInitFactors(origin);
        return factors;
    }

    protected void lazyInitFactors(Index origin) {
        if (factors == null) {
            factors = new FactorsPacket();
            int dateColumnIndex = ratios.getColumnIndex(LinkRatioIndexTableConstraints.DATE);
            int ratioColumnIndex = ratios.getColumnIndex(LinkRatioIndexTableConstraints.LINK_TO_LINK_RATIO);
            double index= 1.0;
            if (ratios.getValues().size() > 0) {
                for (int row = ratios.getTitleRowCount(); row < ratios.getRowCount(); row++) {
                    DateTime indexDate = (DateTime) ratios.getValueAt(row, dateColumnIndex);
                    double ratio = InputFormatConverter.getDouble(ratios.getValueAt(row, ratioColumnIndex));
                    factors.add(indexDate, index);
                    factors.origin = origin;
                    index *= ratio;
                }
            }
            else {
                return; // in the trivial case the returned list has to be void
            }
        }
    }
}
