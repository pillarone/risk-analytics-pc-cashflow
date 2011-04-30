package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.util.MathUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import umontreal.iro.lecuyer.rng.RandomStreamBase
import org.pillarone.riskanalytics.domain.utils.math.randomnumber.UniformDoubleList

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
// todo(jwa): reactivate value comparison (claim have a negative sign in this plugin)
class PMLClaimsGeneratorStrategyTests extends GroovyTestCase {

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    ClaimsGenerator claimsGenerator

    void setUp() {
        claimsGenerator = new ClaimsGenerator()
        claimsGenerator.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20110101, 5)
        claimsGenerator.periodStore = new PeriodStore(claimsGenerator.periodScope)
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
    }


    void testPMLDataNoModification() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.NONE, [:])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(1531)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) DistributionType.getStrategy(DistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 10);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.1d / periods[i]
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(-cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]));
        }

        claimsGenerator.doCalculation();

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
        assertEquals "claims", claimValues, claimsGenerator.outClaims*.ultimate()

    }

    void testPMLDataShift() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.SHIFT, ["shift": 1000])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))

        MathUtils.initRandomStreamBase(1731)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) DistributionType.getStrategy(DistributionType.POISSON,
                new HashMap<String, Double>() {
                    {put("lambda", 10);}
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.1d, 0.2d, 0.3d, 0.4d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.1d / periods[i]
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]) + 1000);
        }

        claimsGenerator.doCalculation();

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
//        assertEquals "claims", claimValues, claimsGenerator.outClaims*.ultimate()

    }

    void testPMLDataTruncation() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.TRUNCATED, ["min": 200d, "max": 8000d])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(1031)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) DistributionType.getStrategy(DistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 1 / 0.1d);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }
        List<Double> truncatedCumProb = []
        for (int i = 0; i < 7; i++) {
            truncatedCumProb[i] = (cumProb[i + 2] - cumProb[1]) / (1 - cumProb[1])
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], truncatedCumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]));
        }

        claimsGenerator.doCalculation();

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
//        assertEquals "claims", claimValues, claimsGenerator.outClaims*.ultimate()
    }

    void testPMLDataTruncationShift() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.TRUNCATEDSHIFT, ["min": 200d, "max": 800d, "shift": 1000])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(5031)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) DistributionType.getStrategy(DistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 1 / 0.1d - 1 / 1.5);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }
        List<Double> truncatedCumProb = []
        for (int i = 0; i < 4; i++) {
            truncatedCumProb[i] = (cumProb[i + 2] - cumProb[1]) / (cumProb[5] - cumProb[1])
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[200d, 500d, 600d, 800d], truncatedCumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]) + 1000);
        }

        claimsGenerator.doCalculation();

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
//        assertEquals "claims", claimValues, claimsGenerator.outClaims*.ultimate()

    }

    void testPMLDataCensored() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.CENSORED, ["min": 0, "max": -800d])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(1037)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) DistributionType.getStrategy(DistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 1 / 0.01d);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }

        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(Math.min(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]), 800d));
        }

        claimsGenerator.doCalculation();

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
//        assertEquals "claims", claimValues, claimsGenerator.outClaims*.ultimate()

    }

    void testPMLDataCensoredShift() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.CENSOREDSHIFT, ["min": 0, "max": -800d, "shift": -2000])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(10037)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) DistributionType.getStrategy(DistributionType.POISSON,
                new HashMap<String, Double>() {
                    {
                        put("lambda", 1 / 0.01d);
                    }
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }

        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], cumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(Math.min(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]), 800d) + 2000);
        }

        claimsGenerator.doCalculation();

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
//        assertEquals "claims", claimValues, claimsGenerator.outClaims*.ultimate()
    }

    void testPMLDataLeftTruncationRightCensored() {

        ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
                [[0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d], [0.1d, 45d, 200d, 500d, 600d, 800d, 1500d, 2016d, 4000d]], ["return period", "maximum claim"], ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
        DistributionModified claimsSizeModification = DistributionModifier.getStrategy(DistributionModifier.LEFTTRUNCATEDRIGHTCENSOREDSHIFT, ["min": 200d, "max": 800d, "shift": 10d])
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.PML,
                ["pmlData": pmlData, "claimsSizeModification": claimsSizeModification,'produceClaim': FrequencySeverityClaimType.SINGLE]))


        MathUtils.initRandomStreamBase(81031)
        RandomStreamBase referenceStream = MathUtils.getRandomStream(MathUtils.getRandomStreamBase(), 0).clone()
        List<Double> randomNumbers = UniformDoubleList.getDoubles((int) 300, false, referenceStream);
        RandomDistribution poissonDist = (RandomDistribution) DistributionType.getStrategy(DistributionType.POISSON,
                new HashMap<String, Double>() {
                    {put("lambda", 1 / 0.1d);}
                });
        double frequency = poissonDist.getDistribution().inverseF(randomNumbers[0]);
        List<Double> cumProb = []; cumProb[0] = 0
        double[] periods = [0.01d, 0.05d, 0.1d, 0.2d, 0.7d, 1.5d, 2.2d, 4.0d, 5.0d]
        for (int i = 1; i < 9; i++) {
            cumProb[i] = 1d - 0.01d / periods[i]
        }
        List<Double> truncatedCumProb = []
        for (int i = 0; i < 7; i++) {
            truncatedCumProb[i] = (cumProb[i + 2] - cumProb[1]) / (1 - cumProb[1])
        }
        TableMultiDimensionalParameter cumValues = new TableMultiDimensionalParameter([[200d, 500d, 600d, 800d, 1500d, 2016d, 4000d], truncatedCumProb], ['observations', 'cumulative probabilities'])
        Map distributionData = new HashMap();
        distributionData.put("discreteEmpiricalCumulativeValues", cumValues);
        RandomDistribution cumEmpiricalDistribution = DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, distributionData);
        List<Double> claimValues = new ArrayList<Double>();
        for (int i = 0; i < frequency; i++) {
            claimValues.add(Math.min(cumEmpiricalDistribution.getDistribution().inverseF(randomNumbers[i + 1]), 800d)+10);
        }

        claimsGenerator.doCalculation();

        assertEquals "number of claims", frequency, claimsGenerator.outClaims.size()
//        assertEquals "claims", claimValues, claimsGenerator.outClaims*.ultimate()

    }

}