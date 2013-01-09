package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import grails.test.GrailsUnitTestCase
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelector
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment

class MatrixCoverAttributeStrategyTests extends GrailsUnitTestCase {

    ConstrainedMultiDimensionalParameter params
    ReinsuranceContract contract1
    ReinsuranceContract contract2
    Segment segment1
    Segment segment2
    ClaimsGenerator peril1
    ClaimsGenerator peril2
    LegalEntity legalEntity1
    LegalEntity legalEntity2

    @Override
    protected void setUp() {
        super.setUp()
        ConstraintsFactory.registerConstraint(new CoverMap())
    }

    void setupParams(List selectedParams) {
        contract1 = new ReinsuranceContract(name: 'reinsurance1')
        contract2 = new ReinsuranceContract(name: 'reinsurance2')
        segment1 = new Segment()
        segment2 = new Segment()
        peril1 = new ClaimsGenerator()
        peril2 = new ClaimsGenerator()
        legalEntity1 = new LegalEntity()
        legalEntity2 = new LegalEntity()
        def se
        params = new ConstrainedMultiDimensionalParameter(selectedParams,
                [CoverMap.CONTRACT_NET_OF, CoverMap.CONTRACT_CEDED_OF, CoverMap.LEGAL_ENTITY,
                        CoverMap.SEGMENTS, CoverMap.GENERATORS, CoverMap.LOSS_KIND_OF],
                ConstraintsFactory.getConstraints(CoverMap.IDENTIFIER))
        params.comboBoxValues[0] = ['': null, 'contract1': contract1, 'contract2': contract2]
        params.comboBoxValues[1] = ['': null, 'contract1': contract1, 'contract2': contract2]
        params.comboBoxValues[2] = ['': null, 'legalEntity1': legalEntity1, 'legalEntity2': legalEntity2]
        params.comboBoxValues[3] = ['': null, 'segment1': segment1, 'segment2': segment2]
        params.comboBoxValues[4] = ['': null, 'peril1':peril1, 'peril2': peril2]
        def claimTypeColumnValues = [:]
        ClaimTypeSelector.values().each {
            claimTypeColumnValues.put(it.name(), it)
        }
        params.comboBoxValues[5] = claimTypeColumnValues

    }

    void testRowFilter_by_net_contract() {
        setupParams([['contract1'], [''], [''], [''], [''], ['ANY']])
        MatrixCoverAttributeStrategy.RowFilter filter = new MatrixCoverAttributeStrategy.RowFilter(1, params)
        def source = new PacketList<ClaimCashflowPacket>()
        source.add(new ClaimCashflowPacket(marker: contract1))
        source.add(new ClaimCashflowPacket(marker: contract2))
        assert 1 == filter.filter(source).size()
    }

    void testRowFilter_by_claim_type() {
        setupParams([[''], [''], [''], [''], [''], ['AGGREGATED_EVENT']])
        MatrixCoverAttributeStrategy.RowFilter filter = new MatrixCoverAttributeStrategy.RowFilter(1, params)
        def source = new PacketList<ClaimCashflowPacket>()
        def expectedPacket1 = new ClaimCashflowPacket(new ClaimRoot(0, ClaimType.AGGREGATED_EVENT, null, null), null)
        source.add(expectedPacket1)
        def expectedPacket2 = new ClaimCashflowPacket(new ClaimRoot(0, ClaimType.AGGREGATED_EVENT, null, null), null)
        source.add(expectedPacket2)
        source.add(new ClaimCashflowPacket())
        source.add(new ClaimCashflowPacket())
        def filteredPackets = filter.filter(source)
        assert 2 == filteredPackets.size()
        assert filteredPackets.contains(expectedPacket1)
        assert filteredPackets.contains(expectedPacket2)
    }

    void testRowFilter_by_ceded_contract() {
        setupParams([[''], ['contract1'], [''], [''], [''], ['ANY']])
        MatrixCoverAttributeStrategy.RowFilter filter = new MatrixCoverAttributeStrategy.RowFilter(1, params)
        def source = new PacketList<ClaimCashflowPacket>()
        source.add(new ClaimCashflowPacket(marker: contract1))
        source.add(new ClaimCashflowPacket(marker: contract2))
        assert 1 == filter.filter(source).size()

    }

    void testRowFilter_by_segment() {
        setupParams([[''], [''], [''], ['segment1'], [''], ['ANY']])
        MatrixCoverAttributeStrategy.RowFilter filter = new MatrixCoverAttributeStrategy.RowFilter(1, params)
        def source = new PacketList<ClaimCashflowPacket>()
        source.add(new ClaimCashflowPacket(marker: segment1))
        source.add(new ClaimCashflowPacket(marker: segment2))
        assert 1 == filter.filter(source).size()
    }

    void testRowFilter_by_peril() {
        setupParams([[''], [''], [''], [''], ['peril1'], ['ANY']])
        MatrixCoverAttributeStrategy.RowFilter filter = new MatrixCoverAttributeStrategy.RowFilter(1, params)
        def source = new PacketList<ClaimCashflowPacket>()
        source.add(new ClaimCashflowPacket(marker: peril1))
        source.add(new ClaimCashflowPacket(marker: peril2))
        assert 1 == filter.filter(source).size()
    }

    void testRowFilter_by_legalEntity() {
        setupParams([[''], [''], ['legalEntity1'], [''], [''], ['ANY']])
        MatrixCoverAttributeStrategy.RowFilter filter = new MatrixCoverAttributeStrategy.RowFilter(1, params)
        def source = new PacketList<ClaimCashflowPacket>()
        source.add(new ClaimCashflowPacket(marker: legalEntity1))
        source.add(new ClaimCashflowPacket(marker: legalEntity2))
        assert 1 == filter.filter(source).size()
    }

    void testMultipleParamLines_collectPacketsByDifferentCriterias() {
        setupParams([['contract1',''], ['',''], ['',''], ['',''], ['',''], ['AGGREGATED_EVENT','ANY']])
        def source = new PacketList<ClaimCashflowPacket>()
        source.add(new ClaimCashflowPacket(marker: contract1))
        source.add(new ClaimCashflowPacket(marker: contract2))
        MatrixCoverAttributeStrategy strategy = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.MATRIX,['flexibleCover':params])
        assert 2 == strategy.coveredClaims(source).size()
    }

}
