package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.apache.commons.lang.builder.HashCodeBuilder
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TestReferenceClaimKey {
    DateTime occurrenceDate
    DateTime updateDate

    TestReferenceClaimKey(DateTime occurrenceDate, DateTime updateDate) {
        this.occurrenceDate = occurrenceDate
        this.updateDate = updateDate
    }

    @Override
    int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(occurrenceDate);
        hashCodeBuilder.append(updateDate);
        return hashCodeBuilder.toHashCode();
    }

    @Override
    boolean equals(Object obj) {
        if (obj instanceof TestReferenceClaimKey) {
            return ((TestReferenceClaimKey) obj).occurrenceDate.equals(occurrenceDate) && ((TestReferenceClaimKey) obj).updateDate.equals(updateDate)
        } else {
            return false;
        }
    }

    @Override
    String toString() {
        "${format(occurrenceDate)} (${format(updateDate)})"
    }

    public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy"
    public static DateTimeFormatter formatter = DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT)

    /**
     * @param dateTime
     * @return "dd.MM.yyyy"
     */
    public static String format(DateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return formatter.print(dateTime);
    }
}