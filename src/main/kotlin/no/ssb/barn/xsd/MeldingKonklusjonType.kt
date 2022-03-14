package no.ssb.barn.xsd

import no.ssb.barn.converter.LocalDateTimeAdapter
import no.ssb.barn.util.TypeUtils
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.xml.bind.annotation.*
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "MeldingKonklusjon",
    propOrder = ["sluttDato", "kode"]
)
data class MeldingKonklusjonType(
    @field:XmlAttribute(name = "SluttDato", required = true)
    @field:XmlSchemaType(name = "dateTime")
    @field:XmlJavaTypeAdapter(
        LocalDateTimeAdapter::class
    )
    var sluttDato: ZonedDateTime = ZonedDateTime.now(),

    @field:XmlAttribute(name = "Kode", required = true)
    var kode: String? = getCodes(LocalDate.now())
        .take(1)
        .map { it.code }
        .firstOrNull()
) {
    companion object {

        @JvmStatic
        fun getCodes(date: LocalDate): List<CodeListItem> =
            TypeUtils.getCodes(date, codeList)

        private val validFrom: LocalDate = LocalDate.parse("2013-01-01")

        private val codeList = mapOf(
            Pair("1", "Henlagt"),
            Pair(
                "2",
                "Ikke henlagt – konklusjonsdato melding (eventuelt 7 dager etter mottatt melding) er startdato undersøkelse"
            ),
            Pair("3", "Henlagt pga. aktive tiltak"),
            Pair("4", "Melding i pågående undersøkelse")
        )
            .map { CodeListItem(it.key, it.value, validFrom) }
    }
}
