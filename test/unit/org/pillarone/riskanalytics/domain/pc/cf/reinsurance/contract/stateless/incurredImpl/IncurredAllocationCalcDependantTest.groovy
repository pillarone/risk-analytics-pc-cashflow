package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl

import com.google.common.collect.Sets
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.AllClaimsRIOutcome
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LossAfterClaimAndAnnualStructures
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLoss
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossAndAP
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossAndLayer
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.IncurredLossWithTerm
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.ContractClaimStoreTestIncurredClaimImpl
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IContractClaimByModelPeriod

/**
*   author simon.parten @ art-allianz . com
 */
class IncurredAllocationCalcDependantTest extends GroovyTestCase {

    DateTime start2010 = new DateTime(2010, 1, 1, 1, 0, 0, 0)
    DateTime start2011 = new DateTime(2011, 1, 1, 1, 0, 0, 0)

    GrossClaimRoot grossClaimRoot1
    GrossClaimRoot grossClaimRoot2
    GrossClaimRoot grossClaimRoot3

    @Override
    protected void setUp() throws Exception {
        super.setUp()    //To change body of overridden methods use File | Settings | File Templates.
        grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i], [1d], 100d, start2010, start2010, start2010) /* 100 */
        grossClaimRoot2 = TestClaimUtils.getGrossClaim([6i, 7i], [0.5d, 1d], 120d, start2010, start2010, start2010) /* 60+60 */
        grossClaimRoot3 = TestClaimUtils.getGrossClaim([7i, 15i], [0.5d, 1d], 100d, start2010, start2010, start2010) /* 50 + 50 */

    }

    void testAllNetAllCeded(){
        Set<IClaimRoot> someClaims = Sets.newHashSet()

        someClaims << grossClaimRoot1 << grossClaimRoot2 << grossClaimRoot3
        PeriodScope scope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IContractClaimByModelPeriod byModelPeriod = new ContractClaimStoreTestIncurredClaimImpl(someClaims)

        final IncurredAllocationCalcDependant allocation = new IncurredAllocationCalcDependant()
        final AllClaimsRIOutcome outcome;

        double totalIncurred = grossClaimRoot1.getUltimate() + grossClaimRoot2.getUltimate() + grossClaimRoot3.getUltimate()

        final LayerParameters parameters = new LayerParameters(1d, 0d, 0d,  1, 1)
        final LossAfterClaimAndAnnualStructures layerResult = new LossAfterClaimAndAnnualStructures(0d, 0d, parameters)
        final IncurredLossAndLayer layer = new IncurredLossAndLayer(layerResult, parameters)
        final IncurredLoss p = new IncurredLoss([ layer ])
        final IncurredLossWithTerm termAllNet = new IncurredLossWithTerm(p, 0d, totalIncurred, null, 0)

        outcome = allocation.allocateClaims(termAllNet, byModelPeriod, scope, ContractCoverBase.LOSSES_OCCURING,)
        assert outcome.allCededClaims*.getUltimate().sum()  == 0d
        assert outcome.allNetClaims*.getUltimate().sum()  == totalIncurred

        final LossAfterClaimAndAnnualStructures layerResult1 = new LossAfterClaimAndAnnualStructures(320d, 320d, parameters)
        final IncurredLossAndLayer layer1 = new IncurredLossAndLayer(layerResult1, parameters)
        final IncurredLoss p1 = new IncurredLoss([ layer1 ])
        final IncurredLossWithTerm termAllCeded = new IncurredLossWithTerm(p1, totalIncurred, 0d, null, 0)
        outcome = allocation.allocateClaims(termAllCeded, byModelPeriod, scope, ContractCoverBase.LOSSES_OCCURING,)
        assert outcome.allCededClaims*.getUltimate().sum()  == totalIncurred
        assert outcome.allNetClaims*.getUltimate().sum()  == 0d
    }

    void testAllocation(){
        Set<IClaimRoot> someClaims = Sets.newHashSet()

        someClaims << grossClaimRoot1 << grossClaimRoot2 << grossClaimRoot3
        PeriodScope scope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IContractClaimByModelPeriod byModelPeriod = new ContractClaimStoreTestIncurredClaimImpl(someClaims)

        final IncurredAllocationCalcDependant allocation = new IncurredAllocationCalcDependant()
        final AllClaimsRIOutcome outcome;

        double totalIncurred = grossClaimRoot1.getUltimate() + grossClaimRoot2.getUltimate() + grossClaimRoot3.getUltimate()

        final LayerParameters parameters1 = new LayerParameters(1d, 50d, 30d, 1, 1)
        final LayerParameters parameters2 = new LayerParameters(0.7d, 80d, 150d,  1, 2)
        final LossAfterClaimAndAnnualStructures layerResult1 = new LossAfterClaimAndAnnualStructures(90d, 90d, parameters1)
        final LossAfterClaimAndAnnualStructures layerResult2 = new LossAfterClaimAndAnnualStructures(80d, 80d, parameters2)
        final IncurredLossAndLayer layer1 = new IncurredLossAndLayer(layerResult1, parameters1)
        final IncurredLossAndLayer layer2 = new IncurredLossAndLayer(layerResult2, parameters2)
        final IncurredLoss p = new IncurredLoss([ layer1, layer2 ])
        final IncurredLossWithTerm termAlloc = new IncurredLossWithTerm(p, 146d, 0d, null, 0)

        outcome = allocation.allocateClaims(termAlloc, byModelPeriod, scope, ContractCoverBase.LOSSES_OCCURING,)
        assert outcome.allCededClaims*.getUltimate().sum()  == 146d
        assert outcome.allNetClaims*.getUltimate().sum()  == totalIncurred - 146

    }
}
