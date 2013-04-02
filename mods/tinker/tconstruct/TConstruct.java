package mods.tinker.tconstruct;

import java.util.Random;

import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.TabTools;
import mods.tinker.tconstruct.player.TPlayerHandler;
import mods.tinker.tconstruct.worldgen.TBaseWorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

/** TConstruct, the tool mod.
 * Craft your tools with style, then modify until the original is gone!
 * @author: mDiyo
 */

@Mod(modid = "TConstruct", name = "TConstruct", version = "1.5.1_1.2.12")
@NetworkMod(serverSideRequired = false, clientSideRequired = true, channels = { "TConstruct" }, packetHandler = mods.tinker.tconstruct.TPacketHandler.class)
public class TConstruct
{
	public static final int ingotLiquidValue = 144;
	public static final int liquidUpdateAmount = 6;
	
	/* Instance of this mod, used for grabbing prototype fields */
	@Instance("TConstruct")
	public static TConstruct instance;
	/* Proxies for sides, used for graphics processing */  
	@SidedProxy(clientSide = "mods.tinker.tconstruct.client.TProxyClient", serverSide = "mods.tinker.tconstruct.TProxyCommon")
	public static TProxyCommon proxy;
	
	public TConstruct()
	{
		//Take that, any mod that does ore dictionary registration in preinit!
	}

	@PreInit
	public void preInit (FMLPreInitializationEvent evt)
	{
		PHConstruct.initProps();
		TConstructRegistry.materialTab = new TabTools("TConstructMaterials");
		TConstructRegistry.toolTab = new TabTools("TConstructTools");
		TConstructRegistry.blockTab = new TabTools("TConstructBlocks");
		content = new TContent();

		events = new TEventHandler();
		MinecraftForge.EVENT_BUS.register(events);
		events.unfuxOreDictionary();

		proxy.registerRenderer();
		proxy.addNames();
		proxy.readManuals();
		//proxy.registerKeys();
	}

	@Init
	public void init (FMLInitializationEvent evt)
	{
		GameRegistry.registerWorldGenerator(new TBaseWorldGenerator());
		GameRegistry.registerFuelHandler(content);
		GameRegistry.registerCraftingHandler(new TCraftingHandler());
		NetworkRegistry.instance().registerGuiHandler(instance, new TGuiHandler());
	}

	@PostInit
	public void postInit (FMLPostInitializationEvent evt)
	{
		content.modIntegration();
		content.oreRegistry();
		
		playerTracker = new TPlayerHandler();
		GameRegistry.registerPlayerTracker(playerTracker);
		MinecraftForge.EVENT_BUS.register(playerTracker);
	}

	public static TEventHandler events;
	public static TPlayerHandler playerTracker;
	public static TContent content;

	public static Random tRand = new Random();
}
