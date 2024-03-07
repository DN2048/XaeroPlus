architectury {
	platformSetupLoomIde()
	fabric()
}

loom {
	accessWidenerPath = project(":common").loom.accessWidenerPath
	runs {
		getByName("client") {
			ideConfigGenerated(true)
			client()
		}
	}
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
configurations.compileClasspath.get().extendsFrom(common)
configurations.runtimeClasspath.get().extendsFrom(common)
configurations.getByName("developmentFabric").extendsFrom(common)

afterEvaluate {
	loom.runs.configureEach {
		// https://fabricmc.net/wiki/tutorial:mixin_hotswaps
		vmArg("-javaagent:${ configurations.compileClasspath.get().find { it.name.contains("sponge-mixin") } }")
//		vmArg("-Dmixin.debug.export=true")
	}
}

val worldmap_version: String by rootProject
val minimap_version: String by rootProject
val minecraft_version: String by rootProject
val parchment_version: String by rootProject
val loader_version: String by rootProject
val fabric_version: String by rootProject
val destArchiveVersion = "${project.version}+${loom.platform.get().id()}-${minecraft_version}"
val destArchiveClassifier = "WM${worldmap_version}-MM${minimap_version}"

dependencies {
	modImplementation("net.fabricmc:fabric-loader:${loader_version}")
	modApi("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
	shadowCommon(libs.sqlite)
	implementation(libs.sqlite)
	modImplementation("maven.modrinth:xaeros-world-map:${worldmap_version}_Fabric_1.19.4")
	modImplementation("maven.modrinth:xaeros-minimap:${minimap_version}_Fabric_1.19.4")
	modCompileOnly(files("libs/baritone-api-fabric-1.20.1-elytra-beta-v1.jar"))
	modCompileOnly(libs.waystones.fabric)
	modCompileOnly(libs.balm.fabric)
	modCompileOnly(libs.fabric.waystones)
//	modRuntimeOnly("maven.modrinth:immediatelyfast:1.2.10+1.20.4-fabric")
//	modRuntimeOnly("maven.modrinth:modmenu:7.2.2")
//	modRuntimeOnly("maven.modrinth:sodium:mc1.20.1-0.5.3")
//	modRuntimeOnly("maven.modrinth:fpsdisplay:3.1.0+1.20.x")
//	modRuntimeOnly("me.shedaniel.cloth:cloth-config-fabric:11.1.118") {
//		exclude(group = "net.fabricmc.fabric-api")
//	}
	implementation(include(libs.caffeine.get())!!)
	implementation(include(libs.lambdaEvents.get())!!)
	common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }
}

tasks {
	processResources {
		filesMatching("fabric.mod.json") {
			expand(mapOf(
				"version" to project.version,
				"worldmap_version" to worldmap_version,
				"minimap_version" to minimap_version
			))
		}
	}

	shadowJar {
		configurations = listOf(shadowCommon)
		exclude("architectury.common.json")
	}

	remapJar {
		injectAccessWidener = true
		dependsOn(shadowJar)
		inputFile.set(shadowJar.get().archiveFile)
		archiveVersion = destArchiveVersion
		archiveClassifier = destArchiveClassifier
	}
}

val javaComponent = components.findByName("java") as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(project.configurations.getByName("shadowRuntimeElements")) {
	skip()
}
