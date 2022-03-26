rootProject.name="micronaut-workshop-ssm-admin"

pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        kotlin("kapt") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
    }
}
