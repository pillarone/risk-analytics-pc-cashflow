package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.cf.segment.ISegmentMarker;
import org.pillarone.riskanalytics.domain.utils.PacketUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): think! should we head for a similar design as for claims (.. root ..)
public class UnderwritingInfoPacket extends MultiValuePacket {
    
    protected double premiumWritten;
    protected double premiumPaid;
    protected double numberOfPolicies;
    protected double sumInsured;
    protected double maxSumInsured;

    protected ExposureInfo exposure;
    protected Integer inceptionPeriod;

    protected UnderwritingInfoPacket original;
    protected ISegmentMarker segment;
    protected IReinsuranceContractMarker reinsuranceContract;

    public UnderwritingInfoPacket() {
        super();
    }

    /**
     * 
     * @param policyFactor
     * @param premiumFactor
     * @return creates a cloned instance with numberOfPolicies and premiumWritten according to parameters
     */
    public UnderwritingInfoPacket withFactorsApplied(double policyFactor, double premiumFactor) {
        UnderwritingInfoPacket modified = (UnderwritingInfoPacket) this.clone();
        modified.numberOfPolicies *= policyFactor;
        modified.premiumWritten *= premiumFactor;
        modified.premiumPaid *= premiumFactor;
        return modified;
    }

    /**
     * @param cededUnderwritingInfo
     * @param proportionalContractApplied   if true sumInsured, maxSumInsured are adjusted too
     * @return
     */
    public UnderwritingInfoPacket getNet(CededUnderwritingInfoPacket cededUnderwritingInfo, boolean proportionalContractApplied) {
        UnderwritingInfoPacket netUnderwritingInfo = (UnderwritingInfoPacket) this.clone();
        netUnderwritingInfo.premiumWritten += cededUnderwritingInfo.getPremiumWritten();
        netUnderwritingInfo.premiumPaid += cededUnderwritingInfo.getPremiumPaid();
        if (proportionalContractApplied) {
            netUnderwritingInfo.sumInsured += cededUnderwritingInfo.getSumInsured();
            netUnderwritingInfo.maxSumInsured += cededUnderwritingInfo.getMaxSumInsured();
        }
        boolean anyRemaining = netUnderwritingInfo.premiumWritten > 0 || netUnderwritingInfo.premiumPaid > 0;
        netUnderwritingInfo.numberOfPolicies = anyRemaining ? netUnderwritingInfo.numberOfPolicies : 0;
        return netUnderwritingInfo;
    }

    /**
     * Has to be applied after withFactorsApplied() to avoid overwritting premiumPaid wrongly.
     * @param positiveWrittenValue
     * @param paidShare
     */
    public void applyPattern(boolean positiveWrittenValue, double paidShare) {
        premiumPaid = premiumWritten * paidShare;
        premiumWritten = positiveWrittenValue ? premiumWritten : 0;
    }

    /**
     * @param base
     * @return return the packet value according the base, 1 for absolute
     */
    public double scaleValue(ExposureBase base) {
        switch (base) {
            case ABSOLUTE:
                return 1d;
            case PREMIUM_WRITTEN:
                return premiumWritten;
            case NUMBER_OF_POLICIES:
                return numberOfPolicies;
        }
        return 0;
    }

    /**
     * @param base
     * @return return the packet value according the base, 1 for absolute
     */
    public double scaleValue(FrequencyBase base) {
        switch (base) {
            case ABSOLUTE:
                return 1d;
            case NUMBER_OF_POLICIES:
                return numberOfPolicies;
        }
        return 1;
    }

    /**
     * Adds additive UnderwritingInfo fields (premium) as well as combining ExposureInfo fields.
     * averageSumInsured is not adjusted!
     *
     * @param other
     * @return UnderwritingInfo packet with resulting fields
     */
    public UnderwritingInfoPacket plus(UnderwritingInfoPacket other) {
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
        premiumPaid += other.premiumPaid;
        premiumWritten += other.premiumWritten;
        return this;
    }

    public boolean sameContent(UnderwritingInfoPacket other) {
        return (PacketUtilities.sameContent(this, other)
                && numberOfPolicies == other.getNumberOfPolicies()
                && sumInsured == other.getSumInsured()
                && maxSumInsured == other.getMaxSumInsured()
                && premiumWritten == other.getPremiumWritten()
                && premiumPaid == other.getPremiumPaid())
                && (exposure == null && other.getExposure() == null
                   || exposure.equals(other.getExposure()));
    }

    private static final String PREMIUM_WRITTEN = "premiumWritten";
    private static final String PREMIUM_PAID = "premiumPaid";
    /**
     * Warning: if the number or names of values is modified, UnderwritingBatchInsertDBCollector
     * has to be corrected accordingly.
     *
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> map = new HashMap<String, Number>();
        map.put(PREMIUM_WRITTEN, premiumWritten);
        map.put(PREMIUM_PAID, premiumPaid);
        return map;
    }

    public final static List<String> FIELD_NAMES = Arrays.asList(PREMIUM_WRITTEN, PREMIUM_PAID);

    @Override
    public List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(premiumWritten);
        result.append(separator);
        result.append(premiumPaid);
        result.append(separator);
        result.append(numberOfPolicies);
        return result.toString();
    }

    public double getPremiumWritten() {
        return premiumWritten;
    }

    public void setPremiumWritten(double premiumWritten) {
        this.premiumWritten = premiumWritten;
    }

    public double getNumberOfPolicies() {
        return numberOfPolicies;
    }

    public void setNumberOfPolicies(double numberOfPolicies) {
        this.numberOfPolicies = numberOfPolicies;
    }

    public double getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(double sumInsured) {
        this.sumInsured = sumInsured;
    }

    public double getMaxSumInsured() {
        return maxSumInsured;
    }

    public void setMaxSumInsured(double maxSumInsured) {
        this.maxSumInsured = maxSumInsured;
    }

    public ExposureInfo getExposure() {
        return exposure;
    }

    public void setExposure(ExposureInfo exposure) {
        this.exposure = exposure;
    }

    public UnderwritingInfoPacket getOriginal() {
        return original;
    }

    public void setOriginal(UnderwritingInfoPacket original) {
        this.original = original;
    }

    public double getPremiumPaid() {
        return premiumPaid;
    }

    public void setPremiumPaid(double premiumPaid) {
        this.premiumPaid = premiumPaid;
    }

    public ISegmentMarker getSegment() {
        return segment;
    }

    public void setSegment(ISegmentMarker segment) {
        this.segment = segment;
    }

    public IReinsuranceContractMarker getReinsuranceContract() {
        return reinsuranceContract;
    }

    public void setReinsuranceContract(IReinsuranceContractMarker reinsuranceContract) {
        this.reinsuranceContract = reinsuranceContract;
    }
}
