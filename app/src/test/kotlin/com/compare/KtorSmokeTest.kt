package com.compare

import io.ktor.server.testing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class KtorSmokeTest {

    @Test
    fun `Ktor server responds to health check`() = testApplication {
        application { module() }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }

    @Test
    fun `Ktor can serialize small object`() = testApplication {
        application { module() }
        val response = client.get("/small")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `Ktor can serialize large object`() = testApplication {
        application { module() }
        val response = client.get("/large")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
