package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.event.IEvent
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType

/**
*   author simon.parten @ art-allianz . com
 */
class TestClaimUtils {

    public static GrossClaimRoot getGrossClaim(List<Integer> patternMonths, List<Double> pattern, double ultimate, DateTime exposureStart, DateTime patternStart, DateTime occurenceDate, String packetId = null) {
        PatternPacket patternPacket = PatternPacketTests.getPattern(patternMonths, pattern, false)
        IClaimRoot claimRoot = new ClaimRoot(ultimate, ClaimType.SINGLE, exposureStart, occurenceDate, packetId)
        GrossClaimRoot grossClaimRoot = new GrossClaimRoot(claimRoot, patternPacket, patternStart, "0")
        return grossClaimRoot
    }

    public static GrossClaimRoot getGrossClaim(List<Integer> patternMonths, List<Double> pattern, double ultimate, DateTime exposureStart, DateTime patternStart, DateTime occurenceDate, IEvent event, String packetId = null) {
        PatternPacket patternPacket = PatternPacketTests.getPattern(patternMonths, pattern, false)
        IClaimRoot claimRoot = new ClaimRoot(ultimate, ClaimType.SINGLE, exposureStart, occurenceDate, packetId, event)
        GrossClaimRoot grossClaimRoot = new GrossClaimRoot(claimRoot, patternPacket, patternStart, "0")
        return grossClaimRoot
    }

    public static  HashMap<Integer, Double>  blankPremMap(){
        HashMap<Integer, Double> premiumPerPeriod =  new HashMap<Integer, Double>()
        premiumPerPeriod.put(0, 0d)
        premiumPerPeriod.put(1, 0d)
        premiumPerPeriod.put(2, 0d)
        premiumPerPeriod.put(3, 0d)
        return premiumPerPeriod
    }

}
