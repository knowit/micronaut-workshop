package no.knowit.chapter.jvm.micronaut.system

import io.micronaut.context.env.Environment
import io.micronaut.context.env.MapPropertySource
import io.micronaut.context.env.PropertySource
import io.micronaut.management.endpoint.info.InfoSource
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import jakarta.inject.Singleton

@Singleton
class JavaInfoSource(private val environment: Environment) : InfoSource {
    override fun getSource(): Publisher<PropertySource> {
        return Flux.just(
            MapPropertySource("java", mapOf("java" to environment.getProperties("java")))
        )
    }
}