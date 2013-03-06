package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

/**
 * author simon.parten @ art-allianz . com
 */
public class AdditionalPremium extends MultiValuePacket {

    private double additionalPremium;
    private CalcAPBasis premiumType;

    public AdditionalPremium() {
        this.additionalPremium = 0;
        premiumType = CalcAPBasis.NONE;
    }

    public AdditionalPremium(double additionalPremium, CalcAPBasis premiumType) {
        this.additionalPremium = additionalPremium;
        this.premiumType = premiumType;
    }

    public double getAdditionalPremium() {
        return additionalPremium;
    }

    public CalcAPBasis getPremiumType() {
        return premiumType;
    }

    public String typeDrillDownName() {
        return "split:" + premiumType.toString().toLowerCase();
    }

    public AdditionalPremium plusForAggregateCollection(AdditionalPremium aPacket) {
        if(!(aPacket instanceof AdditionalPremium)) {
            throw new SimulationException("Recieved incompatible packet type " + aPacket.toString());
        }
        return new AdditionalPremium(((AdditionalPremium) aPacket).getAdditionalPremium() + additionalPremium, CalcAPBasis.AGGREGATED);
    }
}
