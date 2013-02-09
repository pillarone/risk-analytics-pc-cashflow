package models.orsa

model = models.orsa.ORSAModel
displayName = "Segments and Contracts"
components {
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	segments {
		outClaimsCeded = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
		outClaimsGross = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
		outClaimsNet = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
		subsubcomponents {
			outClaimsCeded = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
			outClaimsGross = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
			outClaimsNet = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
			outFinancials = "AGGREGATE_FIN_BY_PERIOD"
			outUnderwritingInfoCeded = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoGross = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoNet = "AGGREGATE_BY_PERIOD"
		}
	}
	structures {
		substructure {
			outClaimsCeded = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
			outClaimsGross = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
			outClaimsNet = "AGGREGATE_outstandingIndexed_ultimate_totalIncrementalIndexed_totalCumulativeIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
			outFinancials = "AGGREGATE_FIN_BY_PERIOD"
		}
	}
}
