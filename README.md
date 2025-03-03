# [Tinkers' Construct](https://slimeknights.github.io/projects/#tinkers-construct)

Modify all the things, then do it again!   
Melt down any metals you find. 	 
Power the world with spinning wind!

## Documentation

For documentation on writing addons or working with Tinkers' Consrtuct datapacks, see the pages on the SlimeKnight's Github.io pages: https://slimeknights.github.io/docs/

## Setting up a Workspace/Compiling from Source

Note: Git MUST be installed and in the system path to use our scripts.
* Setup: Import Tinkers' Construct as a Gradle project into IDEA. Let it run setup.
* Run: Run the `gradlew genIntellijRuns` through IDEA.
* Build: Run `gradlew build`.
* If obscure Gradle issues are found try running `gradlew clean` and `gradlew cleanCache`

## Issue reporting
Please include the following:

* Minecraft version
* Tinkers' Construct version
* Forge version/build
* Versions of any mods potentially related to the issue 
* Any relevant screenshots are greatly appreciated.
* For crashes:
	* Steps to reproduce
	* latest.log (the FML log) from the root folder of the client

## Licenses
Code, Textures and binaries are licensed under the [MIT License](https://tldrlegal.com/license/mit-license).

You are allowed to use the mod in your modpack.
Any modpack which uses Tinkers' Construct takes **full** responsibility for user support queries. For anyone else, we only support official builds from the main CI server, not custom built jars. We also do not take bug reports for outdated builds of Minecraft.

If you have queries about any license or the above support restrictions, please drop by our IRC channel, #TinkersConstruct on irc.esper.net

Any alternate licenses are noted where appropriate.

## Jar Signing
Some jars from our build servers may be signed. Under no circumstances does anyone have permission to verify the signatures on those jars from other mods. The signing is for informational purposes only.
