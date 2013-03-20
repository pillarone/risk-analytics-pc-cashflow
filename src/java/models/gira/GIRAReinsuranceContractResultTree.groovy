package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Reinsurance Contract Financials, Drill Down"

mappings = {
    GIRA {
        "reinsurance" {
            "Financials" {
                "[%contract%]" "GIRA:reinsuranceContracts:[%contract%]:outContractFinancials:contractResult", {
                    "premium" "GIRA:reinsuranceContracts:[%contract%]:outContractFinancials:cededPremium"
                    "commission" "GIRA:reinsuranceContracts:[%contract%]:outContractFinancials:cededCommission"
                    "claim" "GIRA:reinsuranceContracts:[%contract%]:outContractFinancials:cededClaim"
                }
            }
            "DrillDown" {
                "[%contract%]" {
                    "claimsNet" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:ultimate"
                        "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncrementalIndexed"
                        "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncrementalIndexed"
                        "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:outstandingIndexed"
                        "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:IBNRIndexed"
                        "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reservesIndexed"
                        "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:totalIncrementalIndexed"
                        "claimsGross" {
                            "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:ultimate"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:ultimate"
                                }
                            }
                            "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                                }
                            }
                            "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncrementalIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                                }
                            }
                            "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:outstandingIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:outstandingIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                                }
                            }
                            "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:IBNRIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:IBNRIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                                }
                            }
                            "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reservesIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:reservesIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                                }
                            }
                            "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:totalIncrementalIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:totalIncrementalIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                                }
                            }
                        }
                        "claimsCeded" {
                            "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:ultimate"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                                }
                            }
                            "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                                }
                            }
                            "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                                }
                            }
                            "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:outstandingIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                                }
                            }
                            "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:IBNRIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                                }
                            }
                            "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:reservesIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                                }
                            }
                            "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:totalIncrementalIndexed", {
                                "bySegment" {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:totalIncrementalIndexed"
                                }
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                                }
                            }
                        }
                    }
                    "premium" {
                        "premiumWrittenNet" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                            "gross" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumWritten", {
                                "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                            }
                            "ceded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten", {
                                "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                            }
                        }
                        "premiumPaidNet" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                            "gross" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumPaid", {
                                "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                            }
                            "ceded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                                "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid"
                                "fixed" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed", {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed"
                                }
                                "variable" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable", {
                                    "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}