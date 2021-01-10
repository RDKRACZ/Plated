plugins {
  id("fabric-loom") version "0.5.43"
  id("signing")
}

group = "dev.sapphic"
version = "1.0.0"

java {
  withSourcesJar()
  withJavadocJar()
}

minecraft {
  refmapName = "mixins/plated/refmap.json"
}

signing {
  sign(configurations.archives.get())
}

repositories {
  maven("https://repo.spongepowered.org/maven") {
    content {
      includeGroup("org.spongepowered")
    }
  }
}

dependencies {
  minecraft("com.mojang:minecraft:1.16.4")
  mappings(minecraft.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:0.10.8")
  implementation("org.jetbrains:annotations:20.1.0")
  implementation("org.checkerframework:checker-qual:3.8.0")
}

tasks {
  compileJava {
    with(options) {
      options.release.set(8)
      isFork = true
      isDeprecation = true
      encoding = "UTF-8"
      compilerArgs.addAll(listOf("-Xlint:all", "-parameters"))
    }
  }

  processResources {
    filesMatching("/fabric.mod.json") {
      expand("version" to project.version)
    }
  }

  jar {
    from("/LICENSE.md")
    manifest.attributes(
      "Specification-Title" to "MinecraftMod",
      "Specification-Vendor" to project.group,
      "Specification-Version" to "1.0.0",
      "Implementation-Title" to project.name,
      "Implementation-Version" to project.version,
      "Implementation-Vendor" to project.group,
      "Sealed" to "true"
    )
  }

  named<Sign>("signArchives") {
    dependsOn("remapSourcesJar")
  }
}
