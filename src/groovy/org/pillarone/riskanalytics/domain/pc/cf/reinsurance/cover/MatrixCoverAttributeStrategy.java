package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.structure.IStructuringStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.structure.StructuringType;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class MatrixCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy, IContractCover, IStructuringStrategy {

    ConstrainedMultiDimensionalParameter flexibleCover;
    ConstrainedMultiDimensionalParameter benefitContracts;
    private List<MatrixCoverAttributeRow> rowFilters;
    boolean alternativeAggregation;

    public IParameterObjectClassifier getType() {
        return alternativeAggregation ? StructuringType.MATRIX : CoverAttributeStrategyType.MATRIX;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("flexibleCover", flexibleCover);
        if (!alternativeAggregation) {
            parameters.put("benefitContracts", benefitContracts);
        }
        return parameters;
    }

    public List<IReinsuranceContractMarker> getBenefitContracts() {
        return benefitContracts.getValuesAsObjects(0);
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        initRowFilters();
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        for (MatrixCoverAttributeRow rowFilter : getRowFilters()) {
            filteredClaims.addAll(rowFilter.filter(source));
        }
        source.clear();
        source.addAll(filteredClaims);
        return filteredClaims;
    }

    public void initRowFilters() {
        rowFilters = new ArrayList<MatrixCoverAttributeRow>();
        for (int row = flexibleCover.getTitleRowCount(); row < flexibleCover.getRowCount(); row++) {
            rowFilters.add(new MatrixCoverAttributeRow(row, alternativeAggregation, flexibleCover));
        }
    }

    public boolean coverGrossClaimsOnly() {
        if (hasBenefitContracts()) {
            return false;
        }
        for (MatrixCoverAttributeRow rowFilter : getRowFilters()) {
            if (!rowFilter.coverGrossClaimsOnly()) {
                return false;
            }
        }
        return true;
    }

    public List<IReinsuranceContractMarker> coveredCededOfContracts() {
        List<IReinsuranceContractMarker> cededOfContracts = new ArrayList<IReinsuranceContractMarker>();
        for (MatrixCoverAttributeRow rowFilter : getRowFilters()) {
            if (rowFilter.getCededContract() != null) {
                cededOfContracts.add(rowFilter.getCededContract());
            }
        }
        return cededOfContracts;
    }

    public List<IReinsuranceContractMarker> coveredNetOfContracts() {
        List<IReinsuranceContractMarker> netOfContracts = new ArrayList<IReinsuranceContractMarker>();
        for (MatrixCoverAttributeRow rowFilter : getRowFilters()) {
            if (rowFilter.getNetContract() != null) {
                netOfContracts.add(rowFilter.getNetContract());
            }
        }
        return netOfContracts;
    }

    public boolean mergerRequired() {
        return hasBenefitContracts() || getRowFilters().size() > 1 && !coverGrossClaimsOnly();
    }

    private boolean hasBenefitContracts() {
        return getBenefitContracts().size() > 0;
    }

    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source, List<ClaimCashflowPacket> coveredGrossClaims) {
        // todo: avoid code copy from LE Strategy
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        Set<ISegmentMarker> coveredSegments = new HashSet<ISegmentMarker>();
        for (ClaimCashflowPacket claim : coveredGrossClaims) {
            coveredSegments.add(claim.segment());
        }
        for (UnderwritingInfoPacket underwritingInfo : source) {
            if (coveredSegments.contains(underwritingInfo.segment())) {
                filteredUnderwritingInfo.add(underwritingInfo);
            }
        }
        source.clear();
        if (!filteredUnderwritingInfo.isEmpty()) {
            source.addAll(filteredUnderwritingInfo);
        }
        return filteredUnderwritingInfo;
    }

    public List<IReinsuranceContractMarker> getCoveredReinsuranceContracts() {
        // todo
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ReinsuranceContractAndBase> getCoveredReinsuranceContractsAndBase(Map<String, ReinsuranceContract> reinsuranceContracts) {
        // todo
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<MatrixCoverAttributeRow> getRowFilters() {
        if (rowFilters == null) {
            initRowFilters();
        }
        return rowFilters;
    }

    public boolean hasGrossFilters() {
        for (MatrixCoverAttributeRow rowFilter : getRowFilters()) {
            if (rowFilter.coverGrossClaimsOnly()) return true;
        }
        return false;
    }

    public List<ClaimCashflowPacket> filterClaims(List<ClaimCashflowPacket> claims) {
        return coveredClaims(claims);
    }

    public List<UnderwritingInfoPacket> filterUnderwritingInfos(List<UnderwritingInfoPacket> underwritingInfos) {
        initRowFilters();
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        for (MatrixCoverAttributeRow rowFilter : getRowFilters()) {
            filteredUnderwritingInfo.addAll(rowFilter.filterUnderwritingInfos(underwritingInfos));
        }
        return filteredUnderwritingInfo;
    }

    public List<CededUnderwritingInfoPacket> filterUnderwritingInfosCeded(List<CededUnderwritingInfoPacket> underwritingInfos) {
        initRowFilters();
        List<CededUnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<CededUnderwritingInfoPacket>();
        for (MatrixCoverAttributeRow rowFilter : getRowFilters()) {
            filteredUnderwritingInfo.addAll(rowFilter.filterUnderwritingInfos(underwritingInfos));
        }
        return filteredUnderwritingInfo;
    }

}
