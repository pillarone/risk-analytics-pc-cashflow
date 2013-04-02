package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;

import java.util.Arrays;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class DLongevityMortality extends AbstractParameterObject implements IMortalityStrategy  {

    public ConstrainedMultiDimensionalParameter mortalityRates = new ConstrainedMultiDimensionalParameter(MortalityRatesConstraints.initialList , MortalityRatesConstraints.columnnHeaders, new MortalityRatesConstraints());

    public ConstrainedMultiDimensionalParameter historicMortalityRates = new ConstrainedMultiDimensionalParameter(HistoricMortalityRatesConstraints.initialList(), HistoricMortalityRatesConstraints.columnnHeaders, new HistoricMortalityRatesConstraints());

    public ConstrainedMultiDimensionalParameter mortalityRate2010 = new ConstrainedMultiDimensionalParameter(MortalityRatesConstraints.initialList, MortalityRatesConstraints.columnnHeaders, new MortalityRatesConstraints());

    public ConstrainedMultiDimensionalParameter actualMortalityRate = new ConstrainedMultiDimensionalParameter(MortalityRatesConstraints.initialList, MortalityRatesConstraints.columnnHeaders, new MortalityRatesConstraints());

    public DLongevityMortality(ConstrainedMultiDimensionalParameter mortalityRates, ConstrainedMultiDimensionalParameter historicMortalityRates, ConstrainedMultiDimensionalParameter mortalityRate2010, ConstrainedMultiDimensionalParameter actualMortalityRate) {
        this.mortalityRates = mortalityRates;
        this.historicMortalityRates = historicMortalityRates;
        this.mortalityRate2010 = mortalityRate2010;
        this.actualMortalityRate = actualMortalityRate;
    }

    public DLongevityMortality() {
    }

    public IParameterObjectClassifier getType() {
        return MortalityStrategyType.D_LONGEVITY;
    }

    public Map getParameters() {
        Map<String, Object> aMap = Maps.newHashMap();
        aMap.put(MortalityStrategyType.MORTALITY_RATES, mortalityRates);
        aMap.put(MortalityStrategyType.HISTORIC_MORTALITY_RATES, historicMortalityRates);
        aMap.put(MortalityStrategyType.MORTALITY_RATE2010, mortalityRate2010);
        aMap.put(MortalityStrategyType.ACTUAL_MORTALITY_RATE, actualMortalityRate);
        return aMap;
    }

    public ConstrainedMultiDimensionalParameter getMortalityRates() {
        return mortalityRates;
    }

    public void setMortalityRates(ConstrainedMultiDimensionalParameter mortalityRates) {
        this.mortalityRates = mortalityRates;
    }

    public ConstrainedMultiDimensionalParameter getHistoricMortalityRates() {
        return historicMortalityRates;
    }

    public void setHistoricMortalityRates(ConstrainedMultiDimensionalParameter historicMortalityRates) {
        this.historicMortalityRates = historicMortalityRates;
    }

    public ConstrainedMultiDimensionalParameter getMortalityRate2010() {
        return mortalityRate2010;
    }

    public void setMortalityRate2010(ConstrainedMultiDimensionalParameter mortalityRate2010) {
        this.mortalityRate2010 = mortalityRate2010;
    }

    public ConstrainedMultiDimensionalParameter getActualMortalityRate() {
        return actualMortalityRate;
    }

    public void setActualMortalityRate(ConstrainedMultiDimensionalParameter actualMortalityRate) {
        this.actualMortalityRate = actualMortalityRate;
    }

    public IMortalityTable getBusinessActualMortality() {
        return new ActualMortalityTable(actualMortalityRate, 2010d);
    }

    public IMortalityTable getBusinessHistoricMortality() {
        return new HistoricMortalityTable(historicMortalityRates);
    }

    public IMortalityTable getBusinessMortalityRates() {
        return new ActualMortalityTable(mortalityRates, 2010d);
    }

    public IMortalityTable getBusinessMortalityRate2010() {
        return null;
    }
}
