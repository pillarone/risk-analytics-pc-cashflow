package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import grails.test.GrailsUnitTestCase
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract

class MatrixCoverAttributeStrategyTests extends GrailsUnitTestCase {

    ConstrainedMultiDimensionalParameter params
    ReinsuranceContract contract1
    ReinsuranceContract contract2

    @Override
    protected void setUp() {
        super.setUp()
        ConstraintsFactory.registerConstraint(new CoverMap())
    }

    void setupParams(List selectedParams) {
        contract1 = new ReinsuranceContract(name: 'contract1')
        contract2 = new ReinsuranceContract(name: 'contract2')
        params = TestMatrixCoverAttributeRow.getParameters(selectedParams, [contract1, contract2], [contract1, contract2])
    }

    void testMultipleParamLines_collectPacketsByDifferentCriterias() {
        setupParams([['contract1', ''], ['', ''], ['', ''], ['', ''], ['', ''], ['AGGREGATED_EVENT', 'ANY']])
        def source = new PacketList<ClaimCashflowPacket>()
        source.add(new ClaimCashflowPacket(marker: contract1))
        source.add(new ClaimCashflowPacket(marker: contract2))
        MatrixCoverAttributeStrategy strategy = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX, ['flexibleCover': params])
        assert 0 == strategy.coveredClaims(source).size()
    }

    void testMultipleParamLines_collectPacketsByDifferentCriterias_Gross() {
        setupParams([[''], [''], [''], [''], [''], ['ANY']])
        def source = new PacketList<ClaimCashflowPacket>()
        source.add(new ClaimCashflowPacket(marker: contract1))
        source.add(new ClaimCashflowPacket())
        MatrixCoverAttributeStrategy strategy = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX, ['flexibleCover': params])
        assert 1 == strategy.coveredClaims(source).size()
    }

}
