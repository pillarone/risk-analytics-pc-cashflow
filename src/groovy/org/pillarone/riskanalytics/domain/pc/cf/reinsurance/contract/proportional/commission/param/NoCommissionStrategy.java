package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.NoCommission;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author shartmann (at) munichre (dot) com
 */
public class NoCommissionStrategy extends AbstractParameterObject implements ICommissionStrategy {

    public IParameterObjectClassifier getType() {
        return CommissionStrategyType.NOCOMMISSION;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public void calculateCommission(List<ClaimCashflowPacket> claims, List<CededUnderwritingInfoPacket> underwritingInfos,
                                    boolean isFirstPeriod, boolean isAdditive) {
    }

    public ICommission getCalculator(DoubleValue lossCarriedForward) {
        return new NoCommission();
    }

    @Override
    public DoubleValue getInitialLossCarriedForward() {
        return new DoubleValue();
    }
}
