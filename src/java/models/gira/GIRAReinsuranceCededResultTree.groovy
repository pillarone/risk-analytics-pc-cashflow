package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Reinsurance Contract Financials, Ceded Ultimate"

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
                    "claimsCeded" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate", {
                            "bySegment" {
                                "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:ultimate"
                            }
                            "byPerils" {
                                "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                        }
                    }
                    "premium" {
                        "premiumWrittenCeded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten", {
                            "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                        }
                    }
                }
            }
        }
    }
}