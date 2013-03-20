package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Ultimates only"

mappings = {
    GIRA {
        "segments" {
            "[%segment%]" {
                "netClaimsUltimate" "GIRA:segments:[%segment%]:outClaimsNet:ultimate", {
                    "grossClaimsUltimate" "GIRA:segments:[%segment%]:outClaimsGross:ultimate"
                    "cededClaimsUltimate" "GIRA:segments:[%segment%]:outClaimsCeded:ultimate"
                }
            }
        }
        "structures" {
            "[%structure%]" {
                "netClaimsUltimate" "GIRA:structures:[%structure%]:outClaimsNet:ultimate", {
                    "grossClaimsUltimate" "GIRA:structures:[%structure%]:outClaimsGross:ultimate"
                    "cededClaimsUltimate" "GIRA:structures:[%structure%]:outClaimsCeded:ultimate"
                }
            }
        }
        "reinsurance" {
            "[%contract%]" {
                "netClaimsUltimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:ultimate", {
                    "grossClaimsUltimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                    "cededClaimsUltimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                }
            }
        }
        "grossClaimsUltimate" {
            "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
        }
    }
}