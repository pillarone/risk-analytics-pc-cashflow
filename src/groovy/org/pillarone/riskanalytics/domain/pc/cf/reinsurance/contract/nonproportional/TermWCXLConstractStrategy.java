package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TermWCXLConstractStrategy extends XLConstractStrategy implements IReinsuranceContractStrategy {

    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.WCXLTERM;
    }

    public List<IReinsuranceContract> getContracts(List<UnderwritingInfoPacket> underwritingInfoPackets,
                                                   ThresholdStore termDeductible, EqualUsagePerPeriodThresholdStore termLimit) {
        double cededPremiumFixed = getCededPremiumFixed(underwritingInfoPackets);
        List<Double> reinstatementPremiumFactors = (List<Double>) reinstatementPremiums.getValues().get(0);
        return new ArrayList<IReinsuranceContract>(Arrays.asList(new TermWCXLContract(cededPremiumFixed, attachmentPoint,
                limit, aggregateDeductible, aggregateLimit, stabilization, reinstatementPremiumFactors, riPremiumSplit,
                termDeductible, termLimit)));
    }
}
