package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerIdentifier;

import java.util.Collection;
import java.util.Collections;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredLoss {

    private final Collection<IncurredLossAndLayer> loss;

    public IncurredLoss(Collection<IncurredLossAndLayer> loss) {
        this.loss = loss;
    }

    public double getLossWithShareAppliedAllLayers() {
        double totalLoss = 0d;
        for (IncurredLossAndLayer los : loss) {
            totalLoss += los.getLossShareApplied();
        }
        return totalLoss;
    }

    public Collection<IncurredLossAndLayer> getLoss() {
        return Collections.unmodifiableCollection( loss );
    }

    public double getLossAfterClaimStructureOnly(){
        double totalLoss = 0d;
        for (IncurredLossAndLayer los : loss) {
            totalLoss += los.getLossAfterClaimAndAnnualStructures().getLossAfterClaimStructure();
        }
        return totalLoss;

    }
    public IncurredLossAndLayer getLayerAndIncurredLoss(LayerIdentifier layerIdentifier) {
        for (IncurredLossAndLayer los : loss) {
            if(los.getLayerParameters().getLayerIdentifier().equals(layerIdentifier) ) {
                return los;
            }
        }
        throw new SimulationException("Didn't find incurred layer for " + layerIdentifier.toString());
    }

    @Override
    public String toString() {
        return "IncurredLossAndAP{" +
                "loss=" + getLossWithShareAppliedAllLayers() +
                '}';
    }
}
