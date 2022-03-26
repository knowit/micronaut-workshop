package no.knowit.chapter.jvm.micronaut.system

import io.micronaut.context.annotation.Context
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import io.micronaut.core.util.StringUtils
import io.micronaut.core.version.VersionUtils.MICRONAUT_VERSION
import io.micronaut.runtime.context.scope.refresh.RefreshEvent
import io.micronaut.runtime.event.annotation.EventListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.annotation.PostConstruct
import java.util.regex.Pattern
import java.util.regex.Pattern.CASE_INSENSITIVE
import java.util.regex.Pattern.compile

@Context
@Requires(property = "micronaut.startup.logger.enabled", defaultValue = StringUtils.TRUE, value = StringUtils.TRUE)
class ApplicationStartupLogger(private val environment: Environment) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(ApplicationStartupLogger::class.java)
        private val PROPERTY_NAMES_TO_MASK = arrayOf("password", "credential", "certificate", "key", "secret", "token")
        private val MASKING_PATTERNS: List<Pattern> = PROPERTY_NAMES_TO_MASK.map { s -> compile(".*$s.*", CASE_INSENSITIVE) }
    }

    @PostConstruct
    fun logConfig() {
        log.info("----- Start Application Configuration -----")
        printMicronautVersion()
        printEnvironments()
        printConfigSources()
        printConfigProperties()
        log.info("----- End Application Configuration -----")
    }

    private fun printMicronautVersion() = log.info("Micronaut (v${MICRONAUT_VERSION ?: "???"})")
    private fun printEnvironments() = log.info("Environments: ${environment.activeNames}")
    private fun printConfigSources() = environment.propertySources.sortedBy { it.order }.forEach { log.info("${it.order}: ${it.name}") }
    private fun printConfigProperties() = environment.propertySources.sortedBy { it.order }
        .forEach {
                source -> log.info("Property Source '${source.name}:'\n\t${source.map { flatten(it, source.get(it)) }.joinToString("\n\t")}")
        }

    private fun flatten(sourceKey: String, property: Any, level: Int = 0): String {
        return when (property) {
            is List<*> -> property.mapIndexed() { index, item -> flatten("${sourceKey}[${index}]", item as Any, level + 1) }.joinToString("\n\t")
            is Map<*, *> -> property.map { flatten("${sourceKey}.${it.key}", it.value as Any, level + 1) }.joinToString("\n\t")
            is String -> "${sourceKey} = ${maskProperty(sourceKey, property.replace("\r", "\\r").replace("\n","\\n"))}"
            else -> "${sourceKey }= ${maskProperty(sourceKey, property)}"
        }
    }

    private fun maskProperty(key: String, value: Any): Any {
        for (pattern in MASKING_PATTERNS) {
            if (pattern.matcher(key).matches()) {
                val valueString = value.toString()
                return if (valueString.isNotEmpty()) valueString.first() + "*****" else "******"
            }
        }
        return value
    }
}