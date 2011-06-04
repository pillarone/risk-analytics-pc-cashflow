package models.nonLifeCashflow

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = NonLifeCashflowModel
displayName = "Claims"

mappings = {
    NonLifeCashflow {
        "grossClaims" {
            "[%claimsGenerator%]" {
                "ultimate" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
                "reported" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncremental"
                "paid" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncremental"
                "outstanding" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:outstanding"
                "IBNR" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:IBNR"
                "reserves" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:reserves"
                "developedResult" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:developedResult"
                "numberOfClaims" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaimNumber:value"
                "severityIndices" {
                    "continuous" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:continuous"
                    "stepwise, previous" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:stepwisePrevious"
                    "stepwise, next" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:stepwiseNext"
                }
            }
        }
        "grossUnderwritingWritten" "NonLifeCashflow:underwritingSegments:outUnderwritingInfo:premiumWritten"
        "grossUnderwritingPaid" "NonLifeCashflow:underwritingSegments:outUnderwritingInfo:premiumPaid"
        "grossUnderwritingBySegment" {
            "[%underwritingSegment%]" {
                "premiumWritten" "NonLifeCashflow:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumWritten"
                "premiumPaid" "NonLifeCashflow:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumPaid"
                "policyIndex" "NonLifeCashflow:underwritingSegments:[%underwritingSegment%]:outPolicyIndexApplied:value"
                "premiumIndex" "NonLifeCashflow:underwritingSegments:[%underwritingSegment%]:outPremiumIndexApplied:value"
            }
        }
        "segments" {
            "[%segment%]" {
                "claimsNet" {
                    "ultimate" "NonLifeCashflow:segments:[%segment%]:outClaimsNet:ultimate"
                    "reported" "NonLifeCashflow:segments:[%segment%]:outClaimsNet:reportedIncremental"
                    "paid" "NonLifeCashflow:segments:[%segment%]:outClaimsNet:paidIncremental"
                    "outstanding" "NonLifeCashflow:segments:[%segment%]:outClaimsNet:outstanding"
                    "IBNR" "NonLifeCashflow:segments:[%segment%]:outClaimsNet:IBNR"
                    "reserves" "NonLifeCashflow:segments:[%segment%]:outClaimsNet:reserves"
                    "developedResult" "NonLifeCashflow:segments:[%segment%]:outClaimsNet:developedResult"
                    "claimsGross" {
                        "ultimate" "NonLifeCashflow:segments:[%segment%]:outClaimsGross:ultimate"
                        "reported" "NonLifeCashflow:segments:[%segment%]:outClaimsGross:reportedIncremental"
                        "paid" "NonLifeCashflow:segments:[%segment%]:outClaimsGross:paidIncremental"
                        "outstanding" "NonLifeCashflow:segments:[%segment%]:outClaimsGross:outstanding"
                        "IBNR" "NonLifeCashflow:segments:[%segment%]:outClaimsGross:IBNR"
                        "reserves" "NonLifeCashflow:segments:[%segment%]:outClaimsGross:reserves"
                        "developedResult" "NonLifeCashflow:segments:[%segment%]:outClaimsGross:developedResult"
                    }
                    "claimsCeded" {
                        "ultimate" "NonLifeCashflow:segments:[%segment%]:outClaimsCeded:ultimate"
                        "reported" "NonLifeCashflow:segments:[%segment%]:outClaimsCeded:reportedIncremental"
                        "paid" "NonLifeCashflow:segments:[%segment%]:outClaimsCeded:paidIncremental"
                        "outstanding" "NonLifeCashflow:segments:[%segment%]:outClaimsCeded:outstanding"
                        "IBNR" "NonLifeCashflow:segments:[%segment%]:outClaimsCeded:IBNR"
                        "reserves" "NonLifeCashflow:segments:[%segment%]:outClaimsCeded:reserves"
                        "developedResult" "NonLifeCashflow:segments:[%segment%]:outClaimsCeded:developedResult"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid", {
                            "fixed" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed"
                         "variable" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoCeded:commission", {
                        "fixed" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoCeded:commissionFixed"
                        "variable" "NonLifeCashflow:segments:[%segment%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
        "reinsurance" {
            "[%contract%]" {
                "claimsNet" {
                    "ultimate" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsNet:ultimate"
                    "reported" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncremental"
                    "paid" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncremental"
                    "outstanding" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsNet:outstanding"
                    "IBNR" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsNet:IBNR"
                    "reserves" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsNet:reserves"
                    "developedResult" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsNet:developedResult"
                    "claimsGross" {
                        "ultimate" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                        "reported" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncremental"
                        "paid" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncremental"
                        "outstanding" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsGross:outstanding"
                        "IBNR" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsGross:IBNR"
                        "reserves" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsGross:reserves"
                        "developedResult" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsGross:developedResult"
                    }
                    "claimsCeded" {
                        "ultimate" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        "reported" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncremental"
                        "paid" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncremental"
                        "outstanding" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:outstanding"
                        "IBNR" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNR"
                        "reserves" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:reserves"
                        "developedResult" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:developedResult"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                            "fixed" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed"
                         "variable" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission", {
                        "fixed" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionFixed"
                        "variable" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
        "claims" {
            "ultimate" "NonLifeCashflow:claimsGenerators:outClaims:ultimate", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
            }
            "reported" "NonLifeCashflow:claimsGenerators:outClaims:reportedIncremental", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncremental"
            }
            "paid" "NonLifeCashflow:claimsGenerators:outClaims:paidIncremental", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncremental"
            }
            "outstanding" "NonLifeCashflow:claimsGenerators:outClaims:outstanding", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:outstanding"
            }
            "IBNR" "NonLifeCashflow:claimsGenerators:outClaims:IBNR", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:IBNR"
            }
            "reserves" "NonLifeCashflow:claimsGenerators:outClaims:reserves", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:reserves"
            }
            "developedResult" "NonLifeCashflow:claimsGenerators:outClaims:developedResult", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:developedResult"
            }
            "numberOfClaims" {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaimNumber:value"
            }
        }

    }
}