package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ProRataTermXLContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.PeriodLayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ScaledPeriodLayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.AdditionalPremiumConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.LayerConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.TemplateContractType;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NonPropTemplateContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    private double termLimit = 0d;
    private double termExcess = 0d;
    private ConstrainedMultiDimensionalParameter termAP = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[0d],[0d],[0d]]"),
            AdditionalPremiumConstraints.columnHeaders, ConstraintsFactory.getConstraints(AdditionalPremiumConstraints.IDENTIFIER));
    private ConstrainedMultiDimensionalParameter structure = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[1],[1],[1d],[0d],[0d],[0d],[0d],[0d],['PREMIUM']]"),
            LayerConstraints.columnHeaders, ConstraintsFactory.getConstraints(LayerConstraints.IDENTIFIER));

    private PeriodLayerParameters contractsByPeriod;
    private int minPeriodCovered;
    private int maxPeriodCovered;


    public IParameterObjectClassifier getType() {
        return TemplateContractType.NONPROPORTIONAL;
    }

    public Map getParameters() {
        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put(TERM_LIMIT, termLimit);
        params.put(TERM_EXCESS, termExcess);
        params.put(TERM_AP, termAP);
        params.put(STRUCTURE, structure);
        return params;
    }

    /**
     *
     *
     * @param period contracts of this period should be returned, normally this is the current period
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @param base defines which property of the underwritingInfoPackets should be used for scaling. Depending on the
     *             contracts are parametrized, this parameter is ignored and instead a local strategy parameter is used
     * @param termDeductible deductible shared among several contracts
     * @param termLimit limit shared among several contracts
     * @param claims
     * @return list containing ProRataTermXLContract contracts
     */
    public List<IReinsuranceContract> getContracts(int period,
                                                   List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                                   IPeriodDependingThresholdStore termDeductible, IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims) {
        initContractsByPeriodAndPeriodCovered();

        // setting defaults

        IRIPremiumSplitStrategy premiumAllocation = PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, Collections.EMPTY_MAP);
        IStabilizationStrategy stabilization = StabilizationStrategyType.getDefault();
        List<Double> reinstatementPremiumFactors = Collections.emptyList();

        double contractBase = UnderwritingInfoUtils.scalingFactor(underwritingInfoPackets, base);

        List<IReinsuranceContract> contracts = new ArrayList<IReinsuranceContract>();
        for (LayerParameters layer : contractsByPeriod.getLayers(period)) {
            contracts.add(new ProRataTermXLContract(layer, period, contractBase, stabilization,
                    reinstatementPremiumFactors, premiumAllocation, termDeductible, termLimit));
        }
        return contracts;
    }

    /**
     * fill contractsByPeriod based on the parameters in structure
     */
    private void initContractsByPeriodAndPeriodCovered() {
        if (contractsByPeriod == null) {
            contractsByPeriod = new PeriodLayerParameters();
            for (int row = structure.getTitleRowCount(); row < structure.getRowCount(); row++) {
                int period = InputFormatConverter.getInt(structure.getValueAt(row, LayerConstraints.CONTRACT_PERIOD_COLUMN_INDEX)) - 1;
                int layer = InputFormatConverter.getInt(structure.getValueAt(row, LayerConstraints.LAYER_COLUMN_INDEX));
                double share = InputFormatConverter.getDouble(structure.getValueAt(row, LayerConstraints.SHARE_COLUMN_INDEX));
                double attachmentPoint = InputFormatConverter.getDouble(structure.getValueAt(row, LayerConstraints.CLAIM_EXCESS_COLUMN_INDEX));
                double limit = InputFormatConverter.getDouble(structure.getValueAt(row, LayerConstraints.CLAIM_LIMIT_COLUMN_INDEX));
                limit = limit == 0 ? Double.POSITIVE_INFINITY : limit;
                double aggregateDeductible = InputFormatConverter.getDouble(structure.getValueAt(row, LayerConstraints.PERIOD_EXCESS_COLUMN_INDEX));
                double aggregateLimit = InputFormatConverter.getDouble(structure.getValueAt(row, LayerConstraints.PERIOD_LIMIT_COLUMN_INDEX));
                aggregateLimit = aggregateLimit == 0 ? Double.POSITIVE_INFINITY : aggregateLimit;
                double additionalPremium = InputFormatConverter.getDouble(structure.getValueAt(row, LayerConstraints.AP_COLUMN_INDEX));
                APBasis additionalPremiumBasis = APBasis.valueOf((String) structure.getValueAt(row, LayerConstraints.BASIS_COLUMN_INDEX));
                minPeriodCovered = Math.min(period, minPeriodCovered);
                maxPeriodCovered = Math.max(period, maxPeriodCovered);
                contractsByPeriod.add(period, layer, share, attachmentPoint, limit, aggregateDeductible, aggregateLimit,
                        additionalPremium, additionalPremiumBasis);
            }
        }
    }

    public boolean covered(int period) {
        initContractsByPeriodAndPeriodCovered();
        return true;
    }



    public double getTermDeductible() {
        return termExcess;
    }

    public AllTermAPLayers getTermLayers() {
         List<Double> excesses = (List<Double>) termAP.getValuesAsObjects(AdditionalPremiumConstraints.EXCESS_COLUMN_INDEX);
         List<Double> limits = (List<Double>) termAP.getValuesAsObjects(AdditionalPremiumConstraints.LIMIT_COLUMN_INDEX);
         List<Double> rates = (List<Double>) termAP.getValuesAsObjects(AdditionalPremiumConstraints.RATE_COLUMN_INDEX);

        Collection<TermLayer> termLayerses = Lists.newArrayList();
        for (int i = 0; i < excesses.size(); i++) {
            double excess = excesses.get(i);
            double limit = limits.get(i);
            double rate = rates.get(i);

            final TermLayer termLayer = new TermLayer(limit, excess, rate);
            termLayerses.add(termLayer);
        }
        return new AllTermAPLayers(termLayerses);
    }

    /**
     * @return termLimit or Double.POSITIVE_INFINITY if termLimit was not set or set equal to zero
     */
    public double getTermLimit() {
        return termLimit == 0 ? Double.POSITIVE_INFINITY : termLimit;
    }

    public PeriodLayerParameters getContractsByPeriod() {
        initContractsByPeriodAndPeriodCovered();
        return contractsByPeriod;
    }

    public ScaledPeriodLayerParameters scalablePeriodLayerParameters() {
        initContractsByPeriodAndPeriodCovered();
        return new ScaledPeriodLayerParameters(contractsByPeriod);
    }

    public static final String TERM_LIMIT = "termLimit";
    public static final String TERM_EXCESS = "termExcess";
    public static final String TERM_AP = "termAP";
    public static final String STRUCTURE = "structure";
}
