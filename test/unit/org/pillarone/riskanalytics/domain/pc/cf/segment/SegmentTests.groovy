package org.pillarone.riskanalytics.domain.pc.cf.segment

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
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
import org.pillarone.riskanalytics.domain.pc.cf.discounting.Discounting
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.indexing.DeterministicIndexTableConstraints
import org.pillarone.riskanalytics.core.wiring.WiringUtils
import org.pillarone.riskanalytics.core.wiring.WireCategory
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountedValuesPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SegmentTests extends GroovyTestCase {

    double EPSILON = 1E-7

    DateTime projectionStart = new DateTime(2010, 1, 1, 0, 0, 0, 0)
    DateTime updateDate1 = new DateTime(2010, 6, 1, 0, 0, 0, 0)
    DateTime updateDate2 = new DateTime(2010, 10, 1, 0, 0, 0, 0)
    DateTime updateDate3 = new DateTime(2011, 7, 1, 0, 0, 0, 0)
    DateTime updateDate4 = new DateTime(2011, 8, 1, 0, 0, 0, 0)

    IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(projectionStart, 2)
    PeriodScope periodScope = iterationScope.periodScope
    PeriodStore periodStore

    Segments segments = new Segments()
    Segment segment = new Segment()
    Discounting discounting

    ClaimsGenerator motorClaimsGenerator = new ClaimsGenerator(name: 'motor')
    ClaimsGenerator marineClaimsGenerator = new ClaimsGenerator(name: 'marine')
    // is ignored as it is not part of included claims generators
    ClaimsGenerator paClaimsGenerator = new ClaimsGenerator(name: 'personal accident')

    ClaimCashflowPacket marine1000 = getClaimCashflowPacket(null, -1000, -200, updateDate1, updateDate1,
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

    void setUp() {
        ConstraintsFactory.registerConstraint(new DeterministicIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new PerilPortion())
        ConstraintsFactory.registerConstraint(new ReservePortion())
        ConstraintsFactory.registerConstraint(new UnderwritingPortion())

        discounting = new Discounting(name: 'discount index', parmIndex: IndexStrategyType.getStrategy(IndexStrategyType.DETERMINISTICINDEXSERIES,
                [indices: new ConstrainedMultiDimensionalParameter(
                        [[new DateTime(2010, 1, 1, 0, 0, 0, 0), new DateTime(2011, 1, 1, 0, 0, 0, 0), new DateTime(2012, 3, 1, 0, 0, 0, 0)],
                                [1.02, 1.04, 1.062]],
                        DeterministicIndexTableConstraints.COLUMN_TITLES,
                        ConstraintsFactory.getConstraints(DeterministicIndexTableConstraints.IDENTIFIER))]))

        segment.iterationScope = iterationScope
        iterationScope.numberOfPeriods = 2
        segment.periodStore = new PeriodStore(segment.iterationScope.periodScope)
        segment.parmClaimsPortions = new ConstrainedMultiDimensionalParameter(
                [['marine', 'motor'], [1d, 0.5d]], [Segment.PERIL, Segment.PORTION],
                ConstraintsFactory.getConstraints(PerilPortion.IDENTIFIER))
        segment.parmUnderwritingPortions = new ConstrainedMultiDimensionalParameter(
                [['marine', 'motor'], [1d, 0.6d]], [Segment.UNDERWRITING, Segment.PORTION],
                ConstraintsFactory.getConstraints(UnderwritingPortion.IDENTIFIER))
        segment.parmReservesPortions = new ConstrainedMultiDimensionalParameter(
                [['marine reserve', 'motor reserve'], [1d, 0.5d]], [Segment.RESERVE, Segment.PORTION],
                ConstraintsFactory.getConstraints(ReservePortion.IDENTIFIER))
        ComboBoxTableMultiDimensionalParameter discountComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["discount index"], ["Discount Index"], IDiscountMarker)
        discountComboBox.comboBoxValues.put('discount index', discounting)
        segment.setParmDiscounting(discountComboBox)

        WiringUtils.use(WireCategory) {
            segment.inFactors = discounting.outFactors
        }

        List<DiscountedValuesPacket> discountedValues = new TestProbe(segment, "outDiscountedValues").result

    }

    /** apply weight for motor claim, ignore personal accident claim, net calculation        */
    void testUsage() {

        RiskBands marineRisk = new RiskBands(name: 'marine')
        UnderwritingInfoPacket marineUwInfo600 = new UnderwritingInfoPacket(
                premiumWritten: 600, premiumPaid: 500, numberOfPolicies: 10, riskBand: marineRisk
        )
        marineUwInfo600.exposure = new ExposureInfo(new DateTime(2011, 1, 1, 0,0,0,0), 0, 10000, 15000, ExposureBase.ABSOLUTE)

        RiskBands motorRisk = new RiskBands(name: 'motor')
        UnderwritingInfoPacket motorUwInfo450 = new UnderwritingInfoPacket(
                premiumWritten: 450, premiumPaid: 400, numberOfPolicies: 5, riskBand: motorRisk
        )
        motorUwInfo450.exposure = new ExposureInfo(new DateTime(2011, 1, 1, 0,0,0,0), 0, 1000, 2000, ExposureBase.ABSOLUTE)
        RiskBands paRisk = new RiskBands(name: 'pa')
        UnderwritingInfoPacket paUwInfo1000 = new UnderwritingInfoPacket(
                premiumWritten: 1000, premiumPaid: 800, numberOfPolicies: 20, riskBand: paRisk
        )
        paUwInfo1000.exposure = new ExposureInfo(new DateTime(2011, 1, 1, 0,0,0,0), 0, 1500, 5000, ExposureBase.ABSOLUTE)

        segment.inClaims << marine1000 << motor500 << pa400
        segment.inUnderwritingInfo << marineUwInfo600 << motorUwInfo450 << paUwInfo1000
        segment.inReserves << marineReserve2000 << motorReserve600 << paReserve800

        segment.doCalculation(Segment.PHASE_GROSS)

        assertEquals "#gross claims", 4, segment.outClaimsGross.size()
        ClaimCashflowPacket segmentMarine1000 = segment.outClaimsGross[0]
        assertEquals "marine peril", marineClaimsGenerator, segmentMarine1000.peril()
        assertEquals "marine segment", segment, segmentMarine1000.segment()
        assertEquals "marine ultimate", -1000, segmentMarine1000.ultimate()
        assertEquals "marine paid incremental", -200, segmentMarine1000.paidIncrementalIndexed
        ClaimCashflowPacket segmentMotor250 = segment.outClaimsGross[1]
        assertEquals "motor peril", motorClaimsGenerator, segmentMotor250.peril()
        assertEquals "motor segment", segment, segmentMotor250.segment()
        assertEquals "motor ultimate", -250, segmentMotor250.ultimate()
        assertEquals "motor paid incremental", -100, segmentMotor250.paidIncrementalIndexed
        ClaimCashflowPacket segmentMarineReserve2000 = segment.outClaimsGross[2]
        assertEquals "marine reserve", marineReservesGenerator, segment.outClaimsGross[2].reserve()
        assertEquals "marine reserve segment", segment, segment.outClaimsGross[2].segment()
        assertEquals "marine reserve ultimate", -2000, segment.outClaimsGross[2].ultimate()
        assertEquals "marine reserve paid incremental", -200, segment.outClaimsGross[2].paidIncrementalIndexed
        ClaimCashflowPacket segmentMotorReserve300 = segment.outClaimsGross[3]
        assertEquals "motor reserve ", motorReservesGenerator, segment.outClaimsGross[3].reserve()
        assertEquals "motor reserve segment", segment, segment.outClaimsGross[3].segment()
        assertEquals "motor reserve ultimate", -300, segment.outClaimsGross[3].ultimate()
        assertEquals "motor reserve paid incremental", -100, segment.outClaimsGross[3].paidIncrementalIndexed

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
        assertEquals "ceded marine paid incremental", 40, segment.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals "ceded motor peril", motorClaimsGenerator, segment.outClaimsCeded[1].peril()
        assertEquals "ceded motor segment", segment, segment.outClaimsCeded[1].segment()
        assertEquals "ceded motor ultimate", 50, segment.outClaimsCeded[1].ultimate()
        assertEquals "ceded motor paid incremental", 20, segment.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals "ceded marine reserve", marineReservesGenerator, segment.outClaimsCeded[2].reserve()
        assertEquals "ceded marine reserve segment", segment, segment.outClaimsCeded[2].segment()
        assertEquals "ceded marine reserve ultimate", 200, segment.outClaimsCeded[2].ultimate()
        assertEquals "ceded marine reserve paid incremental", 20, segment.outClaimsCeded[2].paidIncrementalIndexed
        assertEquals "ceded motor reserve", motorReservesGenerator, segment.outClaimsCeded[3].reserve()
        assertEquals "ceded motor reserve segment", segment, segment.outClaimsCeded[3].segment()
        assertEquals "ceded motor reserve ultimate", 30, segment.outClaimsCeded[3].ultimate()
        assertEquals "ceded motor paid reserve incremental", 10, segment.outClaimsCeded[3].paidIncrementalIndexed

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
        assertEquals "net marine ultimate", -3070, segment.outClaimsNet*.ultimate().sum()
        assertEquals "net marine paid incremental", -510, segment.outClaimsNet*.paidIncrementalIndexed.sum()

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

    void testDiscountedValues() {

        ClaimCashflowPacket marine1000Two = getClaimCashflowPacket(marine1000.getBaseClaim(), 0d, -500d, updateDate1,
                updateDate2, false, periodScope, marineClaimsGenerator)
        ClaimCashflowPacket marine1000Three = getClaimCashflowPacket(marine1000.getBaseClaim(), 0d, -300d, updateDate1,
                updateDate3, false, periodScope, marineClaimsGenerator)
        ClaimCashflowPacket motor500Two = getClaimCashflowPacket(motor500.getBaseClaim(), 0d, -300, projectionStart,
                updateDate4, false, periodScope, motorClaimsGenerator)

        segment.inClaims << marine1000 << marine1000Two << motor500
        // segment.inReserves << marineReserve2000 << motorReserve600 << paReserve800

        discounting.start()
        segment.doCalculation(Segment.PHASE_NET)

        double factorAtUpdateDate1 = Math.pow(1.04 / 1.02, 151d / 365d)
        double factorAtUpdateDate2 = Math.pow(1.04 / 1.02, 273d / 365d)
        double sumOfDiscountedIncrements0 = -200 / factorAtUpdateDate1 - 500 / factorAtUpdateDate2 - 100
        assertEquals "# discount values", 1, segment.outDiscountedValues.size()
        assertEquals "gross incremental paid", sumOfDiscountedIncrements0,
                segment.outDiscountedValues[0].discountedPaidIncrementalGross, EPSILON

        segment.iterationScope.periodScope.prepareNextPeriod()
        segment.inClaims << marine1000Three << motor500Two
        discounting.start()
        segment.doCalculation(Segment.PHASE_NET)

        double factorAtUpdateDate3 = Math.pow(1.062 / 1.04, 181d / 425d) * 1.04 / 1.02
        double factorAtUpdateDate4 = Math.pow(1.062 / 1.04, 212d / 425d) * 1.04 / 1.02
        double sumOfDiscountedIncrements1 = -300 / factorAtUpdateDate3 - 150 / factorAtUpdateDate4
        assertEquals "# discount values", 1, segment.outDiscountedValues.size()
        assertEquals "gross incremental paid", sumOfDiscountedIncrements1,
                segment.outDiscountedValues[0].discountedPaidIncrementalGross, EPSILON
        assertEquals "# net present values",1, segment.outNetPresentValues.size()
        assertEquals "gross net present value paids", sumOfDiscountedIncrements0+ sumOfDiscountedIncrements1,
                segment.outNetPresentValues[0].netPresentValueGross, EPSILON
        assertEquals " period", 0, segment.outNetPresentValues[0].period
    }

    private ClaimCashflowPacket getCededClaim(ClaimCashflowPacket grossClaim, double quotaShare) {
        ClaimStorage storage = new ClaimStorage(grossClaim)
        storage.lazyInitCededClaimRoot(-quotaShare)
        return ClaimUtils.getCededClaim(grossClaim, storage, -quotaShare, -quotaShare, -quotaShare, false);
    }

    static ClaimCashflowPacket getClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double incrementalPaid,
                                                      DateTime occurrenceDate, DateTime updateDate,
                                                      boolean hasUltimate = false, PeriodScope periodScope, IComponentMarker marker) {
        if (baseClaim == null) {
            baseClaim = new GrossClaimRoot(ultimate, ClaimType.ATTRITIONAL, null, occurrenceDate, null, null)
        }
        ClaimCashflowPacket claim = new ClaimCashflowPacket(baseClaim, ultimate, incrementalPaid, incrementalPaid,
                ultimate - incrementalPaid, 0, 0, null, updateDate, periodScope.getPeriodCounter())
        claim.setMarker(marker)
        claim
    }
}
