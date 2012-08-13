package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionTests
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.util.TestProbe
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SurplusContractTests extends GroovyTestCase {

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class);
    PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);
    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    static ReinsuranceContract getContract0() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                        ReinsuranceContractType.SURPLUS,
                        ["retention": 100,
                         "lines": 5,
                         "defaultCededLossShare": 0d,
                         "coveredByReinsurer": 1d]),
                i: CommissionTests.getTestSimulationScope())
    }

    static ReinsuranceContract getContract1() {
        return new ReinsuranceContract(
                parmContractStrategy: ReinsuranceContractType.getStrategy(
                    ReinsuranceContractType.SURPLUS,
                    ["retention": 100,
                     "lines": 5,
                     "coveredByReinsurer": 1d,
                     "defaultCededLossShare": 0.5]),
                simulationScope: CommissionTests.getTestSimulationScope())
    }

    static ReinsuranceContract getContract(double retention, double lines, double defaultCededLossShare, DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.SURPLUS, [
                        'retention': retention,
                        'lines': lines,
                        'defaultCededLossShare': defaultCededLossShare,
                        'commission': CommissionStrategyType.getNoCommission()]),
                parmCover : CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, [filter: FilterStrategyType.getDefault()]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }

    private List<UnderwritingInfoPacket> getUnderwritingInfos() {
        List<UnderwritingInfoPacket> uwInfos = []
        // first band: ceded = 0
        uwInfos << new UnderwritingInfoPacket(
                numberOfPolicies: 1000,
                premiumWritten: 5050,
                exposure: new ExposureInfo(date20110101, 0, 80, 100, ExposureBase.ABSOLUTE))
        // second band: ceded = (200-100)/200 = 0.5
        uwInfos << new UnderwritingInfoPacket(
                numberOfPolicies: 100,
                premiumWritten: 2050,
                exposure: new ExposureInfo(date20110101, 0, 200, 1000, ExposureBase.ABSOLUTE))
        // third band: ceded = (500-100)/500 = 0.8
        uwInfos << new UnderwritingInfoPacket(
                numberOfPolicies: 50,
                premiumWritten: 4050,
                exposure: new ExposureInfo(date20110101, 0, 500, 800, ExposureBase.ABSOLUTE))
        // 4th band: ceded = 500/1500 = 0.333333333333333
        uwInfos << new UnderwritingInfoPacket(
                numberOfPolicies: 20,
                premiumWritten: 8050,
                exposure: new ExposureInfo(date20110101, 0, 1500, 1800, ExposureBase.ABSOLUTE))
        uwInfos << new UnderwritingInfoPacket(
                numberOfPolicies: 1,
                premiumWritten: 0,
                exposure: new ExposureInfo(date20110101, 0, 0, 0, ExposureBase.ABSOLUTE))
        uwInfos
    }

    List<ClaimCashflowPacket> getClaim(double ultimate, ClaimType claimType, IPeriodCounter periodCounter, UnderwritingInfoPacket uwInfo) {
        getClaim(ultimate, claimType, periodCounter, trivialPayoutPattern, trivialReportingPattern, uwInfo)
    }

    List<ClaimCashflowPacket> getClaim(double ultimate, ClaimType claimType, IPeriodCounter periodCounter,
                                       PatternPacket payoutPattern, PatternPacket reportingPattern, UnderwritingInfoPacket uwInfo) {
        ClaimRoot claimRoot = new ClaimRoot(ultimate, claimType, date20110101, date20110101)
        claimRoot = claimRoot.withExposure(uwInfo?.exposure)
        GrossClaimRoot grossClaimRoot = new GrossClaimRoot(claimRoot, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims = grossClaimRoot.getClaimCashflowPackets(periodCounter)
        claims
    }

    void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }

    void testCalculateCededClaimsOnly() {
        List<UnderwritingInfoPacket> exposures = getUnderwritingInfos()
        ReinsuranceContract contract = getContract(100d, 5d, 0d, date20110101)
        IPeriodCounter periodCounter = contract.iterationScope.periodScope.periodCounter

        contract.inClaims.addAll(getClaim(-100d, ClaimType.ATTRITIONAL, periodCounter, exposures[0]))
        contract.inClaims.addAll(getClaim(-100d, ClaimType.ATTRITIONAL, periodCounter, exposures[1]))
        contract.inClaims.addAll(getClaim(-100d, ClaimType.ATTRITIONAL, periodCounter, exposures[2]))

        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, exposures[0]))
        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, exposures[1]))
        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, exposures[2]))
        contract.inClaims.addAll(getClaim(-300d, ClaimType.ATTRITIONAL, periodCounter, exposures[3]))

        assertTrue contract.outClaimsGross.isEmpty()

        def probeNet = new TestProbe(contract, 'outClaimsNet')    // needed in order to trigger the calculation of net claims
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 7, contract.outClaimsNet.size()
        assertEquals "net claims", [-100, -50, -20, -60, -30, -12, -200], contract.outClaimsNet*.ultimate()
        assertEquals "outClaims.size", 7, contract.outClaimsCeded.size()
        assertEquals "ceded claims", [0, 50, 80, 0, 30, 48, 100], contract.outClaimsCeded*.ultimate()

        contract.reset()
        assertTrue contract.outClaimsCeded.isEmpty()
    }

    void testCalculateCededClaimsOnly2() {
        List<UnderwritingInfoPacket> exposures = getUnderwritingInfos()
        ReinsuranceContract contract = getContract(100d, 5d, 0.5d, date20110101)
        IPeriodCounter periodCounter = contract.iterationScope.periodScope.periodCounter

        contract.inClaims.addAll(getClaim(-100d, ClaimType.ATTRITIONAL, periodCounter, null))
        contract.inClaims.addAll(getClaim(-150d, ClaimType.ATTRITIONAL, periodCounter, null))
        contract.inClaims.addAll(getClaim(-200d, ClaimType.ATTRITIONAL, periodCounter, null))
        contract.inClaims.addAll(getClaim(-300d, ClaimType.ATTRITIONAL, periodCounter, null))

        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, exposures[0]))
        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, exposures[1]))
        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, exposures[2]))
        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, exposures[4]))

        assertTrue contract.outClaimsCeded.isEmpty()
        def probeNet = new TestProbe(contract, "outClaimsNet")    // needed in order to trigger the calculation of net claims
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 8, contract.outClaimsNet.size()
        assertEquals "net claims", [-50d, -75d, -100d, -150d, -60d, -30d, -12d, -60d], contract.outClaimsNet*.ultimate()

        assertEquals "outClaims.size", 8, contract.outClaimsCeded.size()
        assertEquals "ceded claims", [50d, 75d, 100d, 150d, 0d, 30d, 48d, 0d], contract.outClaimsCeded*.ultimate()

        contract.reset()
        assertTrue contract.outClaimsCeded.isEmpty()
    }

    void testDevelopedClaims() {
        List<UnderwritingInfoPacket> exposures = getUnderwritingInfos()
        ReinsuranceContract contract = getContract(100d, 5d, 0d, date20110101)
        IPeriodCounter periodCounter = contract.iterationScope.periodScope.periodCounter

        contract.inClaims.addAll(getClaim(-100d, ClaimType.ATTRITIONAL, periodCounter, annualPayoutPattern, annualReportingPattern, exposures[0]))
        contract.inClaims.addAll(getClaim(-100d, ClaimType.ATTRITIONAL, periodCounter, annualPayoutPattern, annualReportingPattern, exposures[1]))
        contract.inClaims.addAll(getClaim(-100d, ClaimType.ATTRITIONAL, periodCounter, annualPayoutPattern, annualReportingPattern, exposures[2]))

        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, annualPayoutPattern, annualReportingPattern, exposures[0]))
        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, annualPayoutPattern, annualReportingPattern, exposures[1]))
        contract.inClaims.addAll(getClaim(-60d, ClaimType.SINGLE, periodCounter, annualPayoutPattern, annualReportingPattern, exposures[2]))
        contract.inClaims.addAll(getClaim(-300d, ClaimType.ATTRITIONAL, periodCounter, annualPayoutPattern, annualReportingPattern, exposures[3]))

        assertTrue contract.outClaimsGross.isEmpty()

        def probeNet = new TestProbe(contract, 'outClaimsNet')    // needed in order to trigger the calculation of net claims
        contract.doCalculation()

        assertEquals "outClaimsNet.size", 7, contract.outClaimsNet.size()
        assertEquals "net claims ultimate", [-100, -50, -20, -60, -30, -12, -200], contract.outClaimsNet*.ultimate()
        assertEquals "net claims reported", [-30, -15, -6, -18, -9, -3.5999999999999996, -60], contract.outClaimsNet*.reportedIncrementalIndexed
        assertEquals "net claims paid", [0] *7, contract.outClaimsNet*.paidIncrementalIndexed
        assertEquals "outClaims.size", 7, contract.outClaimsCeded.size()
        assertEquals "ceded claims ultimate", [0, 50, 80, 0, 30, 48, 100], contract.outClaimsCeded*.ultimate()
        assertEquals "ceded claims reported", [0, 15d, 24d, 0d, 9d, 14.4, 30d], contract.outClaimsCeded*.reportedIncrementalIndexed
        assertEquals "ceded claims paid", [0, 0, 0, 0, 0, 0, 0], contract.outClaimsCeded*.paidIncrementalIndexed

        contract.reset()
        assertTrue contract.outClaimsCeded.isEmpty()
    }

    void testGetCededUnderwritingInfo() {
        ReinsuranceContract contract = getContract(100, 5, 0.5, date20110101)
        contract.inUnderwritingInfo.addAll(getUnderwritingInfos())
        contract.doCalculation()

        assertEquals "coverUnderwritingInfo.size", contract.inUnderwritingInfo.size(), contract.outUnderwritingInfoCeded.size()
        assertEquals "premium written 0", -0d * contract.inUnderwritingInfo[0].premiumWritten, contract.outUnderwritingInfoCeded[0].premiumWritten, 1E-14
        assertEquals "premium written 1", -0.5 * contract.inUnderwritingInfo[1].premiumWritten, contract.outUnderwritingInfoCeded[1].premiumWritten
        assertEquals "premium written 2", -0.8 * contract.inUnderwritingInfo[2].premiumWritten, contract.outUnderwritingInfoCeded[2].premiumWritten
    }
}
