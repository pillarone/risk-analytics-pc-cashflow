package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.ArrayList;
import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public class LayerAndAP {

    private final LayerParameters layerParameters;
    private final Collection<AdditionalPremium> additionalPremiums;

    public LayerAndAP(LayerParameters layerParameters, Collection<AdditionalPremium> additionalPremiums) {
        this.layerParameters = layerParameters;
        this.additionalPremiums = additionalPremiums;
    }

    public Collection<APSingleValuePacket> getPackets(IPeriodCounter counter) {
        Collection<APSingleValuePacket> apPackets = new ArrayList<APSingleValuePacket>();
        for (AdditionalPremium ap : additionalPremiums) {
            apPackets.add(ap.getPacket(counter));
        }
        return apPackets;
    }


    public LayerParameters getLayerParameters() {
        return layerParameters;
    }

    public Collection<AdditionalPremium> getAdditionalPremiums() {
        return additionalPremiums;
    }
}
