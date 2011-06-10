package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.utils.math.copula.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsGenerator extends Component implements IPerilMarker {

    private SimulationScope simulationScope;

    private RandomDistribution parmDistribution = DistributionType.getStrategy(DistributionType.CONSTANT,
            ArrayUtils.toMap(new Object[][]{{"constant", 0d}}));
    private DistributionModified parmModification = DistributionModifier.getStrategy(DistributionModifier.NONE, new HashMap());

    private PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);

    protected void doCalculation() {
        PeriodScope periodScope = simulationScope.getIterationScope().getPeriodScope();
        List<ClaimRoot> baseClaims = ClaimsGeneratorUtils.generateClaims(1, parmDistribution, parmModification,
                ClaimType.ATTRITIONAL, periodScope);
        IPeriodCounter periodCounter = periodScope.getPeriodCounter();
        List<ClaimCashflowPacket> claims = new ArrayList<ClaimCashflowPacket>();
        for (ClaimRoot baseClaim : baseClaims) {
            GrossClaimRoot grossClaimRoot = new GrossClaimRoot(baseClaim, null, null);
            claims.addAll(grossClaimRoot.getClaimCashflowPackets(periodCounter, null, true));
        }
        outClaims.addAll(claims);
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }

    public RandomDistribution getParmDistribution() {
        return parmDistribution;
    }

    public void setParmDistribution(RandomDistribution parmDistribution) {
        this.parmDistribution = parmDistribution;
    }

    public DistributionModified getParmModification() {
        return parmModification;
    }

    public void setParmModification(DistributionModified parmModification) {
        this.parmModification = parmModification;
    }

    public PacketList<ClaimCashflowPacket> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<ClaimCashflowPacket> outClaims) {
        this.outClaims = outClaims;
    }
}
