package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractSingleClaimsGeneratorStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExternalValuesStrategy extends AbstractSingleClaimsGeneratorStrategy {

    static Log LOG = LogFactory.getLog(ExternalValuesStrategy.class);

    private ExposureBase claimsSizeBase = ExposureBase.ABSOLUTE;
    private IExternalValuesStrategy values = ExternalValuesType.getDefault();
    private FrequencySeverityClaimType produceClaim = FrequencySeverityClaimType.SINGLE;


    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.EXTERNAL_VALUES;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put("claimsSizeBase", claimsSizeBase);
        parameters.put("values", values);
        parameters.put("produceClaim", produceClaim);
        return parameters;
    }

    /**
     *
     * @param baseClaims this list is needed in order to calculate the number of required claims after calculateDependantClaimsWithContractBase
     *                   has been executed i.e. if external severity information is provided and only the missing claim
     *                   number needs to be generated.
     * @param uwInfos
     * @param severityFactors
     * @param uwInfosFilterCriteria
     * @param frequencyFactorsPackets ignored in this case
     * @param periodScope
     * @param systematicFrequencies
     * @param filterCriteria
     * @return
     */
    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
                                          List<Factors> severityFactors, List uwInfosFilterCriteria,
                                          List<FactorsPacket> frequencyFactorsPackets, PeriodScope periodScope,
                                          List<SystematicFrequencyPacket> systematicFrequencies, IPerilMarker filterCriteria) {
        int iteration = ((ClaimsGenerator) filterCriteria).getIterationScope().getCurrentIteration();
        return values.generateClaims(baseClaims, uwInfos, severityFactors, uwInfosFilterCriteria, periodScope, claimType(),
            claimsSizeBase, dateGenerator, iteration);
    }

    @Override
    public void lazyInitClaimsSizeGenerator() {
        LOG.debug("This function is not required");
    }

    public List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                           List<EventDependenceStream> eventStreams,
                                           IPerilMarker filterCriteria, PeriodScope periodScope) {
        return new ArrayList<ClaimRoot>();
    }

    public ClaimType claimType() {
        return produceClaim == FrequencySeverityClaimType.SINGLE ? ClaimType.SINGLE : ClaimType.AGGREGATED_EVENT;
    }

}
