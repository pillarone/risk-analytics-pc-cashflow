package org.pillarone.riskanalytics.domain.pc.cf.claim.allocation;


import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot

/**
 * @author jdittrich (at) munichre (dot) com
 * @author Michael-Noe (at) web (dot) de
 */

// TODO event claims not (yet) considered
// TODO discuss whether for the attritional losses it would be sufficient to first do an aggregation
// TODO discuss: the order of the outclaims is no longer the same as the claims!
class SumInsuredGeneratorRiskAllocatorStrategy extends AbstractParameterObject implements IRiskAllocatorStrategy {

    IRandomNumberGenerator generator
    RandomDistribution distribution = DistributionType.getStrategy(DistributionType.TRIANGULARDIST, ["a": 0d, "b": 1d, "m": 0.01])

    DistributionModified modification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])

    double bandMean = 1d / 3d

    public IParameterObjectClassifier getType() {
        return RiskAllocatorType.SUMINSUREDGENERATOR
    }

    public Map getParameters() {
        ["distribution": distribution,
                "modification": modification,
                "bandMean": bandMean]
    }

    /**
     * Sets exposure information for each claim.
     * Claims of type other than SINGLE have trivial exposure info values.
     */
    public List<ClaimRoot> getAllocatedClaims(List<ClaimRoot> claims, List<UnderwritingInfoPacket> underwritingInfos) {
        generator = RandomNumberGeneratorFactory.getGenerator(distribution, modification)
        // get Maximum Sum Insured (MSI) from upper bound of risk profiles
        double maxSumInsuredUWI = underwritingInfos[-1].maxSumInsured
        List<ClaimRoot> allocatedClaims = new ArrayList<ClaimRoot>();
        // calculate/construct exposure for each claim
        for (ClaimRoot claim: claims) {
            if (claim.hasExposureInfo()) throw new IllegalArgumentException("SumInsuredGeneratorRiskAllocatorStrategy.noExposureRedefinition")
            ExposureInfo exposure = underwritingInfos[-1].getExposure();  // new ExposureInfo(PeriodScope periodScope) what is periodScope?
            exposure.setExposureDefinition(ExposureBase.ABSOLUTE);
            exposure.setMaxSumInsured(maxSumInsuredUWI);
            exposure.setSumInsured(claim.ultimate + generator.nextValue() * (maxSumInsuredUWI - claim.ultimate));
            ClaimRoot copy = claim.withExposure(exposure);
            /*UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket()
            underwritingInfo.exposureDefinition = Exposure.ABSOLUTE
//            if (claim.claimType == ClaimType.SINGLE) {
                underwritingInfo.maxSumInsured = maxSumInsuredUWI
                underwritingInfo.sumInsured = claim.ultimate + generator.nextValue() * (maxSumInsuredUWI - claim.ultimate)
//            }
            claim.exposure = underwritingInfo  */
            allocatedClaims << copy
        }
        return allocatedClaims;
    }
}
