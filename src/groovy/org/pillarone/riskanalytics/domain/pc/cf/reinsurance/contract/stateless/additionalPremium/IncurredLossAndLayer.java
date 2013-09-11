package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IRiLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LossAfterClaimAndAnnualStructures;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredLossAndLayer {

    private final LossAfterClaimAndAnnualStructures lossAfterClaimAndAnnualStructures;
    private final IRiLayer layerParameters;

    public IncurredLossAndLayer(final LossAfterClaimAndAnnualStructures lossAfterClaimAndAnnualStructures, IRiLayer layerParameters) {
        this.lossAfterClaimAndAnnualStructures = lossAfterClaimAndAnnualStructures;
        this.layerParameters = layerParameters;
    }

    public double getLossShareApplied() {
        return lossAfterClaimAndAnnualStructures.getLossAfterAnnualStructureWithShareApplied();
    }

    public IRiLayer getLayerParameters() {
        return layerParameters;
    }

    public LossAfterClaimAndAnnualStructures getLossAfterClaimAndAnnualStructures() {
        return lossAfterClaimAndAnnualStructures;
    }

    @Override
    public String toString() {
        return "IncurredLossAndLayer{" +
                "loss=" + getLossShareApplied() +
                ", layerParameters=" + layerParameters +
                '}';
    }
}
