package org.pillarone.riskanalytics.domain.pc.cf.reinsurance;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ContractFinancialsPacket extends MultiValuePacket {
    private double contractResult;
    private double primaryResult;
    private double cededPremium;
    private double netPremium;
    private double cededClaim;
    private double netClaim;
    private double cededCommission;
    private double cededLossRatio;

    /**
     * Default c´tor required by SumAggregator
     */
    public ContractFinancialsPacket() {
    }

    /**
     * cededClaim and netClaim are filled with the ultimate value, cededPremium and netPremium are filled with the premium written
     * @param cededClaims
     * @param netClaims
     * @param cededUwInformation
     * @param netUwInformation
     */
    public ContractFinancialsPacket(List<ClaimCashflowPacket> cededClaims, List<ClaimCashflowPacket> netClaims,
                                    List<CededUnderwritingInfoPacket> cededUwInformation, List<UnderwritingInfoPacket> netUwInformation) {
        if (!cededClaims.isEmpty()) {
            cededClaim = ClaimUtils.sum(cededClaims, true).ultimate();
        }
        if (!netClaims.isEmpty()) {
            netClaim = ClaimUtils.sum(netClaims, true).ultimate();
        }
        if (!cededUwInformation.isEmpty()) {
            CededUnderwritingInfoPacket aggregateCededUwInfoPacket = UnderwritingInfoUtils.aggregate(cededUwInformation);
            cededCommission = aggregateCededUwInfoPacket.getCommission();
            cededPremium = aggregateCededUwInfoPacket.getPremiumWritten();
        }
        if (!netUwInformation.isEmpty()) {
            netPremium = UnderwritingInfoUtils.aggregate(netUwInformation).getPremiumWritten();
        }
        updateDependingProperties();
    }

    @Override
    public void updateDependingProperties() {
        updateContractResult();
        updatePrimaryResult();
        updateCededLossRatio();
    }

    public double getCededPremium() {
        return cededPremium;
    }

    public void setCededPremium(double cededPremium) {
        this.cededPremium = cededPremium;
        setResultingCosts();
    }

    public double getCededClaim() {
        return cededClaim;
    }

    public void setCededClaim(double cededClaim) {
        this.cededClaim = cededClaim;
        setResultingCosts();
    }

    public double getContractResult() {
        updateContractResult();
        return contractResult;
    }

    private void updateContractResult() {
        contractResult = cededPremium + cededClaim + cededCommission;
    }

    private void setResultingCosts() {
        updateContractResult();
    }
    public void setContractResult(double contractResult) {
        this.contractResult = contractResult;
    }

    public double getCededCommission() {
        return cededCommission;
    }

    public void setCededCommission(double cededCommission) {
        this.cededCommission = cededCommission;
        setResultingCosts();
        updatePrimaryResult();
    }

    public double getCededLossRatio() {
        updateCededLossRatio();
        return cededLossRatio;
    }

    public void setCededLossRatio(double cededLossRatio) {
        updateCededLossRatio();
    }

    private void updateCededLossRatio() {
        this.cededLossRatio = cededPremium == 0d ? 0d : cededClaim / cededPremium;
    }

    public double getPrimaryResult() {
        setPrimaryResult(0d);
        return primaryResult;
    }

    public void setPrimaryResult(double primaryResult) {
        updatePrimaryResult();
    }

    private void updatePrimaryResult() {
        this.primaryResult = netPremium + netClaim + cededCommission;
    }

    public double getNetPremium() {
        return netPremium;
    }

    public void setNetPremium(double netPremium) {
        this.netPremium = netPremium;
        updatePrimaryResult();
    }

    public double getNetClaim() {
        return netClaim;
    }

    public void setNetClaim(double netClaim) {
        this.netClaim = netClaim;
        updatePrimaryResult();
    }
}