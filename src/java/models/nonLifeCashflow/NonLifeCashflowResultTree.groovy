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
        "reinsurance" {
            "[%contract%]" {
                "ultimateCeded" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                "reportedCeded" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncremental"
                "paidCeded" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncremental"
                "ultimateGross" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaims:ultimate"
                "reportedGross" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaims:reportedIncremental"
                "paidGross" "NonLifeCashflow:reinsuranceContracts:[%contract%]:outClaims:paidIncremental"
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