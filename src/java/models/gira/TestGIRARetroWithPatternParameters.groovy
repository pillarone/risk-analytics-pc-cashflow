package models.gira

model=models.gira.GIRAModel
periodCount=1
displayName='Retro with Pattern'
applicationVersion='1.4-RC-5'
periodLabels=["2011-01-01","2012-01-01","2013-01-01"]
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
        parmProjectionStartDate[0]=new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0)
        parmRunOffAfterFirstPeriod[0]=true
    }
    indices {
        subRunOffIndices {
            subMarine {
                parmIndex[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.DETERMINISTICINDEXSERIES, ["indices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[new org.joda.time.DateTime(2008,7,1,0,0,0,0), new org.joda.time.DateTime(2009,1,1,0,0,0,0), new org.joda.time.DateTime(2009,7,1,0,0,0,0), new org.joda.time.DateTime(2010,1,1,0,0,0,0), new org.joda.time.DateTime(2011,7,1,0,0,0,0), new org.joda.time.DateTime(2012,7,1,0,0,0,0), new org.joda.time.DateTime(2013,7,1,0,0,0,0), new org.joda.time.DateTime(2014,7,1,0,0,0,0), new org.joda.time.DateTime(2014,10,1,0,0,0,0)], [102.0, 104.0, 106.0, 110.0, 115.0, 125.0, 140.0, 160.0, 162.0]]),["Date","Index Level"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DETERMINISTICINDEX')),])
            }
            subTriviIndex {
                parmIndex[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.NONE, [:])
            }
        }
    }
    patterns {
        subPayoutAndReportingPatterns {
            subArnoldPattern24 {
                parmPattern[0]=org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.CUMULATIVE, ["cumulativePattern":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0, 12, 24], [0.0, 0.0, 1.0], [1.0, 1.0, 1.0]]),["Months","Cumulative Payout","Cumulative Reported"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PATTERN')),])
            }
            subMarine {
                parmPattern[0]=org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.CUMULATIVE, ["cumulativePattern":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0, 6, 12, 18, 36, 48, 60, 72, 75], [0.2, 0.8, 0.9, 0.95, 1.1, 1.0, 1.0, 1.0, 1.0], [0.0, 0.1, 0.6, 0.8, 0.85, 0.9, 0.95, 0.99, 1.0]]),["Months","Cumulative Reported","Cumulative Payout"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PATTERN')),])
            }
        }
    }
    reinsuranceContracts {
        subRIArnold {
            parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.TRIVIAL, [:])
            parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.ORIGINALCLAIMS, ["filter":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType.SEGMENTS, ["segments":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subSegement1"]]),["Segments"], org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker),]),])
            parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
            parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
        }
    }
    reservesGenerators {
        subMarineReserves {
            parmIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subTriviIndex"], ["CONTINUOUS"]]),["Index","Index Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
            parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subArnoldPattern24')
            parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subArnoldPattern24')
            parmUltimateEstimationMethod[0]=org.pillarone.riskanalytics.domain.pc.cf.reserve.ReserveCalculationType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reserve.ReserveCalculationType.REPORTEDBASED, ["averageInceptionDate":new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0),"reportingDate":new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0),"reportedAtReportingDate":4000.0,"interpolationMode":org.pillarone.riskanalytics.domain.pc.cf.reserve.InterpolationMode.LINEAR,])
        }
    }
    retrospectiveReinsurance {
        subElPeTe {
            parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContractType.LOSSPORTFOLIOTRANSFER, ["limit":10000.0,"cededShare":0.5,"reinsurancePremium":2100.0,])
            parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.RetrospectiveCoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.RetrospectiveCoverAttributeStrategyType.ORIGINALRESERVES, ["reserves":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMarineReserves"]]),["Reserve Generators"], org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker),])
            parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
        }
    }
    segments {
        subSegement1 {
            parmClaimsPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Claims Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
            parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker, '')
            parmDiscounting[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
            parmReservesPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMarineReserves"], [1.0]]),["Reserves Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
            parmUnderwritingPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
        }
    }
}
comments=[]
tags=[]
