package org.pillarone.riskanalytics.domain.pc.cf.claim.allocation;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.apache.commons.lang.NotImplementedException

/**
 * @author martin.melchior (at) fhnw (dot) ch
 */

// TODO event claims not (yet) considered
// TODO discuss whether for the attritional losses it would be sufficient to first do an aggregation
// TODO discuss: the order of the outclaims is no longer the same as the claims!
class RiskToBandAllocatorStrategy extends AbstractParameterObject implements IRiskAllocatorStrategy {

    RiskBandAllocationBaseLimited allocationBase = RiskBandAllocationBaseLimited.PREMIUM

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.RISKTOBAND
    }

    public Map getParameters() {
        ['allocationBase': allocationBase]
    }

    public List<ClaimRoot> getAllocatedClaims(List<ClaimRoot> claims, List<UnderwritingInfoPacket> underwritingInfos) {
        // get risk map
        Map<Double, UnderwritingInfoPacket> riskMap = getRiskMap(underwritingInfos)

        // allocate the claims
        Map<Double, Double> targetDistributionMaxSI = getTargetDistribution(underwritingInfos, allocationBase)
        targetDistributionMaxSI = convertKeysToDouble(targetDistributionMaxSI)

        List<ClaimRoot> allocatedClaims = new ArrayList<ClaimRoot>();
        if (targetDistributionMaxSI) {
            Map<Double, List<ClaimRoot>> largeClaimsAllocation = allocateSingleClaims(
                    filterClaimsByType(claims, ClaimType.SINGLE), riskMap, targetDistributionMaxSI)
            for (Map.Entry<Double, List<ClaimRoot>> entry: largeClaimsAllocation.entrySet()) {
                UnderwritingInfoPacket underwritingInfo = riskMap[entry.key]
                for (ClaimRoot claim: entry.value) {
                    if (claim.hasExposureInfo()) throw new IllegalArgumentException("RiskToBandAllocatorStrategy.impossibleExposureRemap")
                    // todo(jwa): ask Stefan; information from underwritingInfo already completely available in ExposureInfo or
                    // do I have to set underwritingInfo.getExposure().setMaxS... epxlicitly ?
                    ClaimRoot copy = claim.withExposure(underwritingInfo.getExposure());
                    // before: claim.exposure = underwritingInfo
                    allocatedClaims << copy
                }
            }

            allocatedClaims.addAll(internalGetAllocatedClaims(claims, ClaimType.ATTRITIONAL, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(internalGetAllocatedClaims(claims, ClaimType.EVENT, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(internalGetAllocatedClaims(claims, ClaimType.AGGREGATED, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(internalGetAllocatedClaims(claims, ClaimType.AGGREGATED_ATTRITIONAL, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(internalGetAllocatedClaims(claims, ClaimType.AGGREGATED_EVENT, riskMap, targetDistributionMaxSI))
            allocatedClaims.addAll(internalGetAllocatedClaims(claims, ClaimType.AGGREGATED_SINGLE, riskMap, targetDistributionMaxSI))
        }
        return allocatedClaims
    }

    private List<ClaimRoot> internalGetAllocatedClaims(List<ClaimRoot> claims, ClaimType claimType, Map<Double,
                                               UnderwritingInfoPacket> riskMap, Map<Double, Double> targetDistribution) {
        Map<Double, List<ClaimRoot>> aggrAllocation = allocateClaims(filterClaimsByType(claims, claimType), riskMap, targetDistribution)
        List<ClaimRoot> allocatedClaims = new ArrayList<ClaimRoot>(aggrAllocation.size());
        for (Map.Entry<Double, List<ClaimRoot>> entry: aggrAllocation.entrySet()) {
            UnderwritingInfoPacket underwritingInfo = riskMap[entry.key]
            for (ClaimRoot claim: entry.value) {
//                if (claim.hasExposureInfo()) throw new IllegalArgumentException("RiskToBandAllocatorStrategy.impossibleExposureRemap")
                ClaimRoot copy = claim.withExposure(underwritingInfo.getExposure());
                allocatedClaims << copy
            }
        }
        return allocatedClaims
    }

    // todo(sku): refactor and simplify and re-enable custom and loss probability (idea: add property map in packet tUnderwritingInfo)

    private static Map<Double, Double> getTargetDistribution(List<UnderwritingInfoPacket> underwritingInfos, RiskBandAllocationBaseLimited allocationBase) {
        Map<Double, Double> targetDistribution = [:]
        for (UnderwritingInfoPacket underwritingInfo: underwritingInfos) {
            if (allocationBase.is(RiskBandAllocationBaseLimited.NUMBER_OF_POLICIES)) {
                targetDistribution.put(underwritingInfo.getMaxSumInsured(), underwritingInfo.getNumberOfPolicies())
            }
            else if (allocationBase.is(RiskBandAllocationBaseLimited.PREMIUM)) {
                targetDistribution.put(underwritingInfo.getMaxSumInsured(), underwritingInfo.getPremiumWritten())
            }
            else {
                throw new NotImplementedException("RiskBandAllocationBaseLimited " + allocationBase + " not implemented.")
            }
        }
        Double sum = (Double) targetDistribution.values().sum()
        for (Map.Entry<Double, Double> entry: targetDistribution.entrySet()) {
            entry.value /= sum
        }
        return targetDistribution
    }

    /**
     *  This function is temporarily needed as the db returns BigDecimal and the GUI may return
     *  Integer values and the map works properly only with Double keys. Once we can ensure that
     *  we always get double values, it will be obsolete.
     */
    // todo: try to remove this function

    private Map<Double, Double> convertKeysToDouble(Map<Double, Double> targetDistributionMaxSI) {
        boolean keysAreDoubles = targetDistributionMaxSI.keySet().iterator().next() instanceof Double
        if (!keysAreDoubles) {
            Map<Double, Double> mapWithConvertedKeys = [:]
            targetDistributionMaxSI.each {key, value ->
                mapWithConvertedKeys.put(key.toDouble(), value)
            }
            targetDistributionMaxSI = mapWithConvertedKeys
        }
        return targetDistributionMaxSI
    }

    private Map<Double, List<ClaimRoot>> allocateClaims(List<ClaimRoot> claims,
                                                        Map<Double, UnderwritingInfoPacket> riskMap,
                                                        Map<Double, Double> targetDistribution) {
        Map<Double, List<ClaimRoot>> lossToRiskMap = [:]
        for (ClaimRoot claim: claims) {
            for (Map.Entry<Double, Double> entry: targetDistribution.entrySet()) {
                ClaimRoot copy = claim.withScale(entry.value);
                //Claim copy = claim.copy()
                //copy.originalClaim = copy
                //copy.scale(entry.value)
                if (!lossToRiskMap.containsKey(entry.key)) {
                    lossToRiskMap[entry.key] = new ArrayList<ClaimRoot>()
                }
                lossToRiskMap[entry.key] << copy
            }
        }
        lossToRiskMap
    }

    private Map<Double, List<ClaimRoot>> allocateSingleClaims(List<ClaimRoot> claims,
                                                              Map<Double, UnderwritingInfoPacket> riskMap,
                                                              Map<Double, Double> targetDistribution) {
        // initial allocation of the losses: just consider the bounds given in the risk map
        Map<Double, List<ClaimRoot>> lossToRiskMap = [:]
        Map<Double, Integer> lossCounts = [:]
        List<Double> upperBounds = riskMap.keySet().sort()
        int numOfBands = riskMap.size()
        for (ClaimRoot claim: claims) {
            int index = Collections.binarySearch(upperBounds, Math.abs(claim.getUltimate()))
            if (index < 0) index = -index - 1
            double bound = upperBounds.get(Math.min(index, numOfBands - 1))
            if (!lossToRiskMap[bound]) lossToRiskMap[bound] = new ArrayList<ClaimRoot>()
            lossToRiskMap[bound] << claim
            if (!lossCounts[bound]) lossCounts[bound] = 0
            lossCounts[bound] += 1
        }
        int numOfClaims = claims.size()

        // compute the delta between the initial and the desired:
        List<Double> toMove = []
        for (int k = 0; k < numOfBands; k++) {
            double bound = upperBounds[k]
            if (!lossCounts.containsKey(bound)) {
                lossCounts[bound] = 0
                lossToRiskMap[bound] = new ArrayList<ClaimRoot>()
            }
            toMove[k] = lossCounts[bound] - numOfClaims * targetDistribution[bound]
        }

        // iteratively go through the bands starting at the upper end and reallocate the losses:
        for (int k = numOfBands - 2; k >= 0; k--) {
            double giveAway = Math.max(toMove[k], 0);
            List<Double> targets = []
            double factor = 0d
            for (int j = k + 1; j < numOfBands; j++) {
                double x = -Math.min(toMove[j], 0)
                targets << x
                factor += x
            }

            // normalization factor
            factor = Math.min(factor, giveAway) / factor

            for (int j = k + 1; j < numOfBands; j++) {
                int delta = Math.min(Math.round(factor * targets[j - (k + 1)]), toMove[k])
                if (delta > 0) {
                    toMove[k] -= delta
                    List<ClaimRoot> lossList = lossToRiskMap[upperBounds[k]]
                    List<ClaimRoot> extracted = lossList[-1..-delta]
                    List<ClaimRoot> targetList = lossToRiskMap[upperBounds[j]]
                    for (ClaimRoot claim: extracted) {
                        lossList.remove(claim)
                        toMove[j] += 1
                        targetList.add(claim)
                    }
                }
            }
        }
        return lossToRiskMap
    }

    /**
     * Populate a map with the upper boundaries of the risk bands as keys
     */
    private static Map<Double, UnderwritingInfoPacket> getRiskMap(List<UnderwritingInfoPacket> exposures) {
        Map<Double, UnderwritingInfoPacket> riskMap = [:]
        for (UnderwritingInfoPacket exposure: exposures) {
            double maxSumInsured = exposure.maxSumInsured
            if (maxSumInsured > 0) {
                if (riskMap.containsKey(maxSumInsured)) {
                    throw new IllegalStateException("RiskToBandAllocatorStrategy.noDisjointRiskBands")
                }
                riskMap[maxSumInsured] = exposure
            }
        }
        return riskMap
    }

    private static List<ClaimRoot> filterClaimsByType(List<ClaimRoot> claims, ClaimType claimType) {
        List<ClaimRoot> claimsToAllocate = []
        for (ClaimRoot claim: claims) {
            if (claim.claimType == claimType) {
                claimsToAllocate << claim
            }
        }
        return claimsToAllocate
    }
}
