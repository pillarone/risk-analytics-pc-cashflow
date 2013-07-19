package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional;

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.AggregateEventClaimsStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ProRataTermXLContract extends TermWCXLContract {

    private double share;
    private int occurrencePeriod;

    /**
     * All provided values have to be absolute! Scaling is done within the parameter strategy.
     *
     * @param cededPremiumFixed
     * @param attachmentPoint
     * @param limit
     * @param aggregateDeductible
     * @param aggregateLimit
     * @param stabilization
     * @param reinstatementPremiumFactors
     * @param riPremiumSplit
     * @param termDeductible
     * @param termLimit
     */
    public ProRataTermXLContract(double share, int occurrencePeriod, double cededPremiumFixed, double attachmentPoint, double limit,
                                 double aggregateDeductible, double aggregateLimit, IStabilizationStrategy stabilization,
                                 List<Double> reinstatementPremiumFactors,
                                 IRIPremiumSplitStrategy riPremiumSplit, IPeriodDependingThresholdStore termDeductible,
                                 IPeriodDependingThresholdStore termLimit) {
        super(cededPremiumFixed, attachmentPoint, limit, aggregateDeductible, aggregateLimit, stabilization,
                reinstatementPremiumFactors, riPremiumSplit, termDeductible, termLimit);
        this.share = share;
        this.occurrencePeriod = occurrencePeriod;
    }

    public ProRataTermXLContract(LayerParameters layerParameters, int occurrencePeriod, double contractBase,
                                 IStabilizationStrategy stabilization,
                                 List<Double> reinstatementPremiumFactors,
                                 IRIPremiumSplitStrategy riPremiumSplit, IPeriodDependingThresholdStore termDeductible,
                                 IPeriodDependingThresholdStore termLimit) {
        this(layerParameters.getShare(), occurrencePeriod, contractBase, layerParameters.getClaimExcess() * contractBase,
                layerParameters.getClaimLimit() * contractBase,
                layerParameters.getLayerPeriodExcess() * contractBase, layerParameters.getLayerPeriodLimit() * contractBase,
                stabilization, reinstatementPremiumFactors, riPremiumSplit, termDeductible, termLimit);
    }


    protected void cededFactor(BasedOnClaimProperty claimPropertyBase, AggregateEventClaimsStorage storage, double stabilizationFactor) {
        double aggregateLimitValue = periodLimit.get(claimPropertyBase, stabilizationFactor);
        double factor = 0d;
        double cededAfterTermLimit = 0d;
        if (aggregateLimitValue > 0) {
            double termLimitValue = termLimit.get(claimPropertyBase, stabilizationFactor, occurrencePeriod);
            if (termLimitValue > 0) {
                double claimPropertyCumulated = storage.getCumulated(claimPropertyBase);
                // if the following two properties are of different values there are several updates for an event in the same period
                double claimPropertyIncremental = storage.getIncremental(claimPropertyBase);
                double claimPropertyIncrementalLast = storage.getIncrementalLast(claimPropertyBase);
                double ceded = Math.min(Math.max(claimPropertyCumulated - attachmentPoint * stabilizationFactor, 0), limit * stabilizationFactor);

                double cededAfterAAD = Math.max(0, ceded - periodDeductible.get(claimPropertyBase, stabilizationFactor));
                double reduceAAD = ceded - cededAfterAAD;
                cededAfterAAD = Math.max(0, cededAfterAAD - storage.getAadReductionInPeriod(claimPropertyBase));
                storage.addAadReductionInPeriod(claimPropertyBase, reduceAAD);
                periodDeductible.set(Math.max(0, periodDeductible.get(claimPropertyBase) - reduceAAD), claimPropertyBase);

                double cededAfterTermDeductible = Math.max(0, cededAfterAAD - termDeductible.get(claimPropertyBase, stabilizationFactor, occurrencePeriod));
                double reduceTermDeductible = cededAfterAAD - cededAfterTermDeductible;
                termDeductible.plus(-reduceTermDeductible, claimPropertyBase, occurrencePeriod);

                double incrementalCeded = Math.max(0, cededAfterTermDeductible - storage.getCumulatedCeded(claimPropertyBase) / share);
                double cededAfterAAL = aggregateLimitValue > incrementalCeded ? incrementalCeded : aggregateLimitValue;
                periodLimit.plus(-cededAfterAAL, claimPropertyBase);
                cededAfterTermLimit = termLimitValue > cededAfterAAL ? cededAfterAAL : termLimitValue;
                termLimit.plus(-cededAfterTermLimit, claimPropertyBase, occurrencePeriod);
                factor = claimPropertyIncrementalLast == 0 ? 0 : cededAfterTermLimit / claimPropertyIncrementalLast;
                factor *= share;
            }
        }
        storage.setCededFactor(claimPropertyBase, factor);
        // todo(sku): check if cededAfterAAL needs to be multiplied with the share
        storage.update(claimPropertyBase, cededAfterTermLimit * share);
    }

    /**
     * No distinction for different claim types
     * @param grossClaim
     * @return true
     */
    @Override
    protected boolean isClaimTypeCovered(ClaimCashflowPacket grossClaim) {
        return true;
    }
}
