package org.pillarone.riskanalytics.domain.pc.cf.pattern;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PeriodsNotIncreasingException extends RuntimeException {

    public static final String PERIODS_NOT_INCREASING = "Periods need to be increasing!";

    public PeriodsNotIncreasingException() {
        super(PERIODS_NOT_INCREASING);
    }
}
