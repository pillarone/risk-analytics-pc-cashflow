package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.validation

import grails.test.GrailsUnitTestCase
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.validation.ParameterValidation
import org.pillarone.riskanalytics.core.parameterization.validation.ValidationType
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelector
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

class CoverAttributeValidatorTests extends GrailsUnitTestCase {
    List<ParameterHolder> parameters

    @Override
    protected void setUp() {
        super.setUp()
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
        ConstraintsFactory.registerConstraint(new CoverMap())
        setupParamCover()
    }

    private void setupParamCover(List selection = [[''], [''], [''], [''], [''], ['ANY']]) {
        def flexibleCover = new ConstrainedMultiDimensionalParameter(selection, ['', '', '', '', '', ''], ConstraintsFactory.getConstraints(CoverMap.IDENTIFIER))
        parameters = new ArrayList<ParameterHolder>()
        def benefitContracts = new ComboBoxTableMultiDimensionalParameter([], [''], IReinsuranceContractMarker)
        ParameterObjectParameterHolder holder = new ParameterObjectParameterHolder("subContract2:parmCover", 1, new MatrixCoverAttributeStrategy(flexibleCover: flexibleCover, benefitContracts: benefitContracts))
        parameters << holder

    }

    void testValidateNoErrors_noContractsSelected() {
        setupParamCover()
        assert 0 == new CoverAttributeValidator().validate(parameters).size()
    }

    void testValidateNoErrors_noContractsSelected_multipleLines() {
        setupParamCover([['', ''], ['', ''], ['', ''], ['', ''], ['', ''], ['ANY', 'SINGLE']])
        List result = new CoverAttributeValidator().validate(parameters)
        assert 0 == result.size()
    }

    void testValidateNoErrors_otherNetContractSelected() {
        setupParamCover([['subContract1'], [''], [''], [''], [''], ['ANY']])
        assert 0 == new CoverAttributeValidator().validate(parameters).size()
    }

    void testValidateNoErrors_otherCededContractSelected() {
        setupParamCover([[''], ['subContract1'], [''], [''], [''], ['ANY']])
        assert 0 == new CoverAttributeValidator().validate(parameters).size()
    }

    void testValidateErrors_sameCededContractSelected() {
        setupParamCover([[''], ['subContract2'], [''], [''], [''], ['ANY']])
        List result = new CoverAttributeValidator().validate(parameters)
        assert 1 == result.size()
        ParameterValidation validationError = result[0]
        assert ValidationType.ERROR == validationError.getValidationType()
        assert CoverAttributeValidator.SAME_CONTRACT_SELECTED == validationError.msg
        assert 'Contract 2' == validationError.args[0]
    }

    void testValidateErrors_sameCededAndSameNetContractSelected() {
        setupParamCover([['subContract2'], ['subContract2'], [''], [''], [''], ['ANY']])
        List result = new CoverAttributeValidator().validate(parameters)
        assert 2 == result.size()
        ParameterValidation validationError = result[0]
        assert ValidationType.ERROR == validationError.getValidationType()
        assert CoverAttributeValidator.SAME_CONTRACT_SELECTED == validationError.msg
        assert 'Contract 2' == validationError.args[0]
        validationError = result[1]
        assert ValidationType.ERROR == validationError.getValidationType()
        assert CoverAttributeValidator.IDENTICAL_NET_AND_CEDED_CONTRACTS == validationError.msg
        assert 'Contract 2' == validationError.args[0]
    }

    void testValidateErrors_sameNetContractMultipleLines() {
        setupParamCover([['', 'subContract2'], ['', ''], ['', ''], ['', ''], ['', ''], ['ANY', 'ANY']])
        List result = new CoverAttributeValidator().validate(parameters)
        assert 1 == result.size()
        ParameterValidation validationError = result[0]
        assert ValidationType.ERROR == validationError.getValidationType()
        assert CoverAttributeValidator.SAME_CONTRACT_SELECTED == validationError.msg
        assert 'Contract 2' == validationError.args[0]
    }

    void testMultipleIdenticalFilters() {
        setupParamCover([['', ''], ['', ''], ['', ''], ['', ''], ['', ''], ['ANY', 'ANY']])
        List result = new CoverAttributeValidator().validate(parameters)
        assert 1 == result.size()
        ParameterValidation validationError = result[0]
        assert ValidationType.ERROR == validationError.getValidationType()
        assert CoverAttributeValidator.IDENTICAL_FILTER == validationError.msg
    }

    void testSameNetAndCededContract(){
        setupParamCover([[''], [''], [''], [''], [''], ['ANY']])
        List result = new CoverAttributeValidator().validate(parameters)
        assert 0 == result.size()
        setupParamCover([['subContract1'], ['subContract1'], [''], [''], [''], ['ANY']])
        result = new CoverAttributeValidator().validate(parameters)
        assert 1 == result.size()
        ParameterValidation validationError = result[0]
        assert ValidationType.ERROR == validationError.getValidationType()
        assert CoverAttributeValidator.IDENTICAL_NET_AND_CEDED_CONTRACTS == validationError.msg
        assert 'Contract 1' == validationError.args[0]
    }
}
