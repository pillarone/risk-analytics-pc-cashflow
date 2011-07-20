package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimFilterUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.applicable.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This component calculates the commission on a specified set of contracts, defined in parameter
 * parmApplicableContracts, according to a rule specified in parameter parmCommissionStrategy.
 * <p/>
 * Implementation Note: the incoming packets are first filtered to determine which are applicable
 * to the commission calculation. The outUnderwritingInfo packet stream is split into those packets
 * to which the commission applies, which get modified, and those which don't apply and are not modified.
 * <p/>
 * If you want all outPackets, just wire outUnderwritingInfoModified & outUnderwritingInfoUnmodified
 * to the same inChannel, and be aware that the order may not match that of inUnderwritingInfo.
 *
 * @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration (dot) com
 */
public class Commission extends Component {
    
    private SimulationScope simulationScope;

    private PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<CededUnderwritingInfoPacket> inUnderwritingInfo =
            new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);

    private PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoModified =
            new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoUnmodified =
            new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);

    private ICommissionStrategy parmCommissionStrategy = CommissionStrategyType.getStrategy(
            CommissionStrategyType.FIXEDCOMMISSION, ArrayUtils.toMap(new Object[][]{{"commission", 0d}}));

    private IApplicableStrategy parmApplicableStrategy = ApplicableStrategyType.getStrategy(
            ApplicableStrategyType.NONE, Collections.emptyMap());

    protected void doCalculation() {
        if (parmApplicableStrategy instanceof NoApplicableStrategy) {
            outUnderwritingInfoUnmodified.addAll(inUnderwritingInfo);
        }
        else {
            boolean isFirstPeriod = simulationScope.getIterationScope().getPeriodScope().getCurrentPeriod() == 0;

            if (parmApplicableStrategy instanceof ContractApplicableStrategy) {
                List<IReinsuranceContractMarker> applicableContracts = (List<IReinsuranceContractMarker>) ((IContractApplicableStrategy) parmApplicableStrategy)
                        .getApplicableContracts().getValuesAsObjects(0, true);
                PacketList<ClaimCashflowPacket> filteredClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
                filteredClaims.addAll(ClaimFilterUtils.filterClaimsByContract(inClaims, applicableContracts));

                List<CededUnderwritingInfoPacket> applicableUnderwritingInfo = new ArrayList<CededUnderwritingInfoPacket>();
                List<CededUnderwritingInfoPacket> irrelevantUnderwritingInfo = new ArrayList<CededUnderwritingInfoPacket>();

                UnderwritingInfoUtils.segregateUnderwritingInfoByContract(inUnderwritingInfo, applicableContracts,
                        applicableUnderwritingInfo, irrelevantUnderwritingInfo);
                parmCommissionStrategy.getCalculator().calculateCommission(filteredClaims, applicableUnderwritingInfo, isFirstPeriod, true);
                outUnderwritingInfoModified.addAll(applicableUnderwritingInfo);
                outUnderwritingInfoUnmodified.addAll(irrelevantUnderwritingInfo);
            }
            else if (parmApplicableStrategy instanceof AllApplicableStrategy) {
                parmCommissionStrategy.getCalculator().calculateCommission(inClaims, inUnderwritingInfo, isFirstPeriod, true);
                outUnderwritingInfoModified.addAll(inUnderwritingInfo);
            }
            else {
                throw new NotImplementedException("['Commission.notImplemented','" + parmApplicableStrategy.toString() + "']");
            }
        }
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }

    public PacketList<ClaimCashflowPacket> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<ClaimCashflowPacket> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<CededUnderwritingInfoPacket> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<CededUnderwritingInfoPacket> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<CededUnderwritingInfoPacket> getOutUnderwritingInfoModified() {
        return outUnderwritingInfoModified;
    }

    public void setOutUnderwritingInfoModified(PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoModified) {
        this.outUnderwritingInfoModified = outUnderwritingInfoModified;
    }

    public PacketList<CededUnderwritingInfoPacket> getOutUnderwritingInfoUnmodified() {
        return outUnderwritingInfoUnmodified;
    }

    public void setOutUnderwritingInfoUnmodified(PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoUnmodified) {
        this.outUnderwritingInfoUnmodified = outUnderwritingInfoUnmodified;
    }

    public ICommissionStrategy getParmCommissionStrategy() {
        return parmCommissionStrategy;
    }

    public void setParmCommissionStrategy(ICommissionStrategy parmCommissionStrategy) {
        this.parmCommissionStrategy = parmCommissionStrategy;
    }

    public IApplicableStrategy getParmApplicableStrategy() {
        return parmApplicableStrategy;
    }

    public void setParmApplicableStrategy(IApplicableStrategy parmApplicableStrategy) {
        this.parmApplicableStrategy = parmApplicableStrategy;
    }
}
