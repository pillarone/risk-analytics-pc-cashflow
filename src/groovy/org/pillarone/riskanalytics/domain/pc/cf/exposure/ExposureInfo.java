package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.joda.time.DateTime;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExposureInfo {

    private DateTime inceptionDate;

    public ExposureInfo(DateTime inceptionDate) {

        this.inceptionDate = inceptionDate;
    }

    public DateTime getInceptionDate() {
        return inceptionDate;
    }
}
