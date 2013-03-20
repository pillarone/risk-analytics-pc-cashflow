package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class NetPresentValuePacket extends SingleValuePacket {

    private String valueLabel = "net present value";

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }
}
