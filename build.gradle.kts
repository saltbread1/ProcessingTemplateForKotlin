import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.24"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "jp.saltbread1"
version = "1.0"

repositories {
    mavenCentral()
    flatDir { dirs("libs") }
}

val osName = System.getProperty("os.name").lowercase()
val osArch = System.getProperty("os.arch").lowercase()

dependencies {
    // processing core
    implementation(group = "", name = "core", version = "4.4.7")

    // jogl
    implementation(group = "", name = "jogl-all", version = "2.5.0")
    implementation(group = "", name = "gluegen-rt", version = "2.5.0")

    // jogl natives
    when
    {
        osName.contains("mac") ->
        {
            runtimeOnly(group = "", name = "jogl-all-2.5.0-natives-macosx-universal", version = "2.5.0")
            runtimeOnly(group = "", name = "gluegen-rt-2.5.0-natives-macosx-universal", version = "2.5.0")
        }

        osName.contains("windows") ->
        {
            runtimeOnly(group = "", name = "jogl-all-2.5.0-natives-windows-amd64", version = "2.5.0")
            runtimeOnly(group = "", name = "gluegen-rt-2.5.0-natives-windows-amd64", version = "2.5.0")
        }

        osName.contains("linux") ->
        {
            // Linux
            val arch = when
            {
                osArch.contains("aarch64") || osArch.contains("arm64") -> "aarch64"
                else -> "amd64"
            }
            runtimeOnly(group = "", name = "jogl-all-2.5.0-natives-linux-$arch", version = "2.5.0")
            runtimeOnly(group = "", name = "gluegen-rt-2.5.0-natives-linux-$arch", version = "2.5.0")
        }

        else ->
        {
            println("Unsupported OS: $osName")
        }
    }
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks.named<Jar>("jar")
{
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}

tasks.named<ShadowJar>("shadowJar")
{
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}


// java exec
tasks.register<JavaExec>("runMain") {
    mainClass.set("MainKt")
    classpath = sourceSets["main"].runtimeClasspath
}

// jar
tasks.register<Jar>("jarMain") {
    archiveBaseName.set("main")
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    from(sourceSets.main.get().output)
}

// shadow jar
tasks.register<ShadowJar>("fatJarMain") {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    from(sourceSets.main.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
}
