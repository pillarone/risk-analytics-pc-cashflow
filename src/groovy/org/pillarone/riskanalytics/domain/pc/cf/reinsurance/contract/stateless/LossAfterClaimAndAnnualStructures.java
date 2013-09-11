package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

/**
 * Parameter helper class for additional premium.
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LossAfterClaimAndAnnualStructures {

    final double lossAfterAnnualStructure;
    final IRiLayer layerParameters;
    final double lossAfterClaimStructure;

    public LossAfterClaimAndAnnualStructures(final double lossAfterAnnualStructure, final double lossAfterClaimStructure, final IRiLayer layerParameters) {
        this.lossAfterAnnualStructure = lossAfterAnnualStructure;
        this.layerParameters = layerParameters;
        this.lossAfterClaimStructure = lossAfterClaimStructure;
    }

    public double getLossAfterAnnualStructureWithShareApplied() {
        return getLossAfterAnnualStructure() * layerParameters.getShare();
    }

    public double getLossAfterClaimStructureShareApplied() {
        return lossAfterClaimStructure * layerParameters.getShare();
    }

    public double getLossAfterAnnualStructure() {
        return lossAfterAnnualStructure;
    }

    public double getLossAfterClaimStructure() {
        return lossAfterClaimStructure;
    }

}

