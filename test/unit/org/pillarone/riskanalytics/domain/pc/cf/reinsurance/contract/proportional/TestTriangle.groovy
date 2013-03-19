package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TestTriangle {
    String name
    List<DateTime> underwritingPeriodStartDates = []
    List<DateTime> developmentPeriodStartDates = []
    private Map<TestReferenceClaimKey, Double> values = [:]
    private Map<DateTime, List<Double>> valuesByUnderwritingYear = [:]
    private Map<DateTime, List<Period>> periodsByUnderwritingYear = [:]

    TestTriangle(SpreadsheetImporter importer, String sheet, String name, int firstYear, int numberOfYears, String topLeftCell, int sign = 1, boolean buildIncrements = false) {
        this(importer, sheet, name, firstYear, numberOfYears, numberOfYears, topLeftCell, sign, buildIncrements)
    }

    TestTriangle(SpreadsheetImporter importer, String sheet, String name, int firstYear, int numberOfUnderwritingYears,
                 int numberOfDevelopmentYears, String topLeftCell, int sign = 1, boolean buildIncrements = false) {
        this.name = name
        Character column = topLeftCell[0]
        int row = Integer.parseInt(topLeftCell.substring(1))
        for (int i = 0; i < numberOfDevelopmentYears; i++) {
            DateTime updateDate = new DateTime(firstYear + i, 1, 1, 0, 0, 0, 0)
            developmentPeriodStartDates << updateDate
        }
        for (int i = 0; i < numberOfUnderwritingYears; i++) {
            List<Double> rowValues = getRowValues(importer, sheet, column, row + i, numberOfDevelopmentYears - i, sign)
            DateTime occurrenceDate = new DateTime(firstYear + i, 1, 1, 0, 0, 0, 0)
            valuesByUnderwritingYear[occurrenceDate] = rowValues
            underwritingPeriodStartDates << occurrenceDate
            for (int j = 0; j < rowValues.size(); j++) {
                DateTime updateDate = new DateTime(firstYear + i + j, 1, 1, 0, 0, 0, 0)
                if (j > 0 && buildIncrements) {
                    values[new TestReferenceClaimKey(occurrenceDate, updateDate)] = rowValues[j] - rowValues[j - 1]
                } else {
                    values[new TestReferenceClaimKey(occurrenceDate, updateDate)] = rowValues[j]
                }
            }
            column = column.next()
        }
    }

    void add(DateTime inceptionDate, DateTime updateDate, double value) {
        values.put(new TestReferenceClaimKey(inceptionDate, updateDate), value)
    }

    boolean sameValue(DateTime inceptionDate, DateTime updateDate, double value) {
        value == values.get(new TestReferenceClaimKey(inceptionDate, updateDate))
    }

    double referenceValue(ClaimCashflowPacket claim) {
        values.get(new TestReferenceClaimKey(claim.occurrenceDate, claim.updateDate))
    }

    double referenceValue(UnderwritingInfoPacket uwInfo) {
        values.get(new TestReferenceClaimKey(uwInfo.exposure.inceptionDate, uwInfo.date))
    }

    List<Period> cummulativePeriods(DateTime underwritingPeriodStartDate) {
        List<Period> periods = periodsByUnderwritingYear[underwritingPeriodStartDate]
        if (periods == null) {
            periods = []
            DateTime firstPeriodStartDate
            for (DateTime periodStartDate : developmentPeriodStartDates) {
                if (!periodStartDate.isBefore(underwritingPeriodStartDate)) {
                    if (firstPeriodStartDate) {
                        periods << new Period(firstPeriodStartDate, periodStartDate)
                    } else {
                        firstPeriodStartDate = periodStartDate
                        periods << Period.years(0)
                    }
                }
            }
            periodsByUnderwritingYear[underwritingPeriodStartDate] = periods
        }
        return periods
    }

    List<Double> valuesBy(DateTime underwritingPeriodStartDate) {
        valuesByUnderwritingYear[underwritingPeriodStartDate]
    }

    double latestValue(DateTime underwritingPeriodStartDate) {
        valuesByUnderwritingYear[underwritingPeriodStartDate][-1]
    }

    int numberOfUnderwritingPeriods() {
        underwritingPeriodStartDates.size()
    }

    Integer maxYear() {
        developmentPeriodStartDates[-1].year
    }

    /**
     * @param importer
     * @param sheet
     * @param column
     * @param startRow
     * @param numberOfRows
     * @return all values of $column$startRow:$column$(startRow+numberOfRows), missing values are mapped to zero
     */
    List<Double> getRowValues(SpreadsheetImporter importer, String sheet, Character startColumn, int row, int numberOfColumns, int sign = 1) {
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
            result << (params[cell] ? sign * params[cell] : 0d)
        }
        return result
    }

    Map<Integer, GrossClaimRoot> getClaimsByUnderwritingPeriod() {
        Map<Integer, GrossClaimRoot> claimsByOccurrenceYear = [:]
        for (DateTime startOfUnderwritingPeriod : underwritingPeriodStartDates) {
            List<Period> cummulativePeriods = cummulativePeriods(startOfUnderwritingPeriod)
            List<Double> cummulativeValues = valuesBy(startOfUnderwritingPeriod)
            List<Double> cummulativeRatios = []
            for (Double value : cummulativeValues) {
                cummulativeRatios << value / latestValue(startOfUnderwritingPeriod)
            }
            PatternPacket payoutPattern = new PatternPacket(IPayoutPatternMarker, cummulativeRatios, cummulativePeriods)
            claimsByOccurrenceYear[startOfUnderwritingPeriod.year] = new GrossClaimRoot(
                    new ClaimRoot(latestValue(startOfUnderwritingPeriod), ClaimType.ATTRITIONAL,
                            startOfUnderwritingPeriod, startOfUnderwritingPeriod), payoutPattern)
        }
        return claimsByOccurrenceYear
    }


}
