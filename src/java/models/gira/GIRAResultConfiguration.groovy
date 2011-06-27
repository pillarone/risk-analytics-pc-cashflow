package models.gira

model = models.gira.GIRAModel
displayName = "Single Gross Claims"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "SINGLE"
            outClaimNumber ="AGGREGATED"
		}
	}
    reservesGenerators {
		subsubcomponents {
			outReserves = "SINGLE"
            outNominalUltimates = "SINGLE"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
