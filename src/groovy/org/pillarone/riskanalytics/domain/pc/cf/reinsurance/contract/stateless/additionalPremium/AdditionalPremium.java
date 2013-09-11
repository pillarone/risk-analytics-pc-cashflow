package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IRiLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.AdditionalPremiumLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ProfitCommissions;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ReinstatementLayer;

import java.util.ArrayList;

/**
 * author simon.parten @ art-allianz . com
 */
public class AdditionalPremium extends MultiValuePacket {

    private final IRiLayer iRiLayer;
    private double additionalPremium;
    private CalcAPBasis premiumType;

    public AdditionalPremium() {
        this.additionalPremium = 0;
        premiumType = CalcAPBasis.NONE;
        iRiLayer = new ContractLayer(new YearLayerIdentifier(0d, 0d), 0d, 0d, 0d, 0d, 0d, 0d,
                new ArrayList<ReinstatementLayer>(), new ArrayList<AdditionalPremiumLayer>(), new ArrayList<ProfitCommissions>(), 0d);
    }

    public AdditionalPremium(double additionalPremium, CalcAPBasis premiumType, final IRiLayer layer) {
        this.additionalPremium = additionalPremium;
        this.premiumType = premiumType;
        this.iRiLayer = layer;
    }

    public double getAdditionalPremium() {
        return additionalPremium;
    }

    public IRiLayer getiRiLayer() {
        return iRiLayer;
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
        return new AdditionalPremium(
                ((AdditionalPremium) aPacket).getAdditionalPremium() + additionalPremium,
                CalcAPBasis.AGGREGATED, new ContractLayer(new YearLayerIdentifier(0d, 0d), 0d, 0d, 0d, 0d, 0d, 0d,
                new ArrayList<ReinstatementLayer>(), new ArrayList<AdditionalPremiumLayer>(), new ArrayList<ProfitCommissions>(), 0d));
    }
}
