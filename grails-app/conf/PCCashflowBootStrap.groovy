import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitPerSourceCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimateClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimateReportedPaidClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimatePaidClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitByInceptionDateCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateIncludingPremiumReserveRiskCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregatePremiumReserveRiskCollectingModeStrategy

class PCCashflowBootStrap {

    def init = {servletContext ->

        CollectorMapping.withTransaction { status ->
            def c = new CollectorMapping(collectorName: AggregateSplitPerSourceCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: AggregateSplitByInceptionDateCollectingModeStrategy.IDENTIFIER)
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: AggregateUltimateClaimCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: AggregateUltimateReportedPaidClaimCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: AggregateUltimatePaidClaimCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: AggregateIncludingPremiumReserveRiskCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: AggregatePremiumReserveRiskCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
        }

    }

    def destroy = {
    }
}