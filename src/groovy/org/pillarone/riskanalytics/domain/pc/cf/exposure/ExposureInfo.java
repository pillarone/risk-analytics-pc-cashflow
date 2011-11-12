package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExposureInfo {

    private static Log LOG = LogFactory.getLog(ExposureInfo.class);

    private DateTime inceptionDate;
    private Integer inceptionPeriod;

    private double sumInsured;
    private double maxSumInsured;
    private ExposureBase exposureDefinition;

    public ExposureInfo(DateTime inceptionDate, IPeriodCounter periodCounter) {
        this.inceptionDate = inceptionDate;
        try {
            inceptionPeriod = periodCounter.belongsToPeriod(inceptionDate);
        }
        catch (NotInProjectionHorizon ex) {
            LOG.debug("inceptionDate " + inceptionDate + " is not in projection horizon!");
        }
    }

    public ExposureInfo(PeriodScope periodScope) {
        this(periodScope.getPeriodCounter().getCurrentPeriodStart(), periodScope.getPeriodCounter());
    }

    private ExposureInfo(DateTime inceptionDate, Integer inceptionPeriod, double sumInsured, double maxSumInsured,
                        ExposureBase exposureDefinition) {
        this.inceptionDate = inceptionDate;
        this.inceptionPeriod = inceptionPeriod;
        this.sumInsured = sumInsured;
        this.maxSumInsured = maxSumInsured;
        this.exposureDefinition = exposureDefinition;
    }

    public ExposureInfo withScale(double scale) {
        return new ExposureInfo(inceptionDate, inceptionPeriod, sumInsured * scale, maxSumInsured * scale, exposureDefinition);
    }

    public DateTime getInceptionDate() {
        return inceptionDate;
    }

    public Integer getInceptionPeriod() {
        return inceptionPeriod;
    }

    public double getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(double sumInsured) {
        this.sumInsured = sumInsured;
    }

    public double getMaxSumInsured() {
        return maxSumInsured;
    }

    public void setMaxSumInsured(double maxSumInsured) {
        this.maxSumInsured = maxSumInsured;
    }

    public ExposureBase getExposureDefinition() {
        return exposureDefinition;
    }

    public void setExposureDefinition(ExposureBase exposureDefinition) {
        this.exposureDefinition = exposureDefinition;
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(sumInsured);
        result.append(separator);
        result.append(maxSumInsured);
        result.append(separator);
        result.append(exposureDefinition);
        return result.toString();
    }
}
