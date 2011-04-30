package models.nonLifeCashflow

import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType

model=models.nonLifeCashflow.NonLifeCashflowModel
periodCount=1
displayName='Claims'
applicationVersion='1.4-ALPHA-1.3'
components {
	claimsGenerators {
		subMarine {
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(
                    ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":DistributionType.getStrategy(DistributionType.LOGNORMAL, ["mean": 1d, "stDev": 1d]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.ABSOLUTE,])
		}
	}
}
comments=[]
