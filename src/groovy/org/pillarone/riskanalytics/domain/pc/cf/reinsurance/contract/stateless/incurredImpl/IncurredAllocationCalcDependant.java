package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;

import com.google.common.collect.Maps;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossAndLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossWithTerm;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredAllocationCalcDependant implements IIncurredAllocation {

    public AllClaimsRIOutcome allocateClaims(final IncurredLossWithTerm inLossAndAP, IAllContractClaimCache claimStore, PeriodScope periodScope, ContractCoverBase base) {

        Set<IClaimRoot> grossClaimsThisPeriod = claimStore.allIncurredClaimsCurrentModelPeriodForAllocation(periodScope, base);
        final Collection<IncurredLossAndLayer> incurredLossAndLayers = inLossAndAP.getIncurredLoss().getLoss();

        final AllClaimsRIOutcome allClaimsRIOutcome = new AllClaimsRIOutcome();
        for (IClaimRoot iClaimRoot : grossClaimsThisPeriod) {
            Map<IRiLayer, Double> lossByLayer = Maps.newHashMap();
            double cededAmount = 0d;

            for (IncurredLossAndLayer incurredLossAndLayer : incurredLossAndLayers) {
                IRiLayer lp = incurredLossAndLayer.getLayerParameters();
                double lossToAnnualLayer = incurredLossAndLayer.getLossAfterClaimAndAnnualStructures().getLossAfterAnnualStructureWithShareApplied();
                double lossToLayerAfterClaimStructure = incurredLossAndLayer.getLossAfterClaimAndAnnualStructures().getLossAfterClaimStructure();
                double lossFromThisClaim = Math.min( Math.max ( iClaimRoot.getUltimate() - lp.getClaimExcess(), 0d ) , lp.getClaimLimit() );
                double lossByThisLayer = (lossFromThisClaim * lossToAnnualLayer * inLossAndAP.termLimitReAllocationPercentage()) / lossToLayerAfterClaimStructure;
                if (lossToLayerAfterClaimStructure == 0) {
                    lossByThisLayer = 0;
                }
                cededAmount += lossByThisLayer;
                lossByLayer.put(incurredLossAndLayer.getLayerParameters(), lossByThisLayer);
            }

            ICededRoot cededClaim = new CededClaimRoot( cededAmount , iClaimRoot, ClaimType.CEDED);
            ICededRoot netClaim = new CededClaimRoot( iClaimRoot.getUltimate() - cededAmount , iClaimRoot, ClaimType.NET);
            IncurredClaimRIOutcome incurredClaimRIOutcome = new IncurredClaimRIOutcome(netClaim, cededClaim, iClaimRoot);
            allClaimsRIOutcome.addClaim(incurredClaimRIOutcome);
        }

        return allClaimsRIOutcome;
    }


}
