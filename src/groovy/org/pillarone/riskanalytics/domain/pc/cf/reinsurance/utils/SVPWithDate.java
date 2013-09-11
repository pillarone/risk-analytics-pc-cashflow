package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

/**
 * author simon.parten @ art-allianz . com
 */
public class SVPWithDate extends SingleValuePacket {

    public SVPWithDate() {
    }

    public SVPWithDate(final double value, final DateTime dateTime) {
        super(value);
        this.setDate(dateTime);
    }
}
