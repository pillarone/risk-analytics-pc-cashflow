package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelector;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimValidator;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract;
import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MatrixCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy, IContractCover {

    private ICoverAttributeStrategy filter;
    private ClaimValidator claimValidator;
    private ConstrainedMultiDimensionalParameter flexibleCover;
    private ConstrainedMultiDimensionalParameter benefitContracts;
    private List<RowFilter> rowFilters;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.MATRIX;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("flexibleCover", flexibleCover);
        parameters.put("benefitContracts", benefitContracts);
        return parameters;
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        if (claimValidator == null) {
            claimValidator = new ClaimValidator();
        }
        initRowFilters();
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        for (RowFilter rowFilter : rowFilters) {
            filteredClaims.addAll(rowFilter.filter(source));
        }
        source.clear();
        source.addAll(filteredClaims);
        return filteredClaims;
    }

    private void initRowFilters() {
        rowFilters = new ArrayList<RowFilter>();
        for (int row = flexibleCover.getTitleRowCount(); row < flexibleCover.getRowCount(); row++) {
            rowFilters.add(new RowFilter(row, flexibleCover));
        }
    }

    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source, List<ClaimCashflowPacket> coveredGrossClaims) {
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        return filteredUnderwritingInfo;
    }

    public List<IReinsuranceContractMarker> getCoveredReinsuranceContracts() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ReinsuranceContractAndBase> getCoveredReinsuranceContractsAndBase(Map<String, ReinsuranceContract> reinsuranceContracts) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    static class RowFilter {
        IReinsuranceContractMarker netContract;
        IReinsuranceContractMarker cededContract;
        ILegalEntityMarker legalEntity;
        ISegmentMarker segment;
        IPerilMarker peril;
        ClaimTypeSelector claimTypeSelector;

        RowFilter(int rowIndex, ConstrainedMultiDimensionalParameter flexibleCover) {
            netContract = (IReinsuranceContractMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.CONTRACT_NET_OF_COLUMN_INDEX);
            cededContract = (IReinsuranceContractMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.CONTRACT_CEDED_OF_COLUMN_INDEX);
            legalEntity = (ILegalEntityMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.LEGAL_ENTITY_OF_COLUMN_INDEX);
            segment = (ISegmentMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.SEGMENTS_OF_COLUMN_INDEX);
            peril = (IPerilMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.GENERATORS_OF_COLUMN_INDEX);
            claimTypeSelector = ClaimTypeSelector.valueOf((String) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.LOSS_KIND_OF_OF_COLUMN_INDEX));
        }

        public List<ClaimCashflowPacket> filter(List<ClaimCashflowPacket> source) {
            List<ClaimCashflowPacket> result = new ArrayList<ClaimCashflowPacket>();
            for (ClaimCashflowPacket claim : source) {
                if ((netContract == null || netContract == claim.reinsuranceContract()) &&
                        (cededContract == null || cededContract == claim.reinsuranceContract()) &&
                        (legalEntity == null || legalEntity == claim.legalEntity()) &&
                        (segment == null || segment == claim.segment()) &&
                        (peril == null || peril == claim.peril()) &&
                        claimTypeMatches(claimTypeSelector, claim)) {
                    result.add(ClaimValidator.positiveNominalUltimate(claim));
                }
            }
            source.removeAll(result);
            return result;
        }

        private boolean claimTypeMatches(ClaimTypeSelector claimTypeSelector, ClaimCashflowPacket claim) {
            return claimTypeSelector == ClaimTypeSelector.ANY || ClaimType.valueOf(claimTypeSelector.name()) == claim.getClaimType();
        }
    }
}
