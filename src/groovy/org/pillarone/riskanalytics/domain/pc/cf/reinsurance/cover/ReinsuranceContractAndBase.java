package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

/**
 * Helper class used for covering preceding contracts.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContractAndBase {

    public IReinsuranceContractMarker reinsuranceContract;
    public ReinsuranceContractBase contractBase;
    private final ContractAndBaseIdentifier contractAndBaseIdentifier;


    public ReinsuranceContractAndBase(IReinsuranceContractMarker reinsuranceContract, ReinsuranceContractBase contractBase) {
        this.reinsuranceContract = reinsuranceContract;
        this.contractBase = contractBase;
        contractAndBaseIdentifier = new ContractAndBaseIdentifier(reinsuranceContract.getName(), contractBase);
    }

    public ContractAndBaseIdentifier getContractAndBaseIdentifier() {
        return contractAndBaseIdentifier;
    }

    @Override
    public String toString() {
        return reinsuranceContract + " (" + contractBase + ") ";
    }

    public class ContractAndBaseIdentifier {

        final String contractName;
        final ReinsuranceContractBase contractBase;

        public ContractAndBaseIdentifier(final String contractName, final ReinsuranceContractBase contractBase) {
            this.contractName = contractName;
            this.contractBase = contractBase;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof ContractAndBaseIdentifier)) return false;

            final ContractAndBaseIdentifier that = (ContractAndBaseIdentifier) o;

            if (contractBase != that.contractBase) return false;
            if (contractName != null ? !contractName.equals(that.contractName) : that.contractName != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = contractName != null ? contractName.hashCode() : 0;
            result = 31 * result + (contractBase != null ? contractBase.hashCode() : 0);
            return result;
        }
    }
}
