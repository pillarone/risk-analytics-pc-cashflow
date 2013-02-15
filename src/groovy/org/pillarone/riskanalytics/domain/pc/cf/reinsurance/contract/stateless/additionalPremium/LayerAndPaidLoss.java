package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.ArrayList;
import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public class LayerAndPaidLoss {

    private final double paidLoss;
    private final LayerParameters layerParameters;

    public LayerAndPaidLoss(double paidLoss, LayerParameters layerParameters) {
        this.paidLoss = paidLoss;
        this.layerParameters = layerParameters;
    }

    public double getPaidLoss() {
        return paidLoss;
    }

    public LayerParameters getLayerParameters() {
        return layerParameters;
    }

    @Override
    public String toString() {
        return "LayerAndPaidLoss{" +
                "paidLoss=" + paidLoss +
                ", layerParameters=" + layerParameters.toString() +
                '}';
    }
}
