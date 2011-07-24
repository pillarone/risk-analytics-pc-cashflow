package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class StabilizationStrategyType extends AbstractParameterObjectClassifier {

    public static final StabilizationStrategyType NONE = new StabilizationStrategyType("none", "NONE", [:])
    public static final StabilizationStrategyType FULL = new StabilizationStrategyType("full", "FULL",
            ['inflationIndices': new ConstrainedMultiDimensionalParameter(
                Collections.emptyList(), SeverityIndexSelectionTableConstraints.COLUMN_TITLES,
                ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER)),
             'stabilizationBasedOn': StabilizationBasedOn.PAID])
    public static final StabilizationStrategyType INTEGRAL = new StabilizationStrategyType("integral", "INTEGRAL",
            ['inflationIndices': new ConstrainedMultiDimensionalParameter(
                Collections.emptyList(), SeverityIndexSelectionTableConstraints.COLUMN_TITLES,
                ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER)),
             'stabilizationBasedOn': StabilizationBasedOn.PAID,
             'franchise': 0d])
    public static final StabilizationStrategyType SIC = new StabilizationStrategyType("sic", "SIC",
            ['inflationIndices': new ConstrainedMultiDimensionalParameter(
                Collections.emptyList(), SeverityIndexSelectionTableConstraints.COLUMN_TITLES,
                ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER)),
             'stabilizationBasedOn': StabilizationBasedOn.PAID,
             'franchise': 0d])


    public static final all = [NONE, FULL, INTEGRAL, SIC]


    protected static Map types = [:]
    static {
        StabilizationStrategyType.all.each {
            StabilizationStrategyType.types[it.toString()] = it
        }
    }

    private StabilizationStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static StabilizationStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static IStabilizationStrategy getDefault() {
        return new NoStabilizationStrategy()
    }

    public static IStabilizationStrategy getStrategy(StabilizationStrategyType type, Map parameters) {
        IStabilizationStrategy stabilizationStrategy;
        switch (type) {
            case StabilizationStrategyType.NONE:
                stabilizationStrategy = new NoStabilizationStrategy()
                break
            case StabilizationStrategyType.FULL:
                stabilizationStrategy = new FullStabilizationStrategy(
                        inflationIndices : (ConstrainedMultiDimensionalParameter) parameters['inflationIndices'],
                        stabilizationBasedOn: (StabilizationBasedOn) parameters['stabilizationBasedOn'])
                break
            case StabilizationStrategyType.INTEGRAL:
                stabilizationStrategy = new IntegralStabilizationStrategy(
                        inflationIndices : (ConstrainedMultiDimensionalParameter) parameters['inflationIndices'],
                        stabilizationBasedOn: (StabilizationBasedOn) parameters['stabilizationBasedOn'],
                        franchise : (Double) parameters['franchise'])
                break
            case StabilizationStrategyType.SIC:
                stabilizationStrategy = new SICStabilizationStrategy(
                        inflationIndices : (ConstrainedMultiDimensionalParameter) parameters['inflationIndices'],
                        stabilizationBasedOn: (StabilizationBasedOn) parameters['stabilizationBasedOn'],
                        franchise : (Double) parameters['franchise'])
                break
        }
        return stabilizationStrategy
    }
}
