package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Market Claims"

mappings = {
    GIRA {
        "claims" {
            "ultimate" "GIRA:claimsGenerators:outClaims:ultimate", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
            }
            "reportedIncrementalIndexed" "GIRA:claimsGenerators:outClaims:reportedIncrementalIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed"
            }
            "paidIncrementalIndexed" "GIRA:claimsGenerators:outClaims:paidIncrementalIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncrementalIndexed"
            }
            "outstandingIndexed" "GIRA:claimsGenerators:outClaims:outstandingIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:outstandingIndexed"
            }
            "IBNRIndexed" "GIRA:claimsGenerators:outClaims:IBNRIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:IBNRIndexed"
            }
            "reservesIndexed" "GIRA:claimsGenerators:outClaims:reservesIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reservesIndexed"
            }
            "increaseDueToIndex" "GIRA:claimsGenerators:outClaims:totalIncrementalIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:totalIncrementalIndexed"
            }
            "numberOfClaims" {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaimNumber:value"
            }
        }

    }
}