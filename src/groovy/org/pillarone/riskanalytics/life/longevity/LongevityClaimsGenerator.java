package org.pillarone.riskanalytics.life.longevity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.life.longevity.PolicyConstraints;

import java.util.Arrays;
import java.util.List;

/**
 * @author simon.parten (at) art-allianz (dot) com
 */
public class LongevityClaimsGenerator extends AbstractClaimsGenerator {

    static Log LOG = LogFactory.getLog(LongevityClaimsGenerator.class);

    public static final String ACTUAL_MALE_MORTALITY = "actual male mortality";
    public static final String ACTUAL_FEMALE_MORTALITY = "actual female mortality";
    public static final String HISTORIC_FEMALE_MORTALITY = "historic female mortality";
    public static final String HISTORIC_MALE_MORTALITY = "historic male mortality";

    public static final String MALE_SURVIVAL_RATES = "male survival rates";
    public static final String FEMALE_SURVIVAL_RATES = "female survival rates";

    public static final String MALE_INDEXED_TABLE = "male indexed table";
    public static final String FEMALE_INDEXED_TABLE = "female indexed table";

    public static final String POLICY_DATA = "policy data";

    private ConstrainedMultiDimensionalParameter parmPolicyData = new ConstrainedMultiDimensionalParameter(
            Arrays.asList(""), PolicyConstraints.columnnHeaders, new PolicyConstraints());

    private ConstrainedString parmMaleMortality = new ConstrainedString(IMortalityTableMarker.class, "");
    private ConstrainedString parmFemaleMortality = new ConstrainedString(IMortalityTableMarker.class, "");

    private ConstrainedMultiDimensionalParameter parmMaleImprovementIndex = new ConstrainedMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Index"), new SeverityIndexSelectionTableConstraints());
    private ConstrainedMultiDimensionalParameter parmFemaleImprovementIndex = new ConstrainedMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Index"), new SeverityIndexSelectionTableConstraints());

    private IterationStore iterationStore;

    protected void doCalculation(String phase) {
        try {
            initSimulation(phase);
            initIteration(periodStore, periodScope, phase);

//        In this phase check the commutation state from last period. If we are not commuted then calculate claims.
            if (provideClaims(phase)) {
                IPeriodCounter periodCounter = periodScope.getPeriodCounter();
                DateTime periodStartDate = periodScope.getCurrentPeriodStartDate();
                if(parmMaleImprovementIndex.getColumn(0).size() > 1) {
                    throw new SimulationException("");
                }
                List<Factors> maleSeverityFactors = IndexUtils.filterFactors( inFactors, parmMaleImprovementIndex, IndexMode.STEPWISE_PREVIOUS, BaseDateMode.START_OF_PROJECTION, periodStartDate );
                List<Factors> femaleSeverityFactors = IndexUtils.filterFactors( inFactors, parmFemaleImprovementIndex, IndexMode.STEPWISE_PREVIOUS, BaseDateMode.START_OF_PROJECTION, periodStartDate );
                IIndexedMortalityTable maleIndexedTable = ((IIndexedMortalityTable) periodStore.get(MALE_INDEXED_TABLE, -periodScope.getCurrentPeriod()));
                IIndexedMortalityTable femaleIndexedTable = ((IIndexedMortalityTable) periodStore.get(FEMALE_INDEXED_TABLE, -periodScope.getCurrentPeriod()));

                maleIndexedTable.addIndexValue(((Number) periodStartDate.get(DateTimeFieldType.year())).doubleValue(), maleSeverityFactors.get(0).getIncrementalFactor(periodStartDate));
                femaleIndexedTable.addIndexValue(((Number) periodStartDate.get(DateTimeFieldType.year())).doubleValue(), femaleSeverityFactors.get(0).getIncrementalFactor(periodStartDate));
                IMortalityTable maleSurvivalRates = ((IMortalityTable) periodStore.get(MALE_SURVIVAL_RATES, -periodScope.getCurrentPeriod()));
                IMortalityTable femaleSurvivalRates = ((IMortalityTable) periodStore.get(FEMALE_SURVIVAL_RATES, -periodScope.getCurrentPeriod()));

                PolicyData policyData = (PolicyData) iterationStore.get(POLICY_DATA, -periodScope.getCurrentPeriod());
                outClaims.addAll(policyData.claimsInPeriod(maleSurvivalRates, femaleSurvivalRates, periodScope));

                int i = 0;
            }
            else if(phase.equals(PHASE_STORE_COMMUTATION_STATE)) {
                prepareProvidingClaimsInNextPeriodOrNot(phase);
            } else throw new SimulationException("Unknown phase");
        }
        catch (SimulationException e) {
            throw new SimulationException("Problem in claims generator in iteration : "
                    + iterationScope.getCurrentIteration() + ". Period :" + periodScope.getCurrentPeriod()
                    + " with seed : " + simulationScope.getSimulation().getRandomSeed().toString()
                    +  "\n \n " + e.getMessage(), e);
        }
    }

