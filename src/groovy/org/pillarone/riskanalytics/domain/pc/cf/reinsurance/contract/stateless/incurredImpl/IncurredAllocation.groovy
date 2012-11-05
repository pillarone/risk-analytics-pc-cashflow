package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;

import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IIncurredAllocation;


import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredAllocation implements IIncurredAllocation {

    public List<ICededRoot> allocateClaims(double incurredInPeriod, IAllContractClaimCache claimStore, PeriodScope periodScope, ContractCoverBase base) {

        Set<IClaimRoot> grossClaimsThisPeriod = claimStore.allIncurredClaimsCurrentModelPeriod(periodScope, base)

        Double grossIncurred = (Double) grossClaimsThisPeriod*.getUltimate().sum()

        /* If the incurred amount is zero we want to cede no claims ! Divide by a large number */
        if (grossClaimsThisPeriod.size() > 0) {
            if (Math.abs(grossIncurred) == 0) {
                grossIncurred = Double.POSITIVE_INFINITY
            }
        }

        ArrayList<ICededRoot> cededIncurred = grossClaimsThisPeriod.collectAll {    it ->
            double incurredRatio = it.getUltimate() / grossIncurred
            new CededClaimRoot(incurredRatio * incurredInPeriod, it)
        }
        return cededIncurred
    }


}
