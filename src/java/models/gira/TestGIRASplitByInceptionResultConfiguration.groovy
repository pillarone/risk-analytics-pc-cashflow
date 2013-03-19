package models.gira

model = models.gira.GIRAModel
displayName = "Premium Reserve Risk Triangle"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATED"
		}
	}
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "SPLIT_PER_SOURCE"
			outClaimsGross = "SPLIT_PER_SOURCE"
			outClaimsNet = "SPLIT_PER_SOURCE"
		}
	}
	segments {
		outClaimsCeded = "AGGREGATE_BY_PERIOD_outstandingIndexed_totalIncrementalIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
		outClaimsGross = "AGGREGATE_BY_PERIOD_outstandingIndexed_totalIncrementalIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
		outClaimsNet = "AGGREGATE_BY_PERIOD_outstandingIndexed_totalIncrementalIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
		subsubcomponents {
			outClaimsCeded = "AGGREGATE_BY_PERIOD_outstandingIndexed_totalIncrementalIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
			outClaimsGross = "AGGREGATE_BY_PERIOD_outstandingIndexed_totalIncrementalIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
			outClaimsNet = "AGGREGATE_BY_PERIOD_outstandingIndexed_totalIncrementalIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
		}
	}
}
