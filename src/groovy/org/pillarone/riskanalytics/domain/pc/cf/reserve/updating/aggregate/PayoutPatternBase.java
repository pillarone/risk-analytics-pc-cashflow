package org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum PayoutPatternBase {

    CLAIM_OCCURANCE_DATE{

        Log LOG = LogFactory.getLog(PayoutPatternBase.class);

        @Override
        public DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate) {
            if(firstActualPaidDate == null) {
                return claimRoot.getOccurrenceDate();
            }
            if(firstActualPaidDate.isBefore(claimRoot.getOccurrenceDate())) {
//                LOG.info("Claim generated with occurance date after first actual payment. " +
//                        "Overriding claim payments base date to be the date of the first paid claim, which is: " + DateTimeUtilities.formatDate.print(firstActualPaidDate));
                return firstActualPaidDate;
            }
            return claimRoot.getOccurrenceDate();
        }
    }

    , PERIOD_START_DATE {
        @Override
        public DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate) {
            return contractPeriodStart;
        }
    };


    public abstract DateTime startDateForPayouts(ClaimRoot claimRoot, DateTime contractPeriodStart, DateTime firstActualPaidDate);

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
