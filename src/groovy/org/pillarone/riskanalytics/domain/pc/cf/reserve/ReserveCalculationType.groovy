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
            [ultimateAtBaseDate: 0d, occurrenceDate: new DateTime(2010, 1, 1, 0, 0, 0, 0)])

    public static final ReserveCalculationType REPORTEDBASED = new ReserveCalculationType("based on reported information", "REPORTEDBASED",
            [reportedAtBaseDate: 0d, baseDate: new DateTime(2010, 1, 1, 0, 0, 0, 0), occurrenceDate: new DateTime(2010, 1, 1, 0, 0, 0, 0),
                    interpolationMode: InterpolationMode.NONE])

    public static final ReserveCalculationType RESERVEBASED = new ReserveCalculationType("based on reserve information", "RESERVEBASED",
            [reserveAtBaseDate: 0d, baseDate: new DateTime(2010, 1, 1, 0, 0, 0, 0), occurrenceDate: new DateTime(2010, 1, 1, 0, 0, 0, 0),
                    interpolationMode: InterpolationMode.NONE])

    public static final all = [ULTIMATE, REPORTEDBASED, RESERVEBASED]

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
        return new FixedUltimateReserveCalculationStrategy(ultimateAtBaseDate: 0d, occurrenceDate: new DateTime(2010, 1, 1, 0, 0, 0, 0))
    }

    static IReserveCalculationStrategy getStrategy(ReserveCalculationType type, Map parameters) {
        IReserveCalculationStrategy strategy;
        switch (type) {
            case ReserveCalculationType.ULTIMATE:
                strategy = new FixedUltimateReserveCalculationStrategy(ultimateAtBaseDate: (double) parameters["ultimateAtBaseDate"],
                        occurrenceDate: (DateTime) parameters["occurrenceDate"])
                break;
            case ReserveCalculationType.REPORTEDBASED:
                strategy = new ReportedBasedReserveCalculationStrategy(reportedAtBaseDate: (double) parameters["reportedAtBaseDate"],
                        baseDate: (DateTime) parameters["baseDate"], occurrenceDate: (DateTime) parameters["occurrenceDate"],
                        interpolationMode: (InterpolationMode) parameters["interpolationMode"])
                break;
            case ReserveCalculationType.RESERVEBASED:
                strategy = new ReserveBasedReserveCalculationStrategy(reserveAtBaseDate: (double) parameters["reserveAtBaseDate"],
                        baseDate: (DateTime) parameters["baseDate"], occurrenceDate: (DateTime) parameters["occurrenceDate"],
                        interpolationMode: (InterpolationMode) parameters["interpolationMode"])
                break;
        }
        return strategy;
    }
}
