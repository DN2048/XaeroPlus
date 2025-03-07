pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.architectury.dev/")
		maven("https://files.minecraftforge.net/maven/")
		mavenCentral()
		gradlePluginPortal()
	}
}
gradle.extra.apply {
	set("mod_version", "2.5")
	set("maven_group", "xaeroplus")
	set("archives_base_name", "XaeroPlus")
	set("minecraft_version", "1.20.1")
	set("parchment_version", "2023.09.03")
	set("fabric_loader_version", "0.15.2")
	set("fabric_api_version", "0.91.0+1.20.1")
	set("forge_loader_version", "47.1.0")
	set("worldmap_version_fabric", "1.38.1")
	set("minimap_version_fabric", "24.0.3")
	set("worldmap_version_forge", "1.38.1")
	set("minimap_version_forge", "24.0.3")
}

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			library("minecraft", "com.mojang:minecraft:${gradle.extra.get("minecraft_version")}")
			library("fabric-loader", "net.fabricmc:fabric-loader:${gradle.extra.get("fabric_loader_version")}")
			library("forge", "net.minecraftforge:forge:${gradle.extra.get("minecraft_version")}-${gradle.extra.get("forge_loader_version")}")
			library("fabric-api", "net.fabricmc.fabric-api:fabric-api:${gradle.extra.get("fabric_api_version")}")
			library("parchment", "org.parchmentmc.data:parchment-${gradle.extra.get("minecraft_version")}:${gradle.extra.get("parchment_version")}")
			library("worldmap-fabric", "maven.modrinth:xaeros-world-map:${gradle.extra.get("worldmap_version_fabric")}_Fabric_1.20")
			library("worldmap-forge", "maven.modrinth:xaeros-world-map:${gradle.extra.get("worldmap_version_forge")}_Forge_1.20")
			library("minimap-fabric", "maven.modrinth:xaeros-minimap:${gradle.extra.get("minimap_version_fabric")}_Fabric_1.20")
			library("minimap-forge", "maven.modrinth:xaeros-minimap:${gradle.extra.get("minimap_version_forge")}_Forge_1.20")
			library("mixinextras-common", "io.github.llamalad7:mixinextras-common:0.3.5")
			library("mixinextras-forge", "io.github.llamalad7:mixinextras-forge:0.3.5")
			library("caffeine", "com.github.ben-manes.caffeine:caffeine:3.1.8")
			library("lambdaEvents", "net.lenni0451:LambdaEvents:2.4.1")
			library("waystones-fabric", "maven.modrinth:waystones:14.0.2+fabric-1.20")
			library("waystones-forge", "maven.modrinth:waystones:14.0.2+forge-1.20")
			library("balm-fabric", "maven.modrinth:balm:7.1.4+fabric-1.20.1")
			library("balm-forge", "maven.modrinth:balm:7.1.4+forge-1.20.1")
			library("fabric-waystones", "maven.modrinth:fwaystones:3.1.3+mc1.20")
			library("worldtools", "maven.modrinth:worldtools:1.2.0+1.20.1")
			library("sqlite", "com.github.rfresh2:sqlite-jdbc:2ba0c66439") // relocated xerial sqlite to avoid conflicts with other mods
			library("immediatelyfast", "maven.modrinth:immediatelyfast:1.2.10+1.20.4-fabric")
			library("modmenu", "maven.modrinth:modmenu:7.2.2")
			library("sodium", "maven.modrinth:sodium:mc1.20.1-0.5.3")
			library("fpsdisplay", "maven.modrinth:fpsdisplay:3.1.0+1.20.x")
			library("cloth-config-fabric", "me.shedaniel.cloth:cloth-config-fabric:11.1.118")
		}
	}
}



include("common")
include("fabric")
include("forge")

rootProject.name = "XaeroPlus"
