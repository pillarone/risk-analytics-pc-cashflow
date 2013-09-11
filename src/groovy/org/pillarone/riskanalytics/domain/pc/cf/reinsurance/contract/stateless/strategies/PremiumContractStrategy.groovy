package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IPremiumContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.PremiumStructreAPBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureAPConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureProfitCommissionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureReinstatementConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.ContractOrderingMethod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.PremiumContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier;
import sun.reflect.generics.reflectiveObjects.NotImplementedException

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PremiumContractStrategy extends AbstractParameterObject implements IPremiumContractStrategy {

    private static Log LOG = LogFactory.getLog(PremiumContractStrategy.class);

    private ConstrainedMultiDimensionalParameter structure = new ConstrainedMultiDimensionalParameter(
            [[1],[1],[1d],[0d],[0d],[0d],[0d],[0d], [0d], PremiumStructreAPBasis.PREMIUM],
            PremiumStructureConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureConstraints.IDENTIFIER));

    private ConstrainedMultiDimensionalParameter reinstatements = new ConstrainedMultiDimensionalParameter(
            [[1],[1],[0d]],
            PremiumStructureReinstatementConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureReinstatementConstraints.IDENTIFIER));

    private ConstrainedMultiDimensionalParameter additionalPremiums = new ConstrainedMultiDimensionalParameter(
            [[1],[1],[0d],[0d],[0d]],
            PremiumStructureAPConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureAPConstraints.IDENTIFIER));

    private ConstrainedMultiDimensionalParameter profitCommission = new ConstrainedMultiDimensionalParameter(
            [[1],[1],[0d],[0d],],
            PremiumStructureProfitCommissionConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureProfitCommissionConstraints.IDENTIFIER));
    private double termLimit = 0d;
    private double termExcess = 0d;
    private double ncbPercentage = 0d;
    private PremiumStructreAPBasis apBasis = PremiumStructreAPBasis.LOSS

    private ContractOrderingMethod orderingMethod = ContractOrderingMethod.PAID

    public IParameterObjectClassifier getType() {
        return PremiumContractType.XOL;
    }

    public Map getParameters() {
        Map<String, Object> params = [:];
        params.put(STRUCTURE, structure);
        params.put(REINSTATEMENTS, reinstatements);
        params.put(ADDITIONALPREMIUMS, additionalPremiums);
        params.put(PROFITCOMMISSION, profitCommission);
        params.put(TERM_LIMIT, termLimit);
        params.put(TERM_EXCESS, termExcess);
        params.put(ORDERING_METHOD, orderingMethod);
        params.put(NCB_PERCENTAGE, ncbPercentage);
        params.put(AP_BASIS, apBasis);
        return params;
    }

    PremiumContractStrategy(
            final ConstrainedMultiDimensionalParameter structure,
            final ConstrainedMultiDimensionalParameter reinstatements,
            final ConstrainedMultiDimensionalParameter additionalPremiums,
            final ConstrainedMultiDimensionalParameter profitCommission,
            final double termLimit,
            final double termExcess,
            final ContractOrderingMethod orderingMethod) {
        this.structure = structure
        this.reinstatements = reinstatements
        this.additionalPremiums = additionalPremiums
        this.profitCommission = profitCommission
        this.termLimit = termLimit
        this.termExcess = termExcess
        this.orderingMethod = orderingMethod
    }

    private HashMap<YearLayerIdentifier, ContractLayer> hydrateDataStructure() {
        HashMap<YearLayerIdentifier, ContractLayer> contractLayers = Maps.newHashMap()

        List<ReinstatementLayer> reinstatementLayers = constructReinstatementLayers()
        List<AdditionalPremiumLayer> additionalPremiumLayers = constructAdditionalPremiumLayers()
        List<ProfitCommissions> commissionses = contructProfitCommissions()


        List<Integer> years = structure.getValuesAsObjects( PremiumStructureConstraints.CONTRACT_PERIOD_COLUMN_INDEX )
        List<Integer> layers = structure.getValuesAsObjects( PremiumStructureConstraints.LAYER_COLUMN_INDEX )
        List<Double> shares = structure.getValuesAsObjects( PremiumStructureConstraints.SHARE_COLUMN_INDEX )
        List<Double> verticalLimits = structure.getValuesAsObjects( PremiumStructureConstraints.CLAIM_LIMIT_COLUMN_INDEX )
        List<Double> verticalExcesses = structure.getValuesAsObjects( PremiumStructureConstraints.CLAIM_EXCESS_COLUMN_INDEX )
        List<Double> periodExcess = structure.getValuesAsObjects( PremiumStructureConstraints.PERIOD_EXCESS_COLUMN_INDEX )
        List<Double> periodLimit = structure.getValuesAsObjects( PremiumStructureConstraints.PERIOD_LIMIT_COLUMN_INDEX)
        List<Double> initialPremiums = structure.getValuesAsObjects( PremiumStructureConstraints.INITIAL_PREMIUM)
        List<Double> ncbPercentages = structure.getValuesAsObjects( PremiumStructureConstraints.NCB_PERCENTAGE)

        for( i in 0 ..  years.size() - 1) {
            Integer aYear = years.get(i)
            Integer aLayer = layers.get(i)
            Double aShare = shares.get(i)
            Double verticalLimit = verticalLimits.get(i)
            Double verticalExcess = verticalExcesses.get(i)
            Double periodXS = periodExcess.get(i)
            Double periodLimi = periodLimit.get(i)
            Double intialPremium = initialPremiums.get(i)
            Double ncbPercentage = ncbPercentages.get(i)

            Collection<ReinstatementLayer> thisLayerReinstatements = reinstatementLayers.findAll {
                it -> it.getYearLayerIdentifier().equals(new YearLayerIdentifier(aYear, aLayer))
            }
            reinstatementLayers.removeAll(thisLayerReinstatements)

            Collection<ProfitCommissions> thisLayerPC = commissionses.findAll{
                        it -> it.getYearLayerIdentifier().equals(new YearLayerIdentifier(aYear, aLayer))
            }
            commissionses.removeAll(thisLayerPC)

            Collection<AdditionalPremiumLayer> thisLayerAP = additionalPremiumLayers.findAll{
                        it -> it.getIdentifier().equals(new YearLayerIdentifier(aYear, aLayer))
            }
            additionalPremiumLayers.removeAll(thisLayerAP)

            ContractLayer contractLayer = new ContractLayer(
                    new YearLayerIdentifier(aYear, aLayer),
                    aShare, verticalLimit, verticalExcess, periodLimi, periodXS, intialPremium,
                    thisLayerReinstatements, thisLayerAP, thisLayerPC, ncbPercentage
            )
            if(contractLayers.containsKey(new YearLayerIdentifier(aYear, aLayer))) throw new SimulationException("Found duplicate entry for year;" + aYear + " layer;" + aLayer + " please check your contract table")
            contractLayers.put(contractLayer.getIdentifier(), contractLayer)
        }
        checkAllLayersAllocated(reinstatementLayers, commissionses, additionalPremiumLayers)
        return contractLayers
    }

    private void checkAllLayersAllocated(List<ReinstatementLayer> reinstatementLayers, List<ProfitCommissions> commissionses, List<AdditionalPremiumLayer> additionalPremiumLayers) {
        if (reinstatementLayers.size() > 0) {
            String error = "Found orphaned reinstatements layers : "
            for (ReinstatementLayer layer in reinstatementLayers) {
                error = error + " " + layer.toString()
            }
            throw new SimulationException(error)
        }

        if(commissionses.size() > 0) {
            String error = "Found orphaned commission layers : "
            for (ProfitCommissions profitCommission in commissionses) {
                error = error + " " + profitCommission.toString()
            }
            throw new SimulationException(error)
        }
        if(additionalPremiumLayers.size() > 0) {
            String error = "Found orphaned additional premium layers : "
            for (AdditionalPremiumLayer apLayer in additionalPremiumLayers) {
                error = error + " " + apLayer.toString()
            }
            throw new SimulationException(error)
        }
    }

    private List<ProfitCommissions> contructProfitCommissions() {
        List<ProfitCommissions> commissionses = Lists.newArrayList()

        List<Integer> years = profitCommission.getValuesAsObjects( PremiumStructureProfitCommissionConstraints.CONTRACT_PERIOD_COLUMN_INDEX )
        List<Integer> layers = profitCommission.getValuesAsObjects( PremiumStructureProfitCommissionConstraints.LAYER_COLUMN_INDEX )
        List<Double> claimsPercentageOfPremiums = profitCommission.getValuesAsObjects( PremiumStructureProfitCommissionConstraints.CLAIMS_PERCENTAGE_OF_TOTAL_PREMIUM_INDEX)
        List<Double> premiumPercentagePCs = profitCommission.getValuesAsObjects( PremiumStructureProfitCommissionConstraints.PERCENTAGE_OF_PREMIUM_PC_INDEX)

        for( i in 0 ..  years.size() - 1) {
            Integer ayear = years.get(i)
            Integer alayer = layers.get(i)
            Double claimsPercentage = claimsPercentageOfPremiums.get(i)
            Double premiumPercentagePC = premiumPercentagePCs.get(i)
            final ProfitCommissions commissions = new ProfitCommissions(premiumPercentagePC, claimsPercentage, new YearLayerIdentifier(ayear, alayer))
            commissionses.add(commissions)
        }
        return commissionses
    }

    private List<AdditionalPremiumLayer> constructAdditionalPremiumLayers() {
        List<AdditionalPremiumLayer> additionalPremiumLayers = Lists.newArrayList()

        List<Integer> years = additionalPremiums.getValuesAsObjects( PremiumStructureAPConstraints.CONTRACT_PERIOD_COLUMN_INDEX )
        List<Integer> layers = additionalPremiums.getValuesAsObjects( PremiumStructureAPConstraints.LAYER_COLUMN_INDEX )
        List<Double> limitStarts =  additionalPremiums.getValuesAsObjects( PremiumStructureAPConstraints.LIMIT_START_INDEX)
        List<Double> limitTopBands =  additionalPremiums.getValuesAsObjects( PremiumStructureAPConstraints.LIMIT_TOP_BAND_INDEX)
        List<Double> apPercLimitUseds = additionalPremiums.getValuesAsObjects( PremiumStructureAPConstraints.AP_PERC_OF_LIMIT_INDEX)

        for( i in 0 ..  years.size() - 1) {
            Integer ayear = years.get(i)
            Integer alayer = layers.get(i)
            Double limitStart = limitStarts.get(i)
            Double limitTopBand = limitTopBands.get(i)
            Double apPercLimitUsed = apPercLimitUseds.get(i)
            final AdditionalPremiumLayer layer = new AdditionalPremiumLayer(new YearLayerIdentifier(ayear, alayer), limitStart, limitTopBand, apPercLimitUsed, apBasis)
            additionalPremiumLayers.add(layer)
        }
        return additionalPremiumLayers
    }

    private List<ReinstatementLayer> constructReinstatementLayers() {
        List<ReinstatementLayer> layerList = Lists.newArrayList()
        List<Integer> years = reinstatements.getValuesAsObjects( PremiumStructureReinstatementConstraints.CONTRACT_PERIOD_COLUMN_INDEX )
        List<Integer> layers = reinstatements.getValuesAsObjects( PremiumStructureReinstatementConstraints.LAYER_COLUMN_INDEX )
        List<Double> percentages = reinstatements.getValuesAsObjects( PremiumStructureReinstatementConstraints.REINSTATEMENT_PRECENTAGE_INDEX )

        int priority = 0
        for( i in 0 ..  years.size() - 1) {
            Integer anInt = years.get(i)
            Integer alayer = layers.get(i)
            Double percentage = percentages.get(i)
            final ReinstatementLayer riLayer = new ReinstatementLayer(new YearLayerIdentifier(anInt, alayer), priority, percentage);
            layerList.add(riLayer)
            priority++
        }
        return layerList
    }

    public List<IReinsuranceContract> getContracts(int period,
                                                   List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                                   IPeriodDependingThresholdStore termDeductible, IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims) {
        throw new NotImplementedException();
    }

    public static final String TERM_LIMIT = NonPropTemplateContractStrategy.TERM_LIMIT;
    public static final String TERM_EXCESS = NonPropTemplateContractStrategy.TERM_EXCESS;
    public static final String STRUCTURE = NonPropTemplateContractStrategy.STRUCTURE;
    public static final String REINSTATEMENTS = "reinstatements";
    public static final String ADDITIONALPREMIUMS = "additionalPremiums";
    public static final String PROFITCOMMISSION = "profitCommission";
    public static final String ORDERING_METHOD = "orderingMethod";
    public static final String AP_BASIS = "apBasis";
    public static final String NCB_PERCENTAGE = "ncbPercentage";

    public ContractStructure getContractStructure() {
        HashMap<YearLayerIdentifier, ContractLayer> layers = hydrateDataStructure()
        return new ContractStructure(termLimit, termExcess, layers, orderingMethod);

    }
}
