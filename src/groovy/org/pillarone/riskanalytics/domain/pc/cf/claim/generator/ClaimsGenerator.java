package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.model.Model;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IPerilMarker;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGenerator extends Component implements IPerilMarker {

    private SimulationScope simulationScope;

    // attritional, frequency average attritional, ...
    private IClaimsGeneratorStrategy parmClaimsModel = ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, new HashMap());

    private PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);


    /**
     * used for date generation for single claims
     */
//    private IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();

    protected void doCalculation() {

        List<ClaimRoot> baseClaims = parmClaimsModel.generateClaims(simulationScope.getIterationScope().getPeriodScope());
        IPeriodCounter periodCounter = simulationScope.getIterationScope().getPeriodScope().getPeriodCounter();
        List<ClaimCashflowPacket> claims = new ArrayList<ClaimCashflowPacket>();
        for (ClaimRoot baseClaim : baseClaims) {
            claims.addAll(baseClaim.getClaimCashflowPackets(periodCounter, null, true));
        }
//        outClaims.addAll(parmAssociateExposureInfo.getAllocatedClaims(claims, outUnderwritingInfo));
        outClaims.addAll(claims);

//        List<Double> claimValues = new ArrayList<Double>();
//        List<EventPacket> events = new ArrayList<EventPacket>();
//        PacketList<ClaimCashflowPacket> claims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
//        if (!(parmClaimsModel instanceof NoneClaimsGeneratorStrategy)) {
//            double scalingFactor = UnderwritingUtilities.scaleFactor(outUnderwritingInfo, parmClaimsModel.getClaimsSizeBase());
//            ClaimType claimType = ClaimType.ATTRITIONAL;
//            if (parmClaimsModel instanceof AttritionalClaimsGeneratorStrategy) {
//                claimType = ClaimType.ATTRITIONAL;
//                if (this.isReceiverWired(inProbabilities)) {
//                    List<Double> probabilities = filterProbabilities();
//                    if (probabilities.size() > 1) {
//                        throw new IllegalArgumentException("['TypableClaimsGenerator.attritionalClaims','" + this.getNormalizedName() + "']");
//                    } else {
//                        claimValues = calculateClaimsValues(
//                                probabilities,
//                                parmClaimsModel.getClaimsSizeDistribution(),
//                                parmClaimsModel.getClaimsSizeModification());
//                    }
//                }
//                if (claimValues == null || claimValues.size() == 0) {
//                    claimValues = generateClaimsValues(1,
//                            parmClaimsModel.getClaimsSizeDistribution(),
//                            parmClaimsModel.getClaimsSizeModification());
//                }
//
//            } else if (parmClaimsModel instanceof IFrequencyClaimsGeneratorStrategy) {
//                double frequency = generateFrequency(
//                        ((IFrequencyClaimsGeneratorStrategy) parmClaimsModel).getFrequencyDistribution(),
//                        ((IFrequencyClaimsGeneratorStrategy) parmClaimsModel).getFrequencyModification(),
//                        ((IFrequencyClaimsGeneratorStrategy) parmClaimsModel).getFrequencyBase());
//                if (parmClaimsModel instanceof FrequencyAverageAttritionalClaimsGeneratorStrategy) {
//                    claimType = ClaimType.ATTRITIONAL;
//
//                    scalingFactor *= frequency;
//                    claimValues = generateClaimsValues(1,
//                            parmClaimsModel.getClaimsSizeDistribution(),
//                            parmClaimsModel.getClaimsSizeModification());
//                } else if (parmClaimsModel instanceof IFrequencySingleClaimsGeneratorStrategy) {
//                    if (((IFrequencySingleClaimsGeneratorStrategy) parmClaimsModel).getProduceClaim().equals(FrequencySeverityClaimType.AGGREGATED_EVENT)) {
//                        claimType = ClaimType.AGGREGATED_EVENT;
//                        events = generateEvents((int) frequency);
//                    } else if (((IFrequencySingleClaimsGeneratorStrategy) parmClaimsModel).getProduceClaim().equals(FrequencySeverityClaimType.SINGLE)) {
//                        claimType = ClaimType.SINGLE;
//                    }
//                    claimValues = generateClaimsValues((int) frequency,
//                            parmClaimsModel.getClaimsSizeDistribution(),
//                            parmClaimsModel.getClaimsSizeModification());
//                }
//            } else if (parmClaimsModel instanceof ExternalSeverityClaimsGeneratorStrategy) {
//                if (this.isReceiverWired(inEventSeverities)) {
//                    claimType = ClaimType.AGGREGATED_EVENT;
//                    List<EventSeverity> filteredEventSeverities = filterEvents();
//                    claimValues = calculateEventClaimsValues(filteredEventSeverities, parmClaimsModel.getClaimsSizeDistribution());
//                    events = extractEvents(filteredEventSeverities);
//                } else {
//                    throw new IllegalArgumentException("TypableClaimsGenerator.externalSeverityClaims");
//                }
//            } else if (parmClaimsModel instanceof PMLClaimsGeneratorStrategy) {
//                ((PMLClaimsGeneratorStrategy) parmClaimsModel).initDistributions(simulationScope.getIterationScope().getPeriodScope());
//                RandomDistribution frequencyDistribution = ((PMLClaimsGeneratorStrategy) parmClaimsModel).getFrequencyDistribution();
//                RandomDistribution claimsSizeDistribution = parmClaimsModel.getClaimsSizeDistribution();
//                DistributionModified modification = parmClaimsModel.getClaimsSizeModification();
//                double frequency = generateFrequency(frequencyDistribution,
//                        DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap()), FrequencyBase.ABSOLUTE);
//                if (((PMLClaimsGeneratorStrategy) parmClaimsModel).getProduceClaim().equals(FrequencySeverityClaimType.AGGREGATED_EVENT)) {
//                    claimType = ClaimType.AGGREGATED_EVENT;
//                    events = generateEvents((int) frequency);
//                } else if (((PMLClaimsGeneratorStrategy) parmClaimsModel).getProduceClaim().equals(FrequencySeverityClaimType.SINGLE)) {
//                    claimType = ClaimType.SINGLE;
//                }
//                claimValues = generateClaimsValues((int) frequency, claimsSizeDistribution, modification);
//            } else {
//                throw new NotImplementedException("['TypableClaimsGenerator.notImplemented','" + parmClaimsModel.toString() + "']");
//            }
//            if (events.size() == 0) {
//                if (claimValues.size() == 0) {
//                    claimValues.add(0d);
//                }
//                for (Double claimValue : claimValues) {
//                    Claim claim = ClaimPacketFactory.createPacket();
//                    claim.origin = this;
//                    claim.setPeril(this);
//                    claim.setClaimType(claimType);
//                    claim.setUltimate(claimValue * scalingFactor);
//                    setFractionOfPeriod(claimType, claim);
//                    claims.add(claim);
//                }
//            } else {
//                for (int i = 0; i < claimValues.size(); i++) {
//                    Claim claim = ClaimPacketFactory.createPacket();
//                    claim.origin = this;
//                    claim.setPeril(this);
//                    claim.setClaimType(claimType);
//                    claim.setUltimate(claimValues.get(i) * scalingFactor);
//                    claim.setEvent(events.get(i));
//                    claim.setFractionOfPeriod(claim.getEvent().getFractionOfPeriod());
//                    claims.add(claim);
//                }
//            }
//        }
//        outClaims.addAll(parmAssociateExposureInfo.getAllocatedClaims(claims, outUnderwritingInfo));
//        Frequency frequency = new Frequency();
//        frequency.setValue(outClaims.size());
//        outClaimsNumber.add(frequency);
    }

//    protected void setFractionOfPeriod(ClaimType claimType, Claim claim) {
//        if (parmClaimsModel instanceof IOccurrenceClaimsGeneratorStrategy) {
//            IRandomNumberGenerator generator = getCachedGenerator(((IOccurrenceClaimsGeneratorStrategy) parmClaimsModel).getOccurrenceDistribution(), parmClaimsModel.getClaimsSizeModification());
//            claim.setFractionOfPeriod((Double) generator.nextValue());
//        } else {
//            if (claimType.equals(ClaimType.ATTRITIONAL)) {
//                claim.setFractionOfPeriod(0.5d);
//            } else {
//                claim.setFractionOfPeriod((Double) dateGenerator.nextValue());
//            }
//        }
//    }

    protected Model getModel() {
        return simulationScope.getModel();
    }


//    protected double generateFrequency(RandomDistribution distribution, DistributionModified modification, FrequencyBase frequencyBase) {
//        double frequency = 0;
//        IRandomNumberGenerator generator = getCachedGenerator(distribution, modification);
//        // todo(sku): refactor in order to use IExposureBaseStrategy or an equivalent construct
//        if (frequencyBase.equals(FrequencyBase.NUMBER_OF_POLICIES)) {
//            double scaleFactor = 0;
//            for (UnderwritingInfo underwritingInfo : inUnderwritingInfo) {
//                scaleFactor += underwritingInfo.getNumberOfPolicies();
//            }
//            frequency = ((Double) generator.nextValue()) * scaleFactor;
//        } else {
//            frequency = generator.nextValue().intValue();
//        }
//        return frequency;
//    }

//    protected List<Double> generateClaimsValues(int number, RandomDistribution distribution, DistributionModified modification) {
//        if (distribution == null) {
//            throw new IllegalStateException("TypableClaimsGenerator.missingDistribution");
//        }
//        IRandomNumberGenerator generator = getCachedGenerator(distribution, modification);
//        List<Double> claimValues = new ArrayList<Double>(number);
//        for (int i = 0; i < number; i++) {
//            claimValues.add((Double) generator.nextValue());
//        }
//        return claimValues;
//    }

//    protected List<EventPacket> generateEvents(int number) {
//        List<Double> dates = UniformDoubleList.getDoubles(number, true);
//        List<EventPacket> events = new ArrayList<EventPacket>(number);
//        for (Double date : dates) {
//            EventPacket event = new EventPacket();
//            event.setFractionOfPeriod(date);
//            events.add(event);
//        }
//        return events;
//    }

    // todo(sku): refactor once the variate distributions are properly refactored

//    protected List<Double> calculateClaimsValues(List<Double> probabilities, RandomDistribution distribution, DistributionModified modification) {
//        Distribution dist = distribution.getDistribution();
//        if (modification.getType().equals(DistributionModifier.CENSORED) || modification.getType().equals(DistributionModifier.CENSOREDSHIFT)) {
//            dist = new CensoredDistribution(distribution.getDistribution(),
//                    (Double) modification.getParameters().get("min"), (Double) modification.getParameters().get("max"));
//        } else if (modification.getType().equals(DistributionModifier.TRUNCATED) || modification.getType().equals(DistributionModifier.TRUNCATEDSHIFT)) {
//            Double leftBoundary = (Double) modification.getParameters().get("min");
//            Double rightBoundary = (Double) modification.getParameters().get("max");
//            dist = new TruncatedDist((ContinuousDistribution) distribution.getDistribution(), leftBoundary, rightBoundary);
//        }
//        List<Double> claimValues = new ArrayList<Double>(probabilities.size());
//        double shift = modification.getParameters().get("shift") == null ? 0 : (Double) modification.getParameters().get("shift");
//        for (Double probability : probabilities) {
//            claimValues.add(dist.inverseF(probability) + shift);
//        }
//        return claimValues;
//    }

//    protected List<Double> calculateEventClaimsValues(List<EventSeverity> eventSeverities, RandomDistribution distribution) {
//        List<Double> claimValues = new ArrayList<Double>(eventSeverities.size());
//        for (EventSeverity severity : eventSeverities) {
//            claimValues.add(distribution.getDistribution().inverseF(severity.value));
//        }
//        return claimValues;
//    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }

    public IClaimsGeneratorStrategy getParmClaimsModel() {
        return parmClaimsModel;
    }

    public void setParmClaimsModel(IClaimsGeneratorStrategy parmClaimsModel) {
        this.parmClaimsModel = parmClaimsModel;
    }

    /**
     * claims which source is a covered line
     */
    public PacketList<ClaimCashflowPacket> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<ClaimCashflowPacket> outClaims) {
        this.outClaims = outClaims;
    }
}
