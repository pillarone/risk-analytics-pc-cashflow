package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.utils.RandomDistribution;

import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class OccurrenceFrequencySeverityClaimsGeneratorStrategy extends FrequencySeverityClaimsGeneratorStrategy  {

    private RandomDistribution occurrenceDistribution;

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY;
    }

    public Map getParameters() {
        Map<String, Object> parameters = super.getParameters();
        parameters.put(OCCURRENCE_DISTRIBUTION, occurrenceDistribution);
        return parameters;
    }


    public List<ClaimRoot> generateClaims(PeriodScope periodScope) {
        setDateGenerator(occurrenceDistribution);
        return super.generateClaims(periodScope);
    }

}