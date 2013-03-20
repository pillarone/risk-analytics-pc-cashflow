package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

/**
 * Helper objects allowing to prepare parameters for easy lookup during periods
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CommissionPeriodParameters {
    private double quote;
    private double cap;
    private ICommission commission;

    public CommissionPeriodParameters(double quote, double cap, ICommission commission) {
        this.quote = quote;
        this.cap = cap;
        this.commission = commission;
    }

    public double getQuote() {
        return quote;
    }

    public double getCap() {
        return cap;
    }

    public ICommission getCommission() {
        return commission;
    }
}