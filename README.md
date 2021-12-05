#[Tinkers' Construct - GTNH](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2218638-tinkers-construct)

Forked version of Tinker's Construct being maintained for 1.7.10 for use in the GTNH modpack.

Modify all the things, then do it again!   
Melt down any metals you find. 	 
Power the world with spinning wind!

##Development
Install Forge as usual, and setup your IDE as with any other Forge project. Forge Multipart, and CClib must be installed from the  Forge File Server: The DEVELOPMENT version of NEI must be installed to the same directory from [Chicken Bones Site](http://www.chickenbones.craftsaddle.org/Files/New_Versions/links.php). Copy `TCore_dummy.jar` to `forge/mcp/jars/mods/` to enable the Preloader (optional -- only needed when working on the preloader itself)

## IMC
All IMC support and examples can be found here: https://gist.github.com/bonii-xx/e46f9d9e81e29d796b1b

## Compile from Source
Note: Git MUST be installed and in the system path to use our scripts.
* Setup: Run [gradle]in the repository root: `gradlew[.bat] [setupDevWorkspace|setupDecompWorkspace] [eclipse|idea]`
* Build: Run [gradle]in the repository root: `gradlew[.bat] build'
* If obscure Gradle issues are found try running 'gradlew clean' and 'gradlew cleanCache'

## Issue reporting
Please include the following:

* Minecraft version
* Tinkers' Construct version
* Forge version/build
* Versions of any mods potentially related to the issue 
* Any relevant screenshots are greatly appreciated.
* For crashes:
	* Steps to reproduce
	* ForgeModLoader-client-0.log (the FML log) from the root folder of the client

