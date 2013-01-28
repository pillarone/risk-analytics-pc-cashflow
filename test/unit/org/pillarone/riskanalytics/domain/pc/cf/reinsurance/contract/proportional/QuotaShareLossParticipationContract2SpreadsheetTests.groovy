package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import com.google.common.collect.HashMultimap
import com.google.common.collect.SetMultimap
import org.apache.commons.lang.NotImplementedException
import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.*
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.InterpolatedSlidingCommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.ILossParticipationStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import static org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.TestReferenceClaimKey.format

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareLossParticipationContract2SpreadsheetTests extends SpreadsheetUnitTest {

    public static final String SLIDING_IN__TRIANGLE = 'Sliding_in_Triangle'

    void doSetUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }

    @Override
    List<String> getSpreadsheetNames() {
        [
         'PMO2292__LP___SSC_LP_in_the_run_off.xlsx',
         'PMO-2198_a___SSC_usual.xlsx',
         'PMO-2198_a___SSC_usual_1.xlsx',
         'PMO-2198_a___SSC_usual_2.xlsx',
         'PMO-2198_a___SSC_usual_3.xlsx',                 //C247
         'PMO-2198_a___SSC_usual_4.xlsx',                 //C247
         'PMO-2198_a___SSC_usual_5.xlsx',                 //C247
         'PMO-2198_a___SSC_usual_6.xlsx',
         'PMO-2198_a___SSC_usual_7.xlsx',                   //C247, C209
         'PMO-2198_b___SSC_decreasing_losses_1.xlsx',  //C247
         'PMO-2198_b___SSC_decreasing_losses_2.xlsx',
         'PMO-2198_b___SSC_decreasing_losses_3.xlsx',     //C247
         'PMO-2198_b___SSC_decreasing_losses_4.xlsx',       //C247
         'PMO-2198_b___SSC_decreasing_losses_5.xlsx',
         'PMO-2198_b___SSC_decreasing_losses_6.xlsx',
         'PMO-2198_b___SSC_decreasing_losses_7.xlsx',
         'PMO-2198_c___SSC_usual_LP100_5.xlsx',
         'PMO-2198_c___SSC_usual_LP100_6.xlsx',
         'PMO-2198_c___SSC_usual_LP100_7.xlsx',
         'PMO-2198_d___SSC_usual_AAD_AAL_1.xlsx',
         'PMO-2198_d___SSC_usual_AAD_AAL_2.xlsx',
         'PMO-2198_d___SSC_usual_AAD_AAL_3.xlsx',
         'PMO-2198_d___SSC_usual_AAD_AAL_4.xlsx',
         'PMO-2198_d___SSC_usual_AAD_AAL_5.xlsx',
         'PMO-2198_d___SSC_usual_AAD_AAL_6.xlsx',
         'PMO-2198_d___SSC_usual_AAD_AAL_7.xlsx',
         'PMO-2198_e___SSC_decreasing_losses_LP100_5.xlsx',
         'PMO-2198_e___SSC_decreasing_losses_LP100_6.xlsx',
         'PMO-2198_e___SSC_decreasing_losses_LP100_7.xlsx',
         'PMO-2198_f___SSC_no_increments_4.xlsx',
         'PMO-2198_g___SSC_decreasing_losses_LP100_AADAAL_7.xlsx',
         'PMO-2198_h___SSC_decreasing_losses_LP100_AADAAL_100PercentQ_7.xlsx',
        ]
    }

    void testUsage() {
        // enable the following line while writing/debugging the test case but comment it out before committing!
//        setCheckedForValidationErrors(true)
        for (SpreadsheetImporter importer : importers) {
            ReinsuranceContract contract = getQuotaShareContract(importer, 'Sliding_in_Triangle')
            IterationScope iterationScope = contract.iterationScope
            TestTriangle grossClaims = new TestTriangle(importer, SLIDING_IN__TRIANGLE, 'gross claims', 2012, 4, 'C10')
            Map<Integer, GrossClaimRoot> claimsByPeriod = getClaimsByUnderwritingPeriod(grossClaims)
            TestTriangle cededClaims = new TestTriangle(importer, SLIDING_IN__TRIANGLE, 'ceded claims', 2012, 4, 'C247')
            TestTriangle grossPremium = new TestTriangle(importer, SLIDING_IN__TRIANGLE, 'gross premium', 2012, 4, 'C16')
            IPeriodCounter periodCounter = iterationScope.periodScope.periodCounter
            SetMultimap<Integer, UnderwritingInfoPacket> grossUwInfoByPeriod = initGrossPremium(grossPremium, periodCounter)
            TestTriangle totalCommission = new TestTriangle(importer, SLIDING_IN__TRIANGLE, 'total commission', 2012, 4, 'C209', 1, true)
            TestTriangle fixCommission = new TestTriangle(importer, SLIDING_IN__TRIANGLE, 'fix commission', 2012, 4, 'C215', 1, true)
            for (int period = 2012; period <= grossClaims.maxYear(); period++) {
                for (int i = 2012; i <= period; i++) {
                    contract.inClaims.addAll claimsByPeriod.get(i).getClaimCashflowPackets(periodCounter)
                }
                contract.inUnderwritingInfo.addAll grossUwInfoByPeriod.get(period)
                contract.doCalculation()
                for (ClaimCashflowPacket cededClaim : contract.outClaimsCeded) {
                    double referenceValue = cededClaims.referenceValue(cededClaim)
//                    if (Math.abs(referenceValue - cededClaim.paidCumulatedIndexed) > EPSILON) {
//                        println "[${importer.fileName}] correct ceded paid ${format(cededClaim.occurrenceDate)} ${format(cededClaim.updateDate)} ${referenceValue} ${cededClaim.paidCumulatedIndexed}"
//                    }
                    assertEquals "[${importer.fileName}] correct ceded paid ${format(cededClaim.occurrenceDate)} ${format(cededClaim.updateDate)}", cededClaims.referenceValue(cededClaim), cededClaim.paidCumulatedIndexed, EPSILON
                }
                for (CededUnderwritingInfoPacket cededUwInfo : contract.outUnderwritingInfoCeded) {
                    double referenceTotalCommission = totalCommission.referenceValue(cededUwInfo)
                    if (Math.abs(referenceTotalCommission - cededUwInfo.commission) > EPSILON) {
                        println "[${importer.fileName}] correct total commission ${format(cededUwInfo.exposure.inceptionDate)} ${format(cededUwInfo.date)} ${referenceTotalCommission} ${cededUwInfo.commission}"
                    }
                    assertEquals "[${importer.fileName}] correct total commission ${format(cededUwInfo.exposure.inceptionDate)} ${format(cededUwInfo.date)}", referenceTotalCommission, cededUwInfo.commission, EPSILON
                    double referenceFixCommission = fixCommission.referenceValue(cededUwInfo)
//                    if (Math.abs(referenceFixCommission - cededUwInfo.commissionFixed) > EPSILON) {
//                        println "[${importer.fileName}] correct fix commission ${format(cededUwInfo.exposure.inceptionDate)} ${format(cededUwInfo.date)} ${referenceFixCommission} ${cededUwInfo.commissionFixed}"
//                    }
                    assertEquals "[${importer.fileName}] correct fix commission ${format(cededUwInfo.exposure.inceptionDate)} ${format(cededUwInfo.date)}", referenceFixCommission, cededUwInfo.commissionFixed, EPSILON
                }
                contract.reset()
                iterationScope.periodScope.prepareNextPeriod()
            }
            manageValidationErrors(importer)
        }
    }

    private ReinsuranceContract getQuotaShareContract(SpreadsheetImporter importer, String sheet) {
        Map contractParams = importer.cells([sheet: sheet, cellMap: ['C22': 'quote', 'C25': 'aad', 'D25': 'aal']])
        ICommissionStrategy commission = getCommission(importer, sheet)
        ILossParticipationStrategy participation = getLossParticipation(importer, sheet)
        QuotaShareContractTests.getQuotaShareContractAADAALLimit(contractParams.quote, contractParams.aad ?: 0,
                contractParams.aal, beginOfCover, 4,
                participation, commission)
    }

    private static ILossParticipationStrategy getLossParticipation(SpreadsheetImporter importer, String sheet) {
        List<Double> fromLR = getColumnValues(importer, sheet, 'D', 46, 4)
        List<Double> lossPartByCedant = getColumnValues(importer, sheet, 'E', 46, 4)
        int lossParticipationListLength = Math.max(lastElementNotZero(fromLR), lastElementNotZero(lossPartByCedant))
        return LossParticipationStrategyType.getStrategy(LossParticipationStrategyType.LOSSPARTICIPATION,
                [participation: new ConstrainedMultiDimensionalParameter(
                        [fromLR.subList(0, lossParticipationListLength), lossPartByCedant.subList(0, lossParticipationListLength)],
                        [LossParticipationStrategy.LOSS_RATIO, LossParticipationStrategy.LOSS_PART_BY_CEDANT],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
    }

    /**
     * @param importer
     * @param sheet
     * @param column
     * @param startRow
     * @param numberOfRows
     * @return all values of $column$startRow:$column$(startRow+numberOfRows), missing values are mapped to zero
     */
    public static List<Double> getColumnValues(SpreadsheetImporter importer, String sheet, String column, int startRow, int numberOfRows) {
        Map cellMap = [:]
        for (int row = 0; row < numberOfRows; row++) {
            String cell = "$column${startRow+row}"
            cellMap[cell] = cell
        }
        Map params = importer.cells([sheet: sheet, cellMap: cellMap])
        List result = new ArrayList<Double>()
        for (String cell : cellMap.keySet()) {
            result << (params[cell] ? params[cell] : 0d)
        }
        return result
    }

    /**
     * @param importer
     * @param sheet
     * @param column
     * @param startRow
     * @param numberOfRows
     * @return all values of $column$startRow:$column$(startRow+numberOfRows), missing values are mapped to zero
     */
    public static List<Double> getRowValues(SpreadsheetImporter importer, String sheet, Character startColumn, int row, int numberOfColumns) {
        Map cellMap = [:]
        Character col = startColumn
        for (int column = 0; column < numberOfColumns; column++) {
            String cell = "${col}${row}"
            cellMap[cell] = cell
            col = col.next()
        }
        Map params = importer.cells([sheet: sheet, cellMap: cellMap])
        List result = new ArrayList<Double>()
        for (String cell : cellMap.keySet()) {
            result << (params[cell] ? params[cell] : 0d)
        }
        return result
    }

    /**
     * Useful for determining limits of sublists
     * @param values
     * @return last element of the list not equal to zero
     */
    public static int lastElementNotZero(List<Double> values) {
        int lastNonTrivialValue = 0
        for (int i = 0; i < values.size(); i++) {
            if (values[i] != null) {
                lastNonTrivialValue = i + 1
            }
        }
        return lastNonTrivialValue
    }

    private ICommissionStrategy getCommission(SpreadsheetImporter importer, String sheet) {
        Map commissionParams = importer.cells([sheet: 'Sliding_in_Triangle', cellMap: [
                'C203': 'commissionType', 'C151': 'commissionRatioFixed', 'C152': 'costRatioOfPrimaryInsurer',
                'C153': 'profitCommissionRatio', 'C162': 'initialLossCarryForward', 'C171': 'useLossCarryForward',
                'C86': 'fixCommission'
        ]])
        if (commissionParams.commissionType == 'fix') {
            return CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION,
                    ['commission': commissionParams.fixCommission])
        }
        else if (commissionParams.commissionType == 'profit') {
            return CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION,
                    ['profitCommissionRatio': commissionParams.profitCommissionRatio,
                     'commissionRatio': commissionParams.commissionRatioFixed,
                     'costRatio': commissionParams.costRatioOfPrimaryInsurer,
                     'lossCarriedForwardEnabled': commissionParams.useLossCarryForward == 'yes',
                     'initialLossCarriedForward': commissionParams.initialLossCarryForward,
                     'useClaims': BasedOnClaimProperty.PAID])
        }
        else if (commissionParams.commissionType == 'sliding') {
            List<Double> fromLR = getColumnValues(importer, sheet, 'D', 100, 4)
            List<Double> commission = getColumnValues(importer, sheet, 'E', 100, 4)
            int listLength = Math.max(lastElementNotZero(fromLR), lastElementNotZero(commission))
            return CommissionStrategyType.getStrategy(CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION,
                   ['commissionBands': new ConstrainedMultiDimensionalParameter(
                    [fromLR.subList(0, listLength), commission.subList(0, listLength)],
                    [InterpolatedSlidingCommissionStrategy.LOSS_RATIO, InterpolatedSlidingCommissionStrategy.COMMISSION],
                    ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),
                    'useClaims': BasedOnClaimProperty.PAID])
        }
        throw new NotImplementedException()
    }

    private static Map<Integer, GrossClaimRoot> getClaimsByUnderwritingPeriod(TestTriangle triangle) {
        Map<Integer, GrossClaimRoot> claimsByOccurrenceYear = [:]
        for (DateTime startOfUnderwritingPeriod : triangle.underwritingPeriodStartDates) {
            List<Period> cummulativePeriods = triangle.cummulativePeriods(startOfUnderwritingPeriod)
            List<Double> cummulativeValues = triangle.valuesBy(startOfUnderwritingPeriod)
            List<Double> cummulativeRatios = []
            for (Double value : cummulativeValues) {
                cummulativeRatios << value / triangle.latestValue(startOfUnderwritingPeriod)
            }
            PatternPacket payoutPattern = new PatternPacket(IPayoutPatternMarker, cummulativeRatios, cummulativePeriods)
            claimsByOccurrenceYear[startOfUnderwritingPeriod.year] = new GrossClaimRoot(
                    new ClaimRoot(triangle.latestValue(startOfUnderwritingPeriod), ClaimType.ATTRITIONAL,
                            startOfUnderwritingPeriod, startOfUnderwritingPeriod), payoutPattern)
        }
        return claimsByOccurrenceYear
    }

    private DateTime beginOfCover = new DateTime(2012,1,1,0,0,0,0)

    private static final double EPSILON = 1E-8

    private SetMultimap<Integer, UnderwritingInfoPacket> initGrossPremium(TestTriangle triangle, IPeriodCounter periodCounter) {
        SetMultimap<Integer, UnderwritingInfoPacket> grossUwPerUwYear = HashMultimap.create()
        int inceptionPeriod = 0
        for (DateTime calendarYearStartDate : triangle.underwritingPeriodStartDates) {
            for (int uwPeriod = 0; uwPeriod <= inceptionPeriod; uwPeriod++) {
                DateTime startOfUnderwritingPeriod = triangle.underwritingPeriodStartDates[uwPeriod]
                ExposureInfo exposureInfo = new ExposureInfo(startOfUnderwritingPeriod, periodCounter)
                double premiumWritten = uwPeriod == inceptionPeriod ? triangle.latestValue(startOfUnderwritingPeriod) : 0
                double cumulatedPremiumPaid = triangle.valuesBy(startOfUnderwritingPeriod)[inceptionPeriod - uwPeriod]
                double previousPremiumPaid = uwPeriod != inceptionPeriod ? triangle.valuesBy(startOfUnderwritingPeriod)[inceptionPeriod - uwPeriod - 1] : 0d
                grossUwPerUwYear.put(calendarYearStartDate.year, new UnderwritingInfoPacket(
                    premiumWritten: premiumWritten,
                    premiumPaid: cumulatedPremiumPaid - previousPremiumPaid,
                    inceptionPeriod: uwPeriod,
                    exposure: exposureInfo,
                    date: calendarYearStartDate))
            }
            inceptionPeriod++
        }
        grossUwPerUwYear
    }

}
