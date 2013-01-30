package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValuePerPeriod;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.InterpolatedSlidingCommission;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;

import java.util.*;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class InterpolatedSlidingCommissionStrategy extends AbstractCommissionStrategy {

    public static final String LOSS_RATIO = "Loss Ratio (from)";
    public static final String COMMISSION = "Commission rate";
    public static final int LOSS_RATIO_COLUMN_INDEX = 0;
    public static final int COMMISSION_COLUMN_INDEX = 1;
    private Double[] lossRatios;
    private Double[] commissionoRate;

    private ConstrainedMultiDimensionalParameter commissionBands = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d}),
            Arrays.asList(LOSS_RATIO, COMMISSION),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));

    public IParameterObjectClassifier getType() {
        return CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION;
    }

    public Map getParameters() {
        Map<String, Object> map = super.getParameters();
        map.put("commissionBands", commissionBands);
        return map;
    }

    public ICommission getCalculator(DoubleValuePerPeriod lossCarriedForward) {
        TreeMap<Double, List<Double>> commissionRatesPerLossRatio;
        int numberOfEntries = commissionBands.getValueRowCount();
        commissionRatesPerLossRatio = new TreeMap<Double, List<Double>>();
        for (int i = 1; i <= numberOfEntries; i++) {
            double lossRatio = InputFormatConverter.getDouble(commissionBands.getValueAt(i, LOSS_RATIO_COLUMN_INDEX));
            double commissionRate = InputFormatConverter.getDouble(commissionBands.getValueAt(i, COMMISSION_COLUMN_INDEX));
            List<Double> listOfCommissionRates = new ArrayList<Double>();
            if (commissionRatesPerLossRatio.containsKey(lossRatio))
                listOfCommissionRates = commissionRatesPerLossRatio.get(lossRatio);
            listOfCommissionRates.add(commissionRate);
            Collections.sort(listOfCommissionRates);
            commissionRatesPerLossRatio.put(lossRatio, listOfCommissionRates);
        }
        return new InterpolatedSlidingCommission(commissionRatesPerLossRatio, useClaims);
    }


    public Double[] getLossRatios() {
        initValues();
        return lossRatios;
    }

    private void initValues() {
        if (lossRatios == null) {
            lossRatios = new Double[commissionBands.getValueRowCount() - commissionBands.getTitleRowCount() + 1];
            commissionoRate = new Double[commissionBands.getValueRowCount() - commissionBands.getTitleRowCount() + 1];
            for (int i = commissionBands.getTitleRowCount(); i <= commissionBands.getValueRowCount(); i++) {
                lossRatios[i - commissionBands.getTitleRowCount()] = InputFormatConverter.getDouble(commissionBands.getValueAt(i, LOSS_RATIO_COLUMN_INDEX));
                commissionoRate[i - commissionBands.getTitleRowCount()] = InputFormatConverter.getDouble(commissionBands.getValueAt(i, COMMISSION_COLUMN_INDEX));
            }
        }
    }

    public Double[] getCommisionRates() {
        initValues();
        return commissionoRate;
    }
}
