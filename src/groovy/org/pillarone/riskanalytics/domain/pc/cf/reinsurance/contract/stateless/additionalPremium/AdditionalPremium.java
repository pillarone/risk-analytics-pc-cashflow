package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

/**
 * author simon.parten @ art-allianz . com
 */
public class AdditionalPremium {

    private final double additionalPremium;
    private final APBasis premiumType;

    public AdditionalPremium(double additionalPremium, APBasis premiumType) {
        this.additionalPremium = additionalPremium;
        this.premiumType = premiumType;
    }

    public APSingleValuePacket getPacket(IPeriodCounter iPeriodCounter) {
        return new APSingleValuePacket(additionalPremium, this, premiumType.getAPDate(iPeriodCounter));

    }

    public double getAdditionalPremium() {
        return additionalPremium;
    }

    public APBasis getPremiumType() {
        return premiumType;
    }
}
