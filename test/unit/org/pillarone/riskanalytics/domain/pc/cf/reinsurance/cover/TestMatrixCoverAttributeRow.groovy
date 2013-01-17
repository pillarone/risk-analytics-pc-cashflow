package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelector
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment

class TestMatrixCoverAttributeRow {

    public static def getParameters(List selectedParams = [[''], [''], [''], [''], [''], ['ANY']],
                                       List<ReinsuranceContract> netContracts = [],
                                       List<ReinsuranceContract> cededContracts = [],
                                       List<LegalEntity> legalEntities = [],
                                       List<Segment> segments = [],
                                       List<ClaimsGenerator> perils = []) {
        def params = new ConstrainedMultiDimensionalParameter(selectedParams,
                [CoverMap.CONTRACT_NET_OF, CoverMap.CONTRACT_CEDED_OF, CoverMap.LEGAL_ENTITY,
                        CoverMap.SEGMENTS, CoverMap.GENERATORS, CoverMap.LOSS_KIND_OF],
                ConstraintsFactory.getConstraints(CoverMap.IDENTIFIER))
        (0..4).each {
            params.comboBoxValues[it] = [:]
            params.comboBoxValues[it].put('', null)
        }
        netContracts.each {
            params.comboBoxValues[0].put(it.name, it)
        }
        cededContracts.each {
            params.comboBoxValues[1].put(it.name, it)
        }
        legalEntities.each {
            params.comboBoxValues[2].put(it.name, it)
        }
        segments.each {
            params.comboBoxValues[3].put(it.name, it)
        }
        perils.each {
            params.comboBoxValues[4].put(it.name, it)
        }
        def claimTypeColumnValues = [:]
        ClaimTypeSelector.values().each {
            claimTypeColumnValues.put(it.name(), it)
        }
        params.comboBoxValues[5] = claimTypeColumnValues

        return params

    }
}
