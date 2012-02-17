package models.gira

model = models.gira.GIRAModel
displayName = "Net Cashflows (Legal Entites, Segments)"
components {
	legalEntities {
		subsubcomponents {
			outNetFinancials = "AGGREGATED"
		}
	}
	segments {
		subsubcomponents {
			outNetFinancials = "AGGREGATED"
		}
	}
}
