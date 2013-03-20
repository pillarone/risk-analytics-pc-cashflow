package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.Collections;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class TermLossAndLossByLayer {

    final Map<Integer, Double> termLosses;
    final Map<Integer, AllLayersPaidLoss> paidLossesByLayer;

    public TermLossAndLossByLayer(Map<Integer, Double> termLosses, Map<Integer, AllLayersPaidLoss> paidLossesByLayer) {
        this.termLosses = termLosses;
        this.paidLossesByLayer = paidLossesByLayer;
    }

    public Map<Integer, Double> getTermLosses() {
        return Collections.unmodifiableMap( termLosses );
    }

    public Map<Integer, AllLayersPaidLoss> getPaidLossesByLayer() {
        return Collections.unmodifiableMap( paidLossesByLayer );
    }
}
