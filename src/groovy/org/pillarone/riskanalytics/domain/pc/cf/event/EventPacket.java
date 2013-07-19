package org.pillarone.riskanalytics.domain.pc.cf.event;


import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.simulation.SimulationException;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventPacket extends Packet implements IEvent {

    public EventPacket(DateTime date){
        setDate(date);
    }

    public int getEventId() {
        throw new SimulationException("");
    }

    @Override
    public String toString() {
        return new StringBuilder().append(getDate()).toString();
    }
}
