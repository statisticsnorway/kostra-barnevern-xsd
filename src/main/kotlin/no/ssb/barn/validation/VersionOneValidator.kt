package no.ssb.barn.validation

import no.ssb.barn.converter.BarnevernConverter
import no.ssb.barn.report.ValidationReport
import no.ssb.barn.report.WarningLevel
import no.ssb.barn.validation.rule.ReporterUrbanDistrictNumberAndName
import no.ssb.barn.validation.rule.XsdRule
import no.ssb.barn.validation.rule.cases.*
import no.ssb.barn.validation.rule.decision.DecisionStartDateAfterEndDate
import no.ssb.barn.validation.rule.investigation.*
import no.ssb.barn.validation.rule.measure.*
import no.ssb.barn.validation.rule.message.*
import no.ssb.barn.validation.rule.plan.PlanEndDateAfterCaseEndDate
import no.ssb.barn.validation.rule.plan.PlanStartDateAfterEndDate
import no.ssb.barn.validation.rule.plan.PlanStartDateBeforeCaseStartDate

class VersionOneValidator : ValidatorContract {

    private val preCheckRules = listOf(
        XsdRule(xsdResourceName = "Barnevern.xsd")
    )

    private val rules = listOf(
        ReporterUrbanDistrictNumberAndName(),
        CaseAgeAboveEighteenAndMeasures(),
        CaseAgeAboveTwentyFive(),
        CaseEndDateAfterStartDate(),
        CaseHasContent(),
        CaseSocialSecurityId(),
        CaseSocialSecurityIdAndDuf(),
        DecisionStartDateAfterEndDate(),
        InvestigationConcludedMissingDecision(),
        InvestigationDecisionClarificationRequired(),
        InvestigationDecisionMissingClarification(),
        InvestigationDueDatePassedConclusionRequired(),
        InvestigationEndDateAfterCaseEndDate(),
        InvestigationProcessingTimePassedDueDate(),
        InvestigationRelatedFromMessage(),
        InvestigationStartDateAfterEndDate(),
        InvestigationStartDateBeforeCaseStartDate(),
        MeasureLegalBasisAgeAboveEighteenNoMeasure(),
        MeasureLegalBasisValidCode(),
        MeasureLegalBasisWithEndDateClarificationRequired(),
        MeasureAgeAboveElevenAndInSfo(),
        MeasureAgeAboveSevenAndInKindergarten(),
        MeasureClarificationRequired(),
        MeasureStartDateAfterEndDate(),
        MeasureEndDateAfterCaseEndDate(),
        MeasureMultipleAllocationsWithinPeriod(),
        MeasureRepealClarificationRequired(),
        MeasureStartDateAfterIndividStartDate(),
        MessageCaseContentMissingClarification(),
        MessageEndDateAfterCaseEndDate(),
        MessageMissingCaseContent(),
        MessageMissingReporters(),
        MessageProcessingTimeOverdue(),
        MessageReporterMissingClarification(),
        MessageStartDateAfterEndDate(),
        MessageStartDateBeforeCaseStartDate(),
        PlanStartDateAfterEndDate(),
        PlanEndDateAfterCaseEndDate(),
        PlanStartDateBeforeCaseStartDate()
    )

    override fun validate(context: ValidationContext): ValidationReport {

        val reportEntries = preCheckRules.asSequence()
            .mapNotNull { it.validate(context) }
            .flatten()
            .toList()
            .ifEmpty {
                val innerContext = ValidationContext(
                    context.messageId,
                    context.xml,
                    BarnevernConverter.unmarshallXml(context.xml)
                )
                rules.asSequence()
                    .mapNotNull { it.validate(innerContext) }
                    .flatten()
                    .toList()
            }

        return ValidationReport(
            messageId = context.messageId,
            reportEntries = reportEntries,
            severity = reportEntries.asSequence()
                .map { it.warningLevel }
                .maxByOrNull { it.ordinal } ?: WarningLevel.OK
        )
    }
}