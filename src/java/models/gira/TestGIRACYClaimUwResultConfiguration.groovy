package models.gira

model = models.gira.GIRAModel
displayName = "CY Split for Claims and Underwriting"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "SPLIT_BY_INCEPTION_DATE"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "SPLIT_BY_INCEPTION_DATE"
		}
	}
}
