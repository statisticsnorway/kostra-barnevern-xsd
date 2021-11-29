package no.ssb.barn.validation.rule

import no.ssb.barn.framework.AbstractRule
import no.ssb.barn.framework.ValidationContext
import no.ssb.barn.report.ReportEntry
import no.ssb.barn.report.WarningLevel
import no.ssb.barn.xsd.TiltakType

class TodoMeasureEndDateRequired : AbstractRule(
    WarningLevel.ERROR,
    "Tiltak Kontroll 2d: Avslutta 31 12 medfører at sluttdato skal være satt på",
    TiltakType::class.java.simpleName
) {
    override fun validate(context: ValidationContext): List<ReportEntry>? {
        TODO("Not yet implemented")
    }
}