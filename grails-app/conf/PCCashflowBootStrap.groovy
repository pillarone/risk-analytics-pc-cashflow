import org.pillarone.riskanalytics.core.output.CollectorMapping
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitPerSourceCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimateClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimateReportedPaidClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimatePaidClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitByInceptionDateCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateIncludingPremiumReserveRiskCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregatePremiumReserveRiskCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregatePremiumReserveRiskTriangleCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitPerSourceReducedCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateUltimateReportedClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.SingleUltimatePaidClaimCollectingModeStrategy
import org.pillarone.riskanalytics.domain.pc.cf.output.SplitAndFilterCollectionModeStrategy

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
            c = new CollectorMapping(collectorName: AggregatePremiumReserveRiskTriangleCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: AggregateSplitPerSourceReducedCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: AggregateUltimateReportedClaimCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: SingleUltimatePaidClaimCollectingModeStrategy.IDENTIFIER);
            if (CollectorMapping.find(c) == null)
                c.save()
            c = new CollectorMapping(collectorName: new SplitAndFilterCollectionModeStrategy([SplitAndFilterCollectionModeStrategy.SPLIT_BY_SOURCE, SplitAndFilterCollectionModeStrategy.SPLIT_BY_PERIOD],[ClaimCashflowPacket.REPORTED_INDEXED, ClaimCashflowPacket.PAID_INDEXED]).identifier);
            if (CollectorMapping.find(c) == null)
                c.save()
        }

    }

    def destroy = {
    }
}