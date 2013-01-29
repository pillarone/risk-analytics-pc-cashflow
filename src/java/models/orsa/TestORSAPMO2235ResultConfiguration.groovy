package models.orsa

model = models.orsa.ORSAModel
displayName = "Retro by Period"
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
