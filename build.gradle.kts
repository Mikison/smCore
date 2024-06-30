plugins {
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("io.github.goooler.shadow") version "8.1.7"
    id("java")

}

group = "dev.sonmiike"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:4.0.3")
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly ("org.projectlombok:lombok:1.18.32")
    annotationProcessor ("org.projectlombok:lombok:1.18.32")

    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}
// STRING TEMPLATES
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test>().configureEach {
    jvmArgs("--enable-preview")
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--enable-preview")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

tasks.register<Copy>("copyJarToPlugins") {
    dependsOn(tasks.build)
    from("build/libs")
    include("*-dev*.jar")
    into("C:/Users/mati1/Desktop/SERWER/plugins")
    doNotTrackState("md5")
}

tasks.build {
    finalizedBy(tasks.named("copyJarToPlugins"))
}

tasks.shadowJar {
    relocate("com.zaxxer.hikari", "dev.sonmiike.smcore.hikari")
    relocate("org.postgresql", "dev.sonmiike.smcore.postgresql")
    configurations = listOf(project.configurations.runtimeClasspath.get())
}

