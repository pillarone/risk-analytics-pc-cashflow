package org.pillarone.riskanalytics.domain.pc.cf.pattern.runOff;

import groovy.util.GroovyTestCase;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * author simon.parten @ art-allianz . com
 */
public class PatternRunOffTests extends GroovyTestCase {

    public static final double EPSILON = 1E10 - 4;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testPatternRescale(){

        DateTime startDate = new DateTime(2010, 1, 1, 1, 0, 0, 0);
        DateTime interpolationDate1 = new DateTime(2009, 1, 1, 1, 0, 0, 0);
        DateTime interpolationDate2 = new DateTime(2010, 9, 1, 1, 0, 0, 0);
        DateTime interpolationDate3 = new DateTime(2013, 1, 1, 1, 0, 0, 0);
        List<Number> months = Arrays.asList((Number) 0, 6, 12, 24 );
        List<Number> patternValues = Arrays.asList((Number) 0d , 0.5d, 0.8d, 1d );

        DateTime patternEntry1 = startDate.plusMonths(6);
        DateTime patternEntry2 = startDate.plusMonths(12);
        DateTime patternEntry3 = startDate.plusMonths(24);

//       Interpolating before pattern start sould return same pattern.
        TreeMap<DateTime, Double> pattern1 = RunOffPatternUtils.rescaleRunOffPattern(startDate, interpolationDate1, months.size(), months, patternValues, true);
        assertEquals("Pattern " , 0d , pattern1.get(startDate), EPSILON) ;
        assertEquals("Pattern " , 0.5d , pattern1.get(patternEntry1), EPSILON) ;
        assertEquals("Pattern " , 0.8d , pattern1.get(patternEntry2), EPSILON) ;
        assertEquals("Pattern " , 1d , pattern1.get(patternEntry3), EPSILON) ;

//        Interpolating after pattern end should return 1 on interpolation date.
        TreeMap<DateTime, Double> pattern3 = RunOffPatternUtils.rescaleRunOffPattern(startDate, interpolationDate3, months.size(), months, patternValues, true);
        assertEquals("One Entry" ,1d, pattern3.size() );
        assertEquals("Pattern is 1 on update" ,1d, pattern3.get(interpolationDate3) );

//        no interpolation mid pattern.
        TreeMap<DateTime, Double> pattern2 = RunOffPatternUtils.rescaleRunOffPattern(startDate, interpolationDate2, months.size(), months, patternValues, false);
        assertEquals("two Entry" ,2d, pattern2.size(),EPSILON );
        assertEquals("two Entry" ,0.8d, pattern2.get(patternEntry2), EPSILON );
        assertEquals("two Entry" , 1d, pattern2.get(patternEntry3), EPSILON );

//        Finally with interpolation. At the interpolation date we expect to have used 60% of the pattern. The remaining incremental values are rescaled.
        TreeMap<DateTime, Double> pattern2Interpolated = RunOffPatternUtils.rescaleRunOffPattern(startDate, interpolationDate2, months.size(), months, patternValues, true);
        assertEquals("two Entry" ,3d, pattern2Interpolated.size(), EPSILON );
        assertEquals("two Entry" ,0.0d, pattern2Interpolated.get(interpolationDate2), EPSILON );
        assertEquals("two Entry" ,0.5d, pattern2Interpolated.get(patternEntry2), EPSILON );
        assertEquals("two Entry" , 1d, pattern2Interpolated.get(patternEntry3), EPSILON );

    }


    public void testInterpolation() {
        DateTime simulationStart = new DateTime(2012, 6, 15, 0 , 0, 0, 0);

        DateTime patternDate1 = new DateTime(2013, 6, 15, 0, 0, 0, 0);
        DateTime patternDate2 = new DateTime(2014, 6, 15, 0, 0, 0, 0);

        Double patternValue1 = 0.5d;
        Double patternValue2 = 1d;

        TreeMap<DateTime, Double> testPattern = new TreeMap<DateTime, Double>();
        testPattern.put(patternDate1, patternValue1);
        testPattern.put(patternDate2, patternValue2);


        DateTime reportDate1 = patternDate1.minusMillis(1);
        DateTime reportDate2 = patternDate2.minusMillis(1);

        assertEquals("Check report date one is pattern value 1", 0.5d, RunOffPatternUtils.dateRatioInterpolation(simulationStart, null, reportDate1, testPattern), EPSILON);
        assertEquals( "Check report date one is pattern value 1", 1d,  RunOffPatternUtils.dateRatioInterpolation(simulationStart, null, reportDate2, testPattern), EPSILON);

        DateTime midPeriod1 = new DateTime(2012, 12, 15, 0, 0, 0, 0).minusMillis(1);
        DateTime midPeriod2 = new DateTime(2013, 12, 15, 0, 0, 0, 0).minusMillis(1);

        assertEquals( "Check mid year interpolation", (180 / 360) * 0.5d , RunOffPatternUtils.dateRatioInterpolation(simulationStart, null, midPeriod1, testPattern) , EPSILON );
        assertEquals(  "Check mid year interpolation", (180 / 360) * 0.5d + 0.5d , RunOffPatternUtils.dateRatioInterpolation(simulationStart, null, midPeriod2, testPattern) , EPSILON);

        DateTime oneMonth = new DateTime(2012, 7, 15, 0, 0, 0, 0).minusMillis(1);
        assertEquals("Check one month interpolation", (30 / 360) * 0.5d, RunOffPatternUtils.dateRatioInterpolation(simulationStart, null, oneMonth, testPattern), EPSILON);

        DateTime oneQuarterIntoSecondPeriod = new DateTime ( 2013, 9 , 15, 0, 0, 0, 0 ).minusMillis(1);
        assertEquals("Check second period interpolation", 0.5d + (90d / 360d) * 0.5d, RunOffPatternUtils.dateRatioInterpolation(simulationStart, null, oneQuarterIntoSecondPeriod, testPattern), EPSILON);
    }

}
