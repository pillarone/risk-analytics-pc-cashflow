package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.PremiumStructreAPBasis
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureAPConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureProfitCommissionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureReinstatementConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.ContractOrderingMethod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier

/**
*   author simon.parten @ art-allianz . com
 */
class PremiumContractStrategyTest extends GroovyTestCase{

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        ConstraintsFactory.registerConstraint(new PremiumStructureConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureAPConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureReinstatementConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureProfitCommissionConstraints())
    }

    void testIdenitification(){
        ConstrainedMultiDimensionalParameter structurTable = new ConstrainedMultiDimensionalParameter(
                [[1],[1],[1d],[0d],[0d],[0d],[0d],[0d],[0], [PremiumStructreAPBasis.PREMIUM]],
                PremiumStructureConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureConstraints.IDENTIFIER));

        PremiumContractStrategy contractStrategy = new PremiumContractStrategy(structurTable, defaultReinstatementTable(), defaultAPTable(), defaultPCTable(),0d, 0d, ContractOrderingMethod.INCURRED)

        ContractStructure structure = contractStrategy.getContractStructure()
        assert structure.getContractLayer(1, 1).identifier.equals(new YearLayerIdentifier(1, 1))

    }

    private static ConstrainedMultiDimensionalParameter threePeriodTable() {
        return new ConstrainedMultiDimensionalParameter(
                [
                        [1,2,3,3], /*Years*/
                        [1,1,1,2], /*Layers*/
                        [1d,1d, 0.1d, 0.2d], /*Shares*/
                        [0d,0d,1d, 2d], /*vertical limit*/
                        [0d,0d,10d, 20d], /*vertical Excess*/
                        [0d,0d,100d,200d],/*period limit*/
                        [0d,0d,1000d,2000d], /*period excess*/
                        [0d,0d,10000d,20000d], /* initial premium */
                        [0d,0d,0d,0d], /* ncb */
                        [PremiumStructreAPBasis.PREMIUM,PremiumStructreAPBasis.PREMIUM,PremiumStructreAPBasis.PREMIUM,PremiumStructreAPBasis.PREMIUM] /* ap calc basis */
                ],
                PremiumStructureConstraints.columnHeaders,
                ConstraintsFactory.getConstraints(PremiumStructureConstraints.IDENTIFIER)
        );
    }

    void testReadThreeYearsAndLayers() {
        PremiumContractStrategy contractStrategy = new PremiumContractStrategy(threePeriodTable(), defaultReinstatementTable(), defaultAPTable(), defaultPCTable(),0d, 0d, ContractOrderingMethod.INCURRED)
        ContractStructure structure = contractStrategy.getContractStructure()
        assert structure.layers.size() == 4
        assert structure.getContractLayer(3,1).share == 0.1
        assert structure.getContractLayer(3,2).share == 0.2
        assert structure.getContractLayer(3,1).claimLimit == 1
        assert structure.getContractLayer(3,2).claimLimit == 2
        assert structure.getContractLayer(3,1).claimExcess == 10
        assert structure.getContractLayer(3,2).claimExcess == 20
        assert structure.getContractLayer(3,1).layerPeriodLimit == 100
        assert structure.getContractLayer(3,2).layerPeriodLimit == 200
        assert structure.getContractLayer(3,1).layerPeriodExcess == 1000
        assert structure.getContractLayer(3,2).layerPeriodExcess == 2000
        assert structure.getContractLayer(3,1).initialPremium == 10000
        assert structure.getContractLayer(3,2).initialPremium == 20000
    }



    void testMatchReinstatementLayers() {
        ConstrainedMultiDimensionalParameter reinstatements =  new ConstrainedMultiDimensionalParameter(
                [
                        [1,3, 3,3], /* Years */
                        [1,1,1,2], /* Layers*/
                        [0.1, 0.30, 0.31, 0.32] /* percentages */
                ],
                PremiumStructureReinstatementConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureReinstatementConstraints.IDENTIFIER));
        PremiumContractStrategy contractStrategy = new PremiumContractStrategy(threePeriodTable(), reinstatements, defaultAPTable(), defaultPCTable(),0d, 0d, ContractOrderingMethod.INCURRED)
        ContractStructure contractStructure = contractStrategy.getContractStructure()
        assert  contractStructure.getContractLayer(1,1).reinstatements.size() == 1
        assert  contractStructure.getContractLayer(3,1).reinstatements.size() == 2
        assert  contractStructure.getContractLayer(3,2).reinstatements.size() == 1

        ConstrainedMultiDimensionalParameter badReinstatements =  new ConstrainedMultiDimensionalParameter(
                [
                        [4], /* Years */
                        [1], /* Layers*/
                        [0.1] /* percentages */
                ],
                PremiumStructureReinstatementConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureReinstatementConstraints.IDENTIFIER));

        PremiumContractStrategy contractStrategyFail = new PremiumContractStrategy(threePeriodTable(), badReinstatements, defaultAPTable(), defaultPCTable(),0d, 0d, ContractOrderingMethod.INCURRED)
        assert shouldFail {
            /* Should throw an exception telling us about orphaned reinstatements - there is no fourth period */
            ContractStructure contractStructureFail = contractStrategyFail.getContractStructure()
        }
    }

    void testMatchAPLayers() {
        ConstrainedMultiDimensionalParameter apTable = new ConstrainedMultiDimensionalParameter(
                [
                        [1, 3, 3],
                        [1, 1, 2],
                        [0d, 31, 32],
                        [0d, 310, 320],
                        [0d, 3310, 3320]
                ],
                PremiumStructureAPConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureAPConstraints.IDENTIFIER));

        PremiumContractStrategy contractStrategy = new PremiumContractStrategy(
                threePeriodTable(),
                defaultReinstatementTable(),
                apTable,
                defaultPCTable(),
                0d,
                0d,
                ContractOrderingMethod.INCURRED
        )
        ContractStructure contractStructure = contractStrategy.getContractStructure()

        assert  contractStructure.getContractLayer(1,1).getAdditionalPremiums().size() == 1
        assert  contractStructure.getContractLayer(3,1).getAdditionalPremiums().size() == 1
        assert  contractStructure.getContractLayer(3,2).getAdditionalPremiums().size() == 1

        assert contractStructure.getContractLayer(3,1).additionalPremiums.toList().get(0).limitStart == 31
        assert contractStructure.getContractLayer(3,1).additionalPremiums.toList().get(0).limitTopBand == 310
        assert contractStructure.getContractLayer(3,1).additionalPremiums.toList().get(0).limitTopBand == 310
        assert contractStructure.getContractLayer(3,1).additionalPremiums.toList().get(0).limitAPPercent == 3310

        ConstrainedMultiDimensionalParameter apTableFail = new ConstrainedMultiDimensionalParameter(
                [
                        [4, 3, 3],
                        [1, 1, 2],
                        [0d, 31, 32],
                        [0d, 310, 320],
                        [0d, 3310, 3320]
                ],
                PremiumStructureAPConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureAPConstraints.IDENTIFIER));

        PremiumContractStrategy contractStrategyFail = new PremiumContractStrategy(
                threePeriodTable(),
                defaultReinstatementTable(),
                apTableFail,
                defaultPCTable(),
                0d,
                0d,
                ContractOrderingMethod.INCURRED
        )

        assert shouldFail {
            /* 4th period doesn't exist*/
            ContractStructure contractStructureFail = contractStrategyFail.getContractStructure()
        }

    }

    void testMatchPCLayers() {
        ConstrainedMultiDimensionalParameter pcTable = new ConstrainedMultiDimensionalParameter(
                [
                        [1, 3, 3], /* year */
                        [1, 1, 2], /* layer */
                        [1d, 0.31, 0.32], /*  claims as % of premium */
                        [0d, 0.331, 0.332], /* % of premium as PC */
                ],
                PremiumStructureProfitCommissionConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureProfitCommissionConstraints.IDENTIFIER));

        PremiumContractStrategy contractStrategy = new PremiumContractStrategy(threePeriodTable(), defaultReinstatementTable(), defaultAPTable(), pcTable,0d, 0d, ContractOrderingMethod.INCURRED)
        ContractStructure contractStructure = contractStrategy.getContractStructure()

        assert  contractStructure.getContractLayer(1,1).profitCommissions.size() == 1
        assert  contractStructure.getContractLayer(3,1).profitCommissions.size() == 1
        assert  contractStructure.getContractLayer(3,2).profitCommissions.size() == 1

        assert  contractStructure.getContractLayer(3,2).profitCommissions.toList().get(0).claimsAsPercentageOfPremium == 0.32
        assert  contractStructure.getContractLayer(3,2).profitCommissions.toList().get(0).percentageOfPremiumAsPC == 0.332

        ConstrainedMultiDimensionalParameter pcTableFail = new ConstrainedMultiDimensionalParameter(
                [
                        [4, 3, 3], /* year */
                        [1, 1, 2], /* layer */
                        [1d, 0.31, 0.32], /*  claims as % of premium */
                        [0d, 0.331, 0.332], /* % of premium as PC */
                ],
                PremiumStructureProfitCommissionConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureProfitCommissionConstraints.IDENTIFIER));

        PremiumContractStrategy contractStrategyFail = new PremiumContractStrategy(threePeriodTable(), defaultReinstatementTable(), defaultAPTable(), pcTableFail,0d, 0d, ContractOrderingMethod.INCURRED)
        shouldFail {
            /* Fourth period doesn't exist */
            ContractStructure contractStructureFail = contractStrategyFail.getContractStructure()
        }
    }

        static ConstrainedMultiDimensionalParameter defaultPCTable() {
        return new ConstrainedMultiDimensionalParameter(
            [[1],[1],[0d],[0d],],
            PremiumStructureProfitCommissionConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureProfitCommissionConstraints.IDENTIFIER));
    }

    static ConstrainedMultiDimensionalParameter defaultAPTable() {
        return new ConstrainedMultiDimensionalParameter(
                [[1],[1],[0d],[0d],[0d]],
                PremiumStructureAPConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureAPConstraints.IDENTIFIER));

    }
    static ConstrainedMultiDimensionalParameter defaultReinstatementTable() {

    return new ConstrainedMultiDimensionalParameter(
            [[1],[1],[0d]],
            PremiumStructureReinstatementConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureReinstatementConstraints.IDENTIFIER));
    }



}
