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
            "reported" "NonLifeCashflow:claimsGenerators:outClaims:reported", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:reported"
            }
            "paid" "NonLifeCashflow:claimsGenerators:outClaims:paid", {
                "[%claimsGenerator%]" "NonLifeCashflow:claimsGenerators:[%claimsGenerator%]:outClaims:paid"
            }
        }
    }
}