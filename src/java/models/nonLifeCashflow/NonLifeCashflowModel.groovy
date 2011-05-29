package models.nonLifeCashflow

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.cf.global.GlobalParameters
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Indices
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Pattern
import org.pillarone.riskanalytics.domain.pc.cf.pattern.Patterns
import org.pillarone.riskanalytics.domain.pc.cf.dependency.Dependencies
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventGenerators
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ReinsuranceContracts
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NonLifeCashflowModel extends StochasticModel {

    private static Log LOG = LogFactory.getLog(NonLifeCashflowModel);

    GlobalParameters globalParameters
    Indices indices
    Patterns patterns
    UnderwritingSegments underwritingSegments
    ClaimsGenerators claimsGenerators
    Dependencies dependencies
    EventGenerators eventGenerators
    ReinsuranceContracts reinsuranceContracts

    @Override
    void initComponents() {
        globalParameters = new GlobalParameters()
        underwritingSegments = new UnderwritingSegments()
        indices = new Indices()
        patterns = new Patterns()
        claimsGenerators = new ClaimsGenerators()
        dependencies = new Dependencies()
        eventGenerators = new EventGenerators()
        reinsuranceContracts = new ReinsuranceContracts()

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
        reinsuranceContracts.inClaims = claimsGenerators.outClaims
        reinsuranceContracts.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        indices.inEventSeverities = dependencies.outEventSeverities
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
            LOG.debug("payout/reporting pattern $pattern.name $period.months")
            claimsGeneratorPatternLengths.put(pattern.name, period)
        }
        for (Pattern pattern: patterns.subReportingPatterns.componentList) {
            Period period = pattern.parmPattern.getPattern(IReportingPatternMarker.class).getLastCumulativePeriod()
            LOG.debug("payout/reporting pattern $pattern.name $period.months")
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
