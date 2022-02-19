package no.knowit.chapter.jvm.micronaut

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(vararg args: String) {
        Micronaut.build(*args)
            .banner(false)
            .mainClass(Application.javaClass)
            .start()
    }
}

