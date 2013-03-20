package org.pillarone.riskanalytics.domain.pc.cf.discounting;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class DiscountedValuesPacket extends MultiValuePacket{

    private double discountedPaidIncrementalGross;
    private double discountedPaidIncrementalCeded;
    private double discountedPaidIncrementalNet;

    private double discountedReservedGross;
    private double discountedReservedCeded;
    private double discountedReservedNet;

    public double getDiscountedPaidIncrementalGross() {
        return discountedPaidIncrementalGross;
    }

    public void setDiscountedPaidIncrementalGross(double discountedPaidIncrementalGross) {
        this.discountedPaidIncrementalGross = discountedPaidIncrementalGross;
    }

    public double getDiscountedReservedGross() {
        return discountedReservedGross;
    }

    public void setDiscountedReservedGross(double discountedReservedGross) {
        this.discountedReservedGross = discountedReservedGross;
    }

    public double getDiscountedPaidIncrementalCeded() {
        return discountedPaidIncrementalCeded;
    }

    public void setDiscountedPaidIncrementalCeded(double discountedPaidIncrementalCeded) {
        this.discountedPaidIncrementalCeded = discountedPaidIncrementalCeded;
    }

    public double getDiscountedPaidIncrementalNet() {
        return discountedPaidIncrementalNet;
    }

    public void setDiscountedPaidIncrementalNet(double discountedPaidIncrementalNet) {
        this.discountedPaidIncrementalNet = discountedPaidIncrementalNet;
    }

    public double getDiscountedReservedCeded() {
        return discountedReservedCeded;
    }

    public void setDiscountedReservedCeded(double discountedReservedCeded) {
        this.discountedReservedCeded = discountedReservedCeded;
    }

    public double getDiscountedReservedNet() {
        return discountedReservedNet;
    }

    public void setDiscountedReservedNet(double discountedReservedNet) {
        this.discountedReservedNet = discountedReservedNet;
    }
}
