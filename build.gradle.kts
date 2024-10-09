import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    jacoco
    distribution
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

val kafka_version: String by project
val jackson_version: String by project
val commons_text_version: String by project
val feign_version: String by project
val slf4j_version: String by project
val lombok_version: String by project
val wiremock_version: String by project
val mockito_version: String by project
val junit_pioneer_version: String by project
val junit_version: String by project

dependencies {
    compileOnly("org.apache.kafka:connect-api:$kafka_version")

    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jackson_version")
    implementation("com.fasterxml.jackson.core:jackson-core:$jackson_version")

    implementation("org.apache.commons:commons-text:$commons_text_version")

    implementation("io.github.openfeign:feign-core:$feign_version")
    implementation("io.github.openfeign:feign-jackson:$feign_version")

    implementation("org.slf4j:slf4j-api:$slf4j_version")

    compileOnly("org.projectlombok:lombok:$lombok_version")
    annotationProcessor("org.projectlombok:lombok:$lombok_version")

    testImplementation("com.github.tomakehurst:wiremock:$wiremock_version")

    testImplementation("org.mockito:mockito-core:$mockito_version")

    testImplementation("org.junit-pioneer:junit-pioneer:$junit_pioneer_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit_version")

    testImplementation("org.apache.kafka:connect-api:$kafka_version")
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
    }
}

tasks.register<Copy>("connectStandalone") {
    from(configurations.runtimeClasspath)
    from("build/libs/kafka-connect-opensky.jar")
    into("build/connect/opensky")
}

tasks.jar {
    archiveBaseName.set("kafka-connect-opensky")
}

distributions {
    main {
        contents {
            from("docs/manifest.json") {
                filter<ReplaceTokens>("tokens" to mapOf(
                    "name" to project.name,
                    "version" to project.version
                ))
            }
            into("doc/") {
                from(project.projectDir) {
                    include("LICENSE*")
                    include("README*")
                    include("NOTICE*")
                }
                from("${project.projectDir}/docs") {
                    include("*.png")
                }
            }
            into("etc/") {
                from("config")
            }
            into("assets/") {}
            into("lib/") {
                from(tasks.jar)
                from(configurations.runtimeClasspath)
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

