package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractLayer;

import java.util.List;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IContractStructure {

    /*  These two methods basically do the same thing for different contract data structures*/
    List<LayerParameters> getLayers(int period);

    List<IRiLayer> getContractLayers(int period);
    /* end*/

    double getTermLimit();

    double getTermExcess();
}
