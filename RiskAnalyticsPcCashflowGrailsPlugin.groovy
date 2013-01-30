import org.pillarone.riskanalytics.core.output.DrillDownMode
import org.pillarone.riskanalytics.core.packets.Packet
import org.pillarone.riskanalytics.domain.pc.cf.output.SplitAndFilterCollectionModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTableConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.indexing.AnnualIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FrequencyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.PremiumIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.PolicyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.DeterministicIndexTableConstraints

import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.validation.PMLClaimsGeneratorStrategyValidator
import org.pillarone.riskanalytics.core.parameterization.validation.ValidatorRegistry
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ContractFinancialsPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixStructureContraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.validation.CoverAttributeValidator
import org.pillarone.riskanalytics.domain.pc.cf.segment.FinancialsPacket
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.DateTimeConstraints
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry
import org.pillarone.riskanalytics.domain.pc.cf.pattern.validation.RecoveryPatternStrategyValidator
import org.pillarone.riskanalytics.domain.pc.cf.pattern.validation.PatternStrategyValidator
import org.pillarone.riskanalytics.domain.pc.cf.indexing.LinkRatioIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReservesIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.validation.ClaimsGeneratorScalingValidator
import org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.validation.RiskAllocationValidator
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.dependency.validation.CopulaValidator
import org.pillarone.riskanalytics.domain.pc.cf.dependency.validation.MultipleProbabilitiesCopulaValidator
import org.pillarone.riskanalytics.core.output.aggregation.PacketAggregatorRegistry
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket as CCP
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimPacketAggregator
import org.pillarone.riskanalytics.domain.pc.cf.discounting.YieldCurveTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitPerSourceCollectingModeStrategy
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.validation.XLStrategyValidator
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacketAggregator
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractContraints
import org.pillarone.riskanalytics.domain.utils.constraint.SegmentPortion
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimateClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimateReportedPaidClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimatePaidClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitByInceptionDateCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregatePremiumReserveRiskCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateIncludingPremiumReserveRiskCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregatePremiumReserveRiskTriangleCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.structure.validation.ClaimTypeStructuringValidator
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitPerSourceReducedCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimateReportedClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.indexing.RunOffIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.output.SingleUltimatePaidClaimCollectingModeStrategy

class RiskAnalyticsPcCashflowGrailsPlugin {
    // the plugin version
    def version = "0.5.15-kti"
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
        ConstraintsFactory.registerConstraint(new LinkRatioIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new DeterministicIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new PolicyIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new PremiumIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new FrequencyIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new RunOffIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new ReservesIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new PatternTableConstraints())
        ConstraintsFactory.registerConstraint(new CoverMap())
        ConstraintsFactory.registerConstraint(new MatrixStructureContraints())
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
        ConstraintsFactory.registerConstraint(new DateTimeConstraints())
        ConstraintsFactory.registerConstraint(new ClaimTypeSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new YieldCurveTableConstraints())
        ConstraintsFactory.registerConstraint(new SegmentPortion())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractContraints())

        ValidatorRegistry.addValidator(new PMLClaimsGeneratorStrategyValidator())
        ValidatorRegistry.addValidator(new PatternStrategyValidator())
        ValidatorRegistry.addValidator(new RecoveryPatternStrategyValidator())
        ValidatorRegistry.addValidator(new ClaimsGeneratorScalingValidator())
        ValidatorRegistry.addValidator(new RiskAllocationValidator())
        ValidatorRegistry.addValidator(new CopulaValidator())
        ValidatorRegistry.addValidator(new MultipleProbabilitiesCopulaValidator())
        ValidatorRegistry.addValidator(new XLStrategyValidator())
        ValidatorRegistry.addValidator(new ClaimTypeStructuringValidator())
        ValidatorRegistry.addValidator(new CoverAttributeValidator())

        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.claim.generator.validation.pMLClaimsGeneratorStrategyValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.pattern.validation.patternStrategyValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.pattern.validation.recoveryPatternStrategyValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.claim.generator.validation.claimsGeneratorScalingValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.validation.riskAllocationValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.dependency.validation.copulaValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.dependency.validation.multipleProbabilitiesCopulaValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.validation.xlStrategyValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.structure.validation.claimTypeStructuringValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.structure.validation.claimTypeStructuringValidator")
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.VALIDATION, "org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.validation.coverAttributeValidator")

        // add resource bundle for exceptions
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.RESOURCE, "org.pillarone.riskanalytics.domain.pc.cf.exceptionResources")
        // doc urls
        ResourceBundleRegistry.addBundle(ResourceBundleRegistry.HELP, "org/pillarone/riskanalytics/domain/pc/cf/ComponentHelp")

        PacketAggregatorRegistry.registerAggregator(CCP, new ClaimPacketAggregator())
        PacketAggregatorRegistry.registerAggregator(UnderwritingInfoPacket, new UnderwritingInfoPacketAggregator())

        CollectingModeFactory.registerStrategy(new AggregateSplitPerSourceCollectingModeStrategy())
        CollectingModeFactory.registerStrategy(new AggregateSplitPerSourceReducedCollectingModeStrategy())

        CollectingModeFactory.registerStrategy(new AggregateUltimateClaimCollectingModeStrategy())
        CollectingModeFactory.registerStrategy(new AggregateUltimateReportedPaidClaimCollectingModeStrategy())
        CollectingModeFactory.registerStrategy(new AggregateUltimateReportedClaimCollectingModeStrategy())
        CollectingModeFactory.registerStrategy(new AggregateUltimatePaidClaimCollectingModeStrategy())

        CollectingModeFactory.registerStrategy(new SingleUltimatePaidClaimCollectingModeStrategy())
        // PMO-2231

        def f = [
                paid_inc: CCP.PAID_INDEXED,
                paid_cum: CCP.PAID_CUMULATIVE_INDEXED,
                cres_inc: CCP.CHANGES_IN_RESERVES_INDEXED,
                cres_cum: CCP.RESERVES_INDEXED,
                IBNR_inc: CCP.CHANGES_IN_IBNR_INDEXED,
                IBNR_cum: CCP.IBNR_INDEXED,
                outst_inc: CCP.CHANGES_IN_OUTSTANDING_INDEXED,
                outst_cum: CCP.OUTSTANDING_INDEXED,
                rep_inc: CCP.REPORTED_INDEXED,
                rep_cum: CCP.REPORTED_CUMULATIVE_INDEXED,
                total_uix_inc: CCP.ULTIMATE,
                total_inc: CCP.TOTAL_INCREMENTAL_INDEXED,
                total_cum: CCP.TOTAL_CUMULATIVE_INDEXED,
                rb_claim_premium: CCP.PREMIUM_RISK_BASE,
                rb_claim_reserves: CCP.RESERVE_RISK_BASE,
                rb_claim_total: CCP.PREMIUM_AND_RESERVE_RISK_BASE,
                rb_fin_premium: FinancialsPacket.GROSS_PREMIUM_RISK,
                rb_fin_reserves: FinancialsPacket.GROSS_RESERVE_RISK,
                rb_fin_total: FinancialsPacket.GROSS_PREMIUM_RESERVE_RISK]
        def fields = [f.paid_inc, f.paid_cum, f.cres_inc, f.cres_cum, f.IBNR_inc, f.IBNR_cum, f.outst_inc, f.outst_cum,
                f.rep_inc, f.rep_cum, f.total_uix_inc, f.total_inc, f.total_cum, f.rb_claim_premium, f.rb_claim_reserves,
                f.rb_claim_total, f.rb_fin_premium, f.rb_fin_reserves, f.rb_fin_total]

        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([], []))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_PERIOD], []))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE], []))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE, DrillDownMode.BY_PERIOD], []))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE, DrillDownMode.BY_PERIOD], [0, 3, 5, 7, 8, 10, 11, 12, 13, 14, 15].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE, DrillDownMode.BY_PERIOD], [0, 3, 5, 7, 8, 10, 11, 12].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE, DrillDownMode.BY_PERIOD], [7, 11].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE], [7, 11].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_PERIOD], [7, 11].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_PERIOD], [16, 17, 18].collect { fields[it] }, [FinancialsPacket, ContractFinancialsPacket]))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([], [7, 11].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([], [16, 17, 18].collect { fields[it] }, [FinancialsPacket, ContractFinancialsPacket]))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE, DrillDownMode.BY_PERIOD], [7, 11, 13, 14, 15].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE], [7, 11, 13, 14, 15].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_PERIOD], [7, 11, 13, 14, 15].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([], [7, 11, 13, 14, 15].collect { fields[it] }))
        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([], [7, 10, 11, 12, 13, 14, 15].collect { fields[it] }))

        CollectingModeFactory.registerStrategy(new SplitAndFilterCollectionModeStrategy([DrillDownMode.BY_SOURCE, DrillDownMode.BY_PERIOD], [CCP.REPORTED_INDEXED, CCP.PAID_INDEXED]))
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
