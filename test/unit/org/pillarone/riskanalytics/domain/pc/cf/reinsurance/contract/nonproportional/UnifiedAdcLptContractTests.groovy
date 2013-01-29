package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional

import grails.test.GrailsUnitTestCase
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacketTests

class UnifiedAdcLptContractTests extends GrailsUnitTestCase {

    // PMO-2235 Values taken from Spreadsheet
    void testInitBasedOnAggregateCalculations() {
        UnifiedAdcLptContract contract = new UnifiedAdcLptContract(0.6d, 2100, 300)
        List<ClaimCashflowPacket> grossClaims = new ArrayList<ClaimCashflowPacket>()
        // 2018
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(-500, -350, -500, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(-500, -325, -500, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(-500, -200, -400, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(-500, -175, -350, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(-500, -50, -100, null, true))
        contract.initBasedOnAggregateCalculations(grossClaims, null)
        ClaimCashflowPacket cededClaim = contract.calculateClaimCeded(grossClaims[0], null, null)
        assert 300d == cededClaim.developedUltimate()
        assert 0d == cededClaim.reportedCumulatedIndexed
        assert 0d == cededClaim.paidCumulatedIndexed
        grossClaims = []
        // 2019
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -125, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -25, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -125, -100, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -25, -50, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -125, -250, null, true))
        contract.initBasedOnAggregateCalculations(grossClaims, null)
        cededClaim = contract.calculateClaimCeded(grossClaims[0], null, null)
        assert 300d == cededClaim.developedUltimate()
        assert 150d == cededClaim.reportedCumulatedIndexed
        assert 0d == cededClaim.paidCumulatedIndexed
        grossClaims = []
        // 2020
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -25, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -125, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -25, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -125, -100, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -25, -50, null, true))
        contract.initBasedOnAggregateCalculations(grossClaims, null)
        cededClaim = contract.calculateClaimCeded(grossClaims[0], null, null)
        assert 300d == cededClaim.developedUltimate()
        assert 300d == cededClaim.reportedCumulatedIndexed
        assert 0d == cededClaim.paidCumulatedIndexed
        grossClaims = []
        // 2021
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 0, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -25, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -125, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -25, 0, null, true))
        grossClaims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, -125, 0, null, true))
        contract.initBasedOnAggregateCalculations(grossClaims, null)
        cededClaim = contract.calculateClaimCeded(grossClaims[0], null, null)
        assert 300d == cededClaim.developedUltimate()
        assert 300d == cededClaim.reportedCumulatedIndexed
        assert 50d == cededClaim.paidCumulatedIndexed
    }

}
