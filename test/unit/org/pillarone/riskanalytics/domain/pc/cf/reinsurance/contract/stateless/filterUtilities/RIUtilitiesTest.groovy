package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 *   author simon.parten @ art-allianz . com
 */
class RIUtilitiesTest extends GroovyTestCase {


    List<ClaimCashflowPacket> claimCashflowPackets = new ArrayList<ClaimCashflowPacket>()
    DateTime start2010 = new DateTime(2010, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)
    DateTime start2011 = new DateTime(2011, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)
    DateTime start2012 = new DateTime(2012, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)



    void setUp() {


        claimCashflowPackets.clear()
        IPeriodCounter counter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(start2010, 4)


        PatternPacket packet = PatternPacketTests.getPattern([5i, 13i], [0.5d, 1d], false)
        IClaimRoot keyClaim = new ClaimRoot(-100, ClaimType.SINGLE, start2010, start2010)
        IClaimRoot grossClaimRoot = new GrossClaimRoot(keyClaim, packet)

        PatternPacket packet2 = PatternPacketTests.getPattern([11i, 23i, 35i], [0.2d, 0.5d, 1d], false)
        IClaimRoot keyClaim2 = new ClaimRoot(-200, ClaimType.SINGLE, start2010, start2011)
        IClaimRoot grossClaimRoot2 = new GrossClaimRoot(keyClaim2, packet2)

        claimCashflowPackets.addAll(grossClaimRoot.getClaimCashflowPackets(counter))
        claimCashflowPackets.addAll(grossClaimRoot2.getClaimCashflowPackets(counter))

        claimCashflowPackets.addAll(grossClaimRoot.getClaimCashflowPackets(counter.next()))
        claimCashflowPackets.addAll(grossClaimRoot2.getClaimCashflowPackets(counter))
        claimCashflowPackets.addAll(grossClaimRoot2.getClaimCashflowPackets(counter.next()))

    }

    void testIncurredClaims() {

        Set<IClaimRoot> keyClaims = RIUtilities.incurredClaims(claimCashflowPackets, IncurredClaimBase.KEY)
        assertEquals("Should be one incurred claim", 2, keyClaims.size())
        IClaimRoot claimRoot = keyClaims.toList().get(0)
        assertTrue("key claim", claimRoot instanceof GrossClaimRoot)

        Set<IClaimRoot> baseClaims = RIUtilities.incurredClaims(claimCashflowPackets, IncurredClaimBase.BASE)
        assertTrue("base Claim", claimRoot instanceof GrossClaimRoot)

    }

    void testIncurredClaimsByDate() {

        Set<IClaimRoot> incurredClaims = RIUtilities.incurredClaims(claimCashflowPackets, IncurredClaimBase.BASE)
        Set<IClaimRoot> lossesOccuringIn2010 = RIUtilities.incurredClaimsByDate( start2010, start2011, incurredClaims, ContractCoverBase.LOSSES_OCCURING )
        assertEquals("One claim losses occuring in 2010", 1 ,  lossesOccuringIn2010.size())
        assertEquals("claim", -100 ,  lossesOccuringIn2010.toList().get(0).getUltimate())

        Set<IClaimRoot> risksAttaching2010 = RIUtilities.incurredClaimsByDate( start2010, start2011, incurredClaims, ContractCoverBase.RISKS_ATTACHING )
        assertEquals("Two risks attaching occuring in 2010", 2 ,  risksAttaching2010.size())

        Set<IClaimRoot> lossesOccuring2011 = RIUtilities.incurredClaimsByDate( start2011, start2012, incurredClaims, ContractCoverBase.LOSSES_OCCURING)
        assertEquals(" Losses occuring 2011 ", 1, lossesOccuring2011.size() )
        assertEquals(" Losses occuring 2011 sev ", -200, lossesOccuring2011.toList().get(0).getUltimate() )

        Set<IClaimRoot> risksAttaching2012 = RIUtilities.incurredClaimsByDate(start2011 , start2012, incurredClaims, ContractCoverBase.RISKS_ATTACHING)
        assertEquals("risks attaching 2011", 0, risksAttaching2012.size())
    }

    void testLatestCashflowByIncurredClaim() {
        ArrayList<ClaimCashflowPacket> claimCashflowPackets1 = RIUtilities.latestCashflowByIncurredClaim(claimCashflowPackets, IncurredClaimBase.BASE)

        assertEquals("size should be 2", 2, claimCashflowPackets1.size())

        assertEquals "check date", 1,  claimCashflowPackets1.findAll {it -> it.getDate().equals(start2010.plusMonths(13)) }.size()
        assertEquals "check date", 1,  claimCashflowPackets1.findAll {it -> it.getDate().equals(start2011.plusMonths(23)) }.size()
    }

    void testClaimsByPeriod() {
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        List<ClaimCashflowPacket> claims = RIUtilities.cashflowsClaimsByPeriod(0, periodScope.getPeriodCounter(), claimCashflowPackets, ContractCoverBase.RISKS_ATTACHING )
        PatternPacket packet2 = PatternPacketTests.getPattern([1i], [1d], false)
        IClaimRoot noCoverClaim = new ClaimRoot(200, ClaimType.SINGLE, start2011, start2011)
        IClaimRoot noCoverHere = new GrossClaimRoot(noCoverClaim, packet2)

        IPeriodCounter counter = TestPeriodCounterUtilities.getLimitedContinuousPeriodCounter(start2011, 4)
        claimCashflowPackets.addAll(noCoverHere.getClaimCashflowPackets(counter))
        assert claimCashflowPackets.size() == 8
        assertEquals "", 6,  claims.size()
    }





}