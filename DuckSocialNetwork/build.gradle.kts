import sun.jvmstat.monitor.MonitoredVmUtil.mainClass

plugins {
    id("java")
    id ("org.openjfx.javafxplugin").version("0.1.0")
}

group = "org.example"
version = "1.0-SNAPSHOT"
repositories {
    mavenCentral()
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

javafx {
    version = "21.0.4"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.3")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

