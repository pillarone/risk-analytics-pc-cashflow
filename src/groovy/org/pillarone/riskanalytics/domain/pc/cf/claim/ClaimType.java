package org.pillarone.riskanalytics.domain.pc.cf.claim;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public enum ClaimType {
    ATTRITIONAL {
        @Override
        boolean isReserveClaim() {
            return false;
        }
    },
    SINGLE {
        @Override
        boolean isReserveClaim() {
            return false;
        }
    },
    EVENT {
        @Override
        boolean isReserveClaim() {
            return false;
        }
    },
    AGGREGATED_ATTRITIONAL {
        @Override
        boolean isReserveClaim() {
            return false;
        }
    },
    AGGREGATED_SINGLE {
        @Override
        boolean isReserveClaim() {
            return false;
        }
    },
    AGGREGATED_EVENT {
        @Override
        boolean isReserveClaim() {
            return false;
        }
    },
    RESERVE {
        @Override
        boolean isReserveClaim() {
            return true;
        }
    },
    AGGREGATED_RESERVES {
        @Override
        boolean isReserveClaim() {
            return true;
        }
    },
    AGGREGATED {
        @Override
        boolean isReserveClaim() {
            return false;
        }
    },
    CEDED {
        @Override
        boolean isReserveClaim() {
            return false;
        }
    };

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }

    abstract boolean isReserveClaim();
}
