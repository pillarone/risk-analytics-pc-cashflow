package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class Frequency extends SingleValuePacket {

    public String getValueLabel() {
        return "count";
    }
}
