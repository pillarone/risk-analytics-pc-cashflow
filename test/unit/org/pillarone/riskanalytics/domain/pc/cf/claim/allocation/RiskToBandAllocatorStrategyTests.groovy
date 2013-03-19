package org.pillarone.riskanalytics.domain.pc.cf.claim.allocation

import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBandTests
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RiskToBandAllocatorStrategyTests extends GroovyTestCase {


    void testUsage() {
        RiskToBandAllocatorStrategy strategy = new RiskToBandAllocatorStrategy()
        List<ClaimRoot> claims = []
        ClaimRoot claimFire100 = new ClaimRoot(100d, ClaimType.ATTRITIONAL, null, null)
        ClaimRoot claimHull60 = new ClaimRoot(60d, ClaimType.SINGLE, null, null)
        ClaimRoot claimLegal200 = new ClaimRoot(200d, ClaimType.SINGLE, null, null)
        claims << claimFire100 << claimHull60 << claimLegal200
        List<UnderwritingInfoPacket> underwritingInfos = []
        underwritingInfos.addAll RiskBandTests.getUnderwritingInfos()
        List<ClaimRoot> allocatedClaims = strategy.getAllocatedClaims(claims, underwritingInfos)

        assertEquals '#packets', 5, allocatedClaims.size()
    }

    private static PacketList<UnderwritingInfoPacket> getMockExposureData(int n) {
        // prepare mock list of exposure info
        PacketList<UnderwritingInfoPacket> underwritingInfos = []
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(new DateTime(2010,1,1,0,0,0,0), 5)
        for (int i = 1; i <= n; i++) {
            ExposureInfo exposure = new ExposureInfo(periodScope)
            exposure.sumInsured = 1000d*(2*i-1)
            exposure.maxSumInsured = 1000d*2*i
            underwritingInfos << new UnderwritingInfoPacket(
                    premiumWritten: 100d * i,
                    numberOfPolicies: i,
                    sumInsured: 1000d * (2 * i - 1),
                    maxSumInsured: 1000d * 2 * i, exposure: exposure)
        }
        underwritingInfos
    }

    void testGetRiskMap() {
        List<UnderwritingInfoPacket> underwritingInfos = getMockExposureData(5)

        Map<Double, UnderwritingInfoPacket> expMap = RiskToBandAllocatorStrategy.getRiskMap(underwritingInfos)
        assertEquals(underwritingInfos.size(), expMap.size())
        for (int it = 1; it <= underwritingInfos.size(); it++) {
            assertTrue(expMap.containsKey(1000d * 2 * it))
            assertEquals(underwritingInfos[it - 1], expMap[1000d * 2 * it])
        }

        // test for overlapping bands leading to exceptions
        underwritingInfos << new UnderwritingInfoPacket(premiumWritten: 120d * 2, numberOfPolicies: 10, sumInsured: 1000d * (2 * 2 - 1), maxSumInsured: 1000d * 2 * 2)
        try {
            expMap = RiskToBandAllocatorStrategy.getRiskMap(underwritingInfos)
            fail()
        }
        catch (Exception ex) {
            // fine
        }
    }


    void testAllocateAttritionalClaims() {
        int n = 3;
        PacketList<UnderwritingInfoPacket> underwritingInfos = getMockExposureData(n)
        double totalPremium = (Double) underwritingInfos.premiumWritten.sum()

        double value = 1000d
        List<ClaimRoot> claims = []
        claims << new ClaimRoot(value, ClaimType.ATTRITIONAL, null, null)

        RiskToBandAllocatorStrategy strategy = new RiskToBandAllocatorStrategy()
        List<ClaimRoot> allocatedClaims = strategy.getAllocatedClaims(claims, underwritingInfos)

        assertNotNull(allocatedClaims)
        assertEquals(n, allocatedClaims.size())
        for (int i = 0; i < n; i++) {
            ClaimRoot claim = allocatedClaims[i]
            assertEquals "ultimate $i", value * underwritingInfos[i].premiumWritten / totalPremium, claim.ultimate
            assertNotNull "exposure $i not null", claim.getExposureInfo()
            assertEquals "exposure $i", claim.exposure, underwritingInfos[i].exposure
        }
    }

    // todo(sku): verify allocation of single claims

    void testAllocateLargeIncreasingClaims() {
        int n = 10
        PacketList<UnderwritingInfoPacket> underwritingInfos = getMockExposureData(n)

        // generate the claims: # claims per band increasing with the band id
        int multiplier = 50
        List<ClaimRoot> claims = getClaimsIncr(underwritingInfos, multiplier)
        int numOfClaims = claims.size()

        RiskToBandAllocatorStrategy strategy = new RiskToBandAllocatorStrategy()
        List<ClaimRoot> allocatedClaims = strategy.getAllocatedClaims(claims, underwritingInfos)

        assertEquals '#packets incr', numOfClaims, allocatedClaims.size()

        // generate the claims: # claims per band decreasing with the band id
        claims = getClaimsDecr(underwritingInfos, multiplier)
        numOfClaims = claims.size()

        // test the allocation
        allocatedClaims = strategy.getAllocatedClaims(claims, underwritingInfos)
        assertEquals '#packets decr', numOfClaims, allocatedClaims.size()
    }

    // generate claims: Lower bands have more claims than higher bands

    List<ClaimRoot> getClaimsIncr(List<UnderwritingInfoPacket> underwritingInfos, int multiplier) {
        int n = underwritingInfos.size()
        int numOfClaims = multiplier * n * (n + 1) / 2
        List<ClaimRoot> claims = []
        for (int k = n; k > 0; k--) {
            UnderwritingInfoPacket exposure = underwritingInfos[n - k]
            double value = 0.5d * (exposure.sumInsured + exposure.maxSumInsured)
            for (int j = 0; j < k * multiplier; j++) {
                claims << new ClaimRoot(value, ClaimType.SINGLE, null, null)
            }
        }
        return claims
    }

    // generate claims: Lower bands have less claims than higher bands

    List<ClaimRoot> getClaimsDecr(List<UnderwritingInfoPacket> underwritingInfos, int multiplier) {
        int n = underwritingInfos.size()
        int numOfClaims = multiplier * n * (n + 1) / 2
        List<ClaimRoot> claims = []
        for (int k = n; k > 0; k--) {
            UnderwritingInfoPacket exposure = underwritingInfos[k - 1]
            double value = 0.5d * (exposure.sumInsured + exposure.maxSumInsured)
            for (int j = 0; j < k * multiplier; j++) {
                claims << new ClaimRoot(value, ClaimType.SINGLE, null, null)
            }
        }
        return claims
    }
}
