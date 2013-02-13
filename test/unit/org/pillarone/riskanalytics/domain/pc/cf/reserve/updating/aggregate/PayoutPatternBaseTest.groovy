package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.pillarone.riskanalytics.domain.pc.cf.global.SimulationConstants
import org.joda.time.PeriodType
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import com.google.common.collect.Maps
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities

/**
*   author simon.parten @ art-allianz . com
 */
class PayoutPatternBaseTest extends GroovyTestCase {

    void testArtisan1Proxy() {
        DateTime startDate = SimulationConstants.start2010
        List<Period> origPatternPeriods = [
                new Period(0),
                new Period(startDate, SimulationConstants.start2011),
                new Period(startDate, SimulationConstants.start2012),
                new Period(startDate, SimulationConstants.start2013),
                new Period(startDate, SimulationConstants.start2014),
                new Period(startDate, SimulationConstants.start2015),
        ]
        List<Double> origCumPattern = [ 0d, 0.2d, 0.4d, 0.6d, 0.8d, 1.0d ]
        PatternPacket packet = new PatternPacket(null, origCumPattern, origPatternPeriods)
        ClaimRoot claimRoot = new ClaimRoot(10000, ClaimType.AGGREGATED, startDate, startDate.plusMonths(6))
        TreeMap<DateTime, Double> claimPaidUpdate = Maps.newTreeMap()
        claimPaidUpdate.put(SimulationConstants.start2011, 1000d)
        claimPaidUpdate.put(SimulationConstants.start2012, 2000d)
        DateTime updateDate = SimulationConstants.start2013



        PatternPacket afterPacket = PayoutPatternBase.ARTISAN_1_PROXY.patternAccordingToPayoutBaseWithUpdates(
                packet, claimRoot, claimPaidUpdate, updateDate, DateTimeUtilities.Days360.US, true,
                startDate, SimulationConstants.start2011, SimulationConstants.start2013
        )
//        If it hasn't thrown an exception we have a good chance.
        assert afterPacket.cumulativeValues[-1] == 1
        assertEquals("", afterPacket.cumulativeValues[3], 0.733333, 0.0001)
    }
}
