package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase;
import org.pillarone.riskanalytics.domain.utils.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.RandomDistribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FrequencySeverityClaimsGeneratorStrategy extends AbstractSingleClaimsGeneratorStrategy  {

    private FrequencyBase frequencyBase;
    private RandomDistribution frequencyDistribution;
    private DistributionModified frequencyModification;
    private ExposureBase claimsSizeBase;
    private RandomDistribution claimsSizeDistribution;
    private DistributionModified claimsSizeModification;
    private FrequencySeverityClaimType produceClaim;

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.FREQUENCY_SEVERITY;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(7);
        parameters.put(CLAIMS_SIZE_BASE, claimsSizeBase);
        parameters.put(CLAIMS_SIZE_DISTRIBUTION, claimsSizeDistribution);
        parameters.put(CLAIMS_SIZE_MODIFICATION, claimsSizeModification);
        parameters.put(FREQUENCY_BASE, frequencyBase);
        parameters.put(FREQUENCY_DISTRIBUTION, frequencyDistribution);
        parameters.put(FREQUENCY_MODIFICATION, frequencyModification);
        parameters.put(PRODUCE_CLAIM, produceClaim);
        return parameters;
    }


    public List<ClaimRoot> generateClaims(PeriodScope periodScope) {
        setGenerator(claimsSizeDistribution, claimsSizeModification);
        setClaimNumberGenerator(frequencyDistribution, frequencyModification);
        ClaimType claimType = produceClaim == FrequencySeverityClaimType.SINGLE ? ClaimType.SINGLE : ClaimType.AGGREGATED_EVENT;
        return generateClaims(1, claimType, periodScope);
    }

}