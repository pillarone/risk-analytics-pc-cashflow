package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a helper class allowing to update each individual claim correctly. Each storage is correctly distinctly identified
 * by its key (same in all r/i contracts) and base (same for same predecessor) claim property and contract (as a component
 * may have several layer contracts).
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimStorageContainer {

    private Map<ClaimStorageKey, ClaimStorage> container = new HashMap<ClaimStorageKey, ClaimStorage>();

    ClaimStorage get(ClaimCashflowPacket claim, IReinsuranceContract contract) {
        return get(claim.getKeyClaim(), claim.getBaseClaim(), contract);
    }

    ClaimStorage get(IClaimRoot keyClaim, IClaimRoot baseClaim, IReinsuranceContract contract) {
        return container.get(new ClaimStorageKey(keyClaim, baseClaim, contract));
    }

    void add(ClaimCashflowPacket claim, IReinsuranceContract contract, ClaimStorage claimStorage) {
        add(claim.getKeyClaim(), claim.getBaseClaim(), contract, claimStorage);
    }

    void add(IClaimRoot keyClaim, IClaimRoot baseClaim, IReinsuranceContract contract, ClaimStorage claimStorage) {
        container.put(new ClaimStorageKey(keyClaim, baseClaim, contract), claimStorage);
    }

    private class ClaimStorageKey {
        IClaimRoot keyClaim;
        IClaimRoot baseClaim;
        IReinsuranceContract contract;

        private ClaimStorageKey(IClaimRoot keyClaim, IClaimRoot baseClaim, IReinsuranceContract contract) {
            this.keyClaim = keyClaim;
            this.baseClaim = baseClaim;
            this.contract = contract;
        }

        @Override
        public int hashCode() {
            HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(keyClaim);
            builder.append(baseClaim);
            builder.append(contract);
            return builder.toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ClaimStorageKey) {
                if (((ClaimStorageKey) obj).getKeyClaim().equals(keyClaim)
                        && ((ClaimStorageKey) obj).getBaseClaim().equals(baseClaim)
                        && ((ClaimStorageKey) obj).getContract().equals(contract)) {
                    return true;
                }
            }
            return false;
        }

        public IClaimRoot getKeyClaim() {
            return keyClaim;
        }

        public IClaimRoot getBaseClaim() {
            return baseClaim;
        }

        public IReinsuranceContract getContract() {
            return contract;
        }
    }
}
