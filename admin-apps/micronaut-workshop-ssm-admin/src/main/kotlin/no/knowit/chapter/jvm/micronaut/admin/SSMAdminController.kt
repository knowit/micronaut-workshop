package no.knowit.chapter.jvm.micronaut.admin

import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.uri.UriBuilder
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono

import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.ParameterType
import software.amazon.awssdk.services.ssm.model.PutParameterRequest
import software.amazon.awssdk.services.ssm.model.PutParameterResponse


@Controller("/ssm")
class SSMAdminController(private val ssmClient: SsmClient) {

    companion object {
        val APPLICATION_FRAGMENT_NAME = "application"
        val PARAMETER_FRAGMENT_NAME = "parameter"
    }

    private val configPathBuilder: UriBuilder =
        UriBuilder.of("/config/{${APPLICATION_FRAGMENT_NAME}}/{${PARAMETER_FRAGMENT_NAME}}")

    @Post("/add-mapping")
    fun addSSMParameterMapping(
        @Header("x-application-name") applicationName: String,
        @Body request: PropertiesRequest
    ): Publisher<HttpResponse<Any>> {
        val parameterRequests: List<Pair<String, PutParameterRequest>> = request.properties
            .map { (key, value) ->
                Pair(
                    key, PutParameterRequest.builder()
                        .name(
                            configPathBuilder.expand(
                                mutableMapOf(
                                    APPLICATION_FRAGMENT_NAME to applicationName,
                                    PARAMETER_FRAGMENT_NAME to key
                                )
                            ).path
                        )
                        .type(ParameterType.STRING)
                        .value(value)
                        .overwrite(true)
                        .build()
                )
            }

        val ssmResponse: Map<String, String> =
            parameterRequests.map { (key, request) -> Pair(key, ssmClient.putParameter(request).toString()) }.toMap()

        return Mono.just(
            HttpResponse.ok(
                PropertiesResponse(ssmResponse)
            )
        )
    }
}

@Introspected
data class PropertiesRequest(
    val properties: Map<String, String>
)

@Introspected
data class PropertiesResponse(
    val response: Map<String, String>
)