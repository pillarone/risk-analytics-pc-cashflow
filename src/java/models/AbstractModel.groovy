package models

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.CreditDefault
import org.pillarone.riskanalytics.domain.pc.cf.dependency.Dependencies
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventGenerators
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountingYieldCurves
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.cf.global.GlobalParameters
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Indices
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntities
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Pattern
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Patterns
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPattern
import org.pillarone.riskanalytics.domain.pc.cf.reserve.ReservesGenerator
import org.pillarone.riskanalytics.domain.pc.cf.reserve.ReservesGenerators
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segments
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities

abstract class AbstractModel extends StochasticModel {
    protected Log logger = LogFactory.getLog(getClass())

    GlobalParameters globalParameters
    CreditDefault creditDefault
    Indices indices
    DiscountingYieldCurves discounting
    Patterns patterns
    UnderwritingSegments underwritingSegments
    ClaimsGenerators claimsGenerators
    ReservesGenerators reservesGenerators
    Dependencies dependencies
    EventGenerators eventGenerators
    LegalEntities legalEntities
    Segments segments

    @Override
    void initComponents() {
        globalParameters = new GlobalParameters()
        creditDefault = new CreditDefault()
        underwritingSegments = new UnderwritingSegments()
        indices = new Indices()
        discounting = new DiscountingYieldCurves()
        patterns = new Patterns()
        claimsGenerators = new ClaimsGenerators()
        reservesGenerators = new ReservesGenerators()
        dependencies = new Dependencies()
        eventGenerators = new EventGenerators()
        legalEntities = new LegalEntities()
        segments = new Segments()

        addStartComponent creditDefault
        addStartComponent discounting
        addStartComponent patterns
        addStartComponent dependencies
        addStartComponent eventGenerators
    }

    @Override
    void wireComponents() {
        underwritingSegments.inFactors = indices.outFactors
        underwritingSegments.inPatterns = patterns.outPatterns
        claimsGenerators.inFactors = indices.outFactors
        claimsGenerators.inPatterns = patterns.outPatterns
        claimsGenerators.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        claimsGenerators.inEventSeverities = dependencies.outEventSeverities
        claimsGenerators.inEventSeverities = eventGenerators.outEventSeverities
        claimsGenerators.inEventFrequencies = eventGenerators.outEventFrequencies
        reservesGenerators.inFactors = indices.outFactors
        reservesGenerators.inPatterns = patterns.outPatterns
        legalEntities.inPatterns = patterns.outPatterns
        indices.inEventSeverities = dependencies.outEventSeverities
    }


    public int maxNumberOfFullyDistinctPeriods() {
        1
    }

    @Override
    IPeriodCounter createPeriodCounter(DateTime beginOfFirstPeriod) {
        Period developmentPeriod = lastPatternPeriod()
        int numberOfYears = Math.max(1, Math.ceil(developmentPeriod.months / 12d) + 1)
        if (globalParameters.parmProjection.periodNumberRestricted()) {
            numberOfYears = Math.max(1, Math.ceil(developmentPeriod.months / 12d))
        }
        if (globalParameters.isRuntimeRunAtMostFivePeriods()) {
            numberOfYears = Math.min(numberOfYears, 5)
        }
        return new LimitedContinuousPeriodCounter(globalParameters.parmProjectionStartDate, Period.years(1), numberOfYears)
    }

    private Period lastPatternPeriod() {
        Period maxPeriods = Period.months(0)
        if (globalParameters.isRuntimeTrivialPatterns()) return maxPeriods
        if (globalParameters.parmProjection.periodNumberRestricted()) {
            return Period.months(globalParameters.projectionPeriods() * 12)
        }

        Map<String, Period> claimsGeneratorPatternLengths = new HashMap<String, Period>()
        for (PayoutReportingCombinedPattern pattern : patterns.subPayoutAndReportingPatterns.componentList) {
            Period period = pattern.parmPattern.getPayoutPattern().getLastCumulativePeriod()
            logger.debug("combined payout reporting pattern $pattern.name $period.months")
            Period existingPeriod = claimsGeneratorPatternLengths.get(pattern.name)
            if (existingPeriod == null || existingPeriod.months < period.months) {
                claimsGeneratorPatternLengths.put(pattern.name, period)
            }
        }

        if (!claimsGeneratorPatternLengths.isEmpty()) {
            for (ClaimsGenerator generator : claimsGenerators.componentList) {
                Period period = claimsGeneratorPatternLengths.get(generator.parmPayoutPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, period.months))
                }
                period = claimsGeneratorPatternLengths.get(generator.parmReportingPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, period.months))
                }
            }

            for (ReservesGenerator generator : reservesGenerators.componentList) {
                DateTime averageInceptionDate = generator.parmUltimateEstimationMethod.getAverageInceptionDate()
                Double elapsedMonths = DateTimeUtilities.deriveNumberOfMonths(averageInceptionDate, globalParameters.parmProjectionStartDate)
                Period period = claimsGeneratorPatternLengths.get(generator.parmPayoutPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, (Integer) Math.ceil(period.months - elapsedMonths)))
                }
                period = claimsGeneratorPatternLengths.get(generator.parmReportingPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, (Integer) Math.ceil(period.months - elapsedMonths)))
                }
            }
        }

        Map<String, Period> premiumPatternLengths = new HashMap<String, Period>()
        for (Pattern pattern : patterns.subPremiumPatterns.componentList) {
            Period period = pattern.parmPattern.getPattern(IPremiumPatternMarker.class).getLastCumulativePeriod()
            logger.debug("premium pattern $pattern.name $period.months")
            premiumPatternLengths.put("premium ${pattern.name}", period)
        }
        if (!premiumPatternLengths.isEmpty()) {
            for (RiskBands riskBands : underwritingSegments.componentList) {
                Period period = premiumPatternLengths.get(riskBands.parmPremiumPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, period.months))
                }
            }
        }
        logger.debug("max periods: $maxPeriods")
        return maxPeriods
    }

    public Set<String> periodLabelsBeforeProjectionStart() {
        Set<String> periodLabels = []
        for (ReservesGenerator generator : reservesGenerators.componentList) {
            periodLabels << generator.parmUltimateEstimationMethod.averageInceptionDate.year.toString()
        }
        periodLabels
    }




    //abstract void wireWithoutSegments()

    //abstract void wireWithSegments()

    //abstract void wireLegalEntities()


}
