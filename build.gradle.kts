plugins {
    id("fabric-loom") version "0.7-SNAPSHOT"
}

base.archivesBaseName = "tinkers-construct"
group = "slimenights"
version = "2.0.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        name = "Curseforge Maven"
        url = uri("https://www.cursemaven.com")
    }

    maven {
        name = "shedaniel's Maven"
        url = uri("https://maven.shedaniel.me/")
    }

    maven {
        name = "BuildCraft"
        url = uri("https://mod-buildcraft.com/maven")
    }

    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }

    maven {
        name = "One's Maven"
        url = uri("https://storage.googleapis.com/devan-maven/")
    }

    maven {
        name = "Ladysnake Mods"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }

    mavenLocal()
}

val modImplementationAndInclude by configurations.register("modImplementationAndInclude")

sourceSets {
    main {
        resources.srcDirs += file("src/generated/resources")
    }
}

dependencies {
    minecraft("net.minecraft", "minecraft", "1.16.5")
    mappings("net.fabricmc", "yarn", "1.16.5+build.6", classifier = "v2")

    modImplementation("net.fabricmc", "fabric-loader", "0.11.3")
    modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.32.5+1.16")
    modImplementation("slimeknights", "Mantle", "1.6.39-SNAPSHOT")

    modApi("me.shedaniel.cloth", "cloth-config-fabric", "4.11.19")
    modApi("alexiil.mc.lib", "libblockattributes-core", "0.8.9-pre.1")
    modApi("alexiil.mc.lib", "libblockattributes-fluids", "0.8.9-pre.1")

    modRuntime("com.terraformersmc:modmenu:1.16.9")

    modRuntime("me.shedaniel", "RoughlyEnoughItems", "5.11.202")
    modRuntime("curse.maven", "worldedit-225608", "3135186")
    modRuntime("curse.maven", "appleskin-248787", "2987255")
    modRuntime("curse.maven", "hwyla-253449", "3033613")

    // Lombok is bad and worst in game but for now we just deal with it
    compileOnly("org.projectlombok", "lombok", "1.18.20")
    annotationProcessor("org.projectlombok", "lombok", "1.18.20")

    testCompileOnly("org.projectlombok", "lombok", "1.18.20")
    testAnnotationProcessor("org.projectlombok", "lombok", "1.18.20")

    modImplementation("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-base", "2.8.0")
    modImplementation("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-item", "2.8.0")
    modImplementation("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-entity", "2.8.0")
    modImplementation("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-level", "2.8.0")
    modImplementation("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-util", "2.8.0")
    modImplementation("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-world", "2.8.0")
    modImplementation("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-chunk", "2.8.0")

    include("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-base", "2.8.0")
    include("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-item", "2.8.0")
    include("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-entity", "2.8.0")
    include("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-level", "2.8.0")
    include("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-util", "2.8.0")
    include("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-world", "2.8.0")
    include("io.github.onyxstudios.Cardinal-Components-API", "cardinal-components-chunk", "2.8.0")

    add(
        sourceSets.main.get().getTaskName("mod", JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME),
        modImplementationAndInclude
    )
    add(net.fabricmc.loom.util.Constants.Configurations.INCLUDE, modImplementationAndInclude)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

loom {
    accessWidener = file("src/main/resources/tconstruct.aw")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (JavaVersion.current().isJava9Compatible) {
        options.release.set(8)
    } else {
        sourceCompatibility = "8"
        targetCompatibility = "8"
    }
}

tasks.withType<AbstractArchiveTask> {
    from(file("LICENSE"))
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.remapJar {
    doLast {
        input.get().asFile.delete()
    }
}
