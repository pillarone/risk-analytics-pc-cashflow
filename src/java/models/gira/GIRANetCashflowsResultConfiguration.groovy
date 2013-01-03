package models.gira

model = models.gira.GIRAModel
displayName = "Net Cashflows (Legal Entites, Segments)"
components {
	legalEntities {
		subsubcomponents {
			outFinancials = "AGGREGATED"
		}
	}
	segments {
		subsubcomponents {
			outFinancials = "AGGREGATED"
		}
	}
}
