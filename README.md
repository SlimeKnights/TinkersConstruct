#[Tinkers' Construct](http://www.minecraftforum.net/topic/1659892-tinkers-construct/)

Modify all the things, then do it again! 	 
Melt down any metals you find. 	 
Turn everything into golems!

##Development
Install Forge as usual, and setup your IDE as with any other Forge project. Forge Multipart, and CClib must be installed to `forge/mcp/jars/mods/` from the  Forge File Server: [ForgeMultipart](http://files.minecraftforge.net/ForgeMultipart/). [CClib](http://files.minecraftforge.net/CodeChickenLib/).  The DEVELOPMENT version of NEI must be installed to the same directory from [Chicken Bones Site](http://www.chickenbones.craftsaddle.org/Files/New_Versions/links.php). Copy `TCore_dummy.jar` to `forge/mcp/jars/mods/` to enable the Preloader (optional -- only needed when working on the preloader itself)

##Compile from Source
Run [Apache Ant](http://ant.apache.org/bindownload.cgi) in the repository root: `ant package`

##Issue reporting
Please include the following:

* Minecraft version
* Tinkers' Construct version
* Forge version/build
* Versions of any mods potentially related to the issue 
* Any relevant screenshots are greatly appreciated.
* For crashes:
	* Steps to reproduce
	* ForgeModLoader-client-0.log (the FML log) from the root folder of the client

##Licenses
Most code is public domain under [Creative Commons 0](http://creativecommons.org/publicdomain/zero/1.0/).

Textures and binaries are licensed under [Creative Commons 3](http://creativecommons.org/licenses/by/3.0/).

Any modpack which uses Tinkers' Construct takes **full** responsibility for user support queries. For anyone else, we only support official builds from the main CI server, not custom built jars. We also do not take bug reports for outdated builds of Minecraft.

If you have queries about any license or the above support restrictions, please drop by our IRC channel, #TinkersConstruct on irc.esper.net

Any alternate licenses are noted where appropriate.
