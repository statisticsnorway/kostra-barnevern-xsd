package no.ssb.barn.xsd

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import no.ssb.barn.TestUtils.EMPTY_DATE_ERROR
import no.ssb.barn.TestUtils.INVALID_DATE_ERROR
import no.ssb.barn.TestUtils.buildBarnevernXml
import no.ssb.barn.toStreamSource
import no.ssb.barn.util.ValidationUtils.getSchemaValidator
import org.xml.sax.SAXException

class EttervernKonklusjonTypeTest : BehaviorSpec({

    given("misc EttervernKonklusjonType XML") {

        /** make sure it's possible to make a valid test XML */
        `when`("valid XML, expect no exceptions") {
            shouldNotThrowAny {
                getSchemaValidator().validate(
                    buildEttervernKonklusjonTypeXml(
                        "<Konklusjon SluttDato=\"2022-11-14\" Kode=\"1\" />"
                    ).toStreamSource()
                )
            }
        }

        forAll(
            /** SluttDato */
            row(
                "missing SluttDato",
                "<Konklusjon Kode=\"1\" />",
                "cvc-complex-type.4: Attribute 'SluttDato' must appear on element 'Konklusjon'."
            ),
            row(
                "empty SluttDato",
                "<Konklusjon SluttDato=\"\" Kode=\"1\" />",
                EMPTY_DATE_ERROR
            ),
            row(
                "invalid SluttDato",
                "<Konklusjon SluttDato=\"2022\" Kode=\"1\" />",
                INVALID_DATE_ERROR
            ),

            /** Kode */
            row(
                "missing Kode",
                "<Konklusjon SluttDato=\"2022-11-14\" />",
                "cvc-complex-type.4: Attribute 'Kode' must appear on element 'Konklusjon'."
            ),
            row(
                "empty Kode",
                "<Konklusjon SluttDato=\"2022-11-14\" Kode=\"\" />",
                "cvc-length-valid: Value '' with length = '0' is not facet-valid with respect to length '1' " +
                        "for type '#AnonType_KodeKonklusjonEttervernType'."
            ),
            row(
                "invalid Kode",
                "<Konklusjon SluttDato=\"2022-11-14\" Kode=\"42\" />",
                "cvc-length-valid: Value '42' with length = '2' is not facet-valid with respect to length '1' " +
                        "for type '#AnonType_KodeKonklusjonEttervernType'."
            )
        ) { description, partialXml, expectedError ->
            `when`(description) {
                val thrown = shouldThrow<SAXException> {
                    getSchemaValidator().validate(buildEttervernKonklusjonTypeXml(partialXml).toStreamSource())
                }

                then("thrown should be as expected") {
                    thrown.message shouldBe expectedError
                }
            }
        }
    }
}) {
    companion object {
        fun buildEttervernKonklusjonTypeXml(konklusjonXml: String): String = buildBarnevernXml(
            "<Ettervern Id=\"6ee9bf92-7a4e-46ef-a2dd-b5a3a0a9ee2e\" TilbudSendtDato=\"2022-11-14\">" +
                    konklusjonXml + "</Ettervern>"
        )
    }
}