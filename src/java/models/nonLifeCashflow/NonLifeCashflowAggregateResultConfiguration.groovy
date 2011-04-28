package models.nonLifeCashflow

model = models.nonLifeCashflow.NonLifeCashflowModel
displayName = "Aggregate Gross Claims"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATED"
		}
	}
}
