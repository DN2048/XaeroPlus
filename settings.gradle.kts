pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.architectury.dev/")
		maven("https://files.minecraftforge.net/maven/")
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			library("caffeine", "com.github.ben-manes.caffeine:caffeine:3.1.8")
			library("lambdaEvents", "net.lenni0451:LambdaEvents:2.4.1")
			library("waystones-fabric", "maven.modrinth:waystones:15.2.0+fabric-1.20.2")
			library("waystones-forge", "maven.modrinth:waystones:15.2.0+forge-1.20.2")
			library("waystones-neoforge", "maven.modrinth:waystones:15.2.0+neoforge-1.20.2")
			library("balm-fabric", "maven.modrinth:balm:8.0.5+fabric-1.20.2")
			library("balm-forge", "maven.modrinth:balm:8.0.5+forge-1.20.2")
			library("balm-neoforge", "maven.modrinth:balm:8.0.5+neoforge-1.20.2")
			library("fabric-waystones", "maven.modrinth:fwaystones:3.3.1+mc1.20.2")
			library("worldtools", "maven.modrinth:worldtools:1.2.0+1.20.2")
			library("sqlite", "org.xerial:sqlite-jdbc:3.45.1.0")
		}
	}
}



include("common")
include("fabric")
include("forge")
include("neo")

rootProject.name = "XaeroPlus"
