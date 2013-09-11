package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import java.util.Collection;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredAPsWithTerm {

    private Collection<AdditionalPremium> additionalPremiums;

    public IncurredAPsWithTerm(final Collection<AdditionalPremium> additionalPremiums) {
        this.additionalPremiums = additionalPremiums;
    }

    public Collection<AdditionalPremium> getAdditionalPremiums() {
        return additionalPremiums;
    }

    public void setAdditionalPremiums(final Collection<AdditionalPremium> additionalPremiums) {
        this.additionalPremiums = additionalPremiums;
    }
}
