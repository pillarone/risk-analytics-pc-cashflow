package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimPacketAggregator;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.UnifiedAdcLptContract;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnifiedAdcLptContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {


    /** Attachment point and Limit can be expressed as absolute or relative figures. */
    private UnifiedADCLPTBase contractBase;

    /** Ceded share of LPT */
    private double cededShare;

    private double attachmentPoint;
    private double limit;

    public Map getParameters() {
        Map params = new HashMap(4);
        params.put(CEDED_SHARE, cededShare);
        params.put(CONTRACT_BASE, contractBase);
        params.put(ATTACHMENT_POINT, attachmentPoint);
        params.put(LIMIT, limit);
        return params;
    }

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractType.UNIFIEDADCLPT;
    }

    /**
     *
     *
     *
     * @param period ignored
     * @param underwritingInfoPackets used for scaling relative contract parameters if the contract base is GNPI
     * @param base ignored
     * @param termDeductible ignored
     * @param termLimit ignored
     * @param claims
     * @return one contract
     */
    public List<IReinsuranceContract> getContracts(int period, List<UnderwritingInfoPacket> underwritingInfoPackets,
                                                   ExposureBase base, IPeriodDependingThresholdStore termDeductible,
                                                   IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims) {
        double scaledAttachmentPoint = attachmentPoint;
        double scaledLimit = limit;
        ClaimCashflowPacket aggregateClaim = new ClaimPacketAggregator().aggregate(claims);
        double paidCumulative = (aggregateClaim != null) ? -aggregateClaim.getPaidCumulatedIndexed() : 0;
        switch (contractBase) {
            case ABSOLUTE:
                break;
            case OUTSTANDING_PERCENTAGE:
                if (aggregateClaim != null) {
                    double totalOutstanding = -aggregateClaim.outstandingIndexed();
                    scaledAttachmentPoint *= totalOutstanding;
                    scaledLimit *= totalOutstanding;
                }
                else {
                    throw new SimulationException("Contract base set to outstanding percentage does not work as there " +
                            "are no claims provided for scaling.");
                }
                break;
            default:
                throw new NotImplementedException("UnifiedADCLPTBase " + contractBase.toString() + " not implemented.");
        }
        scaledAttachmentPoint += paidCumulative;
        return new ArrayList<IReinsuranceContract>(Arrays.asList(
                new UnifiedAdcLptContract(cededShare, scaledAttachmentPoint, scaledLimit)));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    public static final String CONTRACT_BASE = "contractBase";
    public static final String ATTACHMENT_POINT = "attachmentPoint";
    public static final String LIMIT = "limit";
    public static final String CEDED_SHARE= "cededShare";

}
