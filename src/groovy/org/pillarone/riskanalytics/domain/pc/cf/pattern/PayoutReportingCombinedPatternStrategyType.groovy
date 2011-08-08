package org.pillarone.riskanalytics.domain.pc.cf.pattern

import org.pillarone.riskanalytics.core.parameterization.*

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class PayoutReportingCombinedPatternStrategyType extends AbstractParameterObjectClassifier {

    public static final PayoutReportingCombinedPatternStrategyType NONE = new PayoutReportingCombinedPatternStrategyType(
            'none', 'NONE', [:])
    public static final PayoutReportingCombinedPatternStrategyType INCREMENTAL = new PayoutReportingCombinedPatternStrategyType(
            "incremental", "INCREMENTAL", [
            incrementalPattern :  new ConstrainedMultiDimensionalParameter([[0],[1d],[1d]],
                    [PatternTableConstraints.MONTHS, INCREMENTS_PAYOUT, INCREMENTS_REPORTED],
                    ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])
    public static final PayoutReportingCombinedPatternStrategyType CUMULATIVE = new PayoutReportingCombinedPatternStrategyType(
            "cumulative", "CUMULATIVE", [
            cumulativePattern :  new ConstrainedMultiDimensionalParameter([[0],[1d],[1d]],
                    [PatternTableConstraints.MONTHS, CUMULATIVE_PAYOUT, CUMULATIVE_REPORTED],
                    ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])
    public static final PayoutReportingCombinedPatternStrategyType AGE_TO_AGE = new PayoutReportingCombinedPatternStrategyType(
            "age-to-age", "AGE_TO_AGE", [
            ageToAgePattern :  new ConstrainedMultiDimensionalParameter([[0],[1d],[1d]],
                    [PatternTableConstraints.MONTHS, LINK_RATIOS_PAYOUT, LINK_RATIOS_REPORTED],
                    ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])

    public static final all = [NONE, INCREMENTAL, CUMULATIVE, AGE_TO_AGE]

    public static final String INCREMENTS_REPORTED = "Increments Reported";
    public static final String INCREMENTS_PAYOUT = "Increments Payout";
    public static final String CUMULATIVE_REPORTED = "Cumulative Reported";
    public static final String CUMULATIVE_PAYOUT = "Cumulative Payout";
    public static final String LINK_RATIOS_REPORTED = "Link Ratios Reported";
    public static final String LINK_RATIOS_PAYOUT = "Link Ratios Payout";

    protected static Map types = [:]
    static {
        PayoutReportingCombinedPatternStrategyType.all.each {
            PayoutReportingCombinedPatternStrategyType.types[it.toString()] = it
        }
    }

    private PayoutReportingCombinedPatternStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static PayoutReportingCombinedPatternStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IPayoutReportingCombinedPatternStrategy getDefault() {
        new PayoutReportingCombinedTrivialPatternStrategy();
    }

    static IPayoutReportingCombinedPatternStrategy getStrategy(PayoutReportingCombinedPatternStrategyType type, Map parameters) {
        switch (type) {
            case PayoutReportingCombinedPatternStrategyType.NONE:
                return new PayoutReportingCombinedTrivialPatternStrategy()
            case PayoutReportingCombinedPatternStrategyType.INCREMENTAL:
                return new PayoutReportingCombinedIncrementalPatternStrategy(
                        incrementalPattern : (ConstrainedMultiDimensionalParameter) parameters['incrementalPattern'])
            case PayoutReportingCombinedPatternStrategyType.CUMULATIVE:
                return new PayoutReportingCombinedCumulativePatternStrategy(
                        cumulativePattern : (ConstrainedMultiDimensionalParameter) parameters['cumulativePattern'])
            case PayoutReportingCombinedPatternStrategyType.AGE_TO_AGE:
                return new PayoutReportingCombinedAgeToAgePatternStrategy(
                        ageToAgePattern : (ConstrainedMultiDimensionalParameter) parameters['ageToAgePattern'])
        }
    }
}
