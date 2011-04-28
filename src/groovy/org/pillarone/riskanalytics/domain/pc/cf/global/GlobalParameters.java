package org.pillarone.riskanalytics.domain.pc.cf.global;

import org.pillarone.riskanalytics.core.components.GlobalParameterComponent;
import org.pillarone.riskanalytics.core.parameterization.global.Global;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class GlobalParameters extends GlobalParameterComponent {

    private boolean parmGenerateNewClaimsInFirstPeriodOnly = true;

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
}
