package org.pillarone.riskanalytics.domain.pc.cf.claim.allocation;


import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TrivialRiskAllocatorStrategy extends AbstractParameterObject implements IRiskAllocatorStrategy {

    public List<ClaimRoot> getAllocatedClaims(List<ClaimRoot> claims, List<UnderwritingInfoPacket> underwritingInfos) {
        return claims;
    }

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.NONE;
    }

    public Map getParameters() {
        return new HashMap();
    }

}
