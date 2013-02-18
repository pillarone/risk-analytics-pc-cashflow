package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

/**
 * author simon.parten @ art-allianz . com
 */
public class PaidAdditionalPremium extends Packet {

    private final double paidAmount;
    private final AdditionalPremium incurredPremium;

    public PaidAdditionalPremium() {
        this.paidAmount = 0;
        this.incurredPremium = new AdditionalPremium();
    }

    public PaidAdditionalPremium(double paidAmount, AdditionalPremium incurredPremium) {
        this.paidAmount = paidAmount;
        this.incurredPremium = incurredPremium;
    }

    public APBasis premiumType() {
        return incurredPremium.getPremiumType();
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public AdditionalPremium getIncurredPremium() {
        return incurredPremium;
    }
}
