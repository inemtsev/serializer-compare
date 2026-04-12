package com.compare

import com.compare.dto.SmallObject
import com.compare.dto.LargeObject
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

fun main(args: Array<String>) {
    val port = args.getOrNull(0)?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    routing {
        get("/small") {
            val obj = SmallObject()
            call.respondText(
                text = json.encodeToString(obj),
                contentType = ContentType.Application.Json
            )
        }
        get("/large") {
            val obj = LargeObject()
            call.respondText(
                text = json.encodeToString(obj),
                contentType = ContentType.Application.Json
            )
        }
        post("/small") {
            val text = call.receiveText()
            val obj = json.decodeFromString<SmallObject>(text)
            call.respondText(json.encodeToString(obj), contentType = ContentType.Application.Json)
        }
        post("/large") {
            val text = call.receiveText()
            val obj = json.decodeFromString<LargeObject>(text)
            call.respondText(json.encodeToString(obj), contentType = ContentType.Application.Json)
        }
        get("/health") {
            call.respondText("OK")
        }
    }
}
