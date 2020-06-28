# MCAMC

WIP model, texture, and animation creation tool for Minecraft mods.

To compile this, you will need to use a Java IDE with maven support, and import 'pom.xml' as a maven project.
It's also important that the 'themes' folder be present in your execution directory.

To use this API for your mod:
 - grab the correct API version from the releases
 - unzip the src file into your project - please note there is an example mod, built off the forge example mod, that demonstrates basic MCMS usage by providing a wrapper for zombies.
 - edit your build.gradle file to include the following:
 -- under buildscript -> repositories:
 --- mavenCentral() (if it's not already there)
 --- maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
 -- under buildscript -> dependencies:
 --- classpath 'org.joml:joml:1.9.16'
 -- under dependencies:
 --- compile 'org.joml:joml:1.9.16'
 - refresh your gradle project (./gradlew setupDecompWorkspace Eclipse or ./gradlew genEclipseRuns if you used the built-in eclipse workspace).
 -- this will download JOML from the maven repository and add it to the classpath.
 - inside a static constructor in your mod class (see the examples if you don't know what that is), make sure to run firemerald.mcms.api.API.init()
 
The API automatically manages skeletons, obj data, bone effects data, animations, and models created using the firemerald.mcms.api.util.Loader class. It is highly recommended to use that instead of manual loading!
 - it will discard and clean up unused items
 - it will automatically reload on server start (for world data overrides)
 - it will automatically reload on a resource manager reload
 
If you have questions or need some help, come to the discord: https://discord.com/invite/3ESEY6j