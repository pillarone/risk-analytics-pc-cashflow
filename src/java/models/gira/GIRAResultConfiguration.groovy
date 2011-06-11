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
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
