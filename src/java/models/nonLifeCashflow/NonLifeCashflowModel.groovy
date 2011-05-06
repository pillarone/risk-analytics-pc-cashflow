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
import org.pillarone.riskanalytics.domain.pc.cf.dependency.MultipleDependencies

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class NonLifeCashflowModel extends StochasticModel {

    GlobalParameters globalParameters
    Indices indices
    Patterns patterns
    UnderwritingSegments underwritingSegments
    ClaimsGenerators claimsGenerators
    Dependencies dependencies
    MultipleDependencies multipleDependencies

    @Override
    void initComponents() {
        globalParameters = new GlobalParameters()
        underwritingSegments = new UnderwritingSegments()
        indices = new Indices()
        patterns = new Patterns()
        claimsGenerators = new ClaimsGenerators()
        dependencies = new Dependencies()
        multipleDependencies = new MultipleDependencies()

        addStartComponent indices
        addStartComponent patterns
        addStartComponent dependencies
        addStartComponent multipleDependencies
    }

    @Override
    void wireComponents() {
        underwritingSegments.inFactors = indices.outFactors
        underwritingSegments.inPatterns = patterns.outPatterns
        claimsGenerators.inFactors = indices.outFactors
        claimsGenerators.inPatterns = patterns.outPatterns
        claimsGenerators.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        claimsGenerators.inEventSeverities = dependencies.outEventSeverities
        claimsGenerators.inEventSeverities = multipleDependencies.outEventSeverities
        claimsGenerators.inEventFrequencies = multipleDependencies.outEventFrequencies
    }

    @Override
    IPeriodCounter createPeriodCounter(DateTime beginOfFirstPeriod) {
        Period developmentPeriod = lastPatternPeriod()
        int numberOfYears = Math.max(1, Math.ceil(developmentPeriod.months / 12d) + 1)
        return new LimitedContinuousPeriodCounter(globalParameters.parmProjectionStartDate, Period.years(1), numberOfYears)
    }

    private Period lastPatternPeriod() {
        Period maxPeriods = Period.months(0);
        Map<String, Period> patternLengths = new HashMap<String, Period>()
        for (Pattern pattern: patterns.subPayoutPatterns.componentList) {
            Period period = pattern.parmPattern.pattern.getLastCumulativePeriod()
            patternLengths.put(pattern.name, period)
        }

        if (!patternLengths.isEmpty()) {
            for (ClaimsGenerator generator: claimsGenerators.componentList) {
                Period period = patternLengths.get(generator.parmPayoutPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, period.months))
                }
                period = patternLengths.get(generator.parmReportingPattern?.stringValue)
                if (period != null) {
                    maxPeriods = Period.months(Math.max(maxPeriods.months, period.months))
                }
            }
        }
        return maxPeriods
    }

    public int maxNumberOfFullyDistinctPeriods() {
        1
    }
}
