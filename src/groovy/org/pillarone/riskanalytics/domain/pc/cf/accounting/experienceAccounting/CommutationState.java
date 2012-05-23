package org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

/**
 * This object exists to propogate commutation information, it's state and expected behaviour.
 *
 * @author simon.parten (at) art-allianz (dot) com
 */
public class CommutationState extends MultiValuePacket{
    private final boolean commuted;
    private final double profitShare;
    private final CommutationBehaviour commutationBehaviour;
    private final double discountRate;
    private final int commutationPeriod;
    private final DateTime commutationDate;
    private final boolean commuteThisPeriod;

    public CommutationState() {
        this.commuted = false;
        this.commutationBehaviour = CommutationBehaviour.DEFAULT;
        this.profitShare = 1d;
        this.discountRate = 1d;
        this.commutationPeriod = -1;
        this.commutationDate = new DateTime(1900, 1, 1, 1, 0, 0, 0);
        this.commuteThisPeriod = false;
    }

    public CommutationState(double profitShare) {
        this.commuted = false;
        this.commutationBehaviour = CommutationBehaviour.DEFAULT;
        this.profitShare = profitShare;
        this.discountRate = 1d;
        this.commutationPeriod = -1;
        this.commutationDate = new DateTime(1900, 1, 1, 1, 0, 0, 0);
        this.commuteThisPeriod = false;
    }

    CommutationState(Boolean commuted, Double profitShare, CommutationBehaviour commutationBehaviour, Double discountRate, Integer commutationPeriod, DateTime commutationDate, Boolean commuteThisPeriod) {
        this.commuted = commuted;
        this.profitShare = profitShare;
        this.commutationBehaviour = commutationBehaviour;
        this.discountRate = discountRate;
        this.commutationPeriod = commutationPeriod;
        this.commutationDate = commutationDate;
        this.commuteThisPeriod = commuteThisPeriod;
    }

    /**
     * Answers the question ' Do we want the simulation to continue ?
     * True if we are not commuted, false if commuted
     *
     * @param periodScope periodSCope
     * @return True if we are commuted, false if not commuted
     */
    public boolean checkCommutation(PeriodScope periodScope) {
        return !isCommuted() || isCommuted() && getCommutationPeriod() == periodScope.getCurrentPeriod();
    }


    public boolean isCommuted() {
        return commuted;
    }

    public double getProfitShare() {
        return profitShare;
    }

    public CommutationBehaviour getCommutationBehaviour() {
        return commutationBehaviour;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public int getCommutationPeriod() {
        return commutationPeriod;
    }

    public DateTime getCommutationDate() {
        return commutationDate;
    }

    public boolean isCommuteThisPeriod() {
        return commuteThisPeriod;
    }

    @Override
    public String toString() {
        return "CommutationState{" +
                "commuted=" + commuted +
                ", profitShare=" + profitShare +
                ", discountRate=" + discountRate +
                ", commutationPeriod=" + commutationPeriod +
                ", commutationDate=" + DateTimeUtilities.formatDate.print( commutationDate ) +
                ", commuteThisPeriod=" + commuteThisPeriod +
                '}';
    }
}
