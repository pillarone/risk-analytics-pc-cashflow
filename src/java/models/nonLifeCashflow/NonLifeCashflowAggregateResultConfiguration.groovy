package models.nonLifeCashflow

model = models.nonLifeCashflow.NonLifeCashflowModel
displayName = "Aggregate Gross Claims"
components {
	claimsGenerators {
		subsubcomponents {
			outClaimNumber = "AGGREGATED"
			outClaims = "AGGREGATED"
			outSeverityIndexApplied = "AGGREGATED"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outPolicyIndexApplied = "AGGREGATED"
			outPremiumIndexApplied = "AGGREGATED"
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
