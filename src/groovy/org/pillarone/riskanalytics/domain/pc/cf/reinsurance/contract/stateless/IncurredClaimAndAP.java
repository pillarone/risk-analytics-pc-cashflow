package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

public class IncurredClaimAndAP {

    private final double incurredClaim;
    private final double additionalpremium;

    public IncurredClaimAndAP(double incurredClaim, double additionalpremium) {
        this.incurredClaim = incurredClaim;
        this.additionalpremium = additionalpremium;
    }

    public double getIncurredClaim() {
        return incurredClaim;
    }

    public double getAdditionalpremium() {
        return additionalpremium;
    }

    public IncurredClaimAndAP minus(IncurredClaimAndAP incurredClaimAndAP) {
        return new IncurredClaimAndAP(incurredClaim - incurredClaimAndAP.getIncurredClaim(), additionalpremium - incurredClaimAndAP.getAdditionalpremium());
    }

}
