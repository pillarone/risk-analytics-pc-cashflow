package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AllPeriodUnderwritingInfoPacket extends Packet {
    /**
     * Key; Period *
     */
    private TreeMap<DateTime, UnderwritingInfoPacket> underwritingInfoPerPeriod = new TreeMap<DateTime, UnderwritingInfoPacket>();

    public AllPeriodUnderwritingInfoPacket() {
    }

    public TreeMap<DateTime, UnderwritingInfoPacket> getUnderwritingInfoPerPeriod() {
        return underwritingInfoPerPeriod;
    }

    public void setUnderwritingInfoPerPeriod(TreeMap<DateTime, UnderwritingInfoPacket> underwritingInfoPerPeriod) {
        this.underwritingInfoPerPeriod = underwritingInfoPerPeriod;
    }
}
