package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IContractStructure;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IRiLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.ContractOrderingMethod;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;

import java.util.*;

/**
*   author simon.parten @ art-allianz . com
 */
public class ContractStructure implements IContractStructure {

    private final double termLimit;
    private final double termExcess;
    private final ContractOrderingMethod contractOrder;
    private final Map<YearLayerIdentifier, ContractLayer> layers;

    public ContractStructure(final double termLimit, final double termExcess, final HashMap<YearLayerIdentifier, ContractLayer> layers, final ContractOrderingMethod contractOrder) {
        this.termLimit = termLimit;
        this.termExcess = termExcess;
        this.contractOrder = contractOrder;
        this.layers = Collections.unmodifiableMap( layers );
    }

    public ContractLayer getContractLayer(int year, int layer) {

        ContractLayer contractLayer = layers.get(new YearLayerIdentifier(year, layer));
        if(contractLayer == null) {
            throw new SimulationException("Failed to find contract definition for period " + year + " layer "  + layer);
        }
        return contractLayer;
    }

    /**
     *
     * @param period - contract structure mimics the UI.
     *               The period parameter is adjusted accordingly in the method; i.e we add 1 to the period parameter
     * @return
     */
    @Override
    public List<LayerParameters> getLayers(final int period) {
        List<LayerParameters> layersInPeriod = Lists.newArrayList();
        for (Map.Entry<YearLayerIdentifier, ContractLayer> anEntry : layers.entrySet()) {
            if(anEntry.getKey().getYear() == period + 1  ) {
                layersInPeriod.add(anEntry.getValue().getLayerParameterNoAP());
            }
        }
        return layersInPeriod;
    }

    @Override
    public List<IRiLayer> getContractLayers(final int period) {
        return new ArrayList<IRiLayer>(getLayersInPeriod(period));
    }

    public ContractLayer getLayer(int year, int layer) {
        ContractLayer layer1 = layers.get(new YearLayerIdentifier(year, layer));
        if(layer1 == null) throw new SimulationException("Attempted to lookup layer " + layer +  "in year :" + year
        + " but it's not part of the contract structure. Please check your table and contact development");
        return layer1;
    }

    public Collection<ContractLayer> getLayersInPeriod(int period) {
        Collection<ContractLayer> layersInPeriod = Lists.newArrayList();
        for (YearLayerIdentifier yearLayerIdentifier : layers.keySet()) {
            if (yearLayerIdentifier.getYear() == period) {
                layersInPeriod.add(layers.get(yearLayerIdentifier));
            }
        }
        return layersInPeriod;
    }

    public double getTermLimit() {
        return termLimit;
    }

    public double getTermExcess() {
        return termExcess;
    }
}
