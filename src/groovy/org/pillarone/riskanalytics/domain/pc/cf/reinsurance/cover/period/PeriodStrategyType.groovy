package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.constraint.DateTimeConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.SimulationException
import org.pillarone.riskanalytics.domain.pc.cf.global.AnnualPeriodStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PeriodStrategyType extends AbstractParameterObjectClassifier {

    public static final String PERIOD = "period";

    public static final PeriodStrategyType ONEYEAR = new PeriodStrategyType("One Year as from Projection Start", "ONEYEAR", [:])
    public static final PeriodStrategyType MONTHS = new PeriodStrategyType("Months", "MONTHS",
            ['startCover': new DateTime(2012, 1, 1, 0, 0, 0, 0), 'numberOfMonths': 12])
    public static final PeriodStrategyType CUSTOM = new PeriodStrategyType(
            'Custom', 'CUSTOM', ['periods': new ConstrainedMultiDimensionalParameter(
            [[new DateTime(2012, 1, 1, 0, 0, 0, 0)], [new DateTime(2012, 12, 31, 0, 0, 0, 0)]],
            ['Start Date', 'End Date'], ConstraintsFactory.getConstraints(DateTimeConstraints.IDENTIFIER))])
    public static final PeriodStrategyType ANNUAL = new PeriodStrategyType("Annual", "ANNUAL",
            ['startCover': new DateTime(2010, 1, 1, 0, 0, 0, 0), 'numberOfYears': 3])
    public static final PeriodStrategyType RETROACTIVE = new PeriodStrategyType("Retroactive", "RETROACTIVE",
            ["coveredOccurencePeriodFrom": new DateTime(), "coveredOccurencePeriodTo": new DateTime(),
                    "coveredDevelopmentPeriodStartDate": new DateTime()])

    public static final all = [ONEYEAR, MONTHS, CUSTOM, ANNUAL, RETROACTIVE]

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
        return PeriodStrategyType.getStrategy(PeriodStrategyType.ONEYEAR, [:])
    }

    public static IPeriodStrategy getRetroActiveDefault() {
        DateTime baseDate = new DateTime().withDayOfYear(1)
        return PeriodStrategyType.getStrategy(PeriodStrategyType.RETROACTIVE,
                ['coveredOccurencePeriodFrom': baseDate,
                 'coveredOccurencePeriodTo': baseDate.plusYears(1),
                 'coveredDevelopmentPeriodStartDate': baseDate.plusYears(1)])
    }

    public static IPeriodStrategy getStrategy(PeriodStrategyType type, Map parameters) {
        IPeriodStrategy strategy;
        switch (type) {
            case PeriodStrategyType.ONEYEAR:
                strategy = new OneYearAsFromProjectionStartStrategy()
                break
            case PeriodStrategyType.MONTHS:
                strategy = new MonthPeriodStrategy(startCover: (DateTime) parameters['startCover'],
                        numberOfMonths: (Integer) parameters['numberOfMonths'])
                break
            case PeriodStrategyType.CUSTOM:
                strategy = new CustomPeriodStrategy(periods: (ConstrainedMultiDimensionalParameter) parameters['periods'])
                break
            case PeriodStrategyType.RETROACTIVE:
                strategy = new RetroactivePeriodStrategy(
                        coveredOccurencePeriodFrom: (DateTime) parameters['coveredOccurencePeriodFrom'],
                        coveredOccurencePeriodTo: (DateTime) parameters['coveredOccurencePeriodTo'],
                        coveredDevelopmentPeriodStartDate: (DateTime) parameters['coveredDevelopmentPeriodStartDate'])
                break
            case PeriodStrategyType.ANNUAL:
                strategy = new AnnualPeriodStrategy(startCover: (DateTime) parameters['startCover'],
                        numberOfYears: (Integer) parameters['numberOfYears'])
                break
            default:
                throw new IllegalArgumentException("Unknown strategy type : " + type.toString())
        }
        return strategy;
    }
}
