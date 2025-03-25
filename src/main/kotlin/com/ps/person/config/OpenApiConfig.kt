package com.ps.person.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class OpenApiConfig(private val environment: Environment) : ApplicationListener<ApplicationStartedEvent> {

    private val logger = LoggerFactory.getLogger(OpenApiConfig::class.java)

    @Value("\${server.port:8080}")
    private lateinit var serverPort: String

    @Value("\${springdoc.swagger-ui.path:/swagger-ui.html}")
    private lateinit var swaggerPath: String

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Person API")
                    .description("API for managing person records")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("API Support")
                            .email("support@example.com")
                    )
            )
    }

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        val protocol = if (environment.getProperty("server.ssl.enabled") == "true") "https" else "http"
        val hostAddress = "localhost"
        val contextPath = environment.getProperty("server.servlet.context-path") ?: ""

        val swaggerUrl = "$protocol://$hostAddress:$serverPort$contextPath$swaggerPath"
        logger.info("Swagger UI is available at: $swaggerUrl")
    }
}