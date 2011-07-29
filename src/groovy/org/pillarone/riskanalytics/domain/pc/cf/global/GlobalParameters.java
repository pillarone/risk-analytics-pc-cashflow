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
    private boolean runtimeTrivialPatterns = false;
    private boolean runtimeTrivialIndices = false;

    @Global(identifier = "projectionStartDate")
    public DateTime projectionStartDate() {
        return parmProjectionStartDate;
    }

    @Global(identifier = "generateNewClaimsInFirstPeriodOnly")
    public boolean generateNewClaimsInFirstPeriodOnly() {
        return parmGenerateNewClaimsInFirstPeriodOnly;
    }

    @Global(identifier = "trivialPatterns")
    public boolean trivialPatterns() {
        return isRuntimeTrivialPatterns();
    }

    @Global(identifier = "trivialIndices")
    public boolean trivialIndices() {
        return isRuntimeTrivialIndices();
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

    public boolean isRuntimeTrivialPatterns() {
        return runtimeTrivialPatterns;
    }

    public void setRuntimeTrivialPatterns(boolean runtimeTrivialPatterns) {
        this.runtimeTrivialPatterns = runtimeTrivialPatterns;
    }

    public boolean isRuntimeTrivialIndices() {
        return runtimeTrivialIndices;
    }

    public void setRuntimeTrivialIndices(boolean runtimeTrivialIndices) {
        this.runtimeTrivialIndices = runtimeTrivialIndices;
    }
}
