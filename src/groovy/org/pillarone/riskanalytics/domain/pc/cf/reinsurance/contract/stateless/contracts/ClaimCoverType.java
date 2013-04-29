package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.EventCacheClaimsStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.UberCacheClaimStore;

/**
 * author simon.parten @ art-allianz . com
 */
public enum ClaimCoverType {

    SINGLE_CLAIM {
        @Override
        public IAllContractClaimCache getClaimCache() {
            return new UberCacheClaimStore();
        }
    }, AGGREGATED_EVENT {
        @Override
        public IAllContractClaimCache getClaimCache() {
            return new EventCacheClaimsStore();
        }
    };

    public abstract IAllContractClaimCache getClaimCache();

}
