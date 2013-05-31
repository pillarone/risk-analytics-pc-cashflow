package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredCalc

import com.google.common.collect.Sets
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimUtils
import org.pillarone.riskanalytics.domain.pc.cf.exposure.AllPeriodUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.AllClaimsRIOutcome
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ScaledPeriodLayerParameters
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.LossAfterTermStructure
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.ContractClaimStoreTestIncurredClaimImpl
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IContractClaimByModelPeriod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IContractClaimStore
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.AnnualIncurredCalc
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.IncurredAllocation
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.TermIncurredCalculation

/**
 * Created with IntelliJ IDEA.
 * User: sparten
 * Date: 31.08.12
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
/**
 *   author simon.parten @ art-allianz . com
 */
class IncurredAlloocationTest extends GroovyTestCase {

    DateTime start2010 = new DateTime(2010, 1, 1, 1, 0, 0, 0)
    DateTime start2011 = new DateTime(2011, 1, 1, 1, 0, 0, 0)

    GrossClaimRoot grossClaimRoot1 = TestClaimUtils.getGrossClaim([5i], [1d], 100d, start2010, start2010, start2010) /* 100 */
    GrossClaimRoot grossClaimRoot2 = TestClaimUtils.getGrossClaim([6i, 7i], [0.5d, 1d], 120d, start2010, start2010, start2010) /* 60+60 */
    GrossClaimRoot grossClaimRoot3 = TestClaimUtils.getGrossClaim([7i, 15i], [0.5d, 1d], 100d, start2010, start2010, start2010) /* 50 + 50 */

    void testIncurredAllocation(){
        Set<IClaimRoot> someClaims = Sets.newHashSet()
        someClaims << grossClaimRoot1 << grossClaimRoot2 << grossClaimRoot3
        PeriodScope scope = TestPeriodScopeUtilities.getPeriodScope(start2010, 3)
        IContractClaimByModelPeriod byModelPeriod = new ContractClaimStoreTestIncurredClaimImpl(someClaims)
        final IncurredAllocation allocation = new IncurredAllocation()
        final AllClaimsRIOutcome outcome = new AllClaimsRIOutcome()
        double totalIncurred = grossClaimRoot1.getUltimate() + grossClaimRoot2.getUltimate() + grossClaimRoot3.getUltimate()

        outcome = allocation.allocateClaims(0d, byModelPeriod, scope, ContractCoverBase.LOSSES_OCCURING)
        assert outcome.allCededClaims*.getUltimate().sum()  == 0d
        assert outcome.allNetClaims*.getUltimate().sum()  == totalIncurred

        outcome = allocation.allocateClaims(60d, byModelPeriod, scope, ContractCoverBase.LOSSES_OCCURING)
        assert outcome.allCededClaims*.getUltimate().sum()  == 60d
        assert outcome.allNetClaims*.getUltimate().sum()  == totalIncurred - 60

        outcome = allocation.allocateClaims(totalIncurred, byModelPeriod, scope, ContractCoverBase.LOSSES_OCCURING)
        assert outcome.allCededClaims*.getUltimate().sum()  == totalIncurred
        assert outcome.allNetClaims*.getUltimate().sum()  == 0d
    }
}