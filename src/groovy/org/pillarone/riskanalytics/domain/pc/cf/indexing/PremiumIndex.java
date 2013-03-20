package org.pillarone.riskanalytics.domain.pc.cf.indexing;

import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PremiumIndex extends Index implements IPremiumIndexMarker {

    private PacketList<SingleValuePacket> outIndexValue = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    PeriodScope periodScope;
    PeriodStore periodStore;

    @Override
    protected void doCalculation() {
        super.doCalculation();
        PacketList<FactorsPacket> someFactors = new PacketList<FactorsPacket>();
        someFactors.addAll(super.getOutFactors());
        if(someFactors.size() == 1) {
            FactorsPacket factorsPacket = someFactors.get(0);
            Double indexValue = factorsPacket.getFactorAtDate(periodScope.getCurrentPeriodStartDate());
            if(indexValue != null) {
                outIndexValue.add(new SingleValuePacket(indexValue));
            }
        }
    }

    public PacketList<SingleValuePacket> getOutIndexValue() {
        return outIndexValue;
    }

    public void setOutIndexValue(PacketList<SingleValuePacket> outIndexValue) {
        this.outIndexValue = outIndexValue;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }
}
