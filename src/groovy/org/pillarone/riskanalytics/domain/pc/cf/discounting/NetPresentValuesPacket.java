package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class NetPresentValuesPacket extends MultiValuePacket {

    private double netPresentValueGross;
    private double netPresentValueCeded;
    private double netPresentValueNet;

    public double getNetPresentValueGross() {
        return netPresentValueGross;
    }

    public void setNetPresentValueGross(double netPresentValueGross) {
        this.netPresentValueGross = netPresentValueGross;
    }

    public double getNetPresentValueCeded() {
        return netPresentValueCeded;
    }

    public void setNetPresentValueCeded(double netPresentValueCeded) {
        this.netPresentValueCeded = netPresentValueCeded;
    }

    public double getNetPresentValueNet() {
        return netPresentValueNet;
    }

    public void setNetPresentValueNet(double netPresentValueNet) {
        this.netPresentValueNet = netPresentValueNet;
    }
}
