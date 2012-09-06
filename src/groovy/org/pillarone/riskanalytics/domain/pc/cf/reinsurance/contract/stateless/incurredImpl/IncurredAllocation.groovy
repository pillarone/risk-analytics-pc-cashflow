package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;

import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IIncurredAllocation;


import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredAllocation implements IIncurredAllocation {

    public List<ICededRoot> allocateClaims(double incurredInPeriod, Set<IClaimRoot> allIncurredClaims, PeriodScope periodScope, ContractCoverBase base) {

        Set<IClaimRoot> grossClaimsThisPeriod = RIUtilities.incurredClaimsByPeriod(periodScope.getCurrentPeriod(), periodScope.getPeriodCounter(), allIncurredClaims, base )

        Double grossIncurred = (Double) grossClaimsThisPeriod*.getUltimate().sum()

        ArrayList<ICededRoot> cededIncurred = grossClaimsThisPeriod.collectAll {    it ->
            double incurredRatio = it.getUltimate() / grossIncurred
            new CededClaimRoot(- incurredRatio * incurredInPeriod, it)
        }
        return cededIncurred
    }


}
