package no.nav.emottak.utils.common

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class UuidUtilsTest : StringSpec({
    "parseOrGenerateUuid() should parse a valid UUID string" {
        val validUuidString = "123e4567-e89b-12d3-a456-426614174000"

        val result = validUuidString.parseOrGenerateUuid()

        result.toString() shouldBe validUuidString
    }

    "parseOrGenerateUuid() should generate a new UUID for an invalid UUID string" {
        val invalidUuidString = "invalid-uuid"

        val result = invalidUuidString.parseOrGenerateUuid()

        result.toString() shouldNotBe invalidUuidString
    }
})
