package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.RetrospectiveReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.TrivialContract;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TrivialContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    /**
     * This implementation ignores all provided parameters.
     *
     *
     *
     * @param periodCounter ignored
     * @param underwritingInfoPackets ignored
     * @param base ignored
     * @param termDeductible ignored
     * @param termLimit ignored
     * @param claims
     * @param factors
     * @return one contract
     */
    public List<IReinsuranceContract> getContracts(IPeriodCounter periodCounter,
                                                   List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                                   IPeriodDependingThresholdStore termDeductible,
                                                   IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims, List<FactorsPacket> factors) {
        return new ArrayList<IReinsuranceContract>(Arrays.asList(new TrivialContract()));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public IParameterObjectClassifier getType() {
        return RetrospectiveReinsuranceContractType.TRIVIAL;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
