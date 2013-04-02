package org.pillarone.riskanalytics.life.longevity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints;
import org.pillarone.riskanalytics.life.longevity.PolicyContraints;

import java.util.Arrays;

/**
 * @author simon.parten (at) art-allianz (dot) com
 */
public class LongevityClaimsGenerator extends AbstractClaimsGenerator {

    static Log LOG = LogFactory.getLog(LongevityClaimsGenerator.class);

    public static final String ACTUAL_MALE_MORTALITY = "actual male mortality";
    public static final String ACTUAL_FEMALE_MORTALITY = "actual female mortality";
    public static final String HISTORIC_FEMALE_MORTALITY = "historic female mortality";
    public static final String HISTORIC_MALE_MORTALITY = "historic male mortality";

    private ConstrainedMultiDimensionalParameter parmPolicyData = new ConstrainedMultiDimensionalParameter(
            Arrays.asList(""), PolicyContraints.columnnHeaders, new PolicyContraints());

    private IMortalityStrategy parmMaleMortality = MortalityStrategyType.getDefault();
    private IMortalityStrategy parmFemaleMortality = MortalityStrategyType.getDefault();

    private ConstrainedMultiDimensionalParameter parmMaleImprovementIndex = new ConstrainedMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Index"), new SeverityIndexSelectionTableConstraints());
    private ConstrainedMultiDimensionalParameter parmFemaleImprovementIndex = new ConstrainedMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Index"), new SeverityIndexSelectionTableConstraints());

    private IterationStore iterationStore;

    protected void doCalculation(String phase) {
        try {
            initSimulation(phase);
            initIteration(periodStore, periodScope, PHASE_CLAIMS_CALCULATION);

//        In this phase check the commutation state from last period. If we are not commuted then calculate claims.
            if (provideClaims(phase)) {
                IPeriodCounter periodCounter = periodScope.getPeriodCounter();



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

    private void initSimulation(String phase){
        if(iterationScope.isFirstIteration() && periodScope.isFirstPeriod() && phase.equals(PHASE_CLAIMS_CALCULATION) ) {
            IMortalityTable actualMaleMortality = parmMaleMortality.getBusinessActualMortality();
            IMortalityTable actualFemaleMortality = parmFemaleMortality.getBusinessActualMortality();

            IMortalityTable historicMaleMortality = parmMaleMortality.getBusinessHistoricMortality();
            IMortalityTable historicFemaleMortality = parmFemaleMortality.getBusinessHistoricMortality();

            iterationStore.put(ACTUAL_MALE_MORTALITY, actualMaleMortality);
            iterationStore.put(ACTUAL_FEMALE_MORTALITY, actualFemaleMortality);
            iterationStore.put(HISTORIC_MALE_MORTALITY, historicMaleMortality);
            iterationStore.put(HISTORIC_FEMALE_MORTALITY, historicFemaleMortality);


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

    public IMortalityStrategy getParmMaleMortality() {
        return parmMaleMortality;
    }

    public void setParmMaleMortality(IMortalityStrategy parmMaleMortality) {
        this.parmMaleMortality = parmMaleMortality;
    }

    public IMortalityStrategy getParmFemaleMortality() {
        return parmFemaleMortality;
    }

    public void setParmFemaleMortality(IMortalityStrategy parmFemaleMortality) {
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
