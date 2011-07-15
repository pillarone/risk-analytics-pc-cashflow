package org.pillarone.riskanalytics.domain.pc.cf.segment

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.TestPeriodCounterUtilities
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.domain.utils.constraint.ReservePortion
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker
import org.pillarone.riskanalytics.domain.pc.cf.reserve.ReservesGenerator
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SegmentTests extends GroovyTestCase {

    DateTime projectionStart = new DateTime(2010, 1, 1, 0, 0, 0, 0)
    IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(projectionStart, 5)
    PeriodScope periodScope = iterationScope.periodScope
    PeriodStore periodStore

    Segment segment = new Segment()

    ClaimsGenerator motorClaimsGenerator = new ClaimsGenerator(name: 'motor')
    ClaimsGenerator marineClaimsGenerator = new ClaimsGenerator(name: 'marine')
    // is ignored as it is not part of included claims generators
    ClaimsGenerator paClaimsGenerator = new ClaimsGenerator(name: 'personal accident')

    ClaimCashflowPacket marine1000 = getClaimCashflowPacket(null, -1000, -200, projectionStart, projectionStart,
            true, periodScope, marineClaimsGenerator)
    ClaimCashflowPacket motor500 = getClaimCashflowPacket(null, -500, -200, projectionStart, projectionStart,
            true, periodScope, motorClaimsGenerator)
    ClaimCashflowPacket pa400 = getClaimCashflowPacket(null, -400, -200, projectionStart, projectionStart,
            true, periodScope, paClaimsGenerator)

    ReservesGenerator motorReservesGenerator = new ReservesGenerator(name: 'motor reserve')
    ReservesGenerator marineReservesGenerator = new ReservesGenerator(name: 'marineReserve')
    ReservesGenerator paReservesGenerator = new ReservesGenerator(name: 'pa reserve')

    ClaimCashflowPacket marineReserve2000 = getClaimCashflowPacket(null, -2000, -200, projectionStart, projectionStart,
            true, periodScope, marineReservesGenerator)
    ClaimCashflowPacket motorReserve600 = getClaimCashflowPacket(null, -600, -200, projectionStart, projectionStart,
            true, periodScope, motorReservesGenerator)
    ClaimCashflowPacket paReserve800 = getClaimCashflowPacket(null, -800, -200, projectionStart, projectionStart,
            true, periodScope, paReservesGenerator)
   // todo(jwa): test cases for reserved values over several periods (seem to be incorrect, see PMO-1730)

    void setUp() {

        segment.iterationScope = iterationScope
        segment.periodScope = periodScope
        segment.periodStore = new PeriodStore(segment.periodScope)
        segment.parmClaimsPortions = new ConstrainedMultiDimensionalParameter(
                [['marine', 'motor'], [1d, 0.5d]], [Segment.PERIL, Segment.PORTION],
                ConstraintsFactory.getConstraints(PerilPortion.IDENTIFIER))
        segment.parmUnderwritingPortions = new ConstrainedMultiDimensionalParameter(
                [['marine', 'motor'], [1d, 0.6d]], [Segment.UNDERWRITING, Segment.PORTION],
                ConstraintsFactory.getConstraints(UnderwritingPortion.IDENTIFIER))
        segment.parmReservesPortions = new ConstrainedMultiDimensionalParameter(
                [['marine reserve', 'motor reserve'], [1d, 0.5d]], [Segment.RESERVE, Segment.PORTION],
                ConstraintsFactory.getConstraints(ReservePortion.IDENTIFIER))
        segment.parmDiscounting = new ComboBoxTableMultiDimensionalParameter(["index"], ["Discount Index"], IDiscountMarker)

    }

    /** apply weight for motor claim, ignore personal accident claim, net calculation      */
    void testUsage() {

        RiskBands marineRisk = new RiskBands(name: 'marine')
        UnderwritingInfoPacket marineUwInfo600 = new UnderwritingInfoPacket(
                premiumWritten: 600, premiumPaid: 500, numberOfPolicies: 10, sumInsured: 10000, maxSumInsured: 15000, riskBand: marineRisk
        )
        RiskBands motorRisk = new RiskBands(name: 'motor')
        UnderwritingInfoPacket motorUwInfo450 = new UnderwritingInfoPacket(
                premiumWritten: 450, premiumPaid: 400, numberOfPolicies: 5, sumInsured: 1000, maxSumInsured: 2000, riskBand: motorRisk
        )
        RiskBands paRisk = new RiskBands(name: 'pa')
        UnderwritingInfoPacket paUwInfo1000 = new UnderwritingInfoPacket(
                premiumWritten: 1000, premiumPaid: 800, numberOfPolicies: 20, sumInsured: 1500, maxSumInsured: 5000, riskBand: paRisk
        )

        segment.inClaims << marine1000 << motor500 << pa400
        segment.inUnderwritingInfo << marineUwInfo600 << motorUwInfo450 << paUwInfo1000
        segment.inReserves << marineReserve2000 << motorReserve600 << paReserve800

        segment.doCalculation(Segment.PHASE_GROSS)

        assertEquals "#gross claims", 4, segment.outClaimsGross.size()
        ClaimCashflowPacket segmentMarine1000 = segment.outClaimsGross[0]
        assertEquals "marine peril", marineClaimsGenerator, segmentMarine1000.peril()
        assertEquals "marine segment", segment, segmentMarine1000.segment()
        assertEquals "marine ultimate", -1000, segmentMarine1000.ultimate()
        assertEquals "marine paid incremental", -200, segmentMarine1000.paidIncremental
        ClaimCashflowPacket segmentMotor250 = segment.outClaimsGross[1]
        assertEquals "motor peril", motorClaimsGenerator, segmentMotor250.peril()
        assertEquals "motor segment", segment, segmentMotor250.segment()
        assertEquals "motor ultimate", -250, segmentMotor250.ultimate()
        assertEquals "motor paid incremental", -100, segmentMotor250.paidIncremental
        ClaimCashflowPacket segmentMarineReserve2000 = segment.outClaimsGross[2]
        assertEquals "marine reserve", marineReservesGenerator, segment.outClaimsGross[2].reserve()
        assertEquals "marine reserve segment", segment, segment.outClaimsGross[2].segment()
        assertEquals "marine reserve ultimate", -2000, segment.outClaimsGross[2].ultimate()
        assertEquals "marine reserve paid incremental", -200, segment.outClaimsGross[2].paidIncremental
        ClaimCashflowPacket segmentMotorReserve300 = segment.outClaimsGross[3]
        assertEquals "motor reserve ", motorReservesGenerator, segment.outClaimsGross[3].reserve()
        assertEquals "motor reserve segment", segment, segment.outClaimsGross[3].segment()
        assertEquals "motor reserve ultimate", -300, segment.outClaimsGross[3].ultimate()
        assertEquals "motor reserve paid incremental", -100, segment.outClaimsGross[3].paidIncremental

        assertEquals "#gross underwriting info", 2, segment.outUnderwritingInfoGross.size()
        UnderwritingInfoPacket segmentUwInfoMarine = segment.outUnderwritingInfoGross[0]
        assertEquals "marine riskBand", marineRisk, segmentUwInfoMarine.riskBand()
        assertEquals "marine segment", segment, segmentUwInfoMarine.segment()
        assertEquals "marine premium written", 600, segmentUwInfoMarine.premiumWritten
        assertEquals "marine premium paid", 500, segmentUwInfoMarine.premiumPaid
        UnderwritingInfoPacket segmentUwInfoMotor = segment.outUnderwritingInfoGross[1]
        assertEquals "motor risk band", motorRisk, segmentUwInfoMotor.riskBand()
        assertEquals "motor risk segment", segment, segmentUwInfoMotor.segment()
        assertEquals "motor premium written", 270, segmentUwInfoMotor.premiumWritten
        assertEquals "motor premium paid", 240, segmentUwInfoMotor.premiumPaid


        double quotaShare = 0.2
        ClaimCashflowPacket marine1000Ceded = getCededClaim(segmentMarine1000, quotaShare)
        ClaimCashflowPacket motor500Ceded = getCededClaim(segmentMotor250, quotaShare)
        pa400.setMarker(new Segment(name: 'pa'))
        ClaimCashflowPacket pa400Ceded = getCededClaim(pa400, quotaShare)
        UnderwritingInfoPacket marineUwInfo600Ceded = CededUnderwritingInfoPacket.deriveCededPacketForNonPropContract(
                segmentUwInfoMarine, null, -120, -120, 0)
        UnderwritingInfoPacket motorUwInfo450Ceded = CededUnderwritingInfoPacket.deriveCededPacketForNonPropContract(
                segmentUwInfoMotor, null, -54, -54, 0)
        UnderwritingInfoPacket paUwInfo1000Ceded = CededUnderwritingInfoPacket.deriveCededPacketForNonPropContract(
                paUwInfo1000, null, -120, -120, 0)
        paUwInfo1000Ceded.segment = pa400.segment()

        quotaShare = 0.1
        ClaimCashflowPacket marineReserve2000Ceded = getCededClaim(segmentMarineReserve2000, quotaShare)
        ClaimCashflowPacket motorReserve600Ceded = getCededClaim(segmentMotorReserve300, quotaShare)
        paReserve800.setMarker(new Segment(name: 'pa'))
        ClaimCashflowPacket paReserve800Ceded = getCededClaim(paReserve800, quotaShare)

        segment.inClaimsCeded << marine1000Ceded << motor500Ceded << pa400Ceded << marineReserve2000Ceded << motorReserve600Ceded << paReserve800Ceded
        segment.inUnderwritingInfoCeded << marineUwInfo600Ceded << motorUwInfo450Ceded << paUwInfo1000Ceded
        List<ClaimCashflowPacket> netClaims = new TestProbe(segment, "outClaimsNet").result
        List<ClaimCashflowPacket> netUnderwritingInfo = new TestProbe(segment, "outUnderwritingInfoNet").result
        segment.doCalculation(Segment.PHASE_NET)

        assertEquals "#ceded claims", 4, segment.outClaimsCeded.size()
        assertEquals "ceded marine peril", marineClaimsGenerator, segment.outClaimsCeded[0].peril()
        assertEquals "ceded marine segment", segment, segment.outClaimsCeded[0].segment()
        assertEquals "ceded marine ultimate", 200, segment.outClaimsCeded[0].ultimate()
        assertEquals "ceded marine paid incremental", 40, segment.outClaimsCeded[0].paidIncremental
        assertEquals "ceded motor peril", motorClaimsGenerator, segment.outClaimsCeded[1].peril()
        assertEquals "ceded motor segment", segment, segment.outClaimsCeded[1].segment()
        assertEquals "ceded motor ultimate", 50, segment.outClaimsCeded[1].ultimate()
        assertEquals "ceded motor paid incremental", 20, segment.outClaimsCeded[1].paidIncremental
        assertEquals "ceded marine reserve", marineReservesGenerator, segment.outClaimsCeded[2].reserve()
        assertEquals "ceded marine reserve segment", segment, segment.outClaimsCeded[2].segment()
        assertEquals "ceded marine reserve ultimate", 200, segment.outClaimsCeded[2].ultimate()
        assertEquals "ceded marine reserve paid incremental", 20, segment.outClaimsCeded[2].paidIncremental
        assertEquals "ceded motor reserve", motorReservesGenerator, segment.outClaimsCeded[3].reserve()
        assertEquals "ceded motor reserve segment", segment, segment.outClaimsCeded[3].segment()
        assertEquals "ceded motor reserve ultimate", 30, segment.outClaimsCeded[3].ultimate()
        assertEquals "ceded motor paid reserve incremental", 10, segment.outClaimsCeded[3].paidIncremental

        assertEquals "#ceded underwriting info", 2, segment.outUnderwritingInfoCeded.size()
        assertEquals "ceded marine riskBand", marineRisk, segment.outUnderwritingInfoCeded[0].riskBand()
        assertEquals "ceded marine segment", segment, segment.outUnderwritingInfoCeded[0].segment()
        assertEquals "ceded marine premium written", -120, segment.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals "ceded marine premium paid", -120, segment.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals "ceded motor risk band", motorRisk, segment.outUnderwritingInfoCeded[1].riskBand()
        assertEquals "ceded motor risk segment", segment, segment.outUnderwritingInfoCeded[1].segment()
        assertEquals "ceded motor premium written", -54, segment.outUnderwritingInfoCeded[1].premiumWritten
        assertEquals "ceded motor premium paid", -54, segment.outUnderwritingInfoCeded[1].premiumPaid

        assertEquals "#net claims", 4, segment.outClaimsNet.size()
        assertEquals "net marine peril", marineClaimsGenerator, segment.outClaimsNet[0].peril()
        assertEquals "net marine segment", segment, segment.outClaimsNet[0].segment()
        assertEquals "net marine ultimate", -800, segment.outClaimsNet[0].ultimate()
        assertEquals "net marine paid incremental", -160, segment.outClaimsNet[0].paidIncremental
        assertEquals "net motor peril", motorClaimsGenerator, segment.outClaimsNet[1].peril()
        assertEquals "net motor segment", segment, segment.outClaimsNet[1].segment()
        assertEquals "net motor ultimate", -200, segment.outClaimsNet[1].ultimate()
        assertEquals "net motor paid incremental", -80, segment.outClaimsNet[1].paidIncremental
        assertEquals "net marine reserve", marineReservesGenerator, segment.outClaimsNet[2].reserve()
        assertEquals "net marine reserve segment", segment, segment.outClaimsNet[2].segment()
        assertEquals "net marine reserve ultimate", -1800, segment.outClaimsNet[2].ultimate()
        assertEquals "net marine reserve paid incremental", -180, segment.outClaimsNet[2].paidIncremental
        assertEquals "net motor reserve", motorReservesGenerator, segment.outClaimsNet[3].reserve()
        assertEquals "net motor reserve segment", segment, segment.outClaimsNet[3].segment()
        assertEquals "net motor reserve ultimate", -270, segment.outClaimsNet[3].ultimate()
        assertEquals "net motor paid reserve incremental", -90, segment.outClaimsNet[3].paidIncremental

        assertEquals "#net underwriting info", 2, segment.outUnderwritingInfoNet.size()
        assertEquals "net marine riskBand", marineRisk, segment.outUnderwritingInfoNet[0].riskBand()
        assertEquals "net marine segment", segment, segment.outUnderwritingInfoNet[0].segment()
        assertEquals "net marine premium written", 480, segment.outUnderwritingInfoNet[0].premiumWritten
        assertEquals "net marine premium paid", 380, segment.outUnderwritingInfoNet[0].premiumPaid
        assertEquals "net motor risk band", motorRisk, segment.outUnderwritingInfoNet[1].riskBand()
        assertEquals "net motor risk segment", segment, segment.outUnderwritingInfoNet[1].segment()
        assertEquals "net motor premium written", 216, segment.outUnderwritingInfoNet[1].premiumWritten
        assertEquals "net motor premium paid", 186, segment.outUnderwritingInfoNet[1].premiumPaid
    }

    private ClaimCashflowPacket getCededClaim(ClaimCashflowPacket grossClaim, double quotaShare) {
        ClaimStorage storage = new ClaimStorage(grossClaim)
        storage.lazyInitCededClaimRoot(quotaShare)
        return ClaimUtils.getCededClaim(grossClaim, storage, -quotaShare, -quotaShare, -quotaShare, false);
    }

    static ClaimCashflowPacket getClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double incrementalPaid,
                                                      DateTime occurrenceDate, DateTime updateDate,
                                                      boolean hasUltimate = false, PeriodScope periodScope, IComponentMarker marker) {
        if (baseClaim == null) {
            baseClaim = new GrossClaimRoot(ultimate, ClaimType.ATTRITIONAL, null, occurrenceDate, null, null)
        }
        ClaimCashflowPacket claim = new ClaimCashflowPacket(baseClaim, ultimate, incrementalPaid, 0d, 0d, null,
                updateDate, periodScope.getPeriodCounter())
        claim.setMarker(marker)
        claim
    }
}
