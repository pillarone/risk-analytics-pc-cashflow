package models.gira

model=models.gira.GIRAModel
periodCount=1
displayName='Retro'
applicationVersion='1.4-RC-4'
periodLabels=["2012-01-01"]
components {
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
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0)
		parmRunOffAfterFirstPeriod[0]=true
	}
	reservesGenerators {
		subMarine {
			parmIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_INDEX_SELECTION'))
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, '')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmUltimateEstimationMethod[0]=org.pillarone.riskanalytics.domain.pc.cf.reserve.ReserveCalculationType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reserve.ReserveCalculationType.ULTIMATE, ["averageInceptionDate":new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0),"reportingDate":new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0),"ultimateAtReportingDate":1000.0,])
		}
		subMotor {
			parmIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_INDEX_SELECTION'))
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, '')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmUltimateEstimationMethod[0]=org.pillarone.riskanalytics.domain.pc.cf.reserve.ReserveCalculationType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reserve.ReserveCalculationType.ULTIMATE, ["averageInceptionDate":new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0),"reportingDate":new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0),"ultimateAtReportingDate":2000.0,])
		}
	}
	retrospectiveReinsurance {
		subLPT {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContractType.LOSSPORTFOLIOTRANSFER, ["limit":1000.0,"cededShare":0.4,"reinsurancePremium":50.0,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.RetrospectiveCoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.RetrospectiveCoverAttributeStrategyType.ORIGINALRESERVES, ["reserves":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMarine", "subMotor"]]),["Reserve Generators"], org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker),])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
	}
}
comments=[]
tags=[]
