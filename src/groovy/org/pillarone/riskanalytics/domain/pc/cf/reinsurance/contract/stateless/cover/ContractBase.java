package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover;

import java.util.Map;

public enum ContractBase {
    CEDED, NET;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}