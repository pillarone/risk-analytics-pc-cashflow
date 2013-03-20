package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a helper class allowing to update each individual claim correctly. Each storage is correctly distinctly identified
 * by its key (same in all r/i contracts) and base (same for same predecessor) claim property, contract (as a component
 * may have several layer contracts) and event.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateEventClaimStorageContainer {

    private Map<AggregateEventClaimStorageKey, AggregateEventClaimsStorage> container = new HashMap<AggregateEventClaimStorageKey, AggregateEventClaimsStorage>();

    public AggregateEventClaimsStorage get(ClaimCashflowPacket claim, IReinsuranceContract contract) {
        return get(claim.getKeyClaim(), claim.getBaseClaim(), contract, null);
    }

    public AggregateEventClaimsStorage get(IClaimRoot keyClaim, IClaimRoot baseClaim, IReinsuranceContract contract, EventPacket event) {
        return container.get(new AggregateEventClaimStorageKey(keyClaim, baseClaim, contract, event));
    }

    public void add(ClaimCashflowPacket claim, IReinsuranceContract contract, AggregateEventClaimsStorage claimStorage) {
        add(claim.getKeyClaim(), claim.getBaseClaim(), contract, claimStorage, null);
    }

    public void add(IClaimRoot keyClaim, IClaimRoot baseClaim, IReinsuranceContract contract, AggregateEventClaimsStorage claimStorage, EventPacket event) {
        container.put(new AggregateEventClaimStorageKey(keyClaim, baseClaim, contract, event), claimStorage);
    }

    /**
     * resets the property of all storage to the initial value
     */
    public void reset() {
        for (AggregateEventClaimsStorage storage : container.values()) {
            storage.resetIncrementsAndFactors();
        }
    }

    private class AggregateEventClaimStorageKey {
        IClaimRoot keyClaim;
        IClaimRoot baseClaim;
        IReinsuranceContract contract;
        EventPacket event;

        private AggregateEventClaimStorageKey(IClaimRoot keyClaim, IClaimRoot baseClaim, IReinsuranceContract contract, EventPacket event) {
            this.keyClaim = keyClaim;
            this.baseClaim = baseClaim;
            this.contract = contract;
            this.event = event;
        }

        @Override
        public int hashCode() {
            HashCodeBuilder builder = new HashCodeBuilder();
            builder.append(keyClaim);
            builder.append(baseClaim);
            builder.append(contract);
            builder.append(event);
            return builder.toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AggregateEventClaimStorageKey) {
                if (((AggregateEventClaimStorageKey) obj).getEvent() != null && event != null) {
                    if (((AggregateEventClaimStorageKey) obj).getKeyClaim().equals(keyClaim)
                            && ((AggregateEventClaimStorageKey) obj).getBaseClaim().equals(baseClaim)
                            && ((AggregateEventClaimStorageKey) obj).getContract().equals(contract)
                            && ((AggregateEventClaimStorageKey) obj).getEvent().equals(event)) {
                        return true;
                    }
                }
                else if (((AggregateEventClaimStorageKey) obj).getEvent() == null && event == null) {
                    if (((AggregateEventClaimStorageKey) obj).getKeyClaim().equals(keyClaim)
                            && ((AggregateEventClaimStorageKey) obj).getBaseClaim().equals(baseClaim)
                            && ((AggregateEventClaimStorageKey) obj).getContract().equals(contract)) {
                        return true;
                    }
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

        public EventPacket getEvent() {
            return event;
        }

    }
}
