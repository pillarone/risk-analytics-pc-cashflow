package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DeterministicAnnualChangeIndexStrategy extends AbstractParameterObject implements IIndexStrategy {

    public static final String INDICES = "indices";

    private ConstrainedMultiDimensionalParameter indices;
    private List<FactorsPacket> factors;


    public IParameterObjectClassifier getType() {
        return IndexStrategyType.DETERMINISTICANNUALCHANGE;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(INDICES, indices);
        return params;
    }

    public List<FactorsPacket> getFactors(PeriodScope periodScope, Index origin) {
        lazyInitFactors(origin);
        return factors;
    }

    protected void lazyInitFactors(Index origin) {
        if (factors == null) {
            factors = new ArrayList<FactorsPacket>();
            int dateColumnIndex = indices.getColumnIndex(AnnualIndexTableConstraints.DATE);
            int changeColumnIndex = indices.getColumnIndex(AnnualIndexTableConstraints.CHANGE);
            FactorsPacket factor = new FactorsPacket();
            if (indices.getValues().size() > 0) {
                double factorProduct = 1d;
                DateTime formerDate = null;
                double formerChange = 0d;
                for (int row = indices.getTitleRowCount(); row < indices.getRowCount(); row++) {
                    DateTime date = (DateTime) indices.getValueAt(row, dateColumnIndex);
                    double change = InputFormatConverter.getDouble(indices.getValueAt(row, changeColumnIndex));
                    factorProduct *= incrementalFactor(formerDate, formerChange, date);
                    factor.add(date, factorProduct);
                    formerDate = date;
                    formerChange = change;
                }
                DateTime nextDate = formerDate.plus(Period.years(1));
                factor.add(nextDate, factorProduct * incrementalFactor(formerDate, formerChange, nextDate));
                factor.origin = origin;
            }
            else {
                return; // in the trivial case the returned list has to be void
            }
            factors.add(factor);
        }
    }

    private double incrementalFactor(DateTime formerDate, double formerChange, DateTime date) {
        if (formerDate != null) {
            double elapsedTime = Days.daysBetween(formerDate, date).getDays() / 365.2425;
            return Math.pow(1 + formerChange, elapsedTime);
        }
        return 1d;
    }

}
