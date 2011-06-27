package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Details"

mappings = {
    GIRA {
        "grossClaims" {
            "[%claimsGenerator%]" {
                "ultimate" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
                "reported" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncremental"
                "paid" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncremental"
                "outstanding" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:outstanding"
                "IBNR" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:IBNR"
                "reserves" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reserves"
                "developedResult" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:developedResult"
                "numberOfClaims" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaimNumber:value"
                "severityIndices" {
                    "continuous" "GIRA:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:continuous"
                    "stepwise, previous" "GIRA:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:stepwisePrevious"
                    "stepwise, next" "GIRA:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:stepwiseNext"
                }
            }
        }
        "reserves" {
            "[%reservesGenerator%]" {
                "ultimateFromInceptionPeriod" "GIRA:reservesGenerators:[%reservesGenerator%]:outNominalUltimates:value"
                "reported" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reportedIncremental"
                "paid" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:paidIncremental"
                "outstanding" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:outstanding"
                "IBNR" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:IBNR"
                "reserves" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reserves"
                "developedResult" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:developedResult"
            }
        }
        "grossUnderwritingWritten" "GIRA:underwritingSegments:outUnderwritingInfo:premiumWritten"
        "grossUnderwritingPaid" "GIRA:underwritingSegments:outUnderwritingInfo:premiumPaid"
        "grossUnderwritingBySegment" {
            "[%underwritingSegment%]" {
                "premiumWritten" "GIRA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumWritten"
                "premiumPaid" "GIRA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumPaid"
                "policyIndex" "GIRA:underwritingSegments:[%underwritingSegment%]:outPolicyIndexApplied:value"
                "premiumIndex" "GIRA:underwritingSegments:[%underwritingSegment%]:outPremiumIndexApplied:value"
            }
        }
        "segments" {
            "[%segment%]" {
                "claimsNet" {
                    "ultimate" "GIRA:segments:[%segment%]:outClaimsNet:ultimate"
                    "reported" "GIRA:segments:[%segment%]:outClaimsNet:reportedIncremental"
                    "paid" "GIRA:segments:[%segment%]:outClaimsNet:paidIncremental"
                    "outstanding" "GIRA:segments:[%segment%]:outClaimsNet:outstanding"
                    "IBNR" "GIRA:segments:[%segment%]:outClaimsNet:IBNR"
                    "reserves" "GIRA:segments:[%segment%]:outClaimsNet:reserves"
                    "developedResult" "GIRA:segments:[%segment%]:outClaimsNet:developedResult"
                    "claimsGross" {
                        "ultimate" "GIRA:segments:[%segment%]:outClaimsGross:ultimate"
                        "reported" "GIRA:segments:[%segment%]:outClaimsGross:reportedIncremental"
                        "paid" "GIRA:segments:[%segment%]:outClaimsGross:paidIncremental"
                        "outstanding" "GIRA:segments:[%segment%]:outClaimsGross:outstanding"
                        "IBNR" "GIRA:segments:[%segment%]:outClaimsGross:IBNR"
                        "reserves" "GIRA:segments:[%segment%]:outClaimsGross:reserves"
                        "developedResult" "GIRA:segments:[%segment%]:outClaimsGross:developedResult"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:segments:[%segment%]:outClaimsCeded:ultimate"
                        "reported" "GIRA:segments:[%segment%]:outClaimsCeded:reportedIncremental"
                        "paid" "GIRA:segments:[%segment%]:outClaimsCeded:paidIncremental"
                        "outstanding" "GIRA:segments:[%segment%]:outClaimsCeded:outstanding"
                        "IBNR" "GIRA:segments:[%segment%]:outClaimsCeded:IBNR"
                        "reserves" "GIRA:segments:[%segment%]:outClaimsCeded:reserves"
                        "developedResult" "GIRA:segments:[%segment%]:outClaimsCeded:developedResult"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:segments:[%segment%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:segments:[%segment%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "GIRA:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid", {
                            "fixed" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
        "reinsurance" {
            "[%contract%]" {
                "claimsNet" {
                    "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:ultimate"
                    "reported" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncremental"
                    "paid" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncremental"
                    "outstanding" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:outstanding"
                    "IBNR" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:IBNR"
                    "reserves" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reserves"
//                    "developedResult" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:developedResult"
                    "claimsGross" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                        "reported" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncremental"
                        "paid" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncremental"
                        "outstanding" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:outstanding"
                        "IBNR" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:IBNR"
                        "reserves" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reserves"
                        "developedResult" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:developedResult"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        "reported" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncremental"
                        "paid" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncremental"
                        "outstanding" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:outstanding"
                        "IBNR" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNR"
                        "reserves" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reserves"
//                        "developedResult" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:developedResult"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                            "fixed" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
        "claims" {
            "ultimate" "GIRA:claimsGenerators:outClaims:ultimate", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
            }
            "reported" "GIRA:claimsGenerators:outClaims:reportedIncremental", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncremental"
            }
            "paid" "GIRA:claimsGenerators:outClaims:paidIncremental", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncremental"
            }
            "outstanding" "GIRA:claimsGenerators:outClaims:outstanding", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:outstanding"
            }
            "IBNR" "GIRA:claimsGenerators:outClaims:IBNR", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:IBNR"
            }
            "reserves" "GIRA:claimsGenerators:outClaims:reserves", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reserves"
            }
            "developedResult" "GIRA:claimsGenerators:outClaims:developedResult", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:developedResult"
            }
            "numberOfClaims" {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaimNumber:value"
            }
        }

    }
}