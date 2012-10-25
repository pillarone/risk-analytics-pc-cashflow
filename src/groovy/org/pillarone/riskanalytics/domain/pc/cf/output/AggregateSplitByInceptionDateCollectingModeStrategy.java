package org.pillarone.riskanalytics.domain.pc.cf.output;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy;
import org.pillarone.riskanalytics.core.output.PathMapping;
import org.pillarone.riskanalytics.core.output.SingleValueResultPOJO;
import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.util.PeriodLabelsUtil;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ContractFinancialsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.segment.FinancialsPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;

import java.util.*;

/**
 * This collecting mode strategy splits claim and underwriting information up by inception date.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AggregateSplitByInceptionDateCollectingModeStrategy extends AbstractSplitCollectingModeStrategy implements ICollectingModeStrategy {

    protected static Log LOG = LogFactory.getLog(AggregateSplitByInceptionDateCollectingModeStrategy.class);

    static final String IDENTIFIER = "SPLIT_BY_INCEPTION_DATE";
    protected static final String RESERVE_RISK_BASE = "reserveRiskBase";
    protected static final String PREMIUM_RISK_BASE = "premiumRiskBase";
    protected static final String PREMIUM_AND_RESERVE_RISK_BASE = "premiumAndReserveRiskBase";
    private static final String PERIOD = "period";
    private boolean displayUnderwritingYearOnly = true;
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern(PeriodLabelsUtil.PARAMETER_DISPLAY_FORMAT);

    public List<SingleValueResultPOJO> collect(PacketList packets, boolean crashSimulationOnError) throws IllegalAccessException {
        initSimulation();
        iteration = packetCollector.getSimulationScope().getIterationScope().getCurrentIteration();
        period = packetCollector.getSimulationScope().getIterationScope().getPeriodScope().getCurrentPeriod();

        if (isCompatibleWith(packets.get(0).getClass())) {
            List<SingleValueResultPOJO> singleValueResults = collectDefaultClaimsProperties(packets, crashSimulationOnError);
            if (packets.get(0) instanceof ClaimCashflowPacket) {
                singleValueResults.addAll(createPremiumReserveRisk(packets, crashSimulationOnError));
            }
            return singleValueResults;
        } else {
            String notImplemented = ResourceBundle.getBundle(RESOURCE_BUNDLE).getString("AggregateSplitByInceptionDateCollectingModeStrategy.notImplemented");
            throw new NotImplementedException(notImplemented + "\n(" + packetCollector.getPath() + ")");
        }
    }

    @Override
    protected void initSimulation() {
        super.initSimulation();
        IPeriodCounter periodCounter = packetCollector.getSimulationScope().getIterationScope().getPeriodScope().getPeriodCounter();
        boolean projectionStartsOnFirstJanuary = periodCounter.startOfFirstPeriod().dayOfYear().get() == 1;
        boolean annualPeriods = periodCounter.annualPeriodsOnly(false);
        displayUnderwritingYearOnly = projectionStartsOnFirstJanuary && annualPeriods;
    }

    @Override
    public List<String> filter() {
        return Collections.emptyList();
    }

    protected boolean includeDefaultClaimsProperties() {
        return true;
    }

    private List<SingleValueResultPOJO> collectDefaultClaimsProperties(PacketList packets, boolean crashSimulationOnError) throws IllegalAccessException {
        if (includeDefaultClaimsProperties()) {
            return createSingleValueResults(aggregate(packets), crashSimulationOnError);
        }
        else {
            return new ArrayList<SingleValueResultPOJO>();
        }
    }

    protected SingleValueResultPOJO createSingleValueResult(String path, String fieldName, Double value, boolean crashSimulationOnError) {
        if (checkInvalidValues(fieldName, value, period, iteration, crashSimulationOnError)) return null;
        else {
            SingleValueResultPOJO result = new SingleValueResultPOJO();
            result.setSimulationRun(simulationRun);
            result.setIteration(iteration);
            result.setPeriod(period);
            result.setPath(mappingCache.lookupPath(path));
            result.setCollector(mappingCache.lookupCollector(getIdentifier()));
            result.setField(mappingCache.lookupField(fieldName));
            result.setValueIndex(0);
            result.setValue(value);
            return result;
        }
    }

    /**
     * @param packets
     * @return a map with paths as key
     */
    protected Map<PathMapping, Packet> aggregate(List<Packet> packets) {
        // has to be a LinkedHashMap to make sure the shortest path is the first in the map and gets AGGREGATED as collecting mode
        Map<PathMapping, Packet> resultMap = new LinkedHashMap<PathMapping, Packet>(packets.size());
        if (packets == null || packets.size() == 0) {
            return resultMap;
        }

        for (Packet packet : packets) {
            String originPath = packetCollector.getSimulationScope().getStructureInformation().getPath(packet);
            PathMapping path = mappingCache.lookupPath(originPath);
            addToMap(packet, path, resultMap);

            if (splitByInceptionPeriod()) {
                PathMapping periodPath = getPathMapping(packet);
                addToMap(packet, periodPath, resultMap);
            }
        }
        return resultMap;
    }

    protected boolean splitByInceptionPeriod() {
        return true;
    }
    
    private String inceptionPeriod(Packet packet) {
        DateTime date = null;
        if (packet instanceof ClaimCashflowPacket) {
            date = ((ClaimCashflowPacket) packet).getBaseClaim().getExposureStartDate();
        }
        else if (packet instanceof UnderwritingInfoPacket) {
            date = ((UnderwritingInfoPacket) packet).getExposure().getInceptionDate();
        }
        else if (packet instanceof ContractFinancialsPacket) {
            date = ((ContractFinancialsPacket) packet).getInceptionDate();
        }
        else if (packet instanceof FinancialsPacket) {
            date = ((FinancialsPacket) packet).getInceptionDate();
        }
        else {
            throw new IllegalArgumentException("Packet type " + packet.getClass() + " is not supported.");
        }
        if (displayUnderwritingYearOnly) {
            return String.valueOf(date.getYear());
        }
        else {
            return formatter.print(getPeriodStartDate(date));
        }
    }

    private DateTime getPeriodStartDate(DateTime date) {
        return packetCollector.getSimulationScope().getIterationScope().getPeriodScope().getPeriodCounter().startOfPeriod(date);
    }

    protected void addToMap(Packet packet, PathMapping path, Map<PathMapping, Packet> resultMap) {
        if (packet instanceof ClaimCashflowPacket) {
            addToMap((ClaimCashflowPacket) packet, path, resultMap);
        }
        else if (packet instanceof UnderwritingInfoPacket) {
            addToMap((UnderwritingInfoPacket) packet, path, resultMap);
        }
        else if (packet instanceof ContractFinancialsPacket) {
            addToMap((ContractFinancialsPacket) packet, path, resultMap);
        }
        else if (packet instanceof FinancialsPacket) {
            addToMap((FinancialsPacket) packet, path, resultMap);
        }
        else {
            throw new IllegalArgumentException("Packet type " + packet.getClass() + " is not supported.");
        }
    }

    protected void addToMap(ContractFinancialsPacket packet, PathMapping path, Map<PathMapping, Packet> resultMap) {
        if (path == null) return;
        if (resultMap.containsKey(path)) {
            ContractFinancialsPacket aggregatePacket = (ContractFinancialsPacket) resultMap.get(path);
            aggregatePacket.plus(packet);
            resultMap.put(path, aggregatePacket);
        } else {
            resultMap.put(path, packet.copy());
        }
    }

    protected void addToMap(FinancialsPacket packet, PathMapping path, Map<PathMapping, Packet> resultMap) {
        if (path == null) return;
        if (resultMap.containsKey(path)) {
            FinancialsPacket aggregatePacket = (FinancialsPacket) resultMap.get(path);
            aggregatePacket.plus(packet);
            resultMap.put(path, aggregatePacket);
        } else {
            resultMap.put(path, packet.copy());
        }
    }
    
    protected List<SingleValueResultPOJO> createPremiumReserveRisk(List<ClaimCashflowPacket> claims, boolean crashSimulationOnError) {
        List<SingleValueResultPOJO> results = new ArrayList<SingleValueResultPOJO>();
        double totalReserveRisk = 0;
        double premiumRisk = 0;
        Map<String, Double> reserveRiskByPeriodPath = new HashMap<String, Double>();
        for (ClaimCashflowPacket claim : claims) {
            if (claim.reserveRisk() != 0) {
                // belongs to reserve risk
                totalReserveRisk += claim.reserveRisk();
                if (splitByInceptionPeriod()) {
                    String periodLabel = inceptionPeriod(claim);
                    String pathExtension = PERIOD + PATH_SEPARATOR + periodLabel;
                    String pathExtended = getExtendedPath(claim, pathExtension);
                    Double reserveRisk = reserveRiskByPeriodPath.get(pathExtended);
                    reserveRisk = reserveRisk == null ? claim.reserveRisk() : reserveRisk + claim.reserveRisk();
                    reserveRiskByPeriodPath.put(pathExtended, reserveRisk);
                }
            }
            else if (claim.premiumRisk() != 0) {
                // belongs to premium risk
                premiumRisk += claim.premiumRisk();
            }
        }
        if (splitByInceptionPeriod()) {
            for (Map.Entry<String, Double> reserveRisk : reserveRiskByPeriodPath.entrySet()) {
                results.add(createSingleValueResult(reserveRisk.getKey(), RESERVE_RISK_BASE, reserveRisk.getValue(), crashSimulationOnError));
            }
        }
        if (premiumRisk != 0) {
            results.add(createSingleValueResult(packetCollector.getPath(), PREMIUM_RISK_BASE, premiumRisk, crashSimulationOnError));
        }
        if (totalReserveRisk != 0) {
            results.add(createSingleValueResult(packetCollector.getPath(), RESERVE_RISK_BASE, totalReserveRisk, crashSimulationOnError));
        }
        if (premiumRisk + totalReserveRisk != 0) {
            results.add(createSingleValueResult(packetCollector.getPath(), PREMIUM_AND_RESERVE_RISK_BASE, premiumRisk + totalReserveRisk, crashSimulationOnError));
        }
        return results;
    }

    private PathMapping getPathMapping(Packet packet) {
        String periodLabel = inceptionPeriod(packet);
        String pathExtension = "period" + PATH_SEPARATOR + periodLabel;
        String pathExtended = getExtendedPath(packet, pathExtension);
        return mappingCache.lookupPath(pathExtended);
    }
    
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public boolean isCompatibleWith(Class packetClass) {
        return super.isCompatibleWith(packetClass) || ContractFinancialsPacket.class.isAssignableFrom(packetClass)
                || FinancialsPacket.class.isAssignableFrom(packetClass);
    }
}
