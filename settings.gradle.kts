rootProject.name = "Plated"

pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://files.minecraftforge.net/maven")
    maven("https://repo.spongepowered.org/maven")
  }
  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "net.minecraftforge.gradle" -> {
          useModule("net.minecraftforge.gradle:ForgeGradle:${requested.version}")
        }
        "org.spongepowered.mixin" -> {
          useModule("org.spongepowered:mixingradle:${requested.version}")
        }
      }
    }
  }
}
