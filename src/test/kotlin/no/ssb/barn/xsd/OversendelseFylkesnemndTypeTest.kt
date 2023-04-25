package no.ssb.barn.xsd

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import no.ssb.barn.TestUtils.EMPTY_DATE_ERROR
import no.ssb.barn.TestUtils.EMPTY_ID_ERROR
import no.ssb.barn.TestUtils.INVALID_DATE_ERROR
import no.ssb.barn.TestUtils.INVALID_ID_ERROR
import no.ssb.barn.TestUtils.LOVHJEMMEL_XML
import no.ssb.barn.TestUtils.buildBarnevernXml
import no.ssb.barn.toStreamSource
import no.ssb.barn.util.ValidationUtils.getSchemaValidator
import org.xml.sax.SAXException

class OversendelseFylkesnemndTypeTest : BehaviorSpec({

    Given("misc OversendelseFylkesnemnd XML") {

        /** make sure it's possible to make a valid test XML */
        When("valid XML, expect no exceptions") {
            shouldNotThrowAny {
                getSchemaValidator().validate(
                    buildOversendelseFylkesnemndXml(
                        "<OversendelseFylkesnemnd Id=\"6ee9bf92-7a4e-46ef-a2dd-b5a3a0a9ee2e\" " +
                                "MigrertId=\"4242\" StartDato=\"2022-11-14\">"
                    ).toStreamSource()
                )
            }
        }

        forAll(
            /** Id */
            row(
                "missing Id",
                "<OversendelseFylkesnemnd StartDato=\"2022-11-14\">",
                "cvc-complex-type.4: Attribute 'Id' must appear on element 'OversendelseFylkesnemnd'."
            ),
            row(
                "empty Id",
                "<OversendelseFylkesnemnd Id=\"\" StartDato=\"2022-11-14\">",
                EMPTY_ID_ERROR
            ),
            row(
                "invalid Id",
                "<OversendelseFylkesnemnd Id=\"42\" StartDato=\"2022-11-14\">",
                INVALID_ID_ERROR
            ),

            /** MigrertId */
            row(
                "empty MigrertId",
                "<OversendelseFylkesnemnd Id=\"6ee9bf92-7a4e-46ef-a2dd-b5a3a0a9ee2e\" " +
                        "MigrertId=\"\" StartDato=\"2022-11-14\">",
                "cvc-minLength-valid: Value '' with length = '0' is not facet-valid with respect to minLength '1' " +
                        "for type '#AnonType_MigrertId'."
            ),
            row(
                "too long MigrertId",
                "<OversendelseFylkesnemnd Id=\"6ee9bf92-7a4e-46ef-a2dd-b5a3a0a9ee2e\" " +
                        "MigrertId=\"${"a".repeat(37)}\" StartDato=\"2022-11-14\">",
                "cvc-maxLength-valid: Value '${"a".repeat(37)}' with length = '37' is not facet-valid with " +
                        "respect to maxLength '36' for type '#AnonType_MigrertId'."
            ),

            /** StartDato */
            row(
                "missing StartDato",
                "<OversendelseFylkesnemnd Id=\"6ee9bf92-7a4e-46ef-a2dd-b5a3a0a9ee2e\">",
                "cvc-complex-type.4: Attribute 'StartDato' must appear on element 'OversendelseFylkesnemnd'."
            ),
            row(
                "empty StartDato",
                "<OversendelseFylkesnemnd Id=\"6ee9bf92-7a4e-46ef-a2dd-b5a3a0a9ee2e\" StartDato=\"\">",
                EMPTY_DATE_ERROR
            ),
            row(
                "invalid StartDato",
                "<OversendelseFylkesnemnd Id=\"6ee9bf92-7a4e-46ef-a2dd-b5a3a0a9ee2e\" StartDato=\"2022\">",
                INVALID_DATE_ERROR
            ),
        ) { description, partialXml, expectedError ->
            When(description) {
                val thrown = shouldThrow<SAXException> {
                    getSchemaValidator().validate(buildOversendelseFylkesnemndXml(partialXml).toStreamSource())
                }

                Then("thrown should be as expected") {
                    thrown.message shouldBe expectedError
                }
            }
        }
    }
}) {
    companion object {
        private fun buildOversendelseFylkesnemndXml(elementStart: String): String = buildBarnevernXml(
            "$elementStart$LOVHJEMMEL_XML</OversendelseFylkesnemnd>"
        )
    }
}