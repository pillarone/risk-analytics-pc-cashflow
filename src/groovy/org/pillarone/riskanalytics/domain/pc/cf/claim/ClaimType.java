package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

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

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString());
        }
    },
    SINGLE {
        @Override
        boolean isReserveClaim() {
            return false;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            DateTime earliestStartOfContract = claimRoot.getOccurrenceDate().minusMonths(contractLength).plusDays(1);
            Days contractLengthInDays = Days.daysBetween(earliestStartOfContract, claimRoot.getOccurrenceDate().plusDays(1));
            double randomness = (Double) RandomNumberGeneratorFactory.getUniformGenerator().nextValue();
            int inceptionDay = ((Double) (randomness * ((double) contractLengthInDays.getDays()))).intValue();
            return earliestStartOfContract.plusDays(inceptionDay);
        }
    },
    EVENT {
        @Override
        boolean isReserveClaim() {
            return false;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString());
        }
    },
    AGGREGATED_ATTRITIONAL {
        @Override
        boolean isReserveClaim() {
            return false;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString());
        }
    },
    AGGREGATED_SINGLE {
        @Override
        boolean isReserveClaim() {
            return false;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString());
        }
    },
    AGGREGATED_EVENT {
        @Override
        boolean isReserveClaim() {
            return false;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString() + ". If you are using SMEs please try the per risk option");
        }
    },
    RESERVE {
        @Override
        boolean isReserveClaim() {
            return true;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString());
        }
    },
    AGGREGATED_RESERVES {
        @Override
        boolean isReserveClaim() {
            return true;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString());
        }
    },
    AGGREGATED {
        @Override
        boolean isReserveClaim() {
            return false;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString());
        }
    },
    CEDED {
        @Override
        boolean isReserveClaim() {
            return false;
        }

        @Override
        public DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength) {
            throw new SimulationException("Not implemented for " + this.toString());
        }
    };

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }

    abstract boolean isReserveClaim();

    public abstract DateTime generateInceptionDate(IClaimRoot claimRoot, int contractLength);
}
