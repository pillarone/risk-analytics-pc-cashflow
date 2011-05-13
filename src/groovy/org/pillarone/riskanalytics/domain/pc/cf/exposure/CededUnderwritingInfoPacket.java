package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class CededUnderwritingInfoPacket extends UnderwritingInfoPacket {

    private double premiumPaidFixed;
    private double premiumPaidVariable;

    private double commission;
    private double commissionFixed;
    private double commissionVariable;

    private UnderwritingInfoPacket original;

    public CededUnderwritingInfoPacket() {
        super();
    }

    /**
     *
     * @param policyFactor
     * @param premiumFactor
     * @param commissionFactor
     * @return creates a cloned instance with numberOfPolicies and premiumWritten according to parameters
     */
    public CededUnderwritingInfoPacket withFactorsApplied(double policyFactor, double premiumFactor, double commissionFactor) {
        CededUnderwritingInfoPacket modified = (CededUnderwritingInfoPacket) this.clone();
        modified.numberOfPolicies *= policyFactor;
        modified.premiumWritten *= premiumFactor;
        modified.premiumPaid *= premiumFactor;
        modified.premiumPaidFixed *= premiumFactor;
        modified.premiumPaidVariable *= premiumFactor;
        modified.commission *= commissionFactor;
        modified.commissionFixed *= commissionFactor;
        modified.commissionVariable *= commissionFactor;
        return modified;
    }

    public static CededUnderwritingInfoPacket deriveCededPacket(UnderwritingInfoPacket packet,
                                                                IReinsuranceContractMarker contract) {
        CededUnderwritingInfoPacket cededPacket = new CededUnderwritingInfoPacket();
        cededPacket.premiumWritten = -packet.premiumWritten;
        cededPacket.premiumPaid = -packet.premiumPaid;
        cededPacket.premiumPaidFixed = -packet.premiumPaid;
        cededPacket.numberOfPolicies = packet.numberOfPolicies;
        cededPacket.sumInsured = packet.sumInsured;
        cededPacket.maxSumInsured = packet.maxSumInsured;
        cededPacket.exposure = packet.exposure;
        cededPacket.original = packet.original;
        cededPacket.segment = packet.segment;
        cededPacket.reinsuranceContract = contract;
        cededPacket.original = packet;
        return cededPacket;
    }

    public static CededUnderwritingInfoPacket scale(UnderwritingInfoPacket packet, IReinsuranceContractMarker contract,
                                                    double policyFactor, double premiumFactor, double commissionFactor) {
        CededUnderwritingInfoPacket cededPacket = deriveCededPacket(packet, contract);
        return cededPacket.withFactorsApplied(policyFactor, premiumFactor, commissionFactor);
    }

    public void setCommission(double fixed, double variable) {
        commission = fixed + variable;
        commissionFixed = fixed;
        commissionVariable = variable;
    }

    /**
     * Adds additive UnderwritingInfo fields (premium) as well as combining ExposureInfo fields.
     * averageSumInsured is not adjusted!
     *
     * @param other
     * @return UnderwritingInfo packet with resulting fields
     */
    public CededUnderwritingInfoPacket plus(CededUnderwritingInfoPacket other) {
        if (other == null) return this;
        sumInsured = (numberOfPolicies * sumInsured + other.numberOfPolicies * other.sumInsured);
        numberOfPolicies += other.numberOfPolicies;
        if (numberOfPolicies > 0) {
            sumInsured = sumInsured / numberOfPolicies;
        }
        maxSumInsured = Math.max(maxSumInsured, other.maxSumInsured);
        if (exposure != other.exposure) {
            exposure = null;
        }
        if (original != other.original) {
            original = null;
        }
        premiumPaid += other.premiumPaid;
        premiumPaidFixed += other.premiumPaidFixed;
        premiumPaidVariable += other.premiumPaidVariable;
        premiumWritten += other.premiumWritten;
        commission += other.commission;
        commissionFixed += other.commissionFixed;
        commissionVariable += other.commissionVariable;
        return this;
    }

    public void adjustCommissionProperties(double commissionFactor, double fixedCommissionFactor,
                                           double variableCommissionFactor, boolean isAdditive) {
        if (isAdditive) {
            adjustCommissionProperties(commissionFactor, fixedCommissionFactor, variableCommissionFactor);
        }
        else {
            setCommissionProperties(commissionFactor, fixedCommissionFactor, variableCommissionFactor);
        }
    }

    public void setCommissionProperties(double commissionFactor, double fixedCommissionFactor, double variableCommissionFactor) {
        this.commission = -premiumPaid * commissionFactor;
        commissionFixed = -premiumPaid * fixedCommissionFactor;
        commissionVariable = -premiumPaid * variableCommissionFactor;
    }

    public void adjustCommissionProperties(double commissionFactor, double fixedCommissionFactor, double variableCommissionFactor) {
        this.commission -= premiumPaid * commissionFactor;
        commissionFixed -= premiumPaid * fixedCommissionFactor;
        commissionVariable -= premiumPaid * variableCommissionFactor;
    }

    public boolean sameContent(CededUnderwritingInfoPacket other) {
        return super.sameContent(other)
                && premiumPaidFixed == other.getPremiumPaidFixed()
                && premiumPaidVariable == other.getPremiumPaidVariable()
                && commission == other.getCommission()
                && commissionFixed == other.getCommissionFixed()
                && commissionVariable == other.getCommissionVariable();
    }

    private static final String PREMIUM_WRITTEN = "premiumWritten";
    private static final String PREMIUM_PAID = "premiumPaid";
    private static final String PREMIUM_PAID_FIXED = "premiumPaidFixed";
    private static final String PREMIUM_PAID_VARIABLE = "premiumPaidWariable";
    private static final String COMMISSION = "commission";
    private static final String COMMISSION_FIXED = "commissionFixed";
    private static final String COMMISSION_VARIABLE = "commissionVariable";
    /**
     *
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> map = new HashMap<String, Number>();
        map.put(PREMIUM_WRITTEN, premiumWritten);
        map.put(PREMIUM_PAID, premiumPaid);
        map.put(PREMIUM_PAID_FIXED, premiumPaidFixed);
        map.put(PREMIUM_PAID_VARIABLE, premiumPaidVariable);
        map.put(COMMISSION, commission);
        map.put(COMMISSION_FIXED, commissionFixed);
        map.put(COMMISSION_VARIABLE, commissionVariable);
        return map;
    }

    public final static List<String> FIELD_NAMES = Arrays.asList(PREMIUM_WRITTEN, PREMIUM_PAID, PREMIUM_PAID_FIXED,
            PREMIUM_PAID_VARIABLE, COMMISSION, COMMISSION_FIXED, COMMISSION_VARIABLE);

    @Override
    public List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(premiumWritten);
        result.append(SEPARATOR);
        result.append(premiumPaid);
        result.append(SEPARATOR);
        result.append(premiumPaidFixed);
        result.append(SEPARATOR);
        result.append(premiumPaidVariable);
        result.append(SEPARATOR);
        result.append(commission);
        result.append(SEPARATOR);
        result.append(commissionFixed);
        result.append(SEPARATOR);
        result.append(commissionVariable);
        result.append(SEPARATOR);
        result.append(numberOfPolicies);
        return result.toString();
    }

    public double getPremiumPaidFixed() {
        return premiumPaidFixed;
    }

    public void setPremiumPaidFixed(double premiumPaidFixed) {
        this.premiumPaidFixed = premiumPaidFixed;
    }

    public double getPremiumPaidVariable() {
        return premiumPaidVariable;
    }

    public void setPremiumPaidVariable(double premiumPaidVariable) {
        this.premiumPaidVariable = premiumPaidVariable;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getCommissionFixed() {
        return commissionFixed;
    }

    public void setCommissionFixed(double commissionFixed) {
        this.commissionFixed = commissionFixed;
    }

    public double getCommissionVariable() {
        return commissionVariable;
    }

    public void setCommissionVariable(double commissionVariable) {
        this.commissionVariable = commissionVariable;
    }

    private static final String SEPARATOR = ", ";

    public UnderwritingInfoPacket getOriginal() {
        return original;
    }

    public void setOriginal(UnderwritingInfoPacket original) {
        this.original = original;
    }
}
