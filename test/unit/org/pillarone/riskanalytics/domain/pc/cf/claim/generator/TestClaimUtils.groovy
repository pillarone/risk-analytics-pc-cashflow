package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.event.IEvent
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLoss
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossAndAP
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossWithTerm
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.ContractOrderingMethod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractLayer
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractStructure

/**
*   author simon.parten @ art-allianz . com
 */
class TestClaimUtils {

    public static GrossClaimRoot getGrossClaim(List<Integer> patternMonths, List<Double> pattern, double ultimate, DateTime exposureStart, DateTime patternStart, DateTime occurenceDate) {
        PatternPacket patternPacket = PatternPacketTests.getPattern(patternMonths, pattern, false)
        IClaimRoot claimRoot = new ClaimRoot(ultimate, ClaimType.SINGLE, exposureStart, occurenceDate)
        GrossClaimRoot grossClaimRoot = new GrossClaimRoot(claimRoot, patternPacket, patternStart)
        return grossClaimRoot
    }

    public static GrossClaimRoot getGrossClaim(List<Integer> patternMonths, List<Double> pattern, double ultimate, DateTime exposureStart, DateTime patternStart, DateTime occurenceDate, IEvent event) {
        PatternPacket patternPacket = PatternPacketTests.getPattern(patternMonths, pattern, false)
        IClaimRoot claimRoot = new ClaimRoot(ultimate, ClaimType.SINGLE, exposureStart, occurenceDate, event)
        GrossClaimRoot grossClaimRoot = new GrossClaimRoot(claimRoot, patternPacket, patternStart)
        return grossClaimRoot
    }

    public static  HashMap<Integer, Double>  blankPremMap(){
        HashMap<Integer, Double> premiumPerPeriod =  new HashMap<Integer, Double>()
        premiumPerPeriod.put(0, 0d)
        premiumPerPeriod.put(1, 0d)
        premiumPerPeriod.put(2, 0d)
        premiumPerPeriod.put(3, 0d)
        return premiumPerPeriod
    }

    public static IncurredLossWithTerm getTestLossAndAP(double lossValue){
        final IncurredLoss p = new IncurredLoss(null) {
            @Override
            double getLossWithShareAppliedAllLayers() {
                return 0d
            }
        }
        IncurredLossWithTerm incurredLossWithTerm = new IncurredLossWithTerm(
                p, lossValue, 0d, new ContractStructure(0d, 0d, new HashMap<YearLayerIdentifier, ContractLayer>(), ContractOrderingMethod.INCURRED), 0i )
        return incurredLossWithTerm
    }

}
