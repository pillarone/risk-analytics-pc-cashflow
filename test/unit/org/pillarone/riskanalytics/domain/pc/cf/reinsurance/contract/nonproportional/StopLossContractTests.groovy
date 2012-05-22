package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional

import org.joda.time.DateTime

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors
import org.pillarone.riskanalytics.domain.pc.cf.indexing.BaseDateMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): claims with different patterns
class StopLossContractTests extends GroovyTestCase {
    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualFastReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.8d, 0.9d, 0.95d, 0.98d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket immediateReportingPattern = PatternPacketTests.getPattern([0], [1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])
    PatternPacket annualPayoutPattern2 = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.4d, 0.6d, 0.75d, 0.9d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110201 = new DateTime(2011,2,1,0,0,0,0)
    DateTime date20110301 = new DateTime(2011,3,1,0,0,0,0)
    DateTime date20110401 = new DateTime(2011,4,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110501 = new DateTime(2011,5,1,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)
    DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)
    DateTime date20130101 = new DateTime(2013,1,1,0,0,0,0)
    DateTime date20140101 = new DateTime(2014,1,1,0,0,0,0)
    DateTime date20150101 = new DateTime(2015,1,1,0,0,0,0)

    static ReinsuranceContract getStopLossContract(StopLossBase contractBase, double attachmentPoint, double limit,
                                                   double premium, DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, [
                    'stopLossContractBase': contractBase,
                    'attachmentPoint': attachmentPoint,
                    'limit': limit, 'premiumBase': XLPremiumBase.ABSOLUTE,
                    'premium': premium,
                    'riPremiumSplit': PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:])]),
                parmCover : CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, [filter: FilterStrategyType.getDefault()]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }

    void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
    }

    void testAbsolute() {
        ReinsuranceContract stopLoss = getStopLossContract(StopLossBase.ABSOLUTE, 2400, 800, 400, date20110101)
        stopLoss.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        PeriodScope periodScope = stopLoss.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-2600d, date20110418, ClaimType.ATTRITIONAL),
                getBaseClaim(-600d, date20110418, ClaimType.SINGLE),
                getBaseClaim(-800d, date20110418, ClaimType.SINGLE)]
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 2000, premiumPaid: 2000,
                                                                  numberOfPolicies: 100, exposure: new ExposureInfo(periodScope));
        stopLoss.inUnderwritingInfo.add(uw120)
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, true)
        stopLoss.doCalculation()
        assertEquals 'number of ceded claims', 3, stopLoss.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', [520, 120, 160], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P0 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P0 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P0 ceded reservedIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P0 ceded outstandingIndexed', [0] * 3, stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P0 ceded ibnrIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P0 ceded premium written', -400, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P1 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P1 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P1 ceded reservedIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P1 ceded outstandingIndexed', [0] * 3, stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P1 ceded ibnrIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P1 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P2 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded reported incremental', [519.9999999999999, 120.00000000000004, 160.00000000000009], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded reported cumulated', [519.9999999999999, 120.00000000000004, 160.00000000000009], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P2 ceded paid incremental', [259.99999999999994, 60.0, 79.99999999999999], stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded paid cumulated', [259.99999999999994, 60.0, 79.99999999999999], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P2 ceded reservedIndexed', [260.00000000000006, 60.0, 80.00000000000001], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P2 ceded outstandingIndexed', [259.99999999999994, 60.00000000000004, 80.0000000000001], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P2 ceded ibnrIndexed', [1.1368683772161603E-13, -4.263256414560601E-14, -8.526512829121202E-14], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P2 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P3 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded reported cumulated', [519.9999999999999, 120.00000000000004, 160.00000000000009], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P3 ceded paid incremental', [260.0, 59.99999999999999, 79.99999999999999], stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded paid cumulated', [520.0, 120.0, 159.99999999999997], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P3 ceded reservedIndexed', [0.0, 0.0, 2.8421709430404007E-14], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P3 ceded outstandingIndexed', [-1.1368683772161603E-13, 4.263256414560601E-14, 1.1368683772161603E-13], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P3 ceded ibnrIndexed', [1.1368683772161603E-13, -4.263256414560601E-14, -8.526512829121202E-14], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P3 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P4 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded reported cumulated', [519.9999999999999, 120.00000000000004, 160.00000000000009], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P4 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded paid cumulated', [520.0, 120.0, 159.99999999999997], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P4 ceded reservedIndexed', [0.0, 0.0, 2.8421709430404007E-14], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P4 ceded outstandingIndexed', [-1.1368683772161603E-13, 4.263256414560601E-14, 1.1368683772161603E-13], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P4 ceded ibnrIndexed', [1.1368683772161603E-13, -4.263256414560601E-14, -8.526512829121202E-14], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P4 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    void testApplyingIndex() {
        ReinsuranceContract stopLoss = getStopLossContract(StopLossBase.ABSOLUTE, 2400, 800, 400, date20110101)
        stopLoss.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        PeriodScope periodScope = stopLoss.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        FactorsPacket packet = new FactorsPacket()
        packet.add(date20110101, 1d)
        packet.add(date20120101, 0.9d)
        packet.add(date20130101, 0.8d)
        packet.add(date20140101, 0.95d)
        packet.add(date20150101, 1.05d)
        List<Factors> factors = [new Factors(packet, BaseDateMode.START_OF_PROJECTION, IndexMode.STEPWISE_PREVIOUS, date20110101)]

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-2600d, date20110418, ClaimType.ATTRITIONAL),
                getBaseClaim(-600d, date20110418, ClaimType.SINGLE),
                getBaseClaim(-800d, date20110418, ClaimType.SINGLE)]
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 2000, premiumPaid: 2000,
                numberOfPolicies: 100, exposure: new ExposureInfo(periodScope));
        stopLoss.inUnderwritingInfo.add(uw120)
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, true)
        stopLoss.doCalculation()
        assertEquals 'number of ceded claims', 3, stopLoss.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', [520, 120, 160], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P0 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P0 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P0 ceded reservedIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P0 ceded outstandingIndexed', [0] * 3, stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P0 ceded ibnrIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P0 ceded premium written', -400, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P1 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P1 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P1 ceded reservedIndexed', [468, 108, 144], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P1 ceded outstandingIndexed', [0] * 3, stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P1 ceded ibnrIndexed', [468, 108, 144], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P1 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P2 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded reported incremental', [208.00000000000023, 48.00000000000006, 64.0000000000001], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded reported cumulated', [208.00000000000023, 48.00000000000006, 64.0000000000001], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P2 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P2 ceded reservedIndexed', [208.00000000000023, 48.00000000000006, 64.0000000000001], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P2 ceded outstandingIndexed', [208.00000000000023, 48.00000000000006, 64.0000000000001], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P2 ceded ibnrIndexed', [0] * 3, stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P2 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P3 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded reported incremental', [311.9999999999997, 71.99999999999996, 95.99999999999987], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded reported cumulated', [520.0, 120.00000000000001, 159.99999999999997], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P3 ceded paid incremental', [370.5, 85.5, 113.99999999999999], stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded paid cumulated', [370.5, 85.5, 113.99999999999999], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P3 ceded reservedIndexed', [-162.49999999999977, -37.49999999999994, -49.999999999999886], stopLoss.outClaimsCeded*.reservedIndexed()     // todo(sku): correct?
        assertEquals 'P3 ceded outstandingIndexed', [149.5, 34.500000000000014, 45.999999999999986], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P3 ceded ibnrIndexed', [-311.9999999999998, -71.99999999999996, -95.99999999999987], stopLoss.outClaimsCeded*.ibnrIndexed()               // todo(sku): correct?
        assertEquals 'P3 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P4 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded reported cumulated', [520.0, 120.00000000000001, 159.99999999999997], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P4 ceded paid incremental', [149.5, 34.5, 45.99999999999999], stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded paid cumulated', [520.0, 120.0, 159.99999999999997], stopLoss.outClaimsCeded*.paidCumulatedIndexed
