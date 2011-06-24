package org.pillarone.riskanalytics.domain.pc.cf.legalentity

import org.pillarone.riskanalytics.core.components.DynamicMultiPhaseComposedComponent
import org.pillarone.riskanalytics.domain.utils.constant.Rating;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LegalEntities extends DynamicMultiPhaseComposedComponent {

    public LegalEntity createDefaultSubComponent(){
        new LegalEntity(parmRating: Rating.NO_DEFAULT)
    }

    void allocateChannelsToPhases() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void doCalculation(String phase) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    void wire() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
