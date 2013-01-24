package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.apache.commons.lang.builder.HashCodeBuilder
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: detlef
 * Date: 23.01.13
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
class ReferenceClaimKey {
    DateTime occurrenceDate
    DateTime updateDate

    ReferenceClaimKey(DateTime occurrenceDate, DateTime updateDate) {
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
        if (obj instanceof ReferenceClaimKey) {
            return ((ReferenceClaimKey) obj).occurrenceDate.equals(occurrenceDate) && ((ReferenceClaimKey) obj).updateDate.equals(updateDate)
        } else {
            return false;
        }
    }

    @Override
    String toString() {
        "${format(occurrenceDate)} (${format(updateDate)})"
    }
}
