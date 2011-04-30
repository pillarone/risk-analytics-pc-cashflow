package org.pillarone.riskanalytics.domain.pc.cf.event;


import org.pillarone.riskanalytics.core.packets.Packet;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventPacket extends Packet {

    @Override
    public String toString() {
        return new StringBuilder().append(getDate()).toString();
    }
}
