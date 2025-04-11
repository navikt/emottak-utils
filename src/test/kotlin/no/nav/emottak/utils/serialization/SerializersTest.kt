package no.nav.emottak.utils.serialization

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class SerializersTest : StringSpec({
    "Exception.toJsonString() should return a valid JSON string" {
        val exception = IllegalArgumentException("Test exception message")

        val jsonString = exception.toJsonString()

        jsonString shouldNotBe ""
        jsonString shouldContain "\"ExceptionClass\":\"java.lang.IllegalArgumentException\""
        jsonString shouldContain "\"message\":\"Test exception message\""
        jsonString shouldContain "\"causeMessage\":\"null\""
    }

    "getErrorMessage() should return cause message if localizedMessage is null" {
        val cause = Exception("Cause message")
        val exception = IllegalArgumentException(null, cause)

        val errorMessage = exception.getErrorMessage()

        errorMessage shouldBe "Cause message"
    }

    "getErrorMessage() should return exception class name if both localizedMessage and cause message are null" {
        val exception = IllegalArgumentException(null, null)

        val errorMessage = exception.getErrorMessage()

        errorMessage shouldBe "IllegalArgumentException"
    }

    "toEventDataJson() should return a valid JSON string" {
        val exception = IllegalArgumentException("Test exception message")

        val jsonString = exception.toEventDataJson()

        jsonString shouldBe "{\"error_message\":\"Test exception message\"}"
    }
})
