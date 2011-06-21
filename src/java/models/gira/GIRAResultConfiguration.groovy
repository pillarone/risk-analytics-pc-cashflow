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
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