    @Override
    protected void initIteration(PeriodStore periodStore, PeriodScope periodScope, String phase) {
        if(periodScope.isFirstPeriod() && phase.equals(PHASE_CLAIMS_CALCULATION)) {
//            IMortalityTable maleMortalityRate2010 = parmMaleMortality.getBusinessMortalityRate2010();
//            IMortalityTable maleActualMortalityRates = parmMaleMortality.getBusinessActualMortality();
//            IIndexedMortalityTable maleMortalityIndexedFrom2010Rates = new CombineWithRateActualMortalityTable(maleActualMortalityRates, maleMortalityRate2010, "Simulated Q(x) Male With Index");
//            IMortalityTable maleMortalityRatesAfterSchedule4 = new CombineMortalityTableWithFirstYearTable(parmMaleMortality.getBusinessMortalityRates(), maleMortalityIndexedFrom2010Rates, "Simulated Q(x) Male Combined with Schedule A");
//            IMortalityTable maleSurvivalRates = new SingleMortalityTableTransformStrategy(maleMortalityRatesAfterSchedule4, "Survival Male");

//            IMortalityTable femaleMortalityRate2010 = parmFemaleMortality.getBusinessMortalityRate2010();
//            IMortalityTable femaleActualMortalityRates = parmFemaleMortality.getBusinessActualMortality();
//            IIndexedMortalityTable femaleMortalityIndexedFrom2010Rates = new CombineWithRateActualMortalityTable(femaleActualMortalityRates, femaleMortalityRate2010, "Simulated Q(x) Female");
//            IMortalityTable femaleMortalityRatesAfterSchedule4 = new CombineMortalityTableWithFirstYearTable(parmFemaleMortality.getBusinessMortalityRates(), femaleMortalityIndexedFrom2010Rates, "Simulated Q(x) FeMale");
//            IMortalityTable femaleSurvivalRates = new SingleMortalityTableTransformStrategy(femaleMortalityRatesAfterSchedule4, "Survival Male");

//            periodStore.put(MALE_INDEXED_TABLE, maleMortalityIndexedFrom2010Rates);
//            periodStore.put(FEMALE_INDEXED_TABLE, femaleMortalityIndexedFrom2010Rates);
//
//            periodStore.put(MALE_SURVIVAL_RATES, maleSurvivalRates);
//            periodStore.put(FEMALE_SURVIVAL_RATES, femaleSurvivalRates);

        }
        super.initIteration(periodStore, periodScope, phase);    //To change body of overridden methods use File | Settings | File Templates.

    }

    private void initSimulation(String phase){
        if(iterationScope.isFirstIteration() && periodScope.isFirstPeriod() && phase.equals(PHASE_CLAIMS_CALCULATION) ) {
//            IMortalityTable actualMaleMortality = parmMaleMortality.getBusinessActualMortality();
//            IMortalityTable actualFemaleMortality = parmFemaleMortality.getBusinessActualMortality();
//
//            IMortalityTable historicMaleMortality = parmMaleMortality.getBusinessHistoricMortality();
//            IMortalityTable historicFemaleMortality = parmFemaleMortality.getBusinessHistoricMortality();

//            iterationStore.put(ACTUAL_MALE_MORTALITY, actualMaleMortality);
//            iterationStore.put(ACTUAL_FEMALE_MORTALITY, actualFemaleMortality);
//            iterationStore.put(HISTORIC_MALE_MORTALITY, historicMaleMortality);
//            iterationStore.put(HISTORIC_FEMALE_MORTALITY, historicFemaleMortality);

            PolicyData policyData = new PolicyData(parmPolicyData, periodScope.getCurrentPeriodStartDate());
            iterationStore.put(POLICY_DATA, policyData);

        }
    }

    public ConstrainedMultiDimensionalParameter getParmPolicyData() {
        return parmPolicyData;
    }

    public void setParmPolicyData(ConstrainedMultiDimensionalParameter parmPolicyData) {
        this.parmPolicyData = parmPolicyData;
    }

    public ConstrainedMultiDimensionalParameter getParmMaleImprovementIndex() {
        return parmMaleImprovementIndex;
    }

    public void setParmMaleImprovementIndex(ConstrainedMultiDimensionalParameter parmMaleImprovementIndex) {
        this.parmMaleImprovementIndex = parmMaleImprovementIndex;
    }

    public ConstrainedMultiDimensionalParameter getParmFemaleImprovementIndex() {
        return parmFemaleImprovementIndex;
    }

    public void setParmFemaleImprovementIndex(ConstrainedMultiDimensionalParameter parmFemaleImprovementIndex) {
        this.parmFemaleImprovementIndex = parmFemaleImprovementIndex;
    }

    public ConstrainedString getParmMaleMortality() {
        return parmMaleMortality;
    }

    public void setParmMaleMortality(ConstrainedString parmMaleMortality) {
        this.parmMaleMortality = parmMaleMortality;
    }

    public ConstrainedString getParmFemaleMortality() {
        return parmFemaleMortality;
    }

    public void setParmFemaleMortality(ConstrainedString parmFemaleMortality) {
        this.parmFemaleMortality = parmFemaleMortality;
    }

    public IterationScope getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(IterationScope iterationScope) {
        this.iterationScope = iterationScope;
    }

    public IterationStore getIterationStore() {
        return iterationStore;
    }

    public void setIterationStore(IterationStore iterationStore) {
        this.iterationStore = iterationStore;
    }
}
