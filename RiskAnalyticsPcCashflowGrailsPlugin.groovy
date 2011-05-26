import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTableConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.indexing.AnnualIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FrequencyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.PremiumIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.PolicyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.segment.SegmentPortion
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.validation.PMLClaimsGeneratorStrategyValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ValidatorRegistry

class RiskAnalyticsPcCashflowGrailsPlugin {
    // the plugin version
    def version = "0.1.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.7 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Intuitive Collaboration AG"
    def authorEmail = "info (at) intuitive-collaboration (dot) com"
    def title = "Property Casualty Library for Cashflow Models"
    def description = '''\\
'''

    // URL to the plugin's documentation
    def documentation = "http://www.pillarone.org"

    def groupId = "org.pillarone"


    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        ConstraintsFactory.registerConstraint(new AnnualIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new PolicyIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new PremiumIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new FrequencyIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new PatternTableConstraints())
        ConstraintsFactory.registerConstraint(new SegmentPortion())

        ValidatorRegistry.addValidator(new PMLClaimsGeneratorStrategyValidator())
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
