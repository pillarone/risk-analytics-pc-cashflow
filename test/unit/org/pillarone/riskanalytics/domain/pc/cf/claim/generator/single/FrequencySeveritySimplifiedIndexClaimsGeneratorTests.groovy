package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.single

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.ReinsuranceContractBaseType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.ExposureBaseType
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.PeriodDistributionsConstraints
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.math.dependance.DependancePacket
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionParams
import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.VaryingParametersDistributionType
import umontreal.iro.lecuyer.probdist.PoissonDist

public class FrequencySeveritySimplifiedIndexClaimsGeneratorTests extends GroovyTestCase {

    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    FrequencySeverityClaimsGenerator claimsGenerator
    private static final String generatorName = "booyaka shah"

    void setUp() {
//        So we don't get null pointers from the error handling itself.
        SimulationScope simulationScope = new SimulationScope()
        simulationScope.simulation = new Simulation("testSim")
        simulationScope.getSimulation().setRandomSeed(101)
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(date20110101, 3)
        simulationScope.iterationScope = iterationScope

//        Now setup the actual claims generator
        claimsGenerator = TestClaimsGenerator.getFrequencySeveritySimpleIndexClaimsGenerator('aGenerator', iterationScope)
        claimsGenerator.iterationScope = iterationScope
        claimsGenerator.periodScope = iterationScope.periodScope
        claimsGenerator.simulationScope = simulationScope
        claimsGenerator.globalUpdateDate = date20110101
        claimsGenerator.parmParameterizationBasis = ReinsuranceContractBaseType.getStrategy(
                ReinsuranceContractBaseType.LOSSESOCCURRING, new HashMap());
        claimsGenerator.globalLastCoveredPeriod = 3
        claimsGenerator.setName(generatorName)

        ConstraintsFactory.registerConstraint(new DoubleConstraints())

    }

    void testItRuns(){
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(claimsGenerator)
    }

    void testGenerateSimpleClaims(){
        FrequencySeverityClaimsModel frequencySeverityClaimsModel = prepareConstantGenerator()

        claimsGenerator.subClaimsModel = frequencySeverityClaimsModel
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(claimsGenerator, true)
        assert claimsGenerator.outClaims*.ultimate().sum() == 2 * 2000

        claimsGenerator.reset()
        claimsGenerator.periodScope.prepareNextPeriod()

        TestClaimsGenerator.doClaimsCalcWithNoCommutation(claimsGenerator, true)
        assert claimsGenerator.outClaims*.ultimate().sum() == 3 * 3000
    }

    void testGenerateDependantClaims(){
        double meanClaimNumber = 10d
        FrequencySeverityClaimsModel frequencySeverityClaimsModel = prepareConstantGenerator()
        frequencySeverityClaimsModel.parmFrequencyDistribution = VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.POISSON,
                [
                        (DistributionParams.LAMBDA.toString()): new ConstrainedMultiDimensionalParameter(
                                [[1i], [meanClaimNumber]],
                                [DistributionParams.PERIOD.toString(), DistributionParams.LAMBDA.toString()],
                                ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER)
                        )
                ]
        );
        frequencySeverityClaimsModel.parmSeverityDistribution = VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.CONSTANT,
                [
                        (DistributionParams.CONSTANT.toString()): new ConstrainedMultiDimensionalParameter(
                                [[1i], [1000d]],
                                [DistributionParams.PERIOD.toString(), DistributionParams.CONSTANT.toString()],
                                ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER)
                        )
                ]
        );
        DependancePacket dependancePacket = new DependancePacket([claimsGenerator])
        dependancePacket.addMarginal(generatorName, 0, 0.8)
        dependancePacket.addMarginal(generatorName, 1, 0.5)

        claimsGenerator.inProbabilities << dependancePacket
        claimsGenerator.subClaimsModel = frequencySeverityClaimsModel
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(claimsGenerator, true)
        assert claimsGenerator.outClaims*.ultimate().sum() == new PoissonDist(meanClaimNumber).inverseF(0.8) * 1000d

        claimsGenerator.reset()
        claimsGenerator.periodScope.prepareNextPeriod()

        claimsGenerator.inProbabilities << dependancePacket
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(claimsGenerator, true)
        assert claimsGenerator.outClaims*.ultimate().sum() == new PoissonDist(meanClaimNumber).inverseF(0.5) * 1000d
    }

    void testGenerateSimpleClaimsScaleUWInfo(){
        FrequencySeverityClaimsModel frequencySeverityClaimsModel = prepareConstantGenerator()
        UnderwritingInfoPacket infoPacket = new UnderwritingInfoPacket()
        infoPacket.setPremiumWritten(20d)
        final RiskBands bands = new RiskBands(name: "UWInfo")
        infoPacket.origin = bands

        final ComboBoxTableMultiDimensionalParameter parameter = new ComboBoxTableMultiDimensionalParameter(["UWInfo"], ['Underwriting Info'], IUnderwritingInfoMarker.class)
        parameter.comboBoxValues = ["UWInfo" : bands]
        frequencySeverityClaimsModel.parmSeverityBase = ExposureBaseType.getStrategy(ExposureBaseType.PREMIUM,  [ underwritingInfo : parameter] )
        claimsGenerator.subClaimsModel = frequencySeverityClaimsModel
        claimsGenerator.inUnderwritingInfo << infoPacket
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(claimsGenerator, true)

        assert claimsGenerator.outClaims*.ultimate().sum() == 2 * 2000 * 20d

        claimsGenerator.reset()
        claimsGenerator.periodScope.prepareNextPeriod()
        claimsGenerator.inUnderwritingInfo << infoPacket
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(claimsGenerator, true)
        assert claimsGenerator.outClaims*.ultimate().sum() == 3 * 3000 * 20d
    }

    private FrequencySeverityClaimsModel prepareConstantGenerator() {
        FrequencySeverityClaimsModel frequencySeverityClaimsModel = claimsGenerator.getSubClaimsModel()
        frequencySeverityClaimsModel.parmFrequencyDistribution = VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.CONSTANT,
                [
                        (DistributionParams.CONSTANT.toString()): new ConstrainedMultiDimensionalParameter(
                                [[1i, 2i], [2d, 3d]],
                                [DistributionParams.PERIOD.toString(), DistributionParams.CONSTANT.toString()],
                                ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER)
                        )
                ]
        );
        frequencySeverityClaimsModel.parmSeverityDistribution = VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.CONSTANT,
                [
                        (DistributionParams.CONSTANT.toString()): new ConstrainedMultiDimensionalParameter(
                                [[1i, 2i], [2000d, 3000d]],
                                [DistributionParams.PERIOD.toString(), DistributionParams.CONSTANT.toString()],
                                ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER)
                        )
                ]
        );
        return frequencySeverityClaimsModel
    }
}
