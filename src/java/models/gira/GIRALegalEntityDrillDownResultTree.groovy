package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Legal Entity, Drill Down"

mappings = {
    GIRA {
        "legalEntities" {
            "[%legalEntity%]" {
                "financialsNetCashflow" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:netCashflow", {
                    "lossRatio" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:lossRatio"
                    "premium" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:netPremiumPaid"
                    "commission" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:commission"
                    "claim" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:netClaimPaid"
                }
                "claimsNet" {
                    "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reportedIncrementalIndexed"
                    "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:paidIncrementalIndexed"
                    "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:outstandingIndexed"
                    "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:IBNRIndexed"
                    "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reservesIndexed"
                    "claimsGross" {
                        "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:ultimate", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:ultimate", {
                                "byPeril" {
                                    "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:ultimate"
                                }
                                "bySegment" {
                                    "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:ultimate"
                                }
                            }
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:ultimate", {
                                "byPeril" {
                                    "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:ultimate"
                                }
                                "bySegment" {
                                    "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:ultimate"
                                }
                            }
                        }
                        "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reportedIncrementalIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed", {
                                "byPeril" {
                                    "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                                }
                                "bySegment" {
                                    "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                                }
                            }
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedIncrementalIndexed", {
                                "byPeril" {
                                    "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:reportedIncrementalIndexed"
                                }
                                "bySegment" {
                                    "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:reportedIncrementalIndexed"
                                }
                            }
                        }
                        "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:paidIncrementalIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:paidIncrementalIndexed", {
                                "byPeril" {
                                    "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:paidIncrementalIndexed"
                                }
                                "bySegment" {
                                    "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:paidIncrementalIndexed"
                                }
                            }
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:paidIncrementalIndexed", {
                                "byPeril" {
                                    "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:paidIncrementalIndexed"
                                }
                                "bySegment" {
                                    "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:paidIncrementalIndexed"
                                }
                            }
                        }
                        "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:outstandingIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:outstandingIndexed"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:IBNRIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:IBNRIndexed"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:IBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reservesIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reservesIndexed"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reservesIndexed"
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:ultimate", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:ultimate"
                            }
                            "byContract" {
                                "[%contract%]" "GIRA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                            }
                        }
                        "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "GIRA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:paidIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "GIRA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:outstandingIndexed"
                        "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:IBNRIndexed"
                        "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reservesIndexed"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumWritten", {
                            "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        }
                        "ceded" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumWritten", {
                            "[%contract%]" "GIRA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                        }
                    }
                    "premiumPaidNet" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        }
                        "ceded" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaid", {
                            "fixed" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaidVariable"
                            "byContract" {
                                "[%contract%]" "GIRA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                        }
                    }
                }
                "commission" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
    }
}