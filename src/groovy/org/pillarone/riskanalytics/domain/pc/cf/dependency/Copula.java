package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType;
import org.pillarone.riskanalytics.domain.utils.math.copula.ICopulaStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
 public class Copula extends Component {

    private PacketList<Frequency> inNumber = new PacketList<Frequency>(Frequency.class);
    private PacketList<DependenceStream> outProbabilities = new PacketList<DependenceStream>(DependenceStream.class);

    ICopulaStrategy parmCopulaStrategy = CopulaType.getDefault();

   public void doCalculation() {
        if (isReceiverWired(inNumber) || inNumber.size() > 0) {
            for (Frequency frequency: inNumber) {
                for (int i = 0; i < frequency.value; i++) {
                    outProbabilities.add(buildDependenceStream());
                }
            }
        }
        else {
            outProbabilities.add(buildDependenceStream());
        }
    }

    private DependenceStream buildDependenceStream() {
        return new DependenceStream(getTargetNames(), getRandomVector());
    }

    protected List<Number> getRandomVector() {
        return parmCopulaStrategy.getRandomVector();
    }

    protected List<String> getTargetNames() {
        return parmCopulaStrategy.getTargetNames();
    }

    public PacketList<Frequency> getInNumber() {
        return inNumber;
    }

    public void setInNumber(PacketList<Frequency> inNumber) {
        this.inNumber = inNumber;
    }

    public PacketList<DependenceStream> getOutProbabilities() {
        return outProbabilities;
    }

    public void setOutProbabilities(PacketList<DependenceStream> outProbabilities) {
        this.outProbabilities = outProbabilities;
    }
}