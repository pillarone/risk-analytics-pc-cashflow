package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerIdentifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.Collection;
import java.util.Collections;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredLossAndAP {

    private final Collection<IncurredLossAndLayer> loss;
    private final Collection<LayerAndAP> aps;

    public IncurredLossAndAP(Collection<IncurredLossAndLayer> loss, Collection<LayerAndAP> aps) {
        this.loss = loss;
        this.aps = aps;
    }

    public double getAdditionalPremium() {
        double ap = 0d;
        for ( LayerAndAP aLayer : aps) {
            for (AdditionalPremium aPremium : aLayer.getAdditionalPremiums()) {
                ap += aPremium.getAdditionalPremium();
            }
        }
        return ap;
    }

    public Collection<AdditionalPremium> getAddtionalPremiums() {
        Collection<AdditionalPremium> apSingleValuePackets = Lists.newArrayList();
        for (LayerAndAP apLayer : aps) {
            apSingleValuePackets.addAll(apLayer.getAdditionalPremiums());
        }
        return apSingleValuePackets;

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

    public Collection<LayerAndAP> getAps() {
        return Collections.unmodifiableCollection( aps );
    }

    public IncurredLossAndLayer getLayerAndIncurredLoss(LayerIdentifier layerIdentifier) {
        for (IncurredLossAndLayer los : loss) {
            if(los.getLayerParameters().getLayerIdentifier().equals(layerIdentifier) ) {
                return los;
            }
        }
        throw new SimulationException("Didn't find incurred layer for " + layerIdentifier.toString());
    }

    public void setAPDates(IPeriodCounter iPeriodCounter){
        for (LayerAndAP ap : aps) {
            for (AdditionalPremium additionalPremium : ap.getAdditionalPremiums()) {
                additionalPremium.setDate(additionalPremium.getPremiumType().getAPDate(iPeriodCounter));
            }
        }
    }

    @Override
    public String toString() {
        return "IncurredLossAndAP{" +
                "loss=" + getLossWithShareAppliedAllLayers() +
                ", aps=" + getAdditionalPremium() +
                '}';
    }
}
