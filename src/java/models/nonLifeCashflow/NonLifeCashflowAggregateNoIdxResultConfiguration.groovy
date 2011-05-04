package models.nonLifeCashflow

model = models.nonLifeCashflow.NonLifeCashflowModel
displayName = "Aggregate Gross Claims without Index Collection"
components {
	claimsGenerators {
		subsubcomponents {
			outClaimNumber = "AGGREGATED"
			outClaims = "AGGREGATED"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
