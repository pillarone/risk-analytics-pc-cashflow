package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;

import java.util.TreeMap;

/**
 * author simon.parten @ art-allianz . com
 */
public class InitialCededPremium {

    public final ContractLayer contractLayer;
    public final PatternPacket patternPacket;

    public InitialCededPremium(final ContractLayer contractLayer, final PatternPacket patternPacket) {
        this.contractLayer = contractLayer;
        this.patternPacket = patternPacket;
    }

    public final double initialWrittenPremium() {
        return contractLayer.getShare() * contractLayer.getInitialPremium();
    }

    public final double cumulativePremiumPaidAtDate(DateTime valuationDate, PeriodScope periodScope) {
        DateTime patternStartDate = periodScope.getPeriodCounter().startOfPeriod(((int) (contractLayer.getIdentifier().getYear() - 1)));
        TreeMap<DateTime, Double> pattern = patternPacket.absolutePattern(patternStartDate, false);
        return pattern.floorEntry(valuationDate.plusDays(1)).getValue() * initialWrittenPremium();
    }

    public final double incrementalPaid(DateTime startDate, DateTime endDate, PeriodScope periodScope) {
        return cumulativePremiumPaidAtDate(endDate, periodScope) - cumulativePremiumPaidAtDate(startDate, periodScope);
    }

    public ContractLayer getContractLayer() {
        return contractLayer;
    }

    public PatternPacket getPatternPacket() {
        return patternPacket;
    }
}
