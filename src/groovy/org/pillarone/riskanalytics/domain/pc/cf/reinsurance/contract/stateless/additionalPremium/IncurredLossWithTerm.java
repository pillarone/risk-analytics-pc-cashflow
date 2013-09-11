package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IContractStructure;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredLossWithTerm implements IAnnualIncurredLoss {

    private final IncurredLoss incurredLoss;
    private final double incurredLossAfterTermStructurte;
    private final double netOfTermLimitDifferenceToAllocate;
    private final IContractStructure structure;
    private final int period;

    public IncurredLossWithTerm(final IncurredLoss incurredLoss, final double incurredLossAfterTermStructurte, final double incrementalNetOfTermLimitToAllocate, final IContractStructure structure, final int contractPeriod) {
        this.incurredLoss = incurredLoss;
        this.incurredLossAfterTermStructurte = incurredLossAfterTermStructurte;
        this.structure = structure;
        this.period = contractPeriod;
        this.netOfTermLimitDifferenceToAllocate = incrementalNetOfTermLimitToAllocate;
    }

    public double termLimitReAllocationPercentage() {
        if (incurredLoss.getLossWithShareAppliedAllLayers() == 0d) {
            return 1d;
        };
        return (1 - netOfTermLimitDifferenceToAllocate / incurredLoss.getLossWithShareAppliedAllLayers());
    }

    public IncurredLoss getIncurredLoss() {
        return incurredLoss;
    }

    public double getIncurredLossAfterTermStructurte() {
        return incurredLossAfterTermStructurte;
    }

    public IContractStructure getStructure() {
        return structure;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public String toString() {
        return "IncurredLossWithTerm{" +
                "period=" + period +
                ", incurredLossAfterTermStructurte=" + incurredLossAfterTermStructurte +
                '}';
    }
}
