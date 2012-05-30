package models.gira

model=models.gira.GIRAModel
periodCount=1
displayName='Reserves'
applicationVersion='1.4-ALPHA-3'
periodLabels=["2011-01-01"]
components {
	globalParameters {
		parmRunOffAfterFirstPeriod[0]=true
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0)
	}
	indices {
		subReservesIndices {
			subMarine {
				parmIndex[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.DETERMINISTICINDEXSERIES, ["indices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[new org.joda.time.DateTime(2008,7,1,0,0,0,0), new org.joda.time.DateTime(2009,1,1,0,0,0,0), new org.joda.time.DateTime(2009,7,1,0,0,0,0), new org.joda.time.DateTime(2010,1,1,0,0,0,0), new org.joda.time.DateTime(2011,7,1,0,0,0,0), new org.joda.time.DateTime(2012,7,1,0,0,0,0), new org.joda.time.DateTime(2013,7,1,0,0,0,0), new org.joda.time.DateTime(2014,7,1,0,0,0,0), new org.joda.time.DateTime(2014,10,1,0,0,0,0)], [102.0, 104.0, 106.0, 110.0, 115.0, 125.0, 140.0, 160.0, 162.0]]),["Date","Index Level"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DETERMINISTICINDEX')),])
			}
		}
	}
	patterns {
		subPayoutAndReportingPatterns {
			subMarine {
				parmPattern[0]=org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.CUMULATIVE, ["cumulativePattern":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0, 6, 12, 18, 36, 48, 60, 72, 75], [0.2, 0.8, 0.9, 0.95, 1.1, 1.0, 1.0, 1.0, 1.0], [0.0, 0.1, 0.6, 0.8, 0.85, 0.9, 0.95, 0.99, 1.0]]),["Months","Cumulative Reported","Cumulative Payout"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PATTERN')),])
			}
		}
	}
	reservesGenerators {
		subMarineRepBased {
			parmIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMarine"], ["CONTINUOUS"]]),["Index","Index Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_INDEX_SELECTION'))
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subMarine')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subMarine')
			parmUltimateEstimationMethod[0]=org.pillarone.riskanalytics.domain.pc.cf.reserve.ReserveCalculationType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reserve.ReserveCalculationType.REPORTEDBASED, ["averageInceptionDate":new org.joda.time.DateTime(2008, 7, 1, 0, 0, 0, 0),"reportingDate":new org.joda.time.DateTime(2011, 3, 31, 0, 0, 0, 0),"reportedAtReportingDate":3500.0,"interpolationMode":org.pillarone.riskanalytics.domain.pc.cf.reserve.InterpolationMode.LINEAR,])
		}
	}
}
comments=[]
