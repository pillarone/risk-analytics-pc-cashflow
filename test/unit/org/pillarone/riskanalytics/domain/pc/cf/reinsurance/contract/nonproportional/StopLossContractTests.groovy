package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
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
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.PremiumBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): claims with different patterns
class StopLossContractTests extends GroovyTestCase {
    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualFastReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.8d, 0.9d, 0.95d, 0.98d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
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

    static ReinsuranceContract getStopLossContract(StopLossBase contractBase, double attachmentPoint, double limit,
                                                   double premium, DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.STOPLOSS, [
                    'contractBase': contractBase,
                    'attachmentPoint': attachmentPoint,
                    'limit': limit, 'premiumBase': PremiumBase.ABSOLUTE,
                    'premium': premium,
                    'premiumAllocation': PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:])]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
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
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, true)
        stopLoss.doCalculation()
        assertEquals 'number of ceded claims', 3, stopLoss.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', [520, 120, 160], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P0 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P0 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P0 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P0 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P0 ceded reserved', [520, 120, 160], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P0 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P0 ceded ibnr', [520, 120, 160], stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P0 ceded premium written', -400, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P1 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P1 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P1 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P1 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P1 ceded reserved', [520, 120, 160], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P1 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P1 ceded ibnr', [520, 120, 160], stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P1 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P2 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded reported incremental', [520, 120, 160], stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P2 ceded reported cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P2 ceded paid incremental', [259.99999999999983, 59.999999999999964, 79.99999999999994], stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P2 ceded paid cumulated', [259.99999999999983, 59.999999999999964, 79.99999999999994], stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P2 ceded reserved', [260.00000000000017, 60.000000000000036, 80.00000000000006], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P2 ceded outstanding', [260.00000000000017, 60.000000000000036, 80.00000000000006], stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P2 ceded ibnr', [0] * 3, stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P2 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P3 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P3 ceded reported cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P3 ceded paid incremental', [260.00000000000017, 60.000000000000036, 80.00000000000004], stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P3 ceded paid cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P3 ceded reserved', [0] * 3, stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P3 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P3 ceded ibnr', [0] * 3, stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P3 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P4 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P4 ceded reported cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P4 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P4 ceded paid cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P4 ceded reserved', [0] * 3, stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P4 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P4 ceded ibnr', [0] * 3, stopLoss.outClaimsCeded*.ibnr()
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
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, true)
        stopLoss.doCalculation()
        assertEquals 'number of ceded claims', 3, stopLoss.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', [520, 120, 160], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P0 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P0 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P0 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P0 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P0 ceded reserved', [520, 120, 160], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P0 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P0 ceded ibnr', [520, 120, 160], stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P0 ceded premium written', -400, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -400, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P1 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P1 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P1 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P1 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P1 ceded reserved', [520, 120, 160], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P1 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P1 ceded ibnr', [520, 120, 160], stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P1 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P2 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded reported incremental', [520, 120, 160], stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P2 ceded reported cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P2 ceded paid incremental', [259.99999999999983, 59.999999999999964, 79.99999999999994], stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P2 ceded paid cumulated', [259.99999999999983, 59.999999999999964, 79.99999999999994], stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P2 ceded reserved', [260.00000000000017, 60.000000000000036, 80.00000000000006], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P2 ceded outstanding', [260.00000000000017, 60.000000000000036, 80.00000000000006], stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P2 ceded ibnr', [0] * 3, stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P2 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P3 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P3 ceded reported cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P3 ceded paid incremental', [260.00000000000017, 60.000000000000036, 80.00000000000004], stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P3 ceded paid cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P3 ceded reserved', [0] * 3, stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P3 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P3 ceded ibnr', [0] * 3, stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P3 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        stopLoss.doCalculation()
        assertEquals 'P4 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P4 ceded reported cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P4 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P4 ceded paid cumulated', [520, 120, 160], stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P4 ceded reserved', [0] * 3, stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P4 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P4 ceded ibnr', [0] * 3, stopLoss.outClaimsCeded*.ibnr()
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
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, true)
        stopLoss.doCalculation()
        assertEquals 'number of ceded claims', 2, stopLoss.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', [650, 150], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P0 ceded reported incremental', [0] * 2, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P0 ceded reported cumulated', [0] * 2, stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P0 ceded paid incremental', [0] * 2, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P0 ceded paid cumulated', [0] * 2, stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P0 ceded reserved', [650, 150], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P0 ceded outstanding', [0] * 2, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P0 ceded ibnr', [650, 150], stopLoss.outClaimsCeded*.ibnr()
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
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        addClaimCashflowOfCurrentPeriod(stopLoss, [grossClaimRoot3000], periodCounter, true)
        claimRoots << grossClaimRoot3000
        stopLoss.doCalculation()
        assertEquals 'P1 ceded ultimate', [600, 0, 0], stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded reported incremental', [0] * 3, stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P1 ceded reported cumulated', [0] * 3, stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P1 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P1 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P1 ceded reserved', [600, 650, 150], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P1 ceded outstanding', [0] * 3, stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P1 ceded ibnr', [600, 650, 150], stopLoss.outClaimsCeded*.ibnr()
        assertEquals 'P1 ceded premium written', 0, stopLoss.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', 0, stopLoss.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, stopLoss.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded commission fixed', 0, stopLoss.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded commission variable', 0, stopLoss.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        stopLoss.reset()
        stopLoss.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(stopLoss, claimRoots, periodCounter, false)
        claimRoots << grossClaimRoot3000
        stopLoss.doCalculation()
        assertEquals 'P2 ceded ultimate', [0] * 3, stopLoss.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded reported incremental', [0, 130.0000000000002, 30.000000000000043], stopLoss.outClaimsCeded*.reportedIncremental
        assertEquals 'P2 ceded reported cumulated', [0, 130.0000000000002, 30.000000000000043], stopLoss.outClaimsCeded*.reportedCumulated
        assertEquals 'P2 ceded paid incremental', [0] * 3, stopLoss.outClaimsCeded*.paidIncremental
        assertEquals 'P2 ceded paid cumulated', [0] * 3, stopLoss.outClaimsCeded*.paidCumulated
        assertEquals 'P2 ceded reserved', [600, 650, 150], stopLoss.outClaimsCeded*.reserved()
        assertEquals 'P2 ceded outstanding', [0.0, 130.0000000000002, 30.000000000000043], stopLoss.outClaimsCeded*.outstanding()
        assertEquals 'P2 ceded ibnr', [600.0, 519.9999999999998, 119.99999999999996], stopLoss.outClaimsCeded*.ibnr()
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

    private void addClaimCashflowOfCurrentPeriod(ReinsuranceContract stopLoss, List<GrossClaimRoot> baseClaims,
                                                 IPeriodCounter periodCounter, boolean firstPeriod) {
        for (GrossClaimRoot baseClaim : baseClaims) {
            List<ClaimCashflowPacket> claims = baseClaim.getClaimCashflowPackets(periodCounter, firstPeriod)
            stopLoss.inClaims.addAll(claims)
        }
    }


}