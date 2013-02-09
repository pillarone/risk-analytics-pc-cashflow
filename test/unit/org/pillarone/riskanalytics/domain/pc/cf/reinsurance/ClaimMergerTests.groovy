package org.pillarone.riskanalytics.domain.pc.cf.reinsurance

import grails.test.GrailsUnitTestCase
import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter
import org.pillarone.riskanalytics.core.util.TestPretendInChannelWired
import org.pillarone.riskanalytics.domain.pc.cf.claim.*
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.TermReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeStrategy

class ClaimMergerTests extends GrailsUnitTestCase {
    PacketList<ClaimCashflowPacket> gross
    PacketList<ClaimCashflowPacket> ceded
    PacketList<ClaimCashflowPacket> net
    IComponentMarker marker
    def contract1
    def contract2
    IClaimRoot baseClaim
    ClaimMerger merger

    @Override
    protected void setUp() {
        super.setUp()
        ConstraintsFactory.registerConstraint(new CoverMap())
        merger = new ClaimMerger()
        merger.coverAttributeStrategy = setupStrategy()
        marker = new ClaimsGenerator(name: 'attritional')
        contract1 = new TermReinsuranceContract(name: 'contract1')
        contract2 = new TermReinsuranceContract(name: 'contract2')
        baseClaim = new ClaimRoot(-100, ClaimType.EVENT, new DateTime(), new DateTime())
        gross = new PacketList<ClaimCashflowPacket>()
        ceded = new PacketList<ClaimCashflowPacket>()
        net = new PacketList<ClaimCashflowPacket>()
    }

    private MatrixCoverAttributeStrategy setupStrategy(List parameters = [[''], [''], [''], [''], [''], ['ANY']]) {
        def result = [:]
        result.flexibleCover = new ConstrainedMultiDimensionalParameter(parameters,
                [CoverMap.CONTRACT_NET_OF, CoverMap.CONTRACT_CEDED_OF, CoverMap.LEGAL_ENTITY,
                        CoverMap.SEGMENTS, CoverMap.GENERATORS, CoverMap.LOSS_KIND_OF],
                ConstraintsFactory.getConstraints(CoverMap.IDENTIFIER))
        result.flexibleCover.comboBoxValues[0] = ['': null, 'contract1': contract1, 'contract2': contract2]
        result.flexibleCover.comboBoxValues[1] = ['': null, 'contract1': contract1, 'contract2': contract2]
        result.flexibleCover.comboBoxValues[2] = ['': null]
        result.flexibleCover.comboBoxValues[3] = ['': null]
        result.flexibleCover.comboBoxValues[4] = ['': null]
        def claimTypeColumnValues = [:]
        ClaimTypeSelector.values().each {
            claimTypeColumnValues.put(it.name(), it)
        }
        result.flexibleCover.comboBoxValues[5] = claimTypeColumnValues
        return CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX, result)
    }

    void testNetClaims_make_money_example() {
        ClaimCashflowPacket grossClaim = getClaimCashflowPacket(baseClaim, -100)
        ClaimCashflowPacket netClaim1 = getClaimCashflowPacket(baseClaim, -20, contract1)
        ClaimCashflowPacket netClaim2 = getClaimCashflowPacket(baseClaim, -20, contract2)
        ClaimCashflowPacket cededClaim1 = getClaimCashflowPacket(baseClaim, 80, contract1)
        ClaimCashflowPacket cededClaim2 = getClaimCashflowPacket(baseClaim, 80, contract2)
        merger.coverAttributeStrategy = setupStrategy([['contract1','contract2'], ['',''], ['',''], ['',''], ['',''], ['ANY','ANY']])
        merger.inClaimsNet.add(netClaim1)
        merger.inClaimsNet.add(netClaim2)
        merger.inClaimsGross.add(grossClaim)
        merger.inClaimsCededForNet.add(cededClaim1)
        merger.inClaimsCededForNet.add(cededClaim2)
        new TestPretendInChannelWired(merger, 'inClaimsGross')
        new TestPretendInChannelWired(merger, 'inClaimsNet')
        new TestPretendInChannelWired(merger, 'inClaimsCeded')
        merger.doCalculation()
        assert 1 == merger.outClaims.size()
        checkUltimate(merger.outClaims[0], 60)
    }

    void testNetClaims_no_packets() {
        new TestPretendInChannelWired(merger, 'inClaimsGross')
        new TestPretendInChannelWired(merger, 'inClaimsNet')
        new TestPretendInChannelWired(merger, 'inClaimsCeded')
        merger.doCalculation()
        assert 0 == merger.outClaims.size()
    }

    void testCededClaims() {
        ClaimCashflowPacket cededClaim = getClaimCashflowPacket(baseClaim, 20)
        cededClaim.setMarker(contract1)
        merger.coverAttributeStrategy = setupStrategy([[''], ['contract1'], [''], [''], [''], ['ANY']])
        merger.inClaimsCeded.add(cededClaim)
        new TestPretendInChannelWired(merger, 'inClaimsCeded')
        merger.doCalculation()
        assert 1 == merger.outClaims.size()
        checkUltimate(merger.outClaims[0], -20)
    }

    void testMissingGrossClaim() {

    }

    private void checkUltimate(ClaimCashflowPacket claimCashflowPacket, double ultimate) {
        assert ultimate == claimCashflowPacket.ultimate()
    }

    private ClaimCashflowPacket getClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, IComponentMarker marker = marker) {
        def counter = new LimitedContinuousPeriodCounter(new DateTime(), new Period(1, 0, 0, 0), 10)
        def exposureInfo = new ExposureInfo(new DateTime(), counter)
        ClaimCashflowPacket claim = new ClaimCashflowPacket(baseClaim, ultimate, ultimate, 0, 0,
                ultimate, 0, 0, exposureInfo, new DateTime(), counter)
        claim.marker = marker
        return claim
    }

}
