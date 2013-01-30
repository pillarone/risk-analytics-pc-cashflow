package models.orsa

model=models.orsa.ORSAModel
periodCount=1
displayName='PMO-2235'
applicationVersion='1.6-BETA-2-kti'
periodLabels=["2012-01-01","2013-01-01","2014-01-01","2015-01-01","2016-01-01","2017-01-01","2018-01-01","2019-01-01","2020-01-01","2021-01-01","2022-01-01","2023-01-01","2024-01-01","2025-01-01"]
components {
	claimsGenerators {
		subMarine {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:500.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subMarine')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subMarine')
			parmRunOffIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmSeverityIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
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
		parmProjection[0]=org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType.PERIODS, ["number of periods":14,])
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0)
		parmRunOffAfterFirstPeriod[0]=false
	}
	patterns {
		subPayoutAndReportingPatterns {
			subMarine {
				parmPattern[0]=org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.CUMULATIVE, ["cumulativePattern":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0, 12, 24, 36, 48, 60, 72, 84], [0.05, 0.1, 0.35, 0.4, 0.65, 0.7, 0.95, 1.0], [0.1, 0.2, 0.7, 0.8, 1.0, 1.0, 1.0, 1.0]]),["Months","Cumulative Payout","Cumulative Reported"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PATTERN')),])
			}
		}
	}
	retrospectiveReinsurance {
		subMarineReserves {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.UNIFIEDADCLPT, ["limit":300.0,"cededShare":1.0,"contractBase":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective.UnifiedADCLPTBase.ABSOLUTE,"attachmentPoint":1000.0,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.MATRIX, ["benefitContracts":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Benefit Contract"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RI_CONTRACT')),"flexibleCover":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""], [""], [""], [""], ["subMarine"], ["ANY"]]),["Filtered by Net of Contract","Filtered by Ceded of Contract","Legal Entity","Segments","Generators","Kind of Loss"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COVER_MAP')),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.RETROACTIVE, ["coveredOccurencePeriodFrom":new org.joda.time.DateTime(2013, 1, 1, 0, 0, 0, 0),"coveredOccurencePeriodTo":new org.joda.time.DateTime(2018, 1, 1, 0, 0, 0, 0),"coveredDevelopmentPeriodStartDate":new org.joda.time.DateTime(2018, 1, 1, 0, 0, 0, 0),])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
	}
}
comments=[]
tags=[]
