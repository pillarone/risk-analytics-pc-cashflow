package org.pillarone.riskanalytics.domain.pc.cf.reserve

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.joda.time.DateTime

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class ReserveCalculationType extends AbstractParameterObjectClassifier {

    public static final ReserveCalculationType ULTIMATE = new ReserveCalculationType("fixed ultimate", "ULTIMATE",
            [ultimateAtReportingDate: 0d, reportingDate: new DateTime(new DateTime().getYear(), 1, 1, 0, 0, 0, 0),
             averageInceptionDate: new DateTime(new DateTime().getYear(), 1, 1, 0, 0, 0, 0)])

    public static final ReserveCalculationType REPORTEDBASED = new ReserveCalculationType("based on reported information", "REPORTEDBASED",
            [reportedAtReportingDate: 0d, reportingDate: new DateTime(new DateTime().getYear(), 1, 1, 0, 0, 0, 0),
             averageInceptionDate: new DateTime(new DateTime().getYear(), 1, 1, 0, 0, 0, 0), interpolationMode: InterpolationMode.NONE])

    public static final ReserveCalculationType OUTSTANDINGBASED = new ReserveCalculationType("based on outstanding information", "OUTSTANDINGBASED",
            [outstandingAtReportingDate: 0d, reportingDate: new DateTime(new DateTime().getYear(), 1, 1, 0, 0, 0, 0),
             averageInceptionDate: new DateTime(new DateTime().getYear(), 1, 1, 0, 0, 0, 0), interpolationMode: InterpolationMode.NONE])

    public static final all = [ULTIMATE, REPORTEDBASED, OUTSTANDINGBASED]

    protected static Map types = [:]
    static {
        ReserveCalculationType.all.each {
            ReserveCalculationType.types[it.toString()] = it
        }
    }

    private ReserveCalculationType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ReserveCalculationType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IReserveCalculationStrategy getDefault() {
        return new FixedUltimateReserveCalculationStrategy(
                ultimateAtReportingDate: 0d,
                reportingDate: new DateTime(new DateTime().getYear(), 1, 1, 0, 0, 0, 0),
                averageInceptionDate: new DateTime(new DateTime().getYear()-1, 1, 1, 0, 0, 0, 0))
    }

    static IReserveCalculationStrategy getStrategy(ReserveCalculationType type, Map parameters) {
        IReserveCalculationStrategy strategy;
        switch (type) {
            case ReserveCalculationType.ULTIMATE:
                strategy = new FixedUltimateReserveCalculationStrategy(
                        ultimateAtReportingDate: (double) parameters[FixedUltimateReserveCalculationStrategy.ULTIMATE_AT_REPORTING_DATE],
                        reportingDate: (DateTime) parameters[ReportedBasedReserveCalculationStrategy.REPORTING_DATE],
                        averageInceptionDate: (DateTime) parameters[FixedUltimateReserveCalculationStrategy.AVERAGE_INCEPTION_DATE])
                break;
            case ReserveCalculationType.REPORTEDBASED:
                strategy = new ReportedBasedReserveCalculationStrategy(
                        reportedAtReportingDate: (double) parameters[ReportedBasedReserveCalculationStrategy.REPORTED_AT_REPORTING_DATE],
                        reportingDate: (DateTime) parameters[ReportedBasedReserveCalculationStrategy.REPORTING_DATE],
                        averageInceptionDate: (DateTime) parameters[ReportedBasedReserveCalculationStrategy.AVERAGE_INCEPTION_DATE],
                        interpolationMode: (InterpolationMode) parameters[ReportedBasedReserveCalculationStrategy.INTERPOLATION_MODE])
                break;
            case ReserveCalculationType.OUTSTANDINGBASED:
                strategy = new OutstandingBasedReserveCalculationStrategy(
                        outstandingAtReportingDate: (double) parameters[OutstandingBasedReserveCalculationStrategy.OUTSTANDING_AT_REPORTING_DATE],
                        reportingDate: (DateTime) parameters[OutstandingBasedReserveCalculationStrategy.REPORTING_DATE],
                        averageInceptionDate: (DateTime) parameters[OutstandingBasedReserveCalculationStrategy.AVERAGE_INCEPTION_DATE],
                        interpolationMode: (InterpolationMode) parameters[OutstandingBasedReserveCalculationStrategy.INTERPOLATION_MODE])
                break;
        }
        return strategy;
    }
}
