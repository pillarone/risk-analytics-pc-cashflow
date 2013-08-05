package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import com.google.common.collect.Lists
import groovy.transform.CompileStatic;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IPremiumContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IRIPremiumSplitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ProRataTermXLContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.IStabilizationStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.LayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.PeriodLayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ScaledPeriodLayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.APBasis;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.AdditionalPremiumConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.LayerConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureAPConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureProfitCommissionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureReinstatementConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.ContractOrderingMethod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.PremiumContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.TemplateContractType;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
public class PremiumContractStrategy extends AbstractParameterObject implements IPremiumContractStrategy {

    private ConstrainedMultiDimensionalParameter structure = new ConstrainedMultiDimensionalParameter(
            [[1],[1],[1d],[0d],[0d],[0d],[0d],[0d]],
            PremiumStructureConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureConstraints.IDENTIFIER));

    private ConstrainedMultiDimensionalParameter reinstatements = new ConstrainedMultiDimensionalParameter(
            [[1],[1],[0d],[0d]],
            PremiumStructureReinstatementConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureReinstatementConstraints.IDENTIFIER));

    private ConstrainedMultiDimensionalParameter additionalPremiums = new ConstrainedMultiDimensionalParameter(
            [[1],[1],[0d],[0d],[0d]],
            PremiumStructureAPConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureAPConstraints.IDENTIFIER));

    private ConstrainedMultiDimensionalParameter profitCommission = new ConstrainedMultiDimensionalParameter(
            [[1],[1],[0d],[0d],],
            PremiumStructureProfitCommissionConstraints.columnHeaders, ConstraintsFactory.getConstraints(PremiumStructureProfitCommissionConstraints.IDENTIFIER));
    private double termLimit = 0d;
    private double termExcess = 0d;

    private ContractOrderingMethod orderingMethod = ContractOrderingMethod.PAID

    public IParameterObjectClassifier getType() {
        return PremiumContractType.XOL;
    }

    public Map getParameters() {
        Map<String, Object> params = [:];
        params.put(STRUCTURE, structure);
        params.put(REINSTATEMENTS, reinstatements);
        params.put(ADDITIONALPREMIUMS, additionalPremiums);
        params.put(PROFITCOMMISSION, profitCommission);
        params.put(TERM_LIMIT, termLimit);
        params.put(TERM_EXCESS, termExcess);
        params.put(ORDERING_METHOD, orderingMethod);
        return params;
    }

    PremiumContractStrategy(final ConstrainedMultiDimensionalParameter structure, final ConstrainedMultiDimensionalParameter reinstatements, final ConstrainedMultiDimensionalParameter additionalPremiums, final ConstrainedMultiDimensionalParameter profitCommission, final double termLimit, final double termExcess, final ContractOrderingMethod orderingMethod) {
        this.structure = structure
        this.reinstatements = reinstatements
        this.additionalPremiums = additionalPremiums
        this.profitCommission = profitCommission
        this.termLimit = termLimit
        this.termExcess = termExcess
        this.orderingMethod = orderingMethod
    }
/**
     *
     *
     * @param period contracts of this period should be returned, normally this is the current period
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @param base defines which property of the underwritingInfoPackets should be used for scaling. Depending on the
     *             contracts are parametrized, this parameter is ignored and instead a local strategy parameter is used
     * @param termDeductible deductible shared among several contracts
     * @param termLimit limit shared among several contracts
     * @param claims
     * @return list containing ProRataTermXLContract contracts
     */
    public List<IReinsuranceContract> getContracts(int period,
                                                   List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                                   IPeriodDependingThresholdStore termDeductible, IPeriodDependingThresholdStore termLimit, List<ClaimCashflowPacket> claims) {
        throw new NotImplementedException();
    }

    public static final String TERM_LIMIT = NonPropTemplateContractStrategy.TERM_LIMIT;
    public static final String TERM_EXCESS = NonPropTemplateContractStrategy.TERM_EXCESS;
    public static final String STRUCTURE = NonPropTemplateContractStrategy.STRUCTURE;
    public static final String REINSTATEMENTS = "reinstatements";
    public static final String ADDITIONALPREMIUMS = "additionalPremiums";
    public static final String PROFITCOMMISSION = "profitCommission";
    public static final String ORDERING_METHOD = "orderingMethod";
}
