package models.orsa

model = models.orsa.ORSAModel
displayName = "marine"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATE_BY_PERIOD"
		}
	}
	retrospectiveReinsurance {
		subsubcomponents {
			outClaimsCeded = "AGGREGATE_BY_PERIOD"
			outClaimsGross = "AGGREGATE_BY_PERIOD"
		}
	}
}
