package org.pillarone.riskanalytics.domain.pc.cf.creditrisk;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LegalEntityDefault extends SingleValuePacket {

    private ILegalEntityMarker legalEntity;
    private DateTime dateOfDefault;

    public LegalEntityDefault() {
    }

    public LegalEntityDefault(ILegalEntityMarker legalEntity, DateTime dateOfDefault) {
        this.legalEntity = legalEntity;
        this.dateOfDefault = dateOfDefault;
    }

    @Override
    public double getValue() {
        return isDefault() ? 1d : 0d;
    }

    public boolean isDefault() {
        return dateOfDefault != null;
    }

    @Override
    public String toString() {
        if (isDefault()) {
            return new StringBuffer(legalEntity.getNormalizedName()).append(" is default since ").append(dateOfDefault).toString();
        }
        else {
            return new StringBuffer(legalEntity.getNormalizedName()).append(" did not default.").toString();
        }
    }

    public ILegalEntityMarker getLegalEntity() {
        return legalEntity;
    }

    public void setLegalEntity(ILegalEntityMarker legalEntity) {
        this.legalEntity = legalEntity;
    }

    public DateTime getDateOfDefault() {
        return dateOfDefault;
    }

    public void setDateOfDefault(DateTime dateOfDefault) {
        this.dateOfDefault = dateOfDefault;
    }
}
