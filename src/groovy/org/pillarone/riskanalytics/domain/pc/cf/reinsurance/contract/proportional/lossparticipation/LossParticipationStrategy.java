package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LossParticipationStrategy extends AbstractParameterObject implements ILossParticipationStrategy {

    public static final String LOSS_RATIO = "Loss Ratio (from)";
    public static final String LOSS_PART_BY_CEDANT = "Loss Part by Cedant";
    public static final int LOSS_RATIO_COLUMN_INDEX = 0;
    public static final int LOSS_PART_COLUMN_INDEX = 1;

    private SortedMap<Double, Double> table;
    private Double[] lossRatios;
    private Double[] lossParts;

    private ConstrainedMultiDimensionalParameter participation = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d}),
            Arrays.asList(LOSS_RATIO, LOSS_PART_BY_CEDANT),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));

    public IParameterObjectClassifier getType() {
        return LossParticipationStrategyType.LOSSPARTICIPATION;
    }

    public Map getParameters() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("participation", participation);
        return map;
    }

    private void initTable() {
        if (table == null) {
            table = new TreeMap<Double, Double>();
            lossRatios = new Double[participation.getValueRowCount() - participation.getTitleRowCount() + 1];
            lossParts = new Double[participation.getValueRowCount() - participation.getTitleRowCount() + 1];
            for (int i = participation.getTitleRowCount(); i <= participation.getValueRowCount(); i++) {
                double lossRatio = InputFormatConverter.getDouble(participation.getValueAt(i, LOSS_RATIO_COLUMN_INDEX));
                double lossParticipation = InputFormatConverter.getDouble(participation.getValueAt(i, LOSS_PART_COLUMN_INDEX));
                lossRatios[i - participation.getTitleRowCount()] = lossRatio;
                lossParts[i - participation.getTitleRowCount()] = lossParticipation;
                table.put(lossRatio, lossParticipation);
            }
        }
    }

    public ILossParticipation getLossParticpation() {
        initTable();
        return new LossParticipation(table);
    }

    Double[] getLossRatios() {
        initTable();
        return lossRatios;
    }

    Double[] getLossParts() {
        initTable();
        return lossParts;
    }
}
