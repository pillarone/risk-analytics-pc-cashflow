package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimValidator;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class OriginalClaimsCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy {

    private ICoverAttributeStrategy filter;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.ORIGINALCLAIMS;
    }

    public Map getParameters() {
        Map<String, ICoverAttributeStrategy> parameters = new HashMap<String, ICoverAttributeStrategy>(1);
        parameters.put("filter", filter);
        return parameters;
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        return filter.coveredClaims(ClaimValidator.positiveNominalUltimates(source));
    }

    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source, List<ClaimCashflowPacket> coveredGrossClaims) {
        return filter.coveredUnderwritingInfo(source, coveredGrossClaims);
    }
}
