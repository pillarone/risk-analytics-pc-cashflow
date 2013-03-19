package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

/**
 * author simon.parten @ art-allianz . com
 */
public class AdditionalPremiumAndPaidTuple {

    private final AdditionalPremium additionalPremium;
    private final PaidAdditionalPremium paidAdditionalPremium;

    public AdditionalPremiumAndPaidTuple(AdditionalPremium additionalPremium, PaidAdditionalPremium paidAdditionalPremium) {
        this.additionalPremium = additionalPremium;
        this.paidAdditionalPremium = paidAdditionalPremium;
    }

    public AdditionalPremium getAdditionalPremium() {
        return additionalPremium;
    }

    public PaidAdditionalPremium getPaidAdditionalPremium() {
        return paidAdditionalPremium;
    }
}
