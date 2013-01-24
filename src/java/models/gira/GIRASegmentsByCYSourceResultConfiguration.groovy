package models.gira

model = models.gira.GIRAModel
displayName = "Segments (Calendar Year/Source)"
components {
	segments {
		subsubcomponents {
			outClaimsCeded = "AGGREGATE_BY_SOURCE_BY_PERIOD"
			outClaimsGross = "AGGREGATE_BY_SOURCE_BY_PERIOD"
			outClaimsNet = "AGGREGATE_BY_SOURCE_BY_PERIOD"
			outDiscountedValues = "AGGREGATED"
			outFinancials = "AGGREGATE_BY_SOURCE_BY_PERIOD"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATE_BY_SOURCE_BY_PERIOD"
			outUnderwritingInfoGross = "AGGREGATE_BY_SOURCE_BY_PERIOD"
			outUnderwritingInfoNet = "AGGREGATE_BY_SOURCE_BY_PERIOD"
		}
	}
}
