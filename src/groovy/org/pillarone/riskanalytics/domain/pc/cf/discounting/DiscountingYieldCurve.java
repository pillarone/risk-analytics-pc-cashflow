package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.marker.ICorrelationMarker;

import java.util.Collections;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): do we need a parameter base value including a date?
public class DiscountingYieldCurve extends Component implements IDiscountMarker, ICorrelationMarker {

    private PeriodScope periodScope;

    private PacketList<FactorsPacket> outFactors = new PacketList<FactorsPacket>(FactorsPacket.class);

    private ConstrainedMultiDimensionalParameter parmYieldCurve = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), YieldCurveTableConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(YieldCurveTableConstraints.IDENTIFIER));

    private FactorsPacket factors;

    @Override
    protected void doCalculation() {
        lazyInitFactors();
        if (factors != null && factors.getFactorsPerDate().size() > 0) {
            outFactors.add(factors);
        }
    }

    protected void lazyInitFactors() {
        if (factors == null && periodScope.isFirstPeriod()) {
            DateTime projectionStartDate = periodScope.getCurrentPeriodStartDate();
            double baseValue = 1d;
            factors = new FactorsPacket();
            int dateColumnIndex = parmYieldCurve.getColumnIndex(YieldCurveTableConstraints.MONTHS);
            int rateColumnIndex = parmYieldCurve.getColumnIndex(YieldCurveTableConstraints.RATE);
            if (parmYieldCurve.getValues().size() > 0) {
                for (int row = parmYieldCurve.getTitleRowCount(); row < parmYieldCurve.getRowCount(); row++) {
                    int months = (Integer) parmYieldCurve.getValueAt(row, dateColumnIndex);
                    DateTime indexDate = projectionStartDate.plusMonths(months);
                    double rate = InputFormatConverter.getDouble(parmYieldCurve.getValueAt(row, rateColumnIndex));
                    double index = baseValue * Math.pow(1 + rate, months / 12);
                    factors.add(indexDate, index);
                    factors.origin = this;
                }
            }
        }
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PacketList<FactorsPacket> getOutFactors() {
        return outFactors;
    }

    public void setOutFactors(PacketList<FactorsPacket> outFactors) {
        this.outFactors = outFactors;
    }

    public ConstrainedMultiDimensionalParameter getParmYieldCurve() {
        return parmYieldCurve;
    }

    public void setParmYieldCurve(ConstrainedMultiDimensionalParameter parmYieldCurve) {
        this.parmYieldCurve = parmYieldCurve;
    }
}

