package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

/**
 * author simon.parten @ art-allianz . com
 */
public class PaidAdditionalPremium extends MultiValuePacket {

    private double paidAmount;
    private AdditionalPremium incurredPremium;

    public PaidAdditionalPremium() {
        this.paidAmount = 0;
        this.incurredPremium = new AdditionalPremium();
    }

    public PaidAdditionalPremium(double paidAmount, AdditionalPremium incurredPremium) {
        this.paidAmount = paidAmount;
        this.incurredPremium = incurredPremium;
    }

    public CalcAPBasis premiumType() {
        return incurredPremium.getPremiumType();
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public AdditionalPremium getIncurredPremium() {
        return incurredPremium;
    }

    public DateTime getInceptionPeriod(IPeriodCounter iPeriodCounter) {
        return incurredPremium.getPremiumType().getAPDate(iPeriodCounter) ;
    }

    public String typeDrillDownName() {
        return incurredPremium.typeDrillDownName();
    }

    public PaidAdditionalPremium plusForAggregateCollection(PaidAdditionalPremium additionalPremium) {
        AdditionalPremium additionalPremium1 = incurredPremium.plusForAggregateCollection(additionalPremium.getIncurredPremium());
        return new PaidAdditionalPremium(paidAmount + additionalPremium.getPaidAmount(), additionalPremium1);
    }
}
