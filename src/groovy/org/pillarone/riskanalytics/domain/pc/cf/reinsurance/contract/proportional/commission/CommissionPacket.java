package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CommissionPacket extends MultiValuePacket {
    private double totalCommission;
    private double fixedCommission;
    private double variableCommission;

    public CommissionPacket() {
    }

    public CommissionPacket(double totalCommission, double fixedCommission, double variableCommission) {
        this.totalCommission = totalCommission;
        this.fixedCommission = fixedCommission;
        this.variableCommission = variableCommission;
    }

    public double getFixedCommission() {
        return fixedCommission;
    }

    public void setFixedCommission(double fixedCommission) {
        this.fixedCommission = fixedCommission;
    }

    public double getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(double totalCommission) {
        this.totalCommission = totalCommission;
    }

    public double getVariableCommission() {
        return variableCommission;
    }

    public void setVariableCommission(double variableCommission) {
        this.variableCommission = variableCommission;
    }
}
