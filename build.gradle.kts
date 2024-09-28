import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform") version "2.0.20"
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "io.github.devundef1ned"
version = "0.01-alpha01"

repositories {
    mavenCentral()
}

val coroutineVersions = "1.6.4"

kotlin {
    jvm()
    iosArm64()
    macosArm64()
    macosX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersions")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
    coordinates(group.toString(), name, version.toString())

    pom {
        name.set("Makkina")
        description.set("A simple and lightweight Kotlin Multiplatform Finite State Machine library.")
        url.set("https://github.com/devUndef1ned/makkina")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("devUndef1ned")
                name.set("Oleg Kuzmin aka devUndef1ned")
                email.set("ogkuzmin@gmail.com")
            }
        }

        scm {
            url.set("https://github.com/devUndef1ned/makkina")
            connection.set("scm:git:git//github.com/devUndef1ned/makkina.git")
            developerConnection.set("scm:git:ssh//git@github.com:devUndef1ned/makkina.git")
        }
    }
}