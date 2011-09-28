package org.pillarone.riskanalytics.domain.pc.cf.discounting

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class DiscountingYieldCurves extends DynamicComposedComponent {

    PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket)

    @Override
    Component createDefaultSubComponent() {
        DiscountingYieldCurve discount = new DiscountingYieldCurve();
        return discount
    }

    @Override
    protected void doCalculation() {
        for (DiscountingYieldCurve subComponent : componentList) {
            subComponent.start()
        }
    }

    @Override
    void wire() {
        replicateOutChannels this, 'outFactors'
    }
}
