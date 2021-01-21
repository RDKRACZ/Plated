import net.minecraftforge.gradle.common.task.SignJar
import net.minecraftforge.gradle.common.util.RunConfig
import org.gradle.util.GradleVersion
import java.time.Instant

plugins {
  id("net.minecraftforge.gradle") version "4.0.8"
  id("org.spongepowered.mixin") version "0.7-SNAPSHOT"
  id("net.nemerosa.versioning") version "2.6.1"
  id("signing")
}

group = "dev.sapphic"
version = "2.0.0"

// FIXME https://github.com/SpongePowered/Mixin/issues/463
val mixinRefmap: String = "refmap.plated.json"
val mixinConfigs: List<String> = listOf("client", "server").map {
  "mixins/plated/mixins.$it.json"
}

minecraft {
  mappings("snapshot", "20201028-1.16.3")
  runs {
    fun RunConfig.configured() {
      workingDirectory = file("run").canonicalPath
      mods.create("plated").source(sourceSets["main"])
      mixinConfigs.forEach { arg("-mixin.config=$it") }
      mapOf(
        "forge.logging.console.level" to "debug",
        "mixin.env.disableRefMap" to true,
        "mixin.debug.export" to true,
        "mixin.debug.export.decompile" to false,
        "mixin.debug.verbose" to true,
        "mixin.debug.dumpTargetOnFailure" to true,
        "mixin.checks" to true,
        "mixin.hotSwap" to true
      ).forEach { (k, v) -> property(k, "$v") }
    }

    create("client").configured()
    create("server").configured()
  }
}

mixin {
  add(sourceSets["main"], mixinRefmap)
}

repositories {
  maven("https://dvs1.progwml6.com/files/maven") {
    content {
      includeGroup("slimeknights.mantle")
      includeGroup("knightminer")
    }
  }
  maven("https://maven.blamejared.com") {
    content {
      includeGroup("vazkii.autoreglib")
      includeGroup("vazkii.quark")
    }
  }
}

dependencies {
  minecraft("net.minecraftforge:forge:1.16.5-36.0.1")
  implementation("org.checkerframework:checker-qual:3.9.0")
  implementation(fg.deobf("slimeknights.mantle:Mantle:1.16.5-1.6.75"))
  implementation(fg.deobf("knightminer:Inspirations:1.16.5-1.2.2.27"))
  implementation(fg.deobf("vazkii.autoreglib:AutoRegLib:1.6-48.88"))
  implementation(fg.deobf("vazkii.quark:Quark:r2.4-296.1647"))
  annotationProcessor("org.spongepowered:mixin:0.8.2:processor")
}

tasks {
  compileJava {
    with(options) {
      release.set(8)
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

      "MixinConfigs" to mixinConfigs.joinToString()
    )

    finalizedBy("reobfJar")
  }

  processResources {
    filesMatching(mixinConfigs) {
      expand("refmap" to mixinRefmap)
    }
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

