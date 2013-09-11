package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

/**
 * author simon.parten @ art-allianz . com
 */
public class TermLossesAndAPs {

    private final IncurredLossWithTerm lossesWithTerm;
    private final IncurredAPsWithTerm additionalPremiums;


    public TermLossesAndAPs(final IncurredLossWithTerm lossesWithTerm, final IncurredAPsWithTerm additionalPremiums) {
        this.lossesWithTerm = lossesWithTerm;
        this.additionalPremiums = additionalPremiums;
    }

    public IncurredLossWithTerm getLossesWithTerm() {
        return lossesWithTerm;
    }

    public IncurredAPsWithTerm getAdditionalPremiums() {
        return additionalPremiums;
    }
}
