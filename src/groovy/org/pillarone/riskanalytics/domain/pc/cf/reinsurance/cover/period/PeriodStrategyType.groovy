package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.constraint.DateTimeConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PeriodStrategyType extends AbstractParameterObjectClassifier {

    public static final String PERIOD = "period";

    public static final PeriodStrategyType MONTHS = new PeriodStrategyType("Months", "MONTHS",
            ['startCover': new DateTime(2010,1,1,0,0,0,0), 'numberOfMonths': 12])
    public static final PeriodStrategyType CUSTOM = new PeriodStrategyType(
            'Custom', 'CUSTOM', ['periods': new ConstrainedMultiDimensionalParameter(
                [[new DateTime(2010,1,1,0,0,0,0)], [new DateTime(2010,12,31,0,0,0,0)]],
                ['Start Date','End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER))])

    public static final all = [MONTHS, CUSTOM]

    protected static Map types = [:]
    static {
        PeriodStrategyType.all.each {
            PeriodStrategyType.types[it.toString()] = it
        }
    }

    private PeriodStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static PeriodStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static IPeriodStrategy getDefault() {
        return PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, ['startCover': new DateTime(2011,1,1,0,0,0,0), 'numberOfMonths': 12])
    }

    public static IPeriodStrategy getStrategy(PeriodStrategyType type, Map parameters) {
        IPeriodStrategy strategy;
        switch (type) {
            case PeriodStrategyType.MONTHS:
                strategy = new MonthPeriodStrategy(startCover: (DateTime) parameters['startCover'],
                                                   numberOfMonths: (Integer) parameters['numberOfMonths'])
                break
            case PeriodStrategyType.CUSTOM:
                strategy = new CustomPeriodStrategy(periods : (ConstrainedMultiDimensionalParameter) parameters['periods'])
                break
        }
        return strategy;
    }
}
