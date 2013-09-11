package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;


import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.AdditionalPremiumLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ProfitCommissions;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ReinstatementLayer;

import java.util.*;

/**
 * Helper class used for layers for NonPropTemplateContractStrategy. Missing gaps in the period sequence are filled
 * with values of nearest previous period (floorEntry).
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PeriodLayerParameters implements IContractStructure {

    private TreeMap<Integer, Map<Integer, LayerParameters>> layerByPeriod = new TreeMap<Integer, Map<Integer, LayerParameters>>();
    private double termLimit;
    private double termExcess;

    public PeriodLayerParameters() {
    }

    public PeriodLayerParameters(PeriodLayerParameters layerParams, final double termLimit, final double termExcess) {
        this.layerByPeriod = layerParams.layerByPeriod;
        this.termExcess = termExcess;
        this.termLimit = termLimit;
    }

    private LayerParameters add(int period, int layer, double share, double attachmentPoint, double limit, double annualAttachment, double annualLimit) {
        Map<Integer, LayerParameters> layerMap = layerByPeriod.get(period);
        if (layerMap == null) {
            layerMap = new HashMap<Integer, LayerParameters>();
        }
        LayerParameters layerParameters = layerMap.get(layer);
        if (layerParameters == null) {
            layerParameters = new LayerParameters(share, attachmentPoint, limit, period, layer);
        }
        layerMap.put(layer, layerParameters);
        layerByPeriod.put(period, layerMap);
        return layerParameters;
    }

    public void add(int period, int layer, double share, double attachmentPoint, double limit,
                    double aggregateDeductible,
                    double aggregateLimit,
                    double additionalPremium, APBasis additionalPremiumBasis) {
        LayerParameters layerParameters = add(period, layer, share, attachmentPoint, limit, aggregateDeductible, aggregateLimit);
        layerParameters.addAdditionalPremium(aggregateDeductible, aggregateLimit, additionalPremium, additionalPremiumBasis);
    }

    public List<LayerParameters> getLayers(int period) {
        Map.Entry<Integer, Map<Integer, LayerParameters>> entry = layerByPeriod.floorEntry(period);
        if (entry == null) {
            return Collections.emptyList();
        }
        else {
            return new ArrayList<LayerParameters>(entry.getValue().values());
        }
    }

    public List<IRiLayer> getContractLayers(final int period) {
        List<IRiLayer> aList = Lists.newArrayList();
        for (Map.Entry<Integer, Map<Integer, LayerParameters>> integerMapEntry : layerByPeriod.entrySet()) {
            Integer year = integerMapEntry.getKey();
            for (Map.Entry<Integer, LayerParameters> integerLayerParametersEntry : integerMapEntry.getValue().entrySet()) {
                Integer layer = integerLayerParametersEntry.getKey();
                LayerParameters aLayer = integerLayerParametersEntry.getValue();
                ContractLayer contractLayer = new ContractLayer(
                        new YearLayerIdentifier(year, layer),
                        aLayer.getShare(),
                        aLayer.getClaimLimit(),
                        aLayer.getClaimExcess(),
                        aLayer.getLayerPeriodLimit(),
                        aLayer.getLayerPeriodExcess(),
                        0d,
                        new ArrayList<ReinstatementLayer>(),
                        new ArrayList<AdditionalPremiumLayer>(),
                        new ArrayList<ProfitCommissions>(),
                        aLayer.getAdditionalPremiums(),
                        0d);
                aList.add(contractLayer);
            }

        }
        return aList;
    }

    public double getTermLimit() {
        return termLimit;
    }

    public void setTermLimit(final double termLimit) {
        this.termLimit = termLimit;
    }

    public double getTermExcess() {
        return termExcess;
    }

    public void setTermExcess(final double termExcess) {
        this.termExcess = termExcess;
    }
}
