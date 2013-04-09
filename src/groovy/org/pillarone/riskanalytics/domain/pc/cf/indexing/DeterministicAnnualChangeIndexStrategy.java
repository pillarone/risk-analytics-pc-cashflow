package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DeterministicAnnualChangeIndexStrategy extends AbstractParameterObject implements IIndexStrategy {

    public static final String CHANGES = "changes";

    private ConstrainedMultiDimensionalParameter changes;
    private FactorsPacket factors;
    private DateTimeUtilities.Days360 days360 = DateTimeUtilities.Days360.US;


    public IParameterObjectClassifier getType() {
        return IndexStrategyType.DETERMINISTICANNUALCHANGE;
    }

    public Map getParameters() {
        Map params = new HashMap(1);
        params.put(CHANGES, changes);
        return params;
    }

    public FactorsPacket getFactors(PeriodScope periodScope, Component origin, List<EventDependenceStream> eventStreams) {
        lazyInitFactors(origin);
        return factors;
    }

    protected void lazyInitFactors(Component origin) {
        if (factors == null) {
            factors = new FactorsPacket();
            int dateColumnIndex = changes.getColumnIndex(AnnualIndexTableConstraints.DATE);
            int changeColumnIndex = changes.getColumnIndex(AnnualIndexTableConstraints.ANNUAL_CHANGE);
            if (changes.getValues().size() > 0) {
                double factorProduct = 1d;
                DateTime formerDate = null;
                double formerChange = 0d;
                for (int row = changes.getTitleRowCount(); row < changes.getRowCount(); row++) {
                    DateTime date = (DateTime) changes.getValueAt(row, dateColumnIndex);
                    double change = InputFormatConverter.getDouble(changes.getValueAt(row, changeColumnIndex));
                    double incFactor = incrementalFactor(formerDate, formerChange, date);
                    factorProduct *= incFactor;
                    factors.add(date, factorProduct, incFactor);
                    formerDate = date;
                    formerChange = change;
                }
                DateTime nextDate = formerDate.plus(Period.years(1));
                double incFactor =  incrementalFactor(formerDate, formerChange, nextDate);
                factors.add(nextDate, factorProduct, incFactor);
                factors.origin = origin;
            }
            else {
                return; // in the trivial case the returned list has to be void
            }
        }
    }

    private double incrementalFactor(DateTime formerDate, double formerChange, DateTime date) {
        if (formerDate != null) {
                double days360Length = days360.days360(formerDate, date);
                return 1d + (formerChange * (days360Length / 360d));
        }
        return 1d;
    }

}
