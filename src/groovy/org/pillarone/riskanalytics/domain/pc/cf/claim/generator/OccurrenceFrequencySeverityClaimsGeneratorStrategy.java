package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;

import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class OccurrenceFrequencySeverityClaimsGeneratorStrategy extends FrequencySeverityClaimsGeneratorStrategy {

    private RandomDistribution occurrenceDateDistribution;

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY;
    }

    public Map getParameters() {
        Map<String, Object> parameters = super.getParameters();
        parameters.put(OCCURRENCE_DATE_DISTRIBUTION, occurrenceDateDistribution);
        return parameters;
    }

    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
                                          List<Factors> severityFactors, List uwInfosFilterCriteria,
                                          List<FactorsPacket> factorPackets, PeriodScope periodScope,
                                          List<SystematicFrequencyPacket> systematicFrequencies, IPerilMarker filterCriteria) {
        setDateGenerator(occurrenceDateDistribution);
        return super.generateClaims(baseClaims, uwInfos, severityFactors, uwInfosFilterCriteria, factorPackets,
                                    periodScope, systematicFrequencies, filterCriteria);
    }

}