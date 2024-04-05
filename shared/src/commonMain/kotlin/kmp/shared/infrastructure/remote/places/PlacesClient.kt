package kmp.shared.infrastructure.remote.places

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kmp.shared.system.Config
import kotlin.native.concurrent.ThreadLocal
import co.touchlab.kermit.Logger as KermitLogger
import kotlinx.serialization.json.Json as JsonConfig

internal object PlacesClient {

    fun init( config: Config, engine: HttpClientEngine, apiKey: String) = HttpClient(engine) {
        expectSuccess = true
        developmentMode = !config.isRelease
        followRedirects = false

        install(ContentNegotiation) {
            json(globalJson)
        }

        if (!config.isRelease) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        KermitLogger.d { message }
                    }
                }
                level = LogLevel.ALL
            }
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "places.googleapis.com"
                headers {
                    append("X-Goog-Api-Key", apiKey)
                    append("Content-Type", "application/json")
                }
            }
            contentType(ContentType.Application.Json)
        }


    }
}


@ThreadLocal
val globalJson = JsonConfig {
    ignoreUnknownKeys = true
    coerceInputValues = true
    useAlternativeNames = false
}