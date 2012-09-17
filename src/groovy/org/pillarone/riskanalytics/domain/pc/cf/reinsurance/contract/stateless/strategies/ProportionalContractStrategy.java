package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.ILimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.AALAADQuotaShareContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.QuotaShareContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.CommissionPeriodParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.QuoteCapConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.ICommissionParameterStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.TemplateContractType;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;

import java.util.*;

/**
 * CAP parameter is used as AAL and scaled with incoming underwriting information if p14n is relative.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ProportionalContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    private ConstrainedMultiDimensionalParameter proportionalStructure = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[1],[0],[0]]"),
            QuoteCapConstraints.columnHeaders, ConstraintsFactory.getConstraints(QuoteCapConstraints.IDENTIFIER));
//    private ICommissionParameterStrategy cedingCommissionType = CommissionType.getDefault();

    private TreeMap<Integer, CommissionPeriodParameters> periodParameters;

    public TemplateContractType getType() {
        return TemplateContractType.PROPORTIONAL;
    }

    public Map getParameters() {
        Map params = new HashMap(2);
        params.put(PROPORTIONAL_STRUCTURE, proportionalStructure);
//        params.put(CEDING_COMMISSION_TYPE, cedingCommissionType);
        return params;
    }

    /**
     * This implementation ignores all provided parameters.
     * @param period ignored
     * @param underwritingInfoPackets ignored
     * @param base ignored
     * @param termDeductible ignored
     * @param termLimit ignored
     * @return one contract
     */
    public List<IReinsuranceContract> getContracts(int period,
                                                   List<UnderwritingInfoPacket> underwritingInfoPackets, ExposureBase base,
                                                   IPeriodDependingThresholdStore termDeductible, IPeriodDependingThresholdStore termLimit) {
        initCommissionPeriodParameters();
        IReinsuranceContract contract;
        CommissionPeriodParameters parameters = periodParameters.floorEntry(period).getValue();
        double quotaShare = parameters.getQuote();
        double aal = parameters.getCap() * UnderwritingInfoUtils.scalingFactor(underwritingInfoPackets, base);
        if (aal == 0) {
            contract = new QuotaShareContract(quotaShare, parameters.getCommission());
        }
        else {
            Map<String, Double> aalMap = new HashMap<String, Double>(1);
            aalMap.put("aal", aal);
            ILimitStrategy limit = LimitStrategyType.getStrategy(LimitStrategyType.AAL, aalMap);
            contract = new AALAADQuotaShareContract(quotaShare, parameters.getCommission(), limit);
        }
        return new ArrayList<IReinsuranceContract>(Arrays.asList(contract));
    }

    public double getTermDeductible() {
        return 0;
    }

    public double getTermLimit() {
        return 0;
    }

    private void initCommissionPeriodParameters() {
        if (periodParameters == null) {
            int periods = proportionalStructure.getRowCount();
            periodParameters = new TreeMap<Integer, CommissionPeriodParameters>();
            int contractPeriodColumnIndex = proportionalStructure.getColumnIndex(QuoteCapConstraints.CONTRACT_PERIOD);
            int quotaShareColumnIndex = proportionalStructure.getColumnIndex(QuoteCapConstraints.QUOTA_SHARE);
            int capColumnIndex = proportionalStructure.getColumnIndex(QuoteCapConstraints.CAP);
            for (int row = proportionalStructure.getTitleRowCount(); row < periods; row++) {
                int period = InputFormatConverter.getInt(proportionalStructure.getValueAt(row, contractPeriodColumnIndex)) - 1;
                double quotaShare = InputFormatConverter.getDouble(proportionalStructure.getValueAt(row, quotaShareColumnIndex));
                double cap = InputFormatConverter.getDouble(proportionalStructure.getValueAt(row, capColumnIndex));
//                periodParameters.put(period, new CommissionPeriodParameters(quotaShare, cap, cedingCommissionType.getCommission(period, periods)));
            }
        }
    }

    public static final String PROPORTIONAL_STRUCTURE = "proportionalStructure";
    public static final String CEDING_COMMISSION_TYPE = "cedingCommissionType";
}