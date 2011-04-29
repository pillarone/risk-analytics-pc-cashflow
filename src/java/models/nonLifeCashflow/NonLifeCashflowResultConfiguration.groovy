package models.nonLifeCashflow

model = models.nonLifeCashflow.NonLifeCashflowModel
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
