package models.gira

import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter


import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.dependency.Dependencies
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventGenerators
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.cf.global.GlobalParameters
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Indices
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ReinsuranceContracts
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segments
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Patterns
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Pattern
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPattern
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.reserve.ReservesGenerators
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntities
import org.pillarone.riskanalytics.domain.pc.cf.reserve.ReservesGenerator
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.structure.Structures
import org.pillarone.riskanalytics.domain.pc.cf.discounting.Discountings

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class GIRAModel extends StochasticModel {

    private static Log LOG = LogFactory.getLog(GIRAModel);

    GlobalParameters globalParameters
    Indices indices
    Discountings discountings
    Patterns patterns
    UnderwritingSegments underwritingSegments
    ClaimsGenerators claimsGenerators
    ReservesGenerators reservesGenerators
    Dependencies dependencies
    EventGenerators eventGenerators
    LegalEntities legalEntities
    Segments segments
    ReinsuranceContracts reinsuranceContracts
    Structures structures

    @Override
    void initComponents() {
        globalParameters = new GlobalParameters()
        underwritingSegments = new UnderwritingSegments()
        indices = new Indices()
        discountings = new Discountings()
        patterns = new Patterns()
        claimsGenerators = new ClaimsGenerators()
        reservesGenerators = new ReservesGenerators()
        dependencies = new Dependencies()
        eventGenerators = new EventGenerators()
        legalEntities = new LegalEntities()
        segments = new Segments()
        reinsuranceContracts = new ReinsuranceContracts()
        structures = new Structures()

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
        indices.inEventSeverities = dependencies.outEventSeverities
        discountings.inEventSeverities = dependencies.outEventSeverities
        if (segments.subComponentCount() == 0) {
            reinsuranceContracts.inClaims = claimsGenerators.outClaims
            reinsuranceContracts.inClaims = reservesGenerators.outReserves
            reinsuranceContracts.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
            if (structures.subComponentCount() > 0) {
                structures.inClaimsGross = claimsGenerators.outClaims
                structures.inClaimsCeded = reinsuranceContracts.outClaimsCeded
                structures.inClaimsNet = reinsuranceContracts.outClaimsNet
                structures.inUnderwritingInfoGross = underwritingSegments.outUnderwritingInfo
                structures.inUnderwritingInfoNet = reinsuranceContracts.outUnderwritingInfoNet
                structures.inUnderwritingInfoCeded = reinsuranceContracts.outUnderwritingInfoCeded
            }
        }
        else {
            segments.inClaims = claimsGenerators.outClaims
            // todo(jwa): change inReserves to inClaims as soon as PMO-1733 is solved
            segments.inReserves = reservesGenerators.outReserves
            segments.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
            segments.inFactors = discountings.outFactors
            reinsuranceContracts.inClaims = segments.outClaimsGross
            reinsuranceContracts.inUnderwritingInfo = segments.outUnderwritingInfoGross
            segments.inClaimsCeded = reinsuranceContracts.outClaimsCeded
            segments.inUnderwritingInfoCeded = reinsuranceContracts.outUnderwritingInfoCeded
            if (structures.subComponentCount() > 0){
                structures.inClaimsGross = segments.outClaimsGross
                structures.inClaimsCeded = segments.outClaimsCeded
                structures.inClaimsNet = segments.outClaimsNet
                structures.inUnderwritingInfoGross = segments.outUnderwritingInfoGross
                structures.inUnderwritingInfoNet = segments.outUnderwritingInfoNet
                structures.inUnderwritingInfoCeded = segments.outUnderwritingInfoCeded
            }
        }
    }

    @Override
    IPeriodCounter createPeriodCounter(DateTime beginOfFirstPeriod) {
        Period developmentPeriod = lastPatternPeriod()
        int numberOfYears = Math.max(1, Math.ceil(developmentPeriod.months / 12d) + 1)
        return new LimitedContinuousPeriodCounter(globalParameters.parmProjectionStartDate, Period.years(1), numberOfYears)
    }

    private Period lastPatternPeriod() {
        Period maxPeriods = Period.months(0);

        Map<String, Period> claimsGeneratorPatternLengths = new HashMap<String, Period>()
        for (Pattern pattern: patterns.subPayoutPatterns.componentList) {
            Period period = pattern.parmPattern.getPattern(IPayoutPatternMarker.class).getLastCumulativePeriod()
            LOG.debug("payout pattern $pattern.name $period.months")
            claimsGeneratorPatternLengths.put(pattern.name, period)
        }
        for (Pattern pattern: patterns.subReportingPatterns.componentList) {
            Period period = pattern.parmPattern.getPattern(IReportingPatternMarker.class).getLastCumulativePeriod()
            LOG.debug("reporting pattern $pattern.name $period.months")
            claimsGeneratorPatternLengths.put(pattern.name, period)
        }
        for (PayoutReportingCombinedPattern pattern: patterns.subPayoutAndReportingPatterns.componentList) {
            Period period = pattern.parmPattern.getPayoutPattern().getLastCumulativePeriod()
            LOG.debug("combined payout reporting pattern $pattern.name $period.months")
            claimsGeneratorPatternLengths.put(pattern.name, period)
        }

        if (!claimsGeneratorPatternLengths.isEmpty()) {
            for (ClaimsGenerator generator: claimsGenerators.componentList) {
                Period period = claimsGeneratorPatternLengths.get(generator.parmPayoutPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, period.months))
                }
                period = claimsGeneratorPatternLengths.get(generator.parmReportingPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, period.months))
                }
            }

            for (ReservesGenerator generator: reservesGenerators.componentList) {
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
        for (Pattern pattern: patterns.subPremiumPatterns.componentList) {
            Period period = pattern.parmPattern.getPattern(IPremiumPatternMarker.class).getLastCumulativePeriod()
            LOG.debug("premium pattern $pattern.name $period.months")
            premiumPatternLengths.put(pattern.name, period)
        }
        if (!premiumPatternLengths.isEmpty()) {
            for (RiskBands riskBands: underwritingSegments.componentList) {
                Period period = premiumPatternLengths.get(riskBands.parmPremiumPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, period.months))
                }
            }
        }
        LOG.debug("max periods: $maxPeriods")
        return maxPeriods
    }

    public int maxNumberOfFullyDistinctPeriods() {
        1
    }
}
