package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.hibernate.type.ListType
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeStrategy

class ClaimMerger extends Component {
    PacketList<ClaimCashflowPacket> inClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    // required for calculation in case of net claims.
    PacketList<ClaimCashflowPacket> inClaimsCededForNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)
    PacketList<ClaimCashflowPacket> inClaimsBenefit = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

    PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket)

    MatrixCoverAttributeStrategy coverAttributeStrategy


    @Override
    protected void doCalculation() {
        filterNetAndCededClaims()
        List<ClaimCashflowPacket> claimsCeded = ClaimUtils.correctClaimSign(inClaimsCeded, true)
        ClaimUtils.correctClaimSign(inClaimsGross,false)
        ClaimUtils.correctClaimSign(inClaimsNet,false)
        // TODO (dbe) make more robust, e.g. no gross found.
        if (isReceiverWired(inClaimsGross)) {
            Map<IClaimRoot, ClaimCashflowPacket> cededClaimsByBase = ClaimUtils.aggregateByBaseClaimGroupByKeyClaim(claimsCeded)
            Map<IClaimRoot, ClaimCashflowPacket> cededClaimsForNetByBase = ClaimUtils.aggregateByBaseClaimGroupByKeyClaim(inClaimsCededForNet)
            Map<IClaimRoot, ClaimCashflowPacket> netClaimsByBase = ClaimUtils.aggregateByBaseClaimGroupByKeyClaim(inClaimsNet)
            Map<IClaimRoot, ClaimCashflowPacket> benefitClaimsByBase = ClaimUtils.aggregateByBaseClaimGroupByKeyClaim(inClaimsBenefit)
            //net matrix case
            if (!hasBenefitContracts()) {
                for (Map.Entry<IClaimRoot, ClaimCashflowPacket> netClaim : netClaimsByBase.entrySet()) {
                    if (severalNetContractsCovered()) {
                        outClaims.add(getNetClaim(netClaim, cededClaimsForNetByBase));
                    }
                    else if (severalCededContractsCovered()) {
                        outClaims.addAll(ClaimUtils.aggregateByBaseClaim(claimsCeded))
                    }
                    else if (netAndCededContractsCovered()) {
                        ClaimCashflowPacket netClaimPacket = netClaim.value
                        if (severalNetContractsCovered()) {
                            netClaimPacket = getNetClaim(netClaim, cededClaimsForNetByBase)
                        }
                        ClaimCashflowPacket mergedClaim = ClaimUtils.getNetClaim(netClaimPacket, cededClaimsByBase.get(netClaim.key), netClaim.value.reinsuranceContract())
                        outClaims.add(mergedClaim)

                    }
                    if (coverAttributeStrategy.coveredNetOfContracts().contains(netClaim.value.reinsuranceContract() && coverAttributeStrategy.coveredCededOfContracts().contains(netClaim.value.reinsuranceContract()))){
                        outClaims.add(netClaim.value)
                    }

                }
            } else {
                if (severalNetContractsCovered()) {
                    //benefit net matrix case
                    for (ClaimCashflowPacket netClaim : netClaimsByBase) {
                        ClaimCashflowPacket grossClaim = ClaimUtils.findClaimByBaseClaim(inClaimsGross, netClaim.baseClaim)
                        ClaimCashflowPacket cededClaim = cededClaimsByBase.get(netClaim.baseClaim)
                        ClaimCashflowPacket netClaimBeforeBenefit = ClaimUtils.getNetClaim(grossClaim, cededClaim, netClaim.reinsuranceContract())
                        ClaimCashflowPacket benefitClaim = benefitClaimsByBase.get(netClaim.baseClaim)
                        //TODO (sku) check  valid size of benefit claim.
                        ClaimCashflowPacket netClaimAfterBenefit = ClaimUtils.getNetClaim(netClaimBeforeBenefit, benefitClaim, netClaim.reinsuranceContract())
                        outClaims.add(netClaimAfterBenefit)
                    }
                } else if (severalCededContractsCovered()){
                    //benefit ceded matrix case
                    for (ClaimCashflowPacket cededClaimBase : cededClaimsByBase) {
                        ClaimCashflowPacket cededClaim = cededClaimsByBase.get(cededClaimBase.baseClaim)
                        ClaimCashflowPacket benefitClaim = benefitClaimsByBase.get(cededClaimBase.baseClaim)
                        ClaimCashflowPacket invertedCededClaim = ClaimUtils.scale(cededClaim, -1)
                        //TODO (sku) check  valid size of benefit claim.
                        ClaimCashflowPacket netClaim = ClaimUtils.getNetClaim(invertedCededClaim, benefitClaim, cededClaimBase.reinsuranceContract())
                        outClaims.add(netClaim)
                    }
                } else if (netAndCededContractsCovered()){
                    // TODO might be part of condition 1 (severalNetContractsCovered())
                } else if (grossCovered()){
                    for (ClaimCashflowPacket grossClaimBase : inClaimsGross) {
                        ClaimCashflowPacket benefitClaim = benefitClaimsByBase.get(grossClaimBase.baseClaim)
                        ClaimCashflowPacket netClaim = ClaimUtils.getNetClaim(grossClaimBase,benefitClaim,benefitClaim.reinsuranceContract())
                        outClaims.add(netClaim)
                    }
                }
            }
            //TODO(sku) benefit ceded,net matrix case ?
        } else {
            // TODO think, do we need that at all  ?
            outClaims.addAll(ClaimUtils.aggregateByBaseClaim(inClaimsCeded))
        }
    }

    private boolean grossCovered() {
        coverAttributeStrategy.coveredNetOfContracts().size() == 0 && coverAttributeStrategy.coveredCededOfContracts().size() == 0
    }

    private ClaimCashflowPacket getNetClaim(Map.Entry<IClaimRoot, ClaimCashflowPacket> netClaim, Map<IClaimRoot, ClaimCashflowPacket> cededClaimsForNetByBase) {
        ClaimCashflowPacket grossClaim = ClaimUtils.findClaimByBaseClaim(inClaimsGross, netClaim.key)
        ClaimCashflowPacket cededClaim = cededClaimsForNetByBase.get(netClaim.key);
        return ClaimUtils.getNetClaim(grossClaim, cededClaim, netClaim.value.reinsuranceContract())
    }

    private boolean hasBenefitContracts() {
        isReceiverWired(inClaimsBenefit)
    }

    private boolean netAndCededContractsCovered() {
        coverAttributeStrategy.coveredNetOfContracts().size() > 0 && coverAttributeStrategy.coveredCededOfContracts().size() > 0
    }

    private boolean severalCededContractsCovered() {
        coverAttributeStrategy.coveredNetOfContracts().size() == 0 && coverAttributeStrategy.coveredCededOfContracts().size() > 0
    }

    private boolean severalNetContractsCovered() {
        coverAttributeStrategy.coveredNetOfContracts().size() > 0 && coverAttributeStrategy.coveredCededOfContracts().size() == 0
    }

    private filterNetAndCededClaims() {
        coverAttributeStrategy.coveredClaims(inClaimsNet)
        coverAttributeStrategy.coveredClaims(inClaimsCededForNet)
        coverAttributeStrategy.coveredClaims(inClaimsCeded)
    }
}
