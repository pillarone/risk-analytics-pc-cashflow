package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.apache.commons.lang.builder.HashCodeBuilder

import org.joda.time.DateTime

import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot

import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionBase
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import org.pillarone.riskanalytics.domain.test.SpreadsheetUnitTest
import org.pillarone.riskanalytics.domain.pc.cf.pattern.*
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.grails.plugins.excelimport.ExpectedPropertyType

import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import com.google.common.collect.HashMultimap
import com.sun.xml.internal.bind.v2.schemagen.MultiMap
import com.google.common.collect.SetMultimap
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.ILossParticipationStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategy
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty
import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareLossParticipationContractSpreadsheetTests extends SpreadsheetUnitTest {

    void doSetUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }

    @Override
    List<String> getSpreadsheetNames() {
        ["PMO-2198-SSC_LP_in_the_run_off.xlsx"]
    }

    void testUsage() {
        // enable the following line while writing/debugging the test case but comment it out before committing!
//        setCheckedForValidationErrors(true)
        SpreadsheetImporter importer = importers[0]
        ReinsuranceContract contract = getQuotaShareContract(importer, 'Sliding_in_Triangle')
        IterationScope iterationScope = contract.iterationScope
        Map<Integer, GrossClaimRoot> claimsByPeriod = getGrossClaims(importer, 'Sliding_in_Triangle')
        initCededReferenceClaims(importer, 'CededClaim')
        SetMultimap<Integer, UnderwritingInfoPacket> grossUwInfoByPeriod = initGrossPremium(importer, 'GrossPremium', iterationScope.periodScope.periodCounter)
        initCededReferenceUnderwritingInfo(importer, 'Commission')
        int maxPeriod = 2015
        for (int period = 2012; period <= maxPeriod; period++) {
            for (int i = 2012; i <= period; i++) {
                contract.inClaims.addAll claimsByPeriod.get(i).getClaimCashflowPackets(iterationScope.periodScope.periodCounter)
            }
            contract.inUnderwritingInfo.addAll grossUwInfoByPeriod.get(period)
            contract.doCalculation()
            for (ClaimCashflowPacket cededClaim : contract.outClaimsCeded) {
                ReferenceClaim referenceClaim = cededReferenceClaims.get(cededClaim)
                if (Math.abs(referenceClaim.paid - cededClaim.paidCumulatedIndexed) > EPSILON) {
                    println "[${importer.fileName}] correct ceded paid ${referenceClaim.dateSummary()} ${referenceClaim.paid}, ${cededClaim.paidCumulatedIndexed}"
                }
                assertEquals "[${importer.fileName}] correct ceded paid ${referenceClaim.dateSummary()}", referenceClaim.paid, cededClaim.paidCumulatedIndexed, EPSILON
            }
            for (CededUnderwritingInfoPacket cededUwInfo : contract.outUnderwritingInfoCeded) {
                ReferenceUwInfo referenceUwInfo = cededReferenceUnderwritingInfos.get(cededUwInfo)
                if (Math.abs(referenceUwInfo.commission - cededUwInfo.commission) > EPSILON) {
                    println "[${importer.fileName}] commission total ${cededUwInfo.exposure.inceptionDate} ${cededUwInfo.commission}"
                }
//                assertEquals "[${importer.fileName}] correct commission ${referenceUwInfo.dateSummary()}", referenceUwInfo.commission, cededUwInfo.commission, EPSILON
            }
            contract.reset()
            iterationScope.periodScope.prepareNextPeriod()
        }
        manageValidationErrors(importer)
    }

    private ReinsuranceContract getQuotaShareContract(SpreadsheetImporter importer, String sheet) {
        Map contractParams = importer.cells([sheet: sheet, cellMap: ['C22': 'quote', 'C25': 'aad', 'D25': 'aal']])
        ILossParticipationStrategy lossParticipation = LossParticipationStrategyType.getStrategy(LossParticipationStrategyType.LOSSPARTICIPATION,
                [participation: new ConstrainedMultiDimensionalParameter(
                        [[0d, 0.6d, 0.7d, 0.8d], [1d, 0.5d, 0.5d, 1d]],
                        [LossParticipationStrategy.LOSS_RATIO, LossParticipationStrategy.LOSS_PART_BY_CEDANT],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))])
        QuotaShareContractTests.getQuotaShareContractAADAALLimit(contractParams.quote, contractParams.aad ?: 0,
                contractParams.aal, beginOfCover, 4, lossParticipation, getCommission(importer, sheet))
    }

    private ICommissionStrategy getCommission(SpreadsheetImporter importer, String sheet) {
        Map commissionParams = importer.cells([sheet: 'Sliding_in_Triangle', cellMap: [
                'C177': 'commissionType', 'C140': 'commissionRatioFixed', 'C141': 'costRatioOfPrimaryInsurer',
                'C142': 'profitCommissionRatio', 'C151': 'initialLossCarryForward', 'C160': 'useLossCarryForward',
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
                     'useClaims': CommissionBase.PAID])
        }
        else if (commissionParams.commissionType == 'sliding') {
            throw new NotImplementedException()
        }
        throw new NotImplementedException()
    }

    private Map claimsSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 2, // startRow counting starts at 0
                columnMap: ['A' : 'period', 'B' : 'event', 'C' : 'eventDate', 'D' : 'occurrenceDate', 'E': 'inceptionDate', 'F' : 'ultimate']
        ]
    }


    private Map<Integer, GrossClaimRoot> getGrossClaims(SpreadsheetImporter importer, String sheet) {
        Map<Integer, GrossClaimRoot> claimsByOccurrenceYear = [:]

        Map claimUY2012 = importer.cells([sheet: sheet, cellMap: ['C10': 'CY2012', 'D10': 'CY2013', 'E10': 'CY2014', 'F10': 'CY2015']])
        Map claimUY2013 = importer.cells([sheet: sheet, cellMap: ['D11': 'CY2013', 'E11': 'CY2014', 'F11': 'CY2015']])
        Map claimUY2014 = importer.cells([sheet: sheet, cellMap: ['E12': 'CY2014', 'F12': 'CY2015']])
        Map claimUY2015 = importer.cells([sheet: sheet, cellMap: ['F13': 'CY2015']])

        DateTime date = new DateTime(2012,1,1,0,0,0,0)
        List<Period> cummulativePeriods = [Period.years(0), Period.years(1), Period.years(2), Period.years(3)]
        List<Double> cummulativeValues = [claimUY2012.CY2012 / claimUY2012.CY2015, claimUY2012.CY2013 / claimUY2012.CY2015, claimUY2012.CY2014 / claimUY2012.CY2015, 1d]
        PatternPacket payoutPattern = new PatternPacket(IPayoutPatternMarker, cummulativeValues, cummulativePeriods)
        claimsByOccurrenceYear.put(2012, new GrossClaimRoot(new ClaimRoot(claimUY2012.CY2015, ClaimType.ATTRITIONAL, date, date), payoutPattern))

        date = date.plusYears(1)
        cummulativePeriods = [Period.years(0), Period.years(1), Period.years(2)]
        cummulativeValues = [claimUY2013.CY2013 / claimUY2013.CY2015, claimUY2013.CY2014 / claimUY2013.CY2015, 1d]
        payoutPattern = new PatternPacket(IPayoutPatternMarker, cummulativeValues, cummulativePeriods)
        claimsByOccurrenceYear.put(2013, new GrossClaimRoot(new ClaimRoot(claimUY2013.CY2015, ClaimType.ATTRITIONAL, date, date), payoutPattern))

        date = date.plusYears(1)
        cummulativePeriods = [Period.years(0), Period.years(1), Period.years(2)]
        cummulativeValues = [claimUY2014.CY2014 / claimUY2014.CY2015, 1d]
        payoutPattern = new PatternPacket(IPayoutPatternMarker, cummulativeValues, cummulativePeriods)
        claimsByOccurrenceYear.put(2014, new GrossClaimRoot(new ClaimRoot(claimUY2014.CY2015, ClaimType.ATTRITIONAL, date, date), payoutPattern))

        date = date.plusYears(1)
        payoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker)
        claimsByOccurrenceYear.put(2015, new GrossClaimRoot(new ClaimRoot(claimUY2015.CY2015, ClaimType.ATTRITIONAL, date, date), payoutPattern))

        claimsByOccurrenceYear
    }

    private Map cededClaimsSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 1, // startRow counting starts at 0
                columnMap: ['B' : 'CY2012', 'C' : 'CY2013', 'D' : 'CY2014', 'E' : 'CY2015']
        ]
    }

    private Map cededCommissionSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 1, // startRow counting starts at 0
                columnMap: ['B' : 'CY2012', 'C' : 'CY2013', 'D' : 'CY2014', 'E' : 'CY2015']
        ]
    }

    private Map grossPremiumSheetStructure(String sheet) {
        [
                sheet: sheet, startRow: 1, // startRow counting starts at 0
                columnMap: ['B' : 'CY2012', 'C' : 'CY2013', 'D' : 'CY2014', 'E' : 'CY2015']
        ]
    }


    private DateTime beginOfCover = new DateTime(2012,1,1,0,0,0,0)

    private ReferenceClaimContainer cededReferenceClaims
    private ReferenceUwInfoContainer cededReferenceUnderwritingInfos

    private static final double EPSILON = 1E-8

    private static Map TRIANGLE_VALIDATION = [
            CY2012: ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            CY2013: ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            CY2014: ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d]),
            CY2015: ([expectedType: ExpectedPropertyType.DoubleType, defaultValue: 0d])
    ]

    private void initCededReferenceClaims(SpreadsheetImporter importer, String sheet) {
        cededReferenceClaims = new ReferenceClaimContainer()
        List claimsMap = importer.columns(cededClaimsSheetStructure(sheet), TRIANGLE_VALIDATION)
        int currentYear = beginOfCover.getYear()
        for (Map claim : claimsMap) {
            DateTime date = new DateTime(beginOfCover).plusYears(currentYear - beginOfCover.getYear())
            int period = 0
            double cededUltimate = 0
            if (currentYear == 2012) cededUltimate += claim.CY2012
            if (currentYear <= 2013) cededUltimate += claim.CY2013
            if (currentYear <= 2014) cededUltimate += claim.CY2014
            if (currentYear <= 2015) cededUltimate += claim.CY2015
            if (currentYear == 2012) {
                cededReferenceClaims.add(
                    new ReferenceClaim(date, date, date, date.plusYears(period++), cededUltimate, cededUltimate, claim.CY2012)
                )
            }
            if (currentYear <= 2013) {
                cededReferenceClaims.add(
                    new ReferenceClaim(date, date, date, date.plusYears(period++), cededUltimate, cededUltimate, claim.CY2013)
                )
            }
            if (currentYear <= 2014) {
                cededReferenceClaims.add(
                    new ReferenceClaim(date, date, date, date.plusYears(period++), cededUltimate, cededUltimate, claim.CY2014)
                )
            }
            if (currentYear <= 2015) {
                cededReferenceClaims.add(
                    new ReferenceClaim(date, date, date, date.plusYears(period++), cededUltimate, cededUltimate, claim.CY2015)
                )
            }
            currentYear++
        }
    }

    private void initCededReferenceUnderwritingInfo(SpreadsheetImporter importer, String sheet) {
        cededReferenceUnderwritingInfos = new ReferenceUwInfoContainer()
        List commissionMap = importer.columns(cededCommissionSheetStructure(sheet), TRIANGLE_VALIDATION)
        int currentYear = beginOfCover.getYear()
        for (Map commission : commissionMap) {
            DateTime date = new DateTime(beginOfCover).plusYears(currentYear - beginOfCover.getYear())
            int period = 0
            if (currentYear == 2012) {
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2012))
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2013 - commission.CY2012))
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2014 - commission.CY2013))
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2015 - commission.CY2014))
            }
            if (currentYear == 2013) {
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2013))
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2014 - commission.CY2013))
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2015 - commission.CY2014))
            }
            if (currentYear == 2014) {
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2014))
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2015 - commission.CY2014))
            }
            if (currentYear == 2015) {
                cededReferenceUnderwritingInfos.add(new ReferenceUwInfo(date, date.plusYears(period++), 0, commission.CY2015))
            }
            currentYear++
        }
    }

    private SetMultimap<Integer, UnderwritingInfoPacket> initGrossPremium(SpreadsheetImporter importer, String sheet, IPeriodCounter periodCounter) {
        List premiumMap = importer.columns(grossPremiumSheetStructure(sheet), TRIANGLE_VALIDATION)
        int currentYear = beginOfCover.getYear()
        SetMultimap<Integer, UnderwritingInfoPacket> grossUwPerUwYear = HashMultimap.create()
        DateTime inceptionDate = new DateTime(beginOfCover)
        for (Map premium : premiumMap) {
            if (currentYear == 2012) {
                ExposureInfo exposureInfo = new ExposureInfo(inceptionDate, periodCounter)
                grossUwPerUwYear.put(currentYear, new UnderwritingInfoPacket(premiumWritten: premium.CY2015, premiumPaid: premium.CY2012, inceptionPeriod: 0, exposure: exposureInfo, date: beginOfCover))
                grossUwPerUwYear.put(currentYear + 1, new UnderwritingInfoPacket(premiumWritten: 0, premiumPaid: premium.CY2013 - premium.CY2012, inceptionPeriod: 0, exposure: exposureInfo, date: beginOfCover.plusYears(1)))
                grossUwPerUwYear.put(currentYear + 2, new UnderwritingInfoPacket(premiumWritten: 0, premiumPaid: premium.CY2014 - premium.CY2013, inceptionPeriod: 0, exposure: exposureInfo, date: beginOfCover.plusYears(2)))
                grossUwPerUwYear.put(currentYear + 3, new UnderwritingInfoPacket(premiumWritten: 0, premiumPaid: premium.CY2015 - premium.CY2014, inceptionPeriod: 0, exposure: exposureInfo, date: beginOfCover.plusYears(3)))
            }
            if (currentYear == 2013) {
                ExposureInfo exposureInfo = new ExposureInfo(inceptionDate, periodCounter)
                grossUwPerUwYear.put(currentYear, new UnderwritingInfoPacket(premiumWritten: premium.CY2015, premiumPaid: premium.CY2013, inceptionPeriod: 1, exposure: exposureInfo, date: beginOfCover.plusYears(1)))
                grossUwPerUwYear.put(currentYear + 1, new UnderwritingInfoPacket(premiumWritten: 0, premiumPaid: premium.CY2014 - premium.CY2013, inceptionPeriod: 1, exposure: exposureInfo, date: beginOfCover.plusYears(2)))
                grossUwPerUwYear.put(currentYear + 2, new UnderwritingInfoPacket(premiumWritten: 0, premiumPaid: premium.CY2015 - premium.CY2014, inceptionPeriod: 1, exposure: exposureInfo, date: beginOfCover.plusYears(3)))
            }
            if (currentYear == 2014) {
                ExposureInfo exposureInfo = new ExposureInfo(inceptionDate, periodCounter)
                grossUwPerUwYear.put(currentYear, new UnderwritingInfoPacket(premiumWritten: premium.CY2015, premiumPaid: premium.CY2014, inceptionPeriod: 2, exposure: exposureInfo, date: beginOfCover.plusYears(2)))
                grossUwPerUwYear.put(currentYear + 1, new UnderwritingInfoPacket(premiumWritten: 0, premiumPaid: premium.CY2015 - premium.CY2014, inceptionPeriod: 2, exposure: exposureInfo, date: beginOfCover.plusYears(3)))
            }
            if (currentYear == 2015) {
                ExposureInfo exposureInfo = new ExposureInfo(inceptionDate, periodCounter)
                grossUwPerUwYear.put(currentYear, new UnderwritingInfoPacket(premiumWritten: premium.CY2015, premiumPaid: premium.CY2015, inceptionPeriod: 3, exposure: exposureInfo, date: beginOfCover.plusYears(3)))
            }
            inceptionDate = inceptionDate.plusYears(1)
            currentYear++
        }
        grossUwPerUwYear
    }


    private class ReferenceClaimContainer {

        Map<ReferenceClaimKey, ReferenceClaim> container = [:]

        void add(ReferenceClaim claim) {
            container.put(new ReferenceClaimKey(claim.occurrenceDate, claim.updateDate), claim)
        }

        ReferenceClaim get(ClaimCashflowPacket claim) {
            container.get(new ReferenceClaimKey(claim.occurrenceDate, claim.updateDate))
        }
    }


    private class ReferenceClaimKey {
        DateTime occurrenceDate
        DateTime updateDate

        ReferenceClaimKey(DateTime occurrenceDate, DateTime updateDate) {
            this.occurrenceDate = occurrenceDate
            this.updateDate = updateDate
        }

        @Override
        int hashCode() {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCodeBuilder.append(occurrenceDate);
            hashCodeBuilder.append(updateDate);
            return hashCodeBuilder.toHashCode();
        }

        @Override
        boolean equals(Object obj) {
            if (obj instanceof ReferenceClaimKey) {
                return ((ReferenceClaimKey) obj).occurrenceDate.equals(occurrenceDate) && ((ReferenceClaimKey) obj).updateDate.equals(updateDate)
            } else {
                return false;
            }
        }

        @Override
        String toString() {
            "${format(occurrenceDate)} (${format(updateDate)})"
        }
    }


    private class ReferenceClaim {

        DateTime occurrenceDate
        DateTime inceptionDate
        DateTime eventDate
        DateTime updateDate

        double ultimate
        double reported
        double paid

        ReferenceClaim(DateTime occurrenceDate, DateTime inceptionDate, DateTime eventDate, DateTime updateDate, double ultimate, double reported, double paid) {
            this.occurrenceDate = occurrenceDate
            this.inceptionDate = inceptionDate
            this.eventDate = eventDate
            this.updateDate = updateDate
            this.ultimate = ultimate
            this.reported = reported
            this.paid = paid
        }

        String dateSummary() {
            "${format(occurrenceDate)} (${format(updateDate)})"
        }

        @Override
        String toString() {
            "${dateSummary()} $ultimate $reported $paid"
        }
    }

    private class ReferenceUwInfoContainer {

        Map<ReferenceUwInfoKey, ReferenceUwInfo> container = [:]

        void add(ReferenceUwInfo uwInfo) {
            container.put(new ReferenceUwInfoKey(uwInfo.inceptionDate, uwInfo.updateDate), uwInfo)
        }

        ReferenceUwInfo get(UnderwritingInfoPacket underwritingInfo) {
            container.get(new ReferenceUwInfoKey(underwritingInfo.exposure.inceptionDate, underwritingInfo.date))
        }
    }

    private class ReferenceUwInfoKey {
        DateTime inceptionDate
        DateTime updateDate

        ReferenceUwInfoKey(DateTime inceptionDate, DateTime updateDate) {
            this.inceptionDate = inceptionDate
            this.updateDate = updateDate
        }

        @Override
        int hashCode() {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCodeBuilder.append(inceptionDate);
            hashCodeBuilder.append(updateDate);
            return hashCodeBuilder.toHashCode();
        }

        @Override
        boolean equals(Object obj) {
            if (obj instanceof ReferenceUwInfoKey) {
                return ((ReferenceUwInfoKey) obj).inceptionDate.equals(inceptionDate) && ((ReferenceUwInfoKey) obj).updateDate.equals(updateDate)
            } else {
                return false;
            }
        }

        @Override
        String toString() {
            "${format(inceptionDate)} (${format(updateDate)})"
        }
    }

    private class ReferenceUwInfo {
        DateTime updateDate
        DateTime inceptionDate

        double premium
        double commission

        ReferenceUwInfo(DateTime inceptionDate, DateTime updateDate, double premium, double commission) {
            this.updateDate = updateDate
            this.inceptionDate = inceptionDate
            this.premium = premium
            this.commission = commission
        }

        String dateSummary() {
            "${format(inceptionDate)} (${format(updateDate)})"
        }

        @Override
        String toString() {
            "${dateSummary()} $premium $commission"
        }
    }

    private static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy"
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT)

    /**
     * @param dateTime
     * @return "dd.MM.yyyy"
     */
    private static String format(DateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return formatter.print(dateTime);
    }
}
