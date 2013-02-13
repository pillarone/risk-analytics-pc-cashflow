package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

/**
 * author simon.parten @ art-allianz . com
 */
public class APSingleValuePacket extends SingleValuePacket {

    final private AdditionalPremium additionalPremium;

    public APSingleValuePacket() {
        this.additionalPremium = null;
    }

    public APSingleValuePacket(double value, AdditionalPremium additionalPremium, DateTime dateTime) {
        super(value);
        this.additionalPremium = additionalPremium;
        setDate(dateTime);
    }

    public AdditionalPremium getAdditionalPremium() {
        return additionalPremium;
    }
}
