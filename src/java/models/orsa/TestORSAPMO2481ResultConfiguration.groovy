package models.orsa

model = models.orsa.ORSAModel
displayName = "Segments and Contracts (first splitted)"
components {
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_CLAIM"
		}
	}
	segments {
		subsubcomponents {
			outClaimsCeded = "AGGREGATE_BY_SOURCE_outstandingIndexed_totalIncrementalIndexed"
			outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_CLAIM"
		}
	}
}
