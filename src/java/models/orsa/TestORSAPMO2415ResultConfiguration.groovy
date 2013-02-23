package models.orsa

model = models.orsa.ORSAModel
displayName = "Retro Claims by Period"
components {
	retrospectiveReinsurance {
		subsubcomponents {
			outClaimsCeded = "AGGREGATE_BY_SOURCE_BY_PERIOD"
			outClaimsGross = "AGGREGATE_BY_SOURCE_BY_PERIOD"
			outClaimsNet = "AGGREGATE_BY_SOURCE_BY_PERIOD"
		}
	}
}
