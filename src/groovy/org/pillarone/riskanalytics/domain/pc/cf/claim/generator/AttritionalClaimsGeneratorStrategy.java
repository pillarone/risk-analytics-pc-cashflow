package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.utils.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.RandomDistribution;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): ask msp how we could define default values only once: prefered place are the *Type.groovy classes
public class AttritionalClaimsGeneratorStrategy extends AbstractClaimsGeneratorStrategy  {

    private ExposureBase claimsSizeBase;// = ExposureBase.ABSOLUTE;
    private RandomDistribution claimsSizeDistribution;// = (RandomDistribution) ClaimsGeneratorType.getDefault().getParameters().get(CLAIMS_SIZE_DISTRIBUTION);
    private DistributionModified claimsSizeModification;// = DistributionModifier.getStrategy(DistributionModifier.NONE, Collections.emptyMap());

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.ATTRITIONAL;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put(CLAIMS_SIZE_BASE, claimsSizeBase);
        parameters.put(CLAIMS_SIZE_DISTRIBUTION, claimsSizeDistribution);
        parameters.put(CLAIMS_SIZE_MODIFICATION, claimsSizeModification);
        return parameters;
    }


    public List<ClaimRoot> generateClaims(PeriodScope periodScope) {
        setGenerator(claimsSizeDistribution, claimsSizeModification);
        return generateClaims(1, 1, ClaimType.ATTRITIONAL, periodScope);
    }

}