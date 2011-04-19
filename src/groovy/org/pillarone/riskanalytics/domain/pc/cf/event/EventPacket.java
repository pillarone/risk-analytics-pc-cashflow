package org.pillarone.riskanalytics.domain.pc.cf.event;


import org.pillarone.riskanalytics.core.packets.Packet;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class EventPacket extends Packet {
    private double fractionOfPeriod;

    @Override
    public String toString() {
        return new StringBuilder().append("fractionOfPeriod (fraction of period): ").append(fractionOfPeriod).toString();
    }

    public double getFractionOfPeriod() {
        return fractionOfPeriod;
    }

    public void setFractionOfPeriod(double fractionOfPeriod) {
        this.fractionOfPeriod = fractionOfPeriod;
    }
}
