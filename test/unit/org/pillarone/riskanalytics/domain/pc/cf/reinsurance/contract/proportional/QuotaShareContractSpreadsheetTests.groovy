package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.grails.plugins.excelimport.ExpectedPropertyType
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractSpreadsheetTests extends SpreadsheetUnitTest {

    void doSetUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }

    @Override
    List<String> getSpreadsheetNames() {
        ["QuotaShare.xlsx", "QuotaShareInvalidData.xlsx"]
    }

    void testUsage() {
        SpreadsheetImporter importer = importers[0]
        ReinsuranceContract contract = getQuotaShareContract(importer)
        contract.inClaims.addAll getClaims(importer, 'GrossClaims')
        contract.doCalculation()
        List<ClaimCashflowPacket> cededClaims = getClaims(importer, 'CededClaims')

        assertEquals "[${importer.fileName}] correct ceded ultimates", cededClaims*.ultimate, contract.outClaimsCeded*.ultimate
        assertEquals "[${importer.fileName}] correct ceded reportedIncrementalIndexed", cededClaims*.reportedIncrementalIndexed, contract.outClaimsCeded*.reportedCumulatedIndexed
        assertEquals "[${importer.fileName}] correct ceded paidIncrementalIndexed", cededClaims*.paidIncrementalIndexed, contract.outClaimsCeded*.paidIncrementalIndexed

        manageValidationErrors(importer)
    }

    void testValidationErrors() {
        SpreadsheetImporter importer = importers[1]
        getClaims(importer, 'GrossClaims')

        shouldFail {
            manageValidationErrors(importer)
        }
    }

    ReinsuranceContract getQuotaShareContract(SpreadsheetImporter importer) {
        Map contractParams = importer.cells([sheet: 'ReinsuranceContract', cellMap: ['B2': 'quote']])
        QuotaShareContractTests.getQuotaShareContract(contractParams.quote, 10000, beginOfCover)
    }

    private Map claimsSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 1, // startRow counting starts at 0
                columnMap: ['B' : 'ultimate', 'C' : 'occurrenceDate', 'D' : 'peril']
        ]
    }

    private static Map CLAIMS_VALIDATION = [
            ultimate:([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            occurrenceDate : ([expectedType: ExpectedPropertyType.DateType, defaultValue: new LocalDate(2012,1,1)]),
            peril : ([expectedType: ExpectedPropertyType.StringType, defaultValue: null])
    ]

    List<ClaimCashflowPacket> getClaims(SpreadsheetImporter importer, String sheet) {
        List claimsMap = importer.columns(claimsSheetStructure(sheet), CLAIMS_VALIDATION)
        List<ClaimCashflowPacket> claims = []
        for (Map claim : claimsMap) {
            DateTime date = claim.occurrenceDate.toDateTimeAtStartOfDay()
            GrossClaimRoot baseClaim = new GrossClaimRoot(claim.ultimate, ClaimType.AGGREGATED, date,
                    date, trivialPayoutPattern, trivialReportingPattern)
            claims.addAll baseClaim.getClaimCashflowPackets(iterationScope.periodScope.periodCounter, true)
        }
        claims
    }

    DateTime beginOfCover = new DateTime(2012,1,1,0,0,0,0)
    PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class);
    PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);
    IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
}
