package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimValidator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AllGrossClaimsCoverStrategy extends AbstractParameterObject implements ICoverStrategy {

    public IParameterObjectClassifier getType() {
        return CoverStrategyType.ALLGROSSCLAIMS;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public void coveredClaims(List<ClaimCashflowPacket> source) {
        ClaimValidator.positiveNominalUltimates(source);
    }
}
