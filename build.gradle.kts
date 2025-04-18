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
    maven (url = "https://jogamp.org/deployment/maven/")
    maven (url = "https://maven.scijava.org/content/repositories/public/")
}

dependencies {
    implementation("org.processing:core:4.3.4")

    /**** JOGL natives ****/
    runtimeOnly("org.jogamp.gluegen:gluegen-rt-natives-all:2.5.0")
    runtimeOnly("org.jogamp.jogl:jogl-all-natives-all:2.5.0")
    runtimeOnly("org.jogamp.jogl:nativewindow:2.5.0")
    runtimeOnly("org.jogamp.jogl:nativewindow-main:2.5.0")
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


/**** custom tasks ****/

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
