package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;


import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.*;

/**
 * Helper class used for layers for NonPropTemplateContractStrategy. Missing gaps in the period sequence are filled
 * with values of nearest previous period (floorEntry).
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PeriodLayerParameters {

    private TreeMap<Integer, Map<Integer, LayerParameters>> layerByPeriod = new TreeMap<Integer, Map<Integer, LayerParameters>>();

    public PeriodLayerParameters() {
    }

    public PeriodLayerParameters(PeriodLayerParameters layerParams) {
        this.layerByPeriod = layerParams.layerByPeriod;
    }

    private LayerParameters add(int period, int layer, double share, double attachmentPoint, double limit, double annualAttachment, double annualLimit) {
        Map<Integer, LayerParameters> layerMap = layerByPeriod.get(period);
        if (layerMap == null) {
            layerMap = new HashMap<Integer, LayerParameters>();
        }
        LayerParameters layerParameters = layerMap.get(layer);
        if (layerParameters == null) {
            layerParameters = new LayerParameters(share, attachmentPoint, limit);
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
}
