package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorSeverityIndexTests
import org.pillarone.riskanalytics.domain.pc.cf.indexing.BaseDateMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReinsuranceContractIndex
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReinsuranceContractIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation.XLBoundaryIndexApplication
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation.XLBoundaryIndexType
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
class WXLContractTests extends GroovyTestCase {
    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualFastReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.8d, 0.9d, 0.95d, 0.98d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])
    PatternPacket annualPayoutPattern2 = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.4d, 0.6d, 0.75d, 0.9d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)
    DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)

    static ReinsuranceContract getWXLContract(double attachmentPoint, double limit, double aggregateLimit,
                                              double aggregateDeductible, double premium,
                                              List<Double> reinstatementPremiumFactors,
                                              DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, [
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
        ReinsuranceContract wxl = getWXLContract(20, 30, 100, 0, 100, [0.2d], date20110101)
        wxl.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        PeriodScope periodScope = wxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        GrossClaimRoot claimRoot50 = new GrossClaimRoot(-50, ClaimType.SINGLE,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims50 = claimRoot50.getClaimCashflowPackets(periodCounter)
        wxl.inClaims.addAll(claims50)
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wxl.inUnderwritingInfo.add(uw120)

        wxl.doCalculation()
        assertEquals 'number of ceded claims', 1, wxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimate', 30, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P0.0 ceded reported incremental', 0, wxl.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P0.0 ceded reported cumulated', 0, wxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P0.0 ceded paid incremental', 0, wxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P0.0 ceded paid cumulated', 0, wxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P0.0 ceded reservedIndexed', 30, wxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P0.0 ceded outstandingIndexed', 0, wxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P0.0 ceded ibnrIndexed', 30, wxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P0 ceded premium written', -100, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0 ceded premium paid', -100, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0 ceded premium fixed', -100, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0 ceded commission fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0 ceded commission variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter))
        GrossClaimRoot claimRoot70 = new GrossClaimRoot(-70, ClaimType.SINGLE,
                date20120101, date20120101, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims70 = claimRoot70.getClaimCashflowPackets(periodCounter)
        wxl.inClaims.addAll(claims70)
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wxl.outClaimsCeded.size()
        assertEquals 'P1.1 ceded ultimate', 30, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P1.1 ceded reported incremental', 1, wxl.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P1.1 ceded reported cumulated', 1, wxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P1.1 ceded paid incremental', 0, wxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P1.1 ceded paid cumulated', 0, wxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P1.1 ceded reservedIndexed', 30, wxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P1.1 ceded outstandingIndexed', 1, wxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P1.1 ceded ibnrIndexed', 29, wxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P1.0 ceded ultimate', 0, wxl.outClaimsCeded[1].ultimate()
        assertEquals 'P1.0 ceded reported incremental', 10, wxl.outClaimsCeded[1].reportedIncrementalIndexed
        assertEquals 'P1.0 ceded reported cumulated', 10, wxl.outClaimsCeded[1].reportedCumulatedIndexed
        assertEquals 'P1.0 ceded paid incremental', 0, wxl.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P1.0 ceded paid cumulated', 0, wxl.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P1.0 ceded reservedIndexed', 30, wxl.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P1.0 ceded outstandingIndexed', 10, wxl.outClaimsCeded[1].outstandingIndexed()
        assertEquals 'P1.0 ceded ibnrIndexed', 20, wxl.outClaimsCeded[1].ibnrIndexed()


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter))
        wxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter))
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wxl.outClaimsCeded.size()
        assertEquals 'P2.1 ceded ultimate', 0, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P2.1 ceded reported incremental', 21, wxl.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P2.1 ceded reported cumulated', 22, wxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P2.1 ceded paid incremental', 8, wxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P2.1 ceded paid cumulated', 8, wxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P2.1 ceded reservedIndexed', 22, wxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P2.1 ceded outstandingIndexed', 14, wxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P2.1 ceded ibnrIndexed', 8, wxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P2.0 ceded ultimate', 0, wxl.outClaimsCeded[1].ultimate()
        assertEquals 'P2.0 ceded reported incremental', 10, wxl.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P2.0 ceded reported cumulated', 20, wxl.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'P2.0 ceded paid incremental', 15, wxl.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P2.0 ceded paid cumulated', 15, wxl.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P2.0 ceded reservedIndexed', 15, wxl.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P2.0 ceded outstandingIndexed', 5, wxl.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'P2.0 ceded ibnrIndexed', 10, wxl.outClaimsCeded[1].ibnrIndexed(), EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter))
        wxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter))
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wxl.outClaimsCeded.size()
        assertEquals 'P3.1 ceded ultimate', 0, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P3.1 ceded reported incremental', 8, wxl.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3.1 ceded reported cumulated', 30, wxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P3.1 ceded paid incremental', 21, wxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P3.1 ceded paid cumulated', 29, wxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P3.1 ceded reservedIndexed', 1, wxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P3.1 ceded outstandingIndexed', 1, wxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P3.1 ceded ibnrIndexed', 0, wxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P3.0 ceded ultimate', 0, wxl.outClaimsCeded[1].ultimate()
        assertEquals 'P3.0 ceded reported incremental', 9, wxl.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3.0 ceded reported cumulated', 29, wxl.outClaimsCeded[1].reportedCumulatedIndexed
        assertEquals 'P3.0 ceded paid incremental', 7.5, wxl.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P3.0 ceded paid cumulated', 22.5, wxl.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P3.0 ceded reservedIndexed', 7.5, wxl.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P3.0 ceded outstandingIndexed', 6.5, wxl.outClaimsCeded[1].outstandingIndexed()
        assertEquals 'P3.0 ceded ibnrIndexed', 1, wxl.outClaimsCeded[1].ibnrIndexed()


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter))
        wxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter))
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wxl.outClaimsCeded.size()
        assertEquals 'P4.1 ceded ultimate', 0, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P4.1 ceded reported incremental', 0, wxl.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P4.1 ceded reported cumulated', 30, wxl.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P4.1 ceded paid incremental', 1, wxl.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P4.1 ceded paid cumulated', 30, wxl.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P4.1 ceded reservedIndexed', 0, wxl.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P4.1 ceded outstandingIndexed', 0, wxl.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P4.1 ceded ibnrIndexed', 0, wxl.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P4.0 ceded ultimate', 0, wxl.outClaimsCeded[1].ultimate()
        assertEquals 'P4.0 ceded reported incremental', 1, wxl.outClaimsCeded[1].reportedIncrementalIndexed
        assertEquals 'P4.0 ceded reported cumulated', 30, wxl.outClaimsCeded[1].reportedCumulatedIndexed
        assertEquals 'P4.0 ceded paid incremental', 7.5, wxl.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P4.0 ceded paid cumulated', 30, wxl.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P4.0 ceded reservedIndexed', 0, wxl.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P4.0 ceded outstandingIndexed', 0, wxl.outClaimsCeded[1].outstandingIndexed()
        assertEquals 'P4.0 ceded ibnrIndexed', 0, wxl.outClaimsCeded[1].ibnrIndexed()

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter))
        wxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter))
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 1, wxl.outClaimsCeded.size()
        assertEquals 'P5 summed ceded reported incremental', 0, wxl.outClaimsCeded.reportedIncrementalIndexed.sum()
        assertEquals 'P5 summed ceded reported cumulated', 30, wxl.outClaimsCeded.reportedCumulatedIndexed.sum()
        assertEquals 'P5 summed ceded paid incremental', 0, wxl.outClaimsCeded.paidIncrementalIndexed.sum()
        assertEquals 'P5 summed ceded paid cumulated', 30, wxl.outClaimsCeded.paidCumulatedIndexed.sum()
        assertEquals 'P5 summed ceded reservedIndexed', 0, wxl.outClaimsCeded.reservesIndexed.sum()
        assertEquals 'P5 summed ceded outstandingIndexed', 0, wxl.outClaimsCeded*.outstandingIndexed().sum()
        assertEquals 'P5 summed ceded ibnrIndexed', 0, wxl.outClaimsCeded*.ibnrIndexed().sum()
    }

    /**
     * multiple claims, aggregate deductible delays payments to second period, 2.5 reinstatements, aggregate limit
     * is not limiting cover
     */
    void testAggregateDeductibleAndLimits() {
        ReinsuranceContract wxl = getWXLContract(100, 200, 1000, 500, 800, [0.6d], date20110101)
        PeriodScope periodScope = wxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-400), getBaseClaim(-400), getBaseClaim(-400),
                getBaseClaim(-400), getBaseClaim(-400)]
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, true)

        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wxl.inUnderwritingInfo.add(uw120)

        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimates', [0, 0, 100, 200, 200], wxl.outClaimsCeded*.ultimate()
        assertEquals 'P0.0 ceded incremental reported', [0, 0, 100, 200, 200], wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0.0 ceded incremental paids', [0d] * 5, wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0.0 ceded premium written', -800, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -800, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -800, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0.0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0.0 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded commission fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded commission variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded incremental paids', [0, 0, 0, 60, 140], wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded premium written', -480, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', -480, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', -480, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded incremental paids', [0, 0, 100, 140, 60], wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded premium written', -720, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', -720, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', -720, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded incremental paids', [0d] *5, wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded incremental paids', [0d] *5, wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    /**
     * multiple claims, aggregate deductible delays payments to second period, 1 reinstatement only, aggregate limit with effect
     */
    void testAggregateDeductibleAndLimits2() {
        ReinsuranceContract wxl = getWXLContract(100, 200, 400, 500, 800, [0.6d], date20110101)
        PeriodScope periodScope = wxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-400), getBaseClaim(-400), getBaseClaim(-400),
                getBaseClaim(-400), getBaseClaim(-400)]
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, true)

        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wxl.inUnderwritingInfo.add(uw120)

        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimates', [0, 0, 100, 200, 100], wxl.outClaimsCeded*.ultimate()
        assertEquals 'P0.0 ceded incremental reported', [0, 0, 100, 200, 100], wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P0.0 ceded incremental paids', [0d] * 5, wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P0.0 ceded premium written', -800, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -800, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -800, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0.0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0.0 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P1 ceded incremental paids', [0, 0, 0, 60, 140], wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P1 ceded premium written', -480, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', -480, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', -480, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P2 ceded incremental paids', [0, 0, 100, 100, 0], wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P2 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P3 ceded incremental paids', [0d] *5, wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P3 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals 'P4 ceded incremental paids', [0d] *5, wxl.outClaimsCeded*.paidIncrementalIndexed
        assertEquals 'P4 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    void testLimitBoundaryIndexApplied() {
        ReinsuranceContractIndex index = new ReinsuranceContractIndex(name: 'market')
        ReinsuranceContract wxl = getWXLContract(20, 20, 100, 0, 100, [0.2d], date20110101)
        wxl.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
            startCover: new DateTime(date20110101), numberOfMonths: 24])
        wxl.parmContractStrategy.boundaryIndex = XLBoundaryIndexType.getStrategy(XLBoundaryIndexType.INDEXED,
            [index: new ConstrainedMultiDimensionalParameter(
                [[index.name], [IndexMode.CONTINUOUS.toString()], [BaseDateMode.DATE_OF_LOSS.toString()], [new DateTime()]],
                ["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints(ReinsuranceContractIndexSelectionTableConstraints.IDENTIFIER)),
             indexedValues: XLBoundaryIndexApplication.LIMIT_AGGREGATE_LIMIT])
        wxl.parmContractStrategy.boundaryIndex.index.comboBoxValues.put(0, ['market': index])
        PeriodScope periodScope = wxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [new GrossClaimRoot(-50, ClaimType.SINGLE,
            date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)]
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
            exposure: new ExposureInfo(periodScope));
        wxl.inUnderwritingInfo.add(uw120)

        FactorsPacket indexFactorsPacket = ClaimsGeneratorSeverityIndexTests.getFactorsPacket(
            [ClaimsGeneratorSeverityIndexTests.date20100101, ClaimsGeneratorSeverityIndexTests.date20100701,
             ClaimsGeneratorSeverityIndexTests.date20110101, ClaimsGeneratorSeverityIndexTests.date20120101,
             ClaimsGeneratorSeverityIndexTests.date20130101, ClaimsGeneratorSeverityIndexTests.date20140101],
            [1, 1.02, 1.03, 1.06, 1.07, 1.1], index)
        wxl.inFactors.add(indexFactorsPacket)

        wxl.doCalculation()
        assertEquals 20.6, wxl.outClaimsCeded[0].ultimate()

        claimRoots << new GrossClaimRoot(-50, ClaimType.SINGLE,
            date20120101, date20120101, annualPayoutPattern, annualReportingPatternInclFirst)

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.inFactors.add(indexFactorsPacket)
        wxl.doCalculation()
        assertEqualsOnList "2012, ultimate", [21.2, 20.6], wxl.outClaimsCeded*.developedUltimate()
        assertEqualsOnList "2012, reported", [0, 10.0], wxl.outClaimsCeded*.reportedCumulatedIndexed
        assertEqualsOnList "2012, paid", [0, 0], wxl.outClaimsCeded*.paidCumulatedIndexed

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.inFactors.add(indexFactorsPacket)
        wxl.doCalculation()
        assertEqualsOnList "2013, ultimate", [21.2, 20.6], wxl.outClaimsCeded*.developedUltimate()
        assertEqualsOnList "2013, reported", [10, 20], wxl.outClaimsCeded*.reportedCumulatedIndexed
        assertEqualsOnList "2013, paid", [0, 15], wxl.outClaimsCeded*.paidCumulatedIndexed

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.inFactors.add(indexFactorsPacket)
        wxl.doCalculation()
        assertEqualsOnList "2013, ultimate", [21.2, 20.6], wxl.outClaimsCeded*.developedUltimate()
        assertEqualsOnList "2013, reported", [20, 20.6], wxl.outClaimsCeded*.reportedCumulatedIndexed
        assertEqualsOnList "2013, paid", [15, 20.6], wxl.outClaimsCeded*.paidCumulatedIndexed

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.inFactors.add(indexFactorsPacket)
        wxl.doCalculation()
        assertEqualsOnList "2013, ultimate", [21.2, 20.6], wxl.outClaimsCeded*.developedUltimate()
        assertEqualsOnList "2013, reported", [21.2, 20.6], wxl.outClaimsCeded*.reportedCumulatedIndexed
        assertEqualsOnList "2013, paid", [21.2, 20.6], wxl.outClaimsCeded*.paidCumulatedIndexed
    }

    void testAttachmentPointBoundaryIndexApplied() {
        ReinsuranceContractIndex index = new ReinsuranceContractIndex(name: 'market')
        ReinsuranceContract wxl = getWXLContract(20, 20, 100, 0, 100, [0.2d], date20110101)
        wxl.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
            startCover: new DateTime(date20110101), numberOfMonths: 24])
        wxl.parmContractStrategy.boundaryIndex = XLBoundaryIndexType.getStrategy(XLBoundaryIndexType.INDEXED,
            [index: new ConstrainedMultiDimensionalParameter(
                [[index.name], [IndexMode.CONTINUOUS.toString()], [BaseDateMode.DATE_OF_LOSS.toString()], [new DateTime()]],
                ["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints(ReinsuranceContractIndexSelectionTableConstraints.IDENTIFIER)),
             indexedValues: XLBoundaryIndexApplication.ATTACHMENT_POINT])
        wxl.parmContractStrategy.boundaryIndex.index.comboBoxValues.put(0, ['market': index])
        PeriodScope periodScope = wxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [new GrossClaimRoot(-50, ClaimType.SINGLE,
            date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)]
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
            exposure: new ExposureInfo(periodScope));
        wxl.inUnderwritingInfo.add(uw120)

        FactorsPacket indexFactorsPacket = ClaimsGeneratorSeverityIndexTests.getFactorsPacket(
            [ClaimsGeneratorSeverityIndexTests.date20100101, ClaimsGeneratorSeverityIndexTests.date20100701,
             ClaimsGeneratorSeverityIndexTests.date20110101, ClaimsGeneratorSeverityIndexTests.date20120101,
             ClaimsGeneratorSeverityIndexTests.date20130101, ClaimsGeneratorSeverityIndexTests.date20140101],
            [1, 1.02, 1.03, 1.06, 1.07, 1.1], index)
        wxl.inFactors.add(indexFactorsPacket)

        wxl.doCalculation()
        assertEquals 20, wxl.outClaimsCeded[0].ultimate()

        claimRoots << new GrossClaimRoot(-50, ClaimType.SINGLE,
            date20120101, date20120101, annualPayoutPattern, annualReportingPatternInclFirst)

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.inFactors.add(indexFactorsPacket)
        wxl.doCalculation()
        assertEqualsOnList "2012, ultimate", [20, 20], wxl.outClaimsCeded*.developedUltimate()
        assertEqualsOnList "2012, reported", [0, 9.4], wxl.outClaimsCeded*.reportedCumulatedIndexed
        assertEqualsOnList "2012, paid", [0, 0], wxl.outClaimsCeded*.paidCumulatedIndexed

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.inFactors.add(indexFactorsPacket)
        wxl.doCalculation()
        assertEqualsOnList "2013, ultimate", [20, 20], wxl.outClaimsCeded*.developedUltimate()
        assertEqualsOnList "2013, reported", [8.8, 19.4], wxl.outClaimsCeded*.reportedCumulatedIndexed
        assertEqualsOnList "2013, paid", [0.0, 14.4], wxl.outClaimsCeded*.paidCumulatedIndexed

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.inFactors.add(indexFactorsPacket)
        wxl.doCalculation()
        assertEqualsOnList "2013, ultimate", [20, 20], wxl.outClaimsCeded*.developedUltimate()
        assertEqualsOnList "2013, reported", [18.8, 20.0], wxl.outClaimsCeded*.reportedCumulatedIndexed
        assertEqualsOnList "2013, paid", [13.8, 20.0], wxl.outClaimsCeded*.paidCumulatedIndexed

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.inFactors.add(indexFactorsPacket)
        wxl.doCalculation()
        assertEqualsOnList "2013, ultimate", [20, 20], wxl.outClaimsCeded*.developedUltimate()
        assertEqualsOnList "2013, reported", [20, 20], wxl.outClaimsCeded*.reportedCumulatedIndexed
        assertEqualsOnList "2013, paid", [20, 20], wxl.outClaimsCeded*.paidCumulatedIndexed
    }

    private GrossClaimRoot getBaseClaim(double ultimate) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, ClaimType.SINGLE,
                date20110418, date20110418, annualPayoutPattern2, annualFastReportingPattern)
        return claimRoot
    }

    public static void addClaimCashflowOfCurrentPeriod(ReinsuranceContract wxl, List<GrossClaimRoot> baseClaims,
                                                 IPeriodCounter periodCounter, boolean firstPeriod) {
        for (GrossClaimRoot baseClaim : baseClaims) {
            List<ClaimCashflowPacket> claims = baseClaim.getClaimCashflowPackets(periodCounter)
            wxl.inClaims.addAll(claims)
        }
    }

    public static void assertEqualsOnList(String message, List<Number> expected, List<Number> actual, Double epsilon = 1E-8) {
        for (int i = 0; i < expected.size(); i++) {
            if (Math.abs(expected[i] - actual[i]) > epsilon) {
                failNotEquals(message, expected, actual)
                return
            }
        }
    }


}
