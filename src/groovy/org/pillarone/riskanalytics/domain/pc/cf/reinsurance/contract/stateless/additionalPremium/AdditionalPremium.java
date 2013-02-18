package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.output.TypeDrillDownPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class AdditionalPremium extends TypeDrillDownPacket {

    private double additionalPremium;
    private APBasis premiumType;

    public AdditionalPremium() {
        this.additionalPremium = 0;
        premiumType = APBasis.NONE;
    }

    public AdditionalPremium(double additionalPremium, APBasis premiumType) {
        this.additionalPremium = additionalPremium;
        this.premiumType = premiumType;
    }

    public double getAdditionalPremium() {
        return additionalPremium;
    }

    public APBasis getPremiumType() {
        return premiumType;
    }

    public String typeDrillDownName() {
        return "split:" + premiumType.toString().toLowerCase();
    }

    @Override
    public TypeDrillDownPacket plusForAggregateCollection(TypeDrillDownPacket aPacket) {
        if(!(aPacket instanceof AdditionalPremium)) {
            throw new SimulationException("Recieved incompatible packet type " + aPacket.toString());
        }
        return new AdditionalPremium(((AdditionalPremium) aPacket).getAdditionalPremium() + additionalPremium, APBasis.AGGREGATED);
    }
}
