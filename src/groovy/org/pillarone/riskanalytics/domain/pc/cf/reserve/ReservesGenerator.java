package org.pillarone.riskanalytics.domain.pc.cf.reserve;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker;

import java.util.*;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class ReservesGenerator extends Component implements IReserveMarker {

    private PeriodScope periodScope;
    private PeriodStore periodStore;

    private PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket.class);

    private PacketList<ClaimCashflowPacket> outReserves = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<SingleValuePacket> outNominalUltimates = new PacketList<SingleValuePacket>(SingleValuePacket.class);

    private ConstrainedString parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker.class, "");
    private ConstrainedString parmReportingPattern = new ConstrainedString(IReportingPatternMarker.class, "");
    private ConstrainedMultiDimensionalParameter parmIndices = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), ReservesIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(ReservesIndexSelectionTableConstraints.IDENTIFIER));
    private IReserveCalculationStrategy parmUltimateEstimationMethod = ReserveCalculationType.getDefault();

    protected void doCalculation() {
        IPeriodCounter periodCounter = periodScope.getPeriodCounter();
        List<Factors> factors = IndexUtils.filterFactors(inFactors, parmIndices, parmUltimateEstimationMethod.getReportingDate());
        PatternPacket payoutPattern = getPayoutPattern();
        PatternPacket reportingPattern = getReportingPattern();

        double ultimateAtReportingDate = parmUltimateEstimationMethod.getUltimate(payoutPattern, reportingPattern);
        DateTime averageInceptionDate = parmUltimateEstimationMethod.getAverageInceptionDate();

        GrossClaimRoot baseClaim;
        if (periodScope.isFirstPeriod()) {
            baseClaim = new GrossClaimRoot(ultimateAtReportingDate, ClaimType.AGGREGATED_RESERVES, averageInceptionDate,
                    averageInceptionDate, payoutPattern, reportingPattern);
            baseClaim.updateCumulatedPaidAtStartOfFirstPeriod(periodCounter, factors);
            outNominalUltimates.add(new SingleValuePacket(ultimateAtReportingDate));
        }
        else {
            baseClaim = (GrossClaimRoot) periodStore.get(BASE_CLAIM, -1);
        }
        periodStore.put(BASE_CLAIM, baseClaim);
        outReserves.addAll(baseClaim.getClaimCashflowPackets(periodCounter, factors, false));

    }

    // period store key
    private static final String BASE_CLAIM = "base claim root";

    private PatternPacket getPayoutPattern() {
        PatternPacket payoutPattern = PatternUtils.filterPattern(inPatterns, parmPayoutPattern, IPayoutPatternMarker.class);
        return payoutPattern == null ? new PatternPacket.TrivialPattern(IPayoutPatternMarker.class) : payoutPattern;
    }

    private PatternPacket getReportingPattern() {
        PatternPacket reportingPattern = PatternUtils.filterPattern(inPatterns, parmReportingPattern, IReportingPatternMarker.class);
        return reportingPattern == null ? new PatternPacket.TrivialPattern(IReportingPatternMarker.class) : reportingPattern;
    }

    public IReserveCalculationStrategy getParmUltimateEstimationMethod() {
        return parmUltimateEstimationMethod;
    }

    public void setParmUltimateEstimationMethod(IReserveCalculationStrategy parmUltimateEstimationMethod) {
        this.parmUltimateEstimationMethod = parmUltimateEstimationMethod;
    }

    public ConstrainedString getParmPayoutPattern() {
        return parmPayoutPattern;
    }

    public void setParmPayoutPattern(ConstrainedString parmPayoutPattern) {
        this.parmPayoutPattern = parmPayoutPattern;
    }

    public ConstrainedString getParmReportingPattern() {
        return parmReportingPattern;
    }

    public void setParmReportingPattern(ConstrainedString parmReportingPattern) {
        this.parmReportingPattern = parmReportingPattern;
    }

    public ConstrainedMultiDimensionalParameter getParmIndices() {
        return parmIndices;
    }

    public void setParmIndices(ConstrainedMultiDimensionalParameter parmIndices) {
        this.parmIndices = parmIndices;
    }

    public PacketList<FactorsPacket> getInFactors() {
        return inFactors;
    }

    public void setInFactors(PacketList<FactorsPacket> inFactors) {
        this.inFactors = inFactors;
    }

    public PacketList<PatternPacket> getInPatterns() {
        return inPatterns;
    }

    public void setInPatterns(PacketList<PatternPacket> inPatterns) {
        this.inPatterns = inPatterns;
    }

    public PacketList<ClaimCashflowPacket> getOutReserves() {
        return outReserves;
    }

    public void setOutReserves(PacketList<ClaimCashflowPacket> outReserves) {
        this.outReserves = outReserves;
    }


    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PacketList<SingleValuePacket> getOutNominalUltimates() {
        return outNominalUltimates;
    }

    public void setOutNominalUltimates(PacketList<SingleValuePacket> outNominalUltimates) {
        this.outNominalUltimates = outNominalUltimates;
    }
}
