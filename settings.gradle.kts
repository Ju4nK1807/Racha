pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Racha"
include(":app")

// El proyecto vive dentro de OneDrive: la salida de compilación va fuera para que
// OneDrive no intente sincronizar miles de archivos temporales ni bloquee los de Gradle.
val directorioCompilacion = File("C:/gradle-builds/racha")
gradle.beforeProject {
    val nombre = if (path == ":") "raiz" else path.removePrefix(":").replace(':', '-')
    layout.buildDirectory.set(directorioCompilacion.resolve(nombre))
}
