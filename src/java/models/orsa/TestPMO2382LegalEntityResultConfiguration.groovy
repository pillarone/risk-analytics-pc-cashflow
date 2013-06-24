package models.orsa

model = models.orsa.ORSAModel
displayName = "Legal Entity Ultimates"
components {
    legalEntities {
        subsubcomponents {
            outClaimsCeded = "AGGREGATE_BY_SOURCE_outstandingIndexed_totalIncrementalIndexed_premiumRiskBase_reserveRiskBase_premiumAndReserveRiskBase"
            outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
            outClaimsNet = "AGGREGATED_ULTIMATE_CLAIM"
            outClaimsPrimaryInsurer = "AGGREGATED_ULTIMATE_CLAIM"
            outClaimsReinsurer = "AGGREGATED_ULTIMATE_CLAIM"
//            outUnderwritingInfoCeded = "AGGREGATED"
//            outUnderwritingInfoGross = "AGGREGATED"
//            outUnderwritingInfoNet = "AGGREGATED"
//            outUnderwritingInfoPrimeryInsurer = "AGGREGATED"
//            outUnderwritingInfoReinsurer = "AGGREGATED"
        }
    }
    reinsuranceContracts {
        subsubcomponents {
            outClaimsCeded = "AGGREGATED_ULTIMATE_CLAIM"
            outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
//            outUnderwritingInfoCeded = "AGGREGATED"
//            outUnderwritingInfoGross = "AGGREGATED"
//            outUnderwritingInfoNet = "AGGREGATED"
        }
    }
}
