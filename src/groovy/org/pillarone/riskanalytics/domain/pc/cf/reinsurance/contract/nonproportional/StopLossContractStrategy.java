package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation.IBoundaryIndexStrategy;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class StopLossContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    /** Premium can be expressed as a fraction of a base quantity. */
    private StopLossBase stopLossContractBase;

    /** Premium as a percentage of the premium base */
    private double premium;

    /** Strategy to allocate the ceded premium to the different lines of business  */
    private IRIPremiumSplitStrategy premiumAllocation;
    private double attachmentPoint;
    private double limit;
    protected IBoundaryIndexStrategy boundaryIndex;

    public Map getParameters() {
        Map params = new HashMap(5);
        params.put(CONTRACT_BASE, stopLossContractBase);
        params.put(PREMIUM, premium);
        params.put(PREMIUM_ALLOCATION, premiumAllocation);
        params.put(ATTACHMENT_POINT, attachmentPoint);
        params.put(LIMIT, limit);
        params.put(BOUNDARY_INDEX, boundaryIndex);
        return params;
    }

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractType.STOPLOSS;
    }

    /**
     *
     *
     *
     * @param periodCounter ignored
     * @param underwritingInfoPackets used for scaling relative contract parameters if the contract base is GNPI
     * @param base ignored
     * @param termDeductible ignored
     * @param termLimit ignored
     * @param claims
     * @param factors
     * @return one contract
     */
    public List<IReinsuranceContract> getContracts(IPeriodCounter periodCounter, List<UnderwritingInfoPacket> underwritingInfoPackets,
                                                   ExposureBase base, IPeriodDependingThresholdStore termDeductible,
                                                   IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims, List<FactorsPacket> factors) {
        double cededPremiumFixed = premium;
        double scaledAttachmentPoint = attachmentPoint;
        double scaledLimit = limit;
        switch (stopLossContractBase) {
            case ABSOLUTE:
                break;
            case GNPI:
                double gnpi = UnderwritingInfoUtils.sumPremiumWritten(underwritingInfoPackets);
                cededPremiumFixed *= gnpi;
                scaledAttachmentPoint *= gnpi;
                scaledLimit *= gnpi;
                break;
            default:
                throw new NotImplementedException("StopLossBase " + stopLossContractBase.toString() + " not implemented.");
        }
        return new ArrayList<IReinsuranceContract>(Arrays.asList(
                new StopLossContract(cededPremiumFixed, scaledAttachmentPoint, scaledLimit, premiumAllocation, boundaryIndex, factors, periodCounter)));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public static final String CONTRACT_BASE = "stopLossContractBase";
    public static final String PREMIUM = "premium";
    public static final String PREMIUM_ALLOCATION = "riPremiumSplit";
    public static final String ATTACHMENT_POINT = "attachmentPoint";
    public static final String LIMIT = "limit";
    public static final String BOUNDARY_INDEX = "boundaryIndex";

}
