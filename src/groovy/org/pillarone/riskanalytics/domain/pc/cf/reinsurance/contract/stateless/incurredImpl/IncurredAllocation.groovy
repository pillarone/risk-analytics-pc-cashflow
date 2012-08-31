package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl;

import org.pillarone.riskanalytics.domain.pc.cf.claim.CededClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IIncurredAllocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * author simon.parten @ art-allianz . com
 */
public class IncurredAllocation implements IIncurredAllocation {

    public List<IClaimRoot> allocateClaims(double incurredInPeriod, Set<IClaimRoot> periodClaims) {

        Double grossIncurred = (Double) periodClaims*.getUltimate().sum()

        ArrayList<IClaimRoot> cededIncurred = periodClaims.collectAll {    it ->
            double incurredRatio = it.getUltimate() / grossIncurred
            new CededClaimRoot(- incurredRatio * incurredInPeriod, it)
        }
        return cededIncurred
    }
}
