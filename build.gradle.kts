import net.minecraftforge.gradle.common.task.SignJar
import org.gradle.util.GradleVersion
import java.time.Instant

plugins {
  id("net.minecraftforge.gradle") version "3.0.190"
  id("org.spongepowered.mixin") version "0.7-SNAPSHOT"
  id("net.nemerosa.versioning") version "2.6.1"
  id("signing")
}

group = "dev.sapphic"
version = "2.0.0"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

val mixinRefmap: String = "mixins/plated/refmap.json"
val mixinConfigs: List<String> = listOf("client", "server").map {
  "mixins/plated/mixins.$it.json"
}
val mixinProperties: Map<String, Boolean> = mapOf(
  "mixin.env.disableRefMap" to true,
  "mixin.debug.export" to true,
  "mixin.debug.export.decompile" to false,
  "mixin.debug.verbose" to true,
  "mixin.debug.dumpTargetOnFailure" to true,
  "mixin.checks" to true,
  "mixin.hotSwap" to true
)

minecraft {
  mappings("snapshot", "20201028-1.16.3")
  runs {
    create("client") {
      workingDirectory = file("run").canonicalPath
      mods.create("plated").source(sourceSets["main"])
      mixinConfigs.forEach { arg("-mixin.config=$it") }
      property("forge.logging.console.level", "debug")
      mixinProperties.forEach { (k, v) -> property(k, "$v") }
    }

    create("server") {
      workingDirectory = file("run").canonicalPath
      mods.create("plated").source(sourceSets["main"])
      mixinConfigs.forEach { arg("-mixin.config=$it") }
      property("forge.logging.console.level", "debug")
      mixinProperties.forEach { (k, v) -> property(k, "$v") }
    }
  }
}

mixin {
  add(sourceSets["main"], mixinRefmap)
}

repositories {
  maven("https://cursemaven.com") {
    content {
      includeGroup("curse.maven")
    }
  }
}

dependencies {
  minecraft("net.minecraftforge:forge:1.16.5-36.0.1")
  implementation("org.checkerframework:checker-qual:3.9.0")
  compileOnly(fg.deobf("curse.maven:autoreglib-250363:3128555"))
  compileOnly(fg.deobf("curse.maven:quark-243121:3168455"))
}

tasks {
  compileJava {
    with(options) {
      isFork = true
      isDeprecation = true
      encoding = "UTF-8"
      compilerArgs.addAll(listOf("-Xlint:all", "-parameters"))
    }
  }

  jar {
    archiveClassifier.set("forge")

    from("/LICENSE.md")

    manifest.attributes(
      "Build-Timestamp" to Instant.now(),
      "Build-Revision" to versioning.info.commit,
      "Build-Jvm" to "${
        System.getProperty("java.version")
      } (${
        System.getProperty("java.vendor")
      } ${
        System.getProperty("java.vm.version")
      })",
      "Built-By" to GradleVersion.current(),

      "Implementation-Title" to project.name,
      "Implementation-Version" to project.version,
      "Implementation-Vendor" to project.group,

      "Specification-Title" to "ForgeMod",
      "Specification-Version" to "1.0.0",
      "Specification-Vendor" to project.group,

      "MixinConfigs" to mixinConfigs.joinToString(" ")
    )

    finalizedBy("reobfJar")
  }

  create<SignJar>("signJar") {
    dependsOn("reobfJar")

    setAlias("${project.property("signing.mods.keyalias")}")
    setKeyStore("${project.property("signing.mods.keystore")}")
    setKeyPass("${project.property("signing.mods.password")}")
    setStorePass("${project.property("signing.mods.password")}")
    setInputFile(jar.get().archiveFile.get())
    setOutputFile(inputFile)
  }

  assemble {
    dependsOn("signJar")
  }
}

signing {
  sign(configurations.archives.get())
}
