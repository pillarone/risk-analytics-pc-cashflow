package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
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

    Map<IClaimRoot, IClaimRoot> baseClaimByKeyClaim = [:]


    @Override
    protected void doCalculation() {
        filterNetAndCededClaims()
        // TODO (dbe) make more robust, e.g. no gross found.
        if (isReceiverWired(inClaimsGross)) {
            Map<IClaimRoot, ClaimCashflowPacket> cededClaimsByKeyClaim = ClaimUtils.aggregateByKeyClaim(inClaimsCeded)
            Map<IClaimRoot, ClaimCashflowPacket> cededClaimsForNetByKeyClaim = ClaimUtils.aggregateByKeyClaim(inClaimsCededForNet)
            Map<IClaimRoot, ClaimCashflowPacket> netClaimsByKeyClaim = ClaimUtils.aggregateByKeyClaim(inClaimsNet)
            Map<IClaimRoot, ClaimCashflowPacket> benefitClaimsByKeyClaim = ClaimUtils.aggregateByKeyClaim(inClaimsBenefit)
            //net matrix case
            if (!hasBenefitContracts()) {
                for (Map.Entry<IClaimRoot, ClaimCashflowPacket> netClaim : netClaimsByKeyClaim.entrySet()) {
                    if (severalNetContractsCovered()) {
                        IClaimRoot baseClaim = baseClaimByKeyClaim.get(netClaim.key)
                        outClaims.add(getNetClaim(netClaim, cededClaimsForNetByKeyClaim));
                    }
                    else if (severalCededContractsCovered()) {
                        // todo: think: this won't be used as netClaimsByBase is empty
                        outClaims.addAll(ClaimUtils.aggregateByBaseClaim(inClaimsCeded))
                    }
                    else if (netAndCededContractsCovered()) {
                        ClaimCashflowPacket netClaimPacket = netClaim.value
                        if (severalNetContractsCovered()) {
                            IClaimRoot baseClaim = baseClaimByKeyClaim.get(netClaim.key)
                            netClaimPacket = getNetClaim(netClaim, cededClaimsForNetByKeyClaim)
                        }
                        ClaimCashflowPacket mergedClaim = ClaimUtils.getNetClaim(netClaimPacket, cededClaimsByKeyClaim.get(netClaim.key), netClaim.value.reinsuranceContract())
                        outClaims.add(mergedClaim)
                    }
                    if (coverAttributeStrategy.coveredNetOfContracts().contains(netClaim.value.reinsuranceContract()
                            && coverAttributeStrategy.coveredCededOfContracts().contains(netClaim.value.reinsuranceContract()))){
                        outClaims.add(netClaim.value)
                    }
                }
                if (netClaimsByKeyClaim.isEmpty() && severalCededContractsCovered()) {
                    outClaims.addAll(ClaimUtils.aggregateByBaseClaim(inClaimsCeded))
                }
            }
            else {
                if (severalNetContractsCovered()) {
                    //benefit net matrix case
                    for (ClaimCashflowPacket netClaim : netClaimsByKeyClaim.values()) {
                        ClaimCashflowPacket grossClaim = ClaimUtils.findClaimByBaseClaim(inClaimsGross, netClaim.keyClaim)
                        ClaimCashflowPacket cededClaim = cededClaimsByKeyClaim.get(netClaim.keyClaim)
                        ClaimCashflowPacket netClaimBeforeBenefit = ClaimUtils.getNetClaim(grossClaim, cededClaim, netClaim.reinsuranceContract())
                        ClaimCashflowPacket benefitClaim = benefitClaimsByKeyClaim.get(netClaim.keyClaim)
                        ClaimCashflowPacket netClaimAfterBenefit = ClaimUtils.getNetClaim(netClaimBeforeBenefit, benefitClaim,
                                baseClaim(netClaimBeforeBenefit, benefitClaim), netClaim.reinsuranceContract())
                        outClaims.add(netClaimAfterBenefit)
                    }
                } else if (severalCededContractsCovered()){
                    //benefit ceded matrix case
                    for (ClaimCashflowPacket cededClaimBase : cededClaimsByKeyClaim.values()) {
                        ClaimCashflowPacket cededClaim = cededClaimsByKeyClaim.get(cededClaimBase.keyClaim)
                        ClaimCashflowPacket benefitClaim = benefitClaimsByKeyClaim.get(cededClaimBase.keyClaim)
                        ClaimCashflowPacket netClaim = ClaimUtils.getNetClaim(cededClaim, benefitClaim,
                                baseClaim(cededClaim, benefitClaim), cededClaimBase?.reinsuranceContract())
                        outClaims.add(netClaim)
                    }
                } else if (netAndCededContractsCovered()){
                    // TODO might be part of condition 1 (severalNetContractsCovered())
                } else if (grossCovered()){
                    for (ClaimCashflowPacket grossClaimBase : filterGrossClaims()) {
                        ClaimCashflowPacket benefitClaim = benefitClaimsByKeyClaim.get(grossClaimBase.keyClaim)
                        ClaimCashflowPacket netClaim = ClaimUtils.getNetClaim(grossClaimBase, benefitClaim,
                                baseClaim(grossClaimBase, benefitClaim), benefitClaim?.reinsuranceContract())
                        outClaims.add(netClaim)
                    }
                }
            }
            //TODO(sku) benefit ceded,net matrix case ?
        } else {
            // TODO think, do we need that at all  ?
            outClaims.addAll(ClaimUtils.aggregateByBaseClaim(inClaimsCeded))
        }
        if (coverAttributeStrategy.hasGrossFilters() && !hasBenefitContracts()) {
            outClaims.addAll(filterGrossClaims())
        }
    }

    private List<ClaimCashflowPacket> filterGrossClaims() {
        return coverAttributeStrategy.coveredClaims(new ArrayList(inClaimsGross))
    }

    private boolean grossCovered() {
        coverAttributeStrategy.coveredNetOfContracts().size() == 0 && coverAttributeStrategy.coveredCededOfContracts().size() == 0
    }

    private IClaimRoot baseClaim(ClaimCashflowPacket grossClaim, ClaimCashflowPacket cededClaim) {
        IClaimRoot baseClaim = baseClaimByKeyClaim.get(grossClaim.keyClaim)
        if (!baseClaim) {
            if (grossClaim && cededClaim) {
                baseClaim = ClaimUtils.getNetClaimRoot(grossClaim, cededClaim)
            }
            else if (!grossClaim && cededClaim) {
                DateTime occurrenceDate = cededClaim.getOccurrenceDate();
                baseClaim = new ClaimRoot(0, cededClaim.getClaimType(), occurrenceDate, occurrenceDate);
            }
            else if (grossClaim && !cededClaim) {
                baseClaim = grossClaim.baseClaim
            }
            baseClaimByKeyClaim.put(grossClaim.keyClaim, baseClaim)
        }
        return baseClaim
    }

    private ClaimCashflowPacket getNetClaim(Map.Entry<IClaimRoot, ClaimCashflowPacket> netClaimEntry,
                                            Map<IClaimRoot, ClaimCashflowPacket> cededClaimsForNetByBase) {
        ClaimCashflowPacket grossClaim = ClaimUtils.findClaimByKeyClaim(inClaimsGross, netClaimEntry.key)
        ClaimCashflowPacket cededClaim = cededClaimsForNetByBase.get(netClaimEntry.key);
        ClaimCashflowPacket netClaim = ClaimUtils.getNetClaim(grossClaim, cededClaim, baseClaim(grossClaim, cededClaim), netClaimEntry.value.reinsuranceContract())
        return netClaim
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

    protected void reset() {
     super.reset()
     baseClaimByKeyClaim.clear()
    }

    private filterNetAndCededClaims() {
        coverAttributeStrategy.coveredClaims(inClaimsNet, false)
        coverAttributeStrategy.coveredClaims(inClaimsCededForNet, false)
        coverAttributeStrategy.coveredClaims(inClaimsCeded)
    }
}
