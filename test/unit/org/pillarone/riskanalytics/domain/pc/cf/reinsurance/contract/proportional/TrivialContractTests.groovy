package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TrivialContractTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)
    DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)

    static ReinsuranceContract getTrivialContract(DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.TRIVIAL, [:]),
                parmCover : CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, [filter: FilterStrategyType.getDefault()]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }


    void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }
    
    void testNoCoverWithDefaultFilter() {
        ReinsuranceContract trivial = getTrivialContract(date20110101)
        IPeriodCounter periodCounter = trivial.iterationScope.periodScope.periodCounter
        trivial.inClaims.addAll(grossClaims(periodCounter, [], 4000d))
        trivial.inUnderwritingInfo.addAll(grossUnderwritingInfo(5000d, null, periodCounter))
        def netClaims = new TestProbe(trivial, 'outClaimsNet')
        def netUwInfo = new TestProbe(trivial, 'outUnderwritingInfoNet')
        trivial.doCalculation()
        
        assertEquals 'number of ceded claims', 0, trivial.outClaimsCeded.size()
        assertEquals 'number of ceded underwriting info', 0, trivial.outUnderwritingInfoCeded.size()

        assertEquals 'number of net claims', 1, trivial.outClaimsNet.size()
        assertEquals 'net ultimate', -4000d, trivial.outClaimsNet[0].ultimate()
        assertEquals 'net claim with correct contract reference', trivial, trivial.outClaimsNet[0].reinsuranceContract()
        assertEquals 'number of net underwriting info', 1, trivial.outUnderwritingInfoNet.size()
        assertEquals 'net premium written', 5000d, trivial.outUnderwritingInfoNet[0].premiumWritten
        assertEquals 'underwriting info with correct contract reference', trivial, trivial.outUnderwritingInfoNet[0].reinsuranceContract
    }

    private List<ClaimCashflowPacket> grossClaims(IPeriodCounter periodCounter, List<IComponentMarker> perils, double ultimate, ClaimType claimType = ClaimType.AGGREGATED) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(-ultimate, claimType,
                date20110418, date20110701, annualPayoutPattern, annualReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter)
        perils.each { peril -> claims*.setMarker(peril) }
        return claims
    }

    private List<UnderwritingInfoPacket> grossUnderwritingInfo(double premium, ISegmentMarker segment, IPeriodCounter periodCounter) {
        [new UnderwritingInfoPacket(segment: segment, premiumWritten: premium, premiumPaid: premium, numberOfPolicies: 1,
                exposure: new ExposureInfo(date20110101, periodCounter))]
    }

}


