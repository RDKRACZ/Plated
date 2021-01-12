import net.minecraftforge.gradle.tasks.SignJar
import org.gradle.util.GradleVersion
import java.time.Instant

plugins {
  id("net.minecraftforge.gradle.forge") version "2.3-SNAPSHOT"
  id("net.nemerosa.versioning") version "2.6.1"
  id("signing")
}

version = "1.0.0"
group = "dev.sapphic"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = sourceCompatibility
}

minecraft {
  version = "1.12.2-14.23.5.2847"
  mappings = "stable_39"
  runDir = "run"
}

repositories {
  maven("https://cursemaven.com")
  maven("https://dvs1.progwml6.com/files/maven")
}

dependencies {
  implementation("curse.maven:hwyla-253449:2568751")
  implementation("mezz.jei:jei_1.12.2:4.16.1.302")
  implementation("org.checkerframework:checker-qual:3.8.0")
}

tasks {
  named<JavaCompile>("compileJava") {
    with(options) {
      isFork = true
      isDeprecation = true
      encoding = "UTF-8"
      compilerArgs.addAll(listOf("-Xlint:all", "-parameters"))
    }
  }

  named<ProcessResources>("processResources") {
    filesMatching(setOf("/mcmod.info", "/version.properties")) {
      expand("version" to project.version)
    }
  }

  named<Jar>("jar") {
    from("/LICENSE.md")

    manifest.attributes(
      mapOf(
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

        "Specification-Title" to "MinecraftMod",
        "Specification-Version" to "1.1.0",
        "Specification-Vendor" to project.group,

        "Sealed" to "true"
      )
    )
  }

  if (project.hasProperty("signing.mods.keyalias")) {
    val keyalias = project.property("signing.mods.keyalias")
    val keystore = project.property("signing.mods.keystore")
    val password = project.property("signing.mods.password")

    fun SignJar.sign(task: String, archive: String = task) {
      dependsOn(task)

      val archivePath = named<Jar>(archive).get().archivePath

      setAlias(keyalias)
      setKeyStore(keystore)
      setKeyPass(password)
      setStorePass(password)
      setInputFile(archivePath)
      setOutputFile(inputFile)

      doLast {
        signing.sign(outputFile)
      }
    }

    val signJar = create<SignJar>("signJar") {
      sign(task = "reobfJar", archive = "jar")
    }

    val signSourceJar = create<SignJar>("signSourceJar") {
      sign(task = "sourceJar")
    }

    named</*Assemble*/Task>("assemble") {
      dependsOn(signJar, signSourceJar)
    }
  }
}
