package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.RandomDistribution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsGeneratorStrategy extends AbstractClaimsGeneratorStrategy  {

    protected ExposureBase claimsSizeBase;
    protected RandomDistribution claimsSizeDistribution;
    protected DistributionModified claimsSizeModification;

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

    public List<ClaimRoot> generateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria, PeriodScope periodScope) {
        setGenerator(claimsSizeDistribution, claimsSizeModification);
        return generateClaims(uwInfos, uwInfosFilterCriteria, claimsSizeBase, ClaimType.ATTRITIONAL, periodScope);
    }

}