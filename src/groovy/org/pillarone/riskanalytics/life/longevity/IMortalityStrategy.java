package org.pillarone.riskanalytics.life.longevity;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IMortalityStrategy extends IParameterObject {

    IMortalityTable getBusinessActualMortality();

    IMortalityTable getBusinessHistoricMortality();

    IMortalityTable getBusinessMortalityRates();

    IMortalityTable getBusinessMortalityRate2010();



}
