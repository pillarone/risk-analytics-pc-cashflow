package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.packets.Packet;

/**
 * author simon.parten @ art-allianz . com
 */
public abstract class TypeDrillDownPacket extends MultiValuePacket {

    public abstract String typeDrillDownName();

    public abstract TypeDrillDownPacket plusForAggregateCollection(TypeDrillDownPacket aPacket);
}
