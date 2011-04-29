package org.pillarone.riskanalytics.domain.pc.cf.global;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.GlobalParameterComponent;
import org.pillarone.riskanalytics.core.parameterization.global.Global;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GlobalParameters extends GlobalParameterComponent {

    private DateTime parmProjectionStartDate = new DateTime(2011,1,1,0,0,0,0);
    private boolean parmGenerateNewClaimsInFirstPeriodOnly = true;

    @Global(identifier = "projectionStartDate")
    public DateTime projectionStartDate() {
        return parmProjectionStartDate;
    }

    @Global(identifier = "generateNewClaimsInFirstPeriodOnly")
    public boolean generateNewClaimsInFirstPeriodOnly() {
        return parmGenerateNewClaimsInFirstPeriodOnly;
    }

    public boolean isParmGenerateNewClaimsInFirstPeriodOnly() {
        return parmGenerateNewClaimsInFirstPeriodOnly;
    }

    public void setParmGenerateNewClaimsInFirstPeriodOnly(boolean parmGenerateNewClaimsInFirstPeriodOnly) {
        this.parmGenerateNewClaimsInFirstPeriodOnly = parmGenerateNewClaimsInFirstPeriodOnly;
    }

    public DateTime getParmProjectionStartDate() {
        return parmProjectionStartDate;
    }

    public void setParmProjectionStartDate(DateTime parmProjectionStartDate) {
        this.parmProjectionStartDate = parmProjectionStartDate;
    }
}
