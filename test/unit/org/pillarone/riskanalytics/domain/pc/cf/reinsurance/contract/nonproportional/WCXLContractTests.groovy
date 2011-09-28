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

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationStrategyType
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): claims with different patterns
class WCXLContractTests extends GroovyTestCase {
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

    static ReinsuranceContract getWCXLContract(double attachmentPoint, double limit, double aggregateLimit,
                                              double aggregateDeductible, double premium,
                                              List<Double> reinstatementPremiumFactors,
                                              DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.WCXL, [
                    'aggregateDeductible': aggregateDeductible, 'attachmentPoint': attachmentPoint,
                    'limit': limit, 'aggregateLimit': aggregateLimit, 'premiumBase': XLPremiumBase.ABSOLUTE,
                    'stabilization': StabilizationStrategyType.getDefault(),
                    'premium': premium,
                    'riPremiumSplit': PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),
                    'reinstatementPremiums': new TableMultiDimensionalParameter(reinstatementPremiumFactors, ['Reinstatement Premium'])]),
                parmCover : CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, [filter: FilterStrategyType.getDefault()]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])

    }

    void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
    }


    /**
     * claims occur in different periods, make sure both get the whole cover or more generally a new contract instance
     * no reinstatements and no aggregate deductible applied
     */
    void testIndependenceOfContractsPerPeriod() {
        ReinsuranceContract wcxl = getWCXLContract(20, 30, 100, 0, 100, [0.2d], date20110101)
        wcxl.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        PeriodScope periodScope = wcxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        GrossClaimRoot claimRoot50 = new GrossClaimRoot(-50, ClaimType.EVENT,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst, new EventPacket(date20110101))
        List<ClaimCashflowPacket> claims50 = claimRoot50.getClaimCashflowPackets(periodCounter, true)
        wcxl.inClaims.addAll(claims50)
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wcxl.inUnderwritingInfo.add(uw120)

        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 1, wcxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimate', 30, wcxl.outClaimsCeded[0].ultimate()
        assertEquals 'P0.0 ceded reported incremental', 0, wcxl.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P0.0 ceded reported cumulated', 0, wcxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P0.0 ceded paid incremental', 0, wcxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P0.0 ceded paid cumulated', 0, wcxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P0.0 ceded reservedIndexed', 30, wcxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P0.0 ceded outstandingIndexed', 0, wcxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P0.0 ceded ibnrIndexed', 30, wcxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P0 ceded premium written', -100, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -100, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -100, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON

        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        wcxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        GrossClaimRoot claimRoot70 = new GrossClaimRoot(-70, ClaimType.EVENT,
                date20120101, date20120101, annualPayoutPattern, annualReportingPatternInclFirst, new EventPacket(date20120101))
        List<ClaimCashflowPacket> claims70 = claimRoot70.getClaimCashflowPackets(periodCounter, true)
        wcxl.inClaims.addAll(claims70)
        wcxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wcxl.outClaimsCeded.size()
        assertEquals 'P1.1 ceded ultimate', 30, wcxl.outClaimsCeded[0].ultimate()
        assertEquals 'P1.1 ceded reported incremental', 1, wcxl.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P1.1 ceded reported cumulated', 1, wcxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P1.1 ceded paid incremental', 0, wcxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P1.1 ceded paid cumulated', 0, wcxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P1.1 ceded reservedIndexed', 30, wcxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P1.1 ceded outstandingIndexed', 1, wcxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P1.1 ceded ibnrIndexed', 29, wcxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P1.0 ceded ultimate', 0, wcxl.outClaimsCeded[1].ultimate()
        assertEquals 'P1.0 ceded reported incremental', 10, wcxl.outClaimsCeded[1].reportedIncrementalIndexed
        assertEquals 'P1.0 ceded reported cumulated', 10, wcxl.outClaimsCeded[1].reportedCumulatedIndexed
        assertEquals 'P1.0 ceded paid incremental', 0, wcxl.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P1.0 ceded paid cumulated', 0, wcxl.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P1.0 ceded reservedIndexed', 30, wcxl.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P1.0 ceded outstandingIndexed', 10, wcxl.outClaimsCeded[1].outstandingIndexed()
        assertEquals 'P1.0 ceded ibnrIndexed', 20, wcxl.outClaimsCeded[1].ibnrIndexed()


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        wcxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        wcxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter, false))
        wcxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wcxl.outClaimsCeded.size()
        assertEquals 'P2.1 ceded ultimate', 0, wcxl.outClaimsCeded[0].ultimate()
        assertEquals 'P2.1 ceded reported incremental', 21, wcxl.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P2.1 ceded reported cumulated', 22, wcxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P2.1 ceded paid incremental', 8, wcxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P2.1 ceded paid cumulated', 8, wcxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P2.1 ceded reservedIndexed', 22, wcxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P2.1 ceded outstandingIndexed', 14, wcxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P2.1 ceded ibnrIndexed', 8, wcxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P2.0 ceded ultimate', 0, wcxl.outClaimsCeded[1].ultimate()
        assertEquals 'P2.0 ceded reported incremental', 10, wcxl.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P2.0 ceded reported cumulated', 20, wcxl.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'P2.0 ceded paid incremental', 15, wcxl.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P2.0 ceded paid cumulated', 15, wcxl.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P2.0 ceded reservedIndexed', 15, wcxl.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P2.0 ceded outstandingIndexed', 5, wcxl.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'P2.0 ceded ibnrIndexed', 10, wcxl.outClaimsCeded[1].ibnrIndexed(), EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        wcxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        wcxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter, false))
        wcxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wcxl.outClaimsCeded.size()
        assertEquals 'P3.1 ceded ultimate', 0, wcxl.outClaimsCeded[0].ultimate()
        assertEquals 'P3.1 ceded reported incremental', 8, wcxl.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3.1 ceded reported cumulated', 30, wcxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P3.1 ceded paid incremental', 21, wcxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P3.1 ceded paid cumulated', 29, wcxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P3.1 ceded reservedIndexed', 1, wcxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P3.1 ceded outstandingIndexed', 1, wcxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P3.1 ceded ibnrIndexed', 0, wcxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P3.0 ceded ultimate', 0, wcxl.outClaimsCeded[1].ultimate()
        assertEquals 'P3.0 ceded reported incremental', 9, wcxl.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3.0 ceded reported cumulated', 29, wcxl.outClaimsCeded[1].reportedCumulatedIndexed
        assertEquals 'P3.0 ceded paid incremental', 7.5, wcxl.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P3.0 ceded paid cumulated', 22.5, wcxl.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P3.0 ceded reservedIndexed', 7.5, wcxl.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P3.0 ceded outstandingIndexed', 6.5, wcxl.outClaimsCeded[1].outstandingIndexed()
        assertEquals 'P3.0 ceded ibnrIndexed', 1, wcxl.outClaimsCeded[1].ibnrIndexed()


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        wcxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        wcxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter, false))
        wcxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wcxl.outClaimsCeded.size()
        assertEquals 'P4.1 ceded ultimate', 0, wcxl.outClaimsCeded[0].ultimate()
        assertEquals 'P4.1 ceded reported incremental', 0, wcxl.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P4.1 ceded reported cumulated', 30, wcxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P4.1 ceded paid incremental', 1, wcxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P4.1 ceded paid cumulated', 30, wcxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P4.1 ceded reservedIndexed', 0, wcxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P4.1 ceded outstandingIndexed', 0, wcxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P4.1 ceded ibnrIndexed', 0, wcxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P4.0 ceded ultimate', 0, wcxl.outClaimsCeded[1].ultimate()
        assertEquals 'P4.0 ceded reported incremental', 1, wcxl.outClaimsCeded[1].reportedIncrementalIndexed
        assertEquals 'P4.0 ceded reported cumulated', 30, wcxl.outClaimsCeded[1].reportedCumulatedIndexed
        assertEquals 'P4.0 ceded paid incremental', 7.5, wcxl.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P4.0 ceded paid cumulated', 30, wcxl.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P4.0 ceded reservedIndexed', 0, wcxl.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P4.0 ceded outstandingIndexed', 0, wcxl.outClaimsCeded[1].outstandingIndexed()
        assertEquals 'P4.0 ceded ibnrIndexed', 0, wcxl.outClaimsCeded[1].ibnrIndexed()

        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        wcxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        wcxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter, false))
        wcxl.doCalculation()

        assertEquals 'number of ceded claims', 1, wcxl.outClaimsCeded.size()
        assertEquals 'P5 summed ceded reported incremental', 0, wcxl.outClaimsCeded.reportedIncrementalIndexed.sum()
        assertEquals 'P5 summed ceded reported cumulated', 30, wcxl.outClaimsCeded.reportedCumulatedIndexed.sum()
        assertEquals 'P5 summed ceded paid incremental', 0, wcxl.outClaimsCeded.paidIncrementalIndexed.sum()
        assertEquals 'P5 summed ceded paid cumulated', 30, wcxl.outClaimsCeded.paidCumulatedIndexed.sum()
        assertEquals 'P5 summed ceded reservedIndexed', 0, wcxl.outClaimsCeded.reservesIndexed.sum()
        assertEquals 'P5 summed ceded outstandingIndexed', 0, wcxl.outClaimsCeded*.outstandingIndexed().sum()
        assertEquals 'P5 summed ceded ibnrIndexed', 0, wcxl.outClaimsCeded*.ibnrIndexed().sum()
    }

    /**
     * multiple claims, aggregate deductible delays payments to second period, 2.5 reinstatements, aggregate limit
     * is not limiting cover
     */
    void testAggregateDeductibleAndLimits() {
        ReinsuranceContract wcxl = getWCXLContract(100, 200, 1000, 500, 800, [0.6d], date20110101)
        PeriodScope periodScope = wcxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-400, date20110101), getBaseClaim(-400, date20110201, ClaimType.SINGLE),
                getBaseClaim(-400, date20110301), getBaseClaim(-400, date20110401), getBaseClaim(-400, date20110501)]
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, true)

        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wcxl.inUnderwritingInfo.add(uw120)

        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimates', [0, 0, 100, 200, 200], wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P0.0 ceded incremental reported', [0, 0, 100, 200, 200], wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0.0 ceded incremental paids', [0d] * 5, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0.0 ceded premium written', -800, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -800, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -800, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0.0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0.0 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded commission fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded commission variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimates', [0d] * 5, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded incremental reported', [0d] * 5, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded incremental paids', [0, 0, 0, 60, 140], wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded premium written', -480, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', -480, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', -480, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimates', [0d] * 5, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded incremental reported', [0d] * 5, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded incremental paids', [0, 0, 100, 140, 60], wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded premium written', -720, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', -720, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', -720, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimates', [0d] * 5, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded incremental reported', [0d] * 5, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded incremental paids', [0d] *5, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded premium written', 0, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimates', [0d] * 5, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded incremental reported', [0d] * 5, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded incremental paids', [0d] *5, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded premium written', 0, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    /**
     * multiple claims, aggregate deductible delays payments to second period, 1 reinstatement only, aggregate limit with effect
     */
    void testAggregateDeductibleAndAggregateLimits() {
        ReinsuranceContract wcxl = getWCXLContract(100, 200, 400, 500, 800, [0.6d], date20110101)
        PeriodScope periodScope = wcxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-400, date20110101), getBaseClaim(-400, date20110201),
                getBaseClaim(-400, date20110301, ClaimType.SINGLE), getBaseClaim(-400, date20110401), getBaseClaim(-400, date20110501)]
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, true)
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wcxl.inUnderwritingInfo.add(uw120)

        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimates', [0, 0, 100, 200, 100], wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P0.0 ceded incremental reported', [0, 0, 100, 200, 100], wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0.0 ceded incremental paids', [0d] * 5, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0.0 ceded premium written', -800, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -800, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -800, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0.0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0.0 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimates', [0d] * 5, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded incremental reported', [0d] * 5, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded incremental paids', [0, 0, 0, 60, 140], wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded premium written', -480, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', -480, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', -480, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimates', [0d] * 5, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded incremental reported', [0d] * 5, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded incremental paids', [0, 0, 100, 100, 0], wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded premium written', 0, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimates', [0d] * 5, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded incremental reported', [0d] * 5, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded incremental paids', [0d] *5, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded premium written', 0, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wcxl.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimates', [0d] * 5, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded incremental reported', [0d] * 5, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded incremental paids', [0d] *5, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded premium written', 0, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    /**
     * multiple claims, aggregate deductible delays payments to second period, 2.5 reinstatements, aggregate limit
     * is not limiting cover
     */
    void testSeveralClaimsPerEventAggregateDeductibleAndLimits() {
        ReinsuranceContract wcxl = getWCXLContract(100, 200, 1000, 500, 800, [0.6d], date20110101)
        PeriodScope periodScope = wcxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        EventPacket event20110101 = new EventPacket(date20110101)
        EventPacket event20110201 = new EventPacket(date20110201)
        EventPacket event20110301 = new EventPacket(date20110301)
        EventPacket event20110401 = new EventPacket(date20110401)
        EventPacket event20110501 = new EventPacket(date20110501)

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-400, date20110101, ClaimType.SINGLE), getBaseClaim(-400, event20110201),
                getBaseClaim(-400, date20110301, ClaimType.SINGLE), getBaseClaim(-100, event20110401),
                getBaseClaim(-100, event20110401), getBaseClaim(-200, event20110401), getBaseClaim(-50, event20110501),
                getBaseClaim(-350, event20110501)]
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, true)

        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wcxl.inUnderwritingInfo.add(uw120)

        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 8, wcxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimates', [0, 0, 100, 50, 50, 100, 25, 175], wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P0.0 ceded incremental reported', [0, 0, 100, 50, 50, 100, 25, 175], wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0.0 ceded incremental paids', [0d] * 8, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0.0 ceded premium written', -800, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -800, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -800, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0.0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0.0 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded commission fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded commission variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 8, wcxl.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimates', [0d] * 8, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded incremental reported', [0d] * 8, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded incremental paids', [0, 0, 0, 15, 15, 30, 17.5, 122.49999999999999], wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded premium written', -480, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', -480, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', -480, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 8, wcxl.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimates', [0d] * 8, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded incremental reported', [0d] * 8, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded incremental paids', [0, 0, 100, 35, 35, 70, 7.5, 52.5], wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded premium written', -720, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', -720, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', -720, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 8, wcxl.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimates', [0d] * 8, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded incremental reported', [0d] * 8, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded incremental paids', [0d] *8, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded premium written', 0, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wcxl.reset()
        wcxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wcxl, claimRoots, periodCounter, false)
        wcxl.doCalculation()
        assertEquals 'number of ceded claims', 8, wcxl.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimates', [0d] * 8, wcxl.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded incremental reported', [0d] * 8, wcxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded incremental paids', [0d] *8, wcxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded premium written', 0, wcxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, wcxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded premium fixed', 0, wcxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded premium variable', 0, wcxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }


    void testCorrectClaimTypeCover() {

    }

    private GrossClaimRoot getBaseClaim(double ultimate, EventPacket event) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, ClaimType.EVENT,
                event.getDate(), event.getDate(), annualPayoutPattern2, annualFastReportingPattern, event)
        return claimRoot
    }

    private GrossClaimRoot getBaseClaim(double ultimate, DateTime eventDate) {
        EventPacket event = new EventPacket(eventDate)
        GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, ClaimType.EVENT,
                eventDate, eventDate, annualPayoutPattern2, annualFastReportingPattern, event)
        return claimRoot
    }

    private GrossClaimRoot getBaseClaim(double ultimate, DateTime eventDate, ClaimType claimType) {
        if (claimType.equals(ClaimType.EVENT)) {
            return getBaseClaim(ultimate, eventDate)
        }
        else {
            EventPacket event = new EventPacket(eventDate)
            GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, claimType,
                eventDate, eventDate, annualPayoutPattern2, annualFastReportingPattern)
            return claimRoot
        }
    }

    private void addClaimCashflowOfCurrentPeriod(ReinsuranceContract wcxl, List<GrossClaimRoot> baseClaims,
                                                 IPeriodCounter periodCounter, boolean firstPeriod) {
        for (GrossClaimRoot baseClaim : baseClaims) {
            List<ClaimCashflowPacket> claims = baseClaim.getClaimCashflowPackets(periodCounter, firstPeriod)
            wcxl.inClaims.addAll(claims)
        }
    }


}
