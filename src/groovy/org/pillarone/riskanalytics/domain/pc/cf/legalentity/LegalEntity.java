package org.pillarone.riskanalytics.domain.pc.cf.legalentity;

import org.pillarone.riskanalytics.core.components.MultiPhaseComponent;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.constant.Rating;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LegalEntity extends MultiPhaseComponent implements ILegalEntityMarker {

    private Rating parmRating = Rating.NO_DEFAULT;

    @Override
    public void doCalculation(String phase) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void allocateChannelsToPhases() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Rating getParmRating() {
        return parmRating;
    }

    public void setParmRating(Rating parmRating) {
        this.parmRating = parmRating;
    }
}
