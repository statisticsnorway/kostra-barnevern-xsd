package no.ssb.barn.xsd

import jakarta.xml.bind.annotation.*
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
import no.ssb.barn.codelists.CodeListItem
import no.ssb.barn.converter.LocalDateAdapter
import no.ssb.barn.util.TypeUtils
import java.time.LocalDate

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Status", propOrder = ["endretDato", "kode"])
data class VedtakStatusType(
    @field:XmlAttribute(name = "EndretDato", required = true)
    @field:XmlSchemaType(name = "date")
    @field:XmlJavaTypeAdapter(
        LocalDateAdapter::class
    )
    var endretDato: LocalDate = LocalDate.now(),

    @field:XmlAttribute(name = "Kode", required = true)
    var kode: String = getCodes(LocalDate.of(2022, 1, 1))[0].code
) {
    companion object {
        private val validFrom: LocalDate = LocalDate.parse("2022-01-01")
        private val codeList =
            mapOf(
                Pair(
                    "1",
                    "Godkjent"
                ),
                Pair(
                    "2",
                    "Begjæring oversendt nemnd"
                ),
                Pair(
                    "3",
                    "Utgår / Bortfalt etter BVL"
                ),
                Pair(
                    "4",
                    "Avslått / Avsluttet"
                )
            )
                .map { CodeListItem(it.key, it.value, validFrom) }

        @JvmStatic
        fun getCodes(date: LocalDate): List<CodeListItem> =
            TypeUtils.getCodes(date, codeList)
    }
}
