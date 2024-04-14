package kmp.shared.infrastructure.remote.maps

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kmp.shared.infrastructure.remote.places.globalJson
import kmp.shared.system.Config

/**
 * This object provides a client for interacting with the Google Maps API.
 */
internal object MapsClient {

    /**
     * This function initializes and returns an HttpClient configured for interacting with the Google Maps API.
     * It sets up the client with the necessary plugins and default request parameters.
     *
     * @param config The configuration object containing the application settings.
     * @param engine The HttpClientEngine to use for the client.
     * @param apiKey The API key to use for the Google Maps API.
     * @return An HttpClient configured for interacting with the Google Maps API.
     */
    fun init(config: Config, engine: HttpClientEngine, apiKey: String) = HttpClient(engine) {
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
                        co.touchlab.kermit.Logger.d { message }
                    }
                }
                level = LogLevel.ALL
            }
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "maps.googleapis.com"
                parameters["key"] = apiKey
            }
            contentType(ContentType.Application.Json)
        }
    }
}