package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional

import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.VaryingParametersDistributionType
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.IClaimsGeneratorStrategy
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionParams
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.PeriodDistributionsConstraints
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.DefaultContractBase

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalClaimsModelTests extends GroovyTestCase {

    AttritionalClaimsModel model;

    void setUp() {
        ConstraintsFactory.registerConstraint(new PeriodDistributionsConstraints())
        model = new AttritionalClaimsModel()
        model.parmSeverityDistribution = VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.CONSTANT, ["constant": new ConstrainedMultiDimensionalParameter([[1, 3], [1000d, 2000d]],
                                [DistributionParams.PERIOD.toString(), DistributionParams.CONSTANT.toString()],
                                ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER))])
    }

    void testClaimsModel() {
        IClaimsGeneratorStrategy claimsGeneratorPeriod0 = model.claimsModel(0)
        IClaimsGeneratorStrategy claimsGeneratorPeriod1 = model.claimsModel(1)
        assertEquals "same distribution in period 1 and 3", claimsGeneratorPeriod0.claimsSizeDistribution, claimsGeneratorPeriod1.claimsSizeDistribution
        IClaimsGeneratorStrategy claimsGeneratorPeriod2 = model.claimsModel(2)
        IClaimsGeneratorStrategy claimsGeneratorPeriod3 = model.claimsModel(3)
        assertEquals "same distribution in period 2 and 4", claimsGeneratorPeriod2.claimsSizeDistribution, claimsGeneratorPeriod3.claimsSizeDistribution
    }

    void testClaimsModelDefaultBeforeFirstDefined() {
        model = new AttritionalClaimsModel(parmSeverityDistribution : VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.CONSTANT, ["constant": new ConstrainedMultiDimensionalParameter([[3], [2000d]],
                        [DistributionParams.PERIOD.toString(), DistributionParams.CONSTANT.toString()],
                        ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER))]))
        for (int period = 0; period < 2; period++) {
            IClaimsGeneratorStrategy claimsGeneratorPeriod = model.claimsModel(period)
            assertEquals "P$period: default constant = 0", 0d, claimsGeneratorPeriod.claimsSizeDistribution.parameters[DistributionParams.CONSTANT]
        }
        IClaimsGeneratorStrategy claimsGeneratorPeriod = model.claimsModel(2)
        assertEquals "none trivial as of period 3", 2000d, claimsGeneratorPeriod.claimsSizeDistribution.parameters[DistributionParams.CONSTANT]
    }

    void testUsageBaseClaims() {
        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(new DateTime(2012,1,1,0,0,0,0), 4)
        PacketList<UnderwritingInfoPacket> underwritingInfos = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket)
        PacketList<SystematicFrequencyPacket> systematicFrequencies = new PacketList<SystematicFrequencyPacket>(SystematicFrequencyPacket)
        PacketList<EventDependenceStream> events = new PacketList<EventDependenceStream>(EventDependenceStream)
        PacketList<FactorsPacket> factors = new PacketList<FactorsPacket>(FactorsPacket)

        List<ClaimRoot> claimRoot = model.baseClaims(underwritingInfos, systematicFrequencies, events, factors, new DefaultContractBase(), null, periodScope)
        assertEquals "P0 one claim only", 1, claimRoot.size()
        assertEquals "P0 unmodified ultimate", -1000d, claimRoot[0].ultimate

        periodScope.prepareNextPeriod()
        claimRoot = model.baseClaims(underwritingInfos, systematicFrequencies, events, factors, new DefaultContractBase(), null, periodScope)
        assertEquals "P1 one claim only", 1, claimRoot.size()
        assertEquals "P1 unmodified ultimate", -1000d, claimRoot[0].ultimate

        periodScope.prepareNextPeriod()
        claimRoot = model.baseClaims(underwritingInfos, systematicFrequencies, events, factors, new DefaultContractBase(), null, periodScope)
        assertEquals "P2 one claim only", 1, claimRoot.size()
        assertEquals "P2 unmodified ultimate", -2000d, claimRoot[0].ultimate
    }
}
