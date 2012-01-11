package org.pillarone.riskanalytics.domain.pc.cf.creditrisk;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;

/**
 * The current default and recovery implementation in ReinsuranceContract allows only one immediate recovery. It's ratio
 * is kept in firstInstantRecovery.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LegalEntityDefault extends SingleValuePacket {

    private ILegalEntityMarker legalEntity;
    private DateTime dateOfDefault;
    /** only the first entry is currently evaluated, invalid patterns are prevented by RecoveryPatternStrategyValidator */
    private PatternPacket recoveryPattern;
    /** if a default occurs this ratio is immediately recovered */
    private Double firstInstantRecovery;

    public LegalEntityDefault() {
    }

    public LegalEntityDefault(ILegalEntityMarker legalEntity, DateTime dateOfDefault, PatternPacket recoveryPattern) {
        this.legalEntity = legalEntity;
        this.dateOfDefault = dateOfDefault;
        this.recoveryPattern = recoveryPattern;
    }

    @Override
    public double getValue() {
        return isDefault() ? 1d : 0d;
    }

    public boolean isDefault() {
        return dateOfDefault != null;
    }
    
    public double getFirstInstantRecovery() {
        if (firstInstantRecovery == null) {
            firstInstantRecovery = 0d;
            if (recoveryPattern != null && recoveryPattern.getCumulativeValues().size() > 0) {
                firstInstantRecovery = recoveryPattern.getCumulativeValues().get(0);
            }
        }
        return firstInstantRecovery;
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