//        assertEquals 'P4 ceded reservedIndexed', [0.0, 0.0, 2.8421709430404007E-14], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P4 ceded outstandingIndexed', [0.0, 1.4210854715202004E-14, 0.0], stopLoss.outClaimsCeded*.outstandingIndexed()
//        assertEquals 'P4 ceded ibnrIndexed', [1.1368683772161603E-13, -4.263256414560601E-14, -8.526512829121202E-14], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P4 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    void testOverReserving() {
        ReinsuranceContract stopLoss = getStopLossContract(StopLossBase.ABSOLUTE, 2800, 800, 400, date20110101)
        stopLoss.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        PeriodScope periodScope = stopLoss.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<Factors> factors = getFactors()

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-2600d, date20110418, ClaimType.ATTRITIONAL, annualPayoutPattern, immediateReportingPattern),
                getBaseClaim(-600d, date20110418, ClaimType.SINGLE, annualPayoutPattern, immediateReportingPattern),
                getBaseClaim(-800d, date20110418, ClaimType.SINGLE, annualPayoutPattern, immediateReportingPattern)]
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 2000, premiumPaid: 2000,
                numberOfPolicies: 100, exposure: new ExposureInfo(periodScope));
        stopLoss.inUnderwritingInfo.add(uw120)
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, true)
        stopLoss.doCalculation()
        assertEquals 'number of ceded claims', 3, stopLoss.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', [520, 120, 160], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P0 ceded reported incremental', [520, 120, 160], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0 ceded reported cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P0 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P0 ceded reservedIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P0 ceded outstandingIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P0 ceded ibnrIndexed', [0] * 3, stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P0 ceded premium written', -400, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P1 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded reported incremental', [0, 0, 0], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded reported cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P1 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P1 ceded reservedIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P1 ceded outstandingIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P1 ceded ibnrIndexed', [0] * 3, stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P1 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P2 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded reported incremental', [-156, -36, -48], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded reported cumulated', [364, 84, 112], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P2 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P2 ceded reservedIndexed', [364, 84, 112], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P2 ceded outstandingIndexed', [364, 84, 112], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P2 ceded ibnrIndexed', [0] * 3, stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P2 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P3 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded reported incremental', [-78, -18, -24], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded reported cumulated', [286, 66, 88], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P3 ceded paid incremental', [13.0, 3.0, 4.0], stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded paid cumulated', [13.0, 3.0, 4.0], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P3 ceded reservedIndexed', [273.0, 63.0, 84.0], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P3 ceded outstandingIndexed', [273.0, 63.0, 84.0], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P3 ceded ibnrIndexed', [0] * 3, stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P3 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, factors, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P4 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded reported incremental', [136.5, 31.5, 42], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded reported cumulated', [422.5, 97.5, 130.0], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P4 ceded paid incremental', [409.49999999999994, 94.5, 125.99999999999999], stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded paid cumulated', [422.49999999999994, 97.5, 130.0], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P4 ceded reservedIndexed', [5.6843418860808015E-14, 0.0, 0.0], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P4 ceded outstandingIndexed', [5.6843418860808015E-14, 0.0, 0.0], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P4 ceded ibnrIndexed', [0.0] * 3, stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P4 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    void testGNPI() {
        ReinsuranceContract stopLoss = getStopLossContract(StopLossBase.GNPI, 1.2, 0.4, 0.2, date20110101)
        stopLoss.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        PeriodScope periodScope = stopLoss.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-2600d, date20110418, ClaimType.ATTRITIONAL),
                getBaseClaim(-600d, date20110418, ClaimType.SINGLE),
                getBaseClaim(-800d, date20110418, ClaimType.SINGLE)]
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 2000, premiumPaid: 2000,
                                                                  numberOfPolicies: 100, exposure: new ExposureInfo(periodScope));
        stopLoss.inUnderwritingInfo.add(uw120)
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, true)
        stopLoss.doCalculation()
        assertEquals 'number of ceded claims', 3, stopLoss.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', [520, 120, 160], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P0 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P0 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P0 ceded reservedIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P0 ceded outstandingIndexed', [0] * 3, stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P0 ceded ibnrIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P0 ceded premium written', -400, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P1 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P1 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P1 ceded reservedIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P1 ceded outstandingIndexed', [0] * 3, stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P1 ceded ibnrIndexed', [520, 120, 160], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P1 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P2 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded reported incremental', [519.9999999999999, 120.00000000000004, 160.00000000000009], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded reported cumulated', [519.9999999999999, 120.00000000000004, 160.00000000000009], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P2 ceded paid incremental', [259.99999999999994, 60.0, 79.99999999999999], stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded paid cumulated', [259.99999999999994, 60.0, 79.99999999999999], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P2 ceded reservedIndexed', [260.00000000000006, 60.0, 80.00000000000001], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P2 ceded outstandingIndexed', [259.99999999999994, 60.00000000000004, 80.0000000000001], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P2 ceded ibnrIndexed', [1.1368683772161603E-13, -4.263256414560601E-14, -8.526512829121202E-14], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P2 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P3 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded reported cumulated', [519.9999999999999, 120.00000000000004, 160.00000000000009], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P3 ceded paid incremental', [260.0, 59.99999999999999, 79.99999999999999], stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded paid cumulated', [520.0, 120.0, 159.99999999999997], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P3 ceded reservedIndexed', [0.0, 0.0, 2.8421709430404007E-14], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P3 ceded outstandingIndexed', [-1.1368683772161603E-13, 4.263256414560601E-14, 1.1368683772161603E-13], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P3 ceded ibnrIndexed', [1.1368683772161603E-13, -4.263256414560601E-14, -8.526512829121202E-14], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P3 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P4 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded reported cumulated', [519.9999999999999, 120.00000000000004, 160.00000000000009], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P4 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded paid cumulated', [520.0, 120.0, 159.99999999999997], stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P4 ceded reservedIndexed', [0.0, 0.0, 2.8421709430404007E-14], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P4 ceded outstandingIndexed', [-1.1368683772161603E-13, 4.263256414560601E-14, 1.1368683772161603E-13], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P4 ceded ibnrIndexed', [1.1368683772161603E-13, -4.263256414560601E-14, -8.526512829121202E-14], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P4 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    /**
     * claims occur in different periods, make sure both get the whole cover or more generally a new contract instance is applied
     */
    void testIndependenceOfContractsPerPeriod() {
                ReinsuranceContract stopLoss = getStopLossContract(StopLossBase.ABSOLUTE, 2400, 800, 400, date20110101)
        stopLoss.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        PeriodScope periodScope = stopLoss.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-2600d, date20110418, ClaimType.ATTRITIONAL),
                getBaseClaim(-600d, date20110418, ClaimType.SINGLE)]
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 2000, premiumPaid: 2000,
                                                                  numberOfPolicies: 100, exposure: new ExposureInfo(periodScope));
        stopLoss.inUnderwritingInfo.add(uw120)
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, true)
        stopLoss.doCalculation()
        assertEquals 'number of ceded claims', 2, stopLoss.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', [650, 150], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P0 ceded reported incremental', [0] * 2, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0 ceded reported cumulated', [0] * 2, stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P0 ceded paid incremental', [0] * 2, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0 ceded paid cumulated', [0] * 2, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P0 ceded reservedIndexed', [650, 150], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P0 ceded outstandingIndexed', [0] * 2, stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P0 ceded ibnrIndexed', [650, 150], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P0 ceded premium written', -400, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        GrossClaimRoot grossClaimRoot3000 = getBaseClaim(-3000d, date20120101, ClaimType.SINGLE)
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        addClaimCashflowOfCurrentPeriod(stopLoss, [grossClaimRoot3000], null, periodCounter, true)
        claimRoots << grossClaimRoot3000
        stopLoss.doCalculation()
        assertEquals 'P1 ceded ultimate', [600, 0, 0], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P1 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P1 ceded reservedIndexed', [600, 650, 150], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P1 ceded outstandingIndexed', [0] * 3, stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P1 ceded ibnrIndexed', [600, 650, 150], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P1 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, null, periodCounter, false)
        claimRoots << grossClaimRoot3000
        stopLoss.doCalculation()
        assertEquals 'P2 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded reported incremental', [0.0, 130.0, 30.000000000000014], stopLoss.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded reported cumulated', [0.0, 130.0, 30.000000000000014], stopLoss.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals 'P2 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulatedIndexed
        assertEquals 'P2 ceded reservedIndexed', [600, 650, 150], stopLoss.outClaimsCeded*.reservedIndexed()
        assertEquals 'P2 ceded outstandingIndexed', [0.0, 130.0, 30.000000000000014], stopLoss.outClaimsCeded*.outstandingIndexed()
        assertEquals 'P2 ceded ibnrIndexed', [600.0, 520.0, 119.99999999999999], stopLoss.outClaimsCeded*.ibnrIndexed()
        assertEquals 'P2 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    private GrossClaimRoot getBaseClaim(double ultimate, EventPacket event) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, ClaimType.EVENT,
                event.getDate(), event.getDate(), annualPayoutPattern2, annualFastReportingPattern, event)
        return claimRoot
    }

    private GrossClaimRoot getBaseClaim(double ultimate, DateTime date, ClaimType claimType) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, claimType,
                date, date, annualPayoutPattern, annualReportingPatternInclFirst)
        return claimRoot
    }

    private GrossClaimRoot getBaseClaim(double ultimate, DateTime date, ClaimType claimType,
                                        PatternPacket payoutPattern, PatternPacket reportingPattern) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, claimType,
                date, date, payoutPattern, reportingPattern)
        return claimRoot
    }

    private void addClaimCashflowOfCurrentPeriod(ReinsuranceContract stopLoss, List<GrossClaimRoot> baseClaims,
                                                 List<Factors> factors, IPeriodCounter periodCounter, boolean firstPeriod) {
        for (GrossClaimRoot baseClaim : baseClaims) {
            List<ClaimCashflowPacket> claims = baseClaim.getClaimCashflowPackets(periodCounter, factors, firstPeriod)
            stopLoss.inClaims.addAll(claims)
        }
    }

    private List<Factors> getFactors() {
        FactorsPacket packet = new FactorsPacket()
        packet.add(date20110101, 1d)
        packet.add(date20120101, 0.9d)
        packet.add(date20130101, 0.8d)
        packet.add(date20140101, 0.7d)
        packet.add(date20150101, 1.05d)
        List<Factors> factors = [new Factors(packet, BaseDateMode.START_OF_PROJECTION, IndexMode.STEPWISE_PREVIOUS, date20110101)]
        return factors
    }
}
