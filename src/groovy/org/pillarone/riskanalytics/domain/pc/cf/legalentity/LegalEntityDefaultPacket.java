package org.pillarone.riskanalytics.domain.pc.cf.legalentity;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LegalEntityDefaultPacket extends SingleValuePacket {

    private ILegalEntityMarker legalEntity;
    private boolean defaultOccurred = false;

    public LegalEntityDefaultPacket() {
    }

    public LegalEntityDefaultPacket(ILegalEntityMarker legalEntity, boolean defaultOccurred) {
        this.legalEntity = legalEntity;
        this.defaultOccurred = defaultOccurred;
        this.value = defaultOccurred ? 1d: 0d;
    }

    @Override
    public String toString() {
        return new StringBuffer(legalEntity.getNormalizedName()).append(" is default: ").append(defaultOccurred).toString();
    }

    @Override
    public double getValue() {
        return defaultOccurred ? 1d : 0d;
    }

    @Override
    public void setValue(double value) {
        this.value = defaultOccurred ? 1d: 0d;
    }

    public boolean isDefaultOccurred() {
        return defaultOccurred;
    }

    public void setDefaultOccurred(boolean defaultOccurred) {
        this.defaultOccurred = defaultOccurred;
        this.value = defaultOccurred ? 1d: 0d;
    }

    public ILegalEntityMarker getLegalEntity() {
        return legalEntity;
    }

    public void setLegalEntity(ILegalEntityMarker legalEntity) {
        this.legalEntity = legalEntity;
    }
}
