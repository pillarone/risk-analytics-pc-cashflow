package models.nonLifeCashflow

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = NonLifeCashflowModel
displayName = "Claims"
language = "en"

mappings = {
    NonLifeCashflow {
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
        }
        "gross claims" {
            "[%claimsGenerator%]" {
                "ultimate" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
                "reported" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncremental"
                "paid" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncremental"
                "outstanding" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:outstanding"
                "IBNR" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:IBNR"
                "reserves" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:reserves"
                "developedResult" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:developedResult"
            }
        }
    }
}