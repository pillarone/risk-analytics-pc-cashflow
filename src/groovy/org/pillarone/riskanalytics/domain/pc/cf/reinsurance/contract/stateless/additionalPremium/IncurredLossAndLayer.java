package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.ArrayList;
import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredLossAndLayer {

    private final double loss;
    private final LayerParameters layerParameters;

    public IncurredLossAndLayer(double loss, LayerParameters layerParameters) {
        this.loss = loss;
        this.layerParameters = layerParameters;
    }

    public double getLoss() {
        return loss;
    }

    public LayerParameters getLayerParameters() {
        return layerParameters;
    }

    @Override
    public String toString() {
        return "IncurredLossAndLayer{" +
                "loss=" + loss +
                ", layerParameters=" + layerParameters +
                '}';
    }
}
