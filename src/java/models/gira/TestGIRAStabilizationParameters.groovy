package models.gira

import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints

model=models.gira.GIRAModel
periodCount=1
displayName='XL Stabilization'
applicationVersion='1.4-BETA-8'
periodLabels=["2011-01-01","2012-01-01","2013-01-01","2014-01-01","2015-01-01"]
components {
	claimsGenerators {
		subMarine {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase.ABSOLUTE,"frequencyModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"occurrenceDateDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:0.263]),"produceClaim":org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType.SINGLE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType.CONSTANT, [constant:1.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:1000.0]),"frequencyIndices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('FREQUENCY_INDEX_SELECTION')),])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subMarine')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subMarine')
			parmRunOffIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["clause inflation", "superimposed inflation"], ["CONTINUOUS", "CONTINUOUS"], ["FIXED_DATE", "DATE_OF_LOSS"], [new org.joda.time.DateTime(2010,6,30,0,0,0,0), new org.joda.time.DateTime(2011,1,1,0,0,0,0)]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
	}
	creditDefault {
		parmDefaultA[0]=5.0E-4
		parmDefaultAA[0]=1.0E-4
		parmDefaultAAA[0]=2.0E-5
		parmDefaultB[0]=0.04175
		parmDefaultBB[0]=0.012
		parmDefaultBBB[0]=0.0024
		parmDefaultC[0]=0.04175
		parmDefaultCC[0]=0.04175
		parmDefaultCCC[0]=0.04175
	}
	globalParameters {
		parmProjection[0]=org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType.COMPLETEROLLOUT, [:])
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0)
		parmRunOffAfterFirstPeriod[0]=true
	}
	indices {
		subRunOffIndices {
			subClauseInflation {
				parmIndex[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.DETERMINISTICINDEXSERIES, ["indices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[new org.joda.time.DateTime(2010,6,30,0,0,0,0), new org.joda.time.DateTime(2011,4,6,0,0,0,0), new org.joda.time.DateTime(2011,7,6,0,0,0,0), new org.joda.time.DateTime(2012,4,6,0,0,0,0), new org.joda.time.DateTime(2013,4,6,0,0,0,0), new org.joda.time.DateTime(2015,4,6,0,0,0,0)], [104.0, 110.0, 115.94, 137.42, 127.43, 129.98]]),["Date","Index Level"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DETERMINISTICINDEX')),])
			}
			subSuperimposedInflation {
				parmIndex[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.DETERMINISTICINDEXSERIES, ["indices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[new org.joda.time.DateTime(2011,4,6,0,0,0,0), new org.joda.time.DateTime(2011,7,6,0,0,0,0), new org.joda.time.DateTime(2012,4,6,0,0,0,0), new org.joda.time.DateTime(2013,4,6,0,0,0,0), new org.joda.time.DateTime(2015,4,6,0,0,0,0)], [90.0, 100.0, 110.0, 130.0, 140.0]]),["Date","Index Level"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DETERMINISTICINDEX')),])
			}
		}
	}
	patterns {
		subPayoutAndReportingPatterns {
			subMarine {
				parmPattern[0]=org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.CUMULATIVE, ["cumulativePattern":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0, 12, 24, 36, 48], [0.7, 0.8, 1.0, 1.0, 1.0], [0.01, 0.1, 0.6, 0.7, 1.0]]),["Months","Cumulative Reported","Cumulative Payout"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PATTERN')),])
			}
		}
	}
	reinsuranceContracts {
		subXL {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.WXL, ["limit":1000.0,"stabilization":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationStrategyType.FULL, ["stabilizationBasedOn":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationBasedOn.PAID,"inflationIndices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["clause inflation", "superimposed inflation"], ["CONTINUOUS", "CONTINUOUS"], ["FIXED_DATE", "DATE_OF_LOSS"], [new org.joda.time.DateTime(2010,6,30,0,0,0,0), new org.joda.time.DateTime(2011,1,1,0,0,0,0)]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION')),]),"reinstatementPremiums":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reinstatement Premium"],ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),"riPremiumSplit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType.PREMIUM_SHARES, [:]),"aggregateLimit":1000.0,"premium":0.0,"premiumBase":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.XLPremiumBase.ABSOLUTE,"aggregateDeductible":0.0,"attachmentPoint":500.0,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.ORIGINALCLAIMS, ["filter":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType.ALL, [:]),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
	}
}
comments=[]
tags=[]
