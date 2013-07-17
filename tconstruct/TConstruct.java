package tconstruct;

import net.minecraftforge.common.MinecraftForge;

import tconstruct.client.event.EventCloakRender;
import tconstruct.client.tabs.InventoryTabArmorExtended;
import tconstruct.client.tabs.InventoryTabKnapsack;
import tconstruct.client.tabs.InventoryTabVanilla;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.common.TContent;
import tconstruct.common.TProxyCommon;
import tconstruct.landmine.logic.behavior.Behavior;
import tconstruct.landmine.logic.behavior.stackCombo.SpecialStackHandler;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.util.TabTools;
import tconstruct.util.PHConstruct;
import tconstruct.util.TCraftingHandler;
import tconstruct.util.TEventHandler;
import tconstruct.util.player.TPlayerHandler;
import tconstruct.worldgen.SlimeIslandGen;
import tconstruct.worldgen.TBaseWorldGenerator;
import tconstruct.worldgen.TerrainGenEventHandler;
import tconstruct.worldgen.village.TVillageTrades;
import tconstruct.worldgen.village.VillageSmelteryHandler;
import tconstruct.worldgen.village.VillageToolStationHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

/** TConstruct, the tool mod.
 * Craft your tools with style, then modify until the original is gone!
 * @author: mDiyo
 * @dependencies: IC2 API, MFR API
 */

@Mod(modid = "TConstruct", name = "TConstruct", version = "1.6.2_1.4.3", dependencies = "required-after:Forge@[7.7.1.687,)")
@NetworkMod(serverSideRequired = false, clientSideRequired = true, channels = { "TConstruct" }, packetHandler = tconstruct.util.network.TPacketHandler.class)
public class TConstruct
{
    /** The value of one ingot in millibuckets */
    public static final int ingotLiquidValue = 144;
    public static final int liquidUpdateAmount = 6;

    /* Instance of this mod, used for grabbing prototype fields */
    @Instance("TConstruct")
    public static TConstruct instance;
    /* Proxies for sides, used for graphics processing */
    @SidedProxy(clientSide = "tconstruct.client.TProxyClient", serverSide = "tconstruct.common.TProxyCommon")
    public static TProxyCommon proxy;

    public TConstruct()
    {
        if (Loader.isModLoaded("Natura"))
        {
            System.out.println("[TConstruct] Natura, what are we going to do tomorrow night?");
            System.out.println("[Natura] TConstruct, we're going to take over the world!");
            /*System.out.println("[TConstruct] They're Pinky, they're Pinky and");
            System.out.println("[Natura] the Brain Brain Brain Brain Brain~");*/
        }
        else
        {
            System.out.println("[TConstruct] Preparing to take over the world");
        }
    }

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        PHConstruct.initProps(event.getModConfigurationDirectory());
        TConstructRegistry.materialTab = new TabTools("TConstructMaterials");
        TConstructRegistry.toolTab = new TabTools("TConstructTools");
        TConstructRegistry.blockTab = new TabTools("TConstructBlocks");

        tableCasting = new LiquidCasting();
        basinCasting = new LiquidCasting();
        chiselDetailing = new Detailing();

        content = new TContent();
        content.modRecipes();

        events = new TEventHandler();
        events.unfuxOreDictionary();
        MinecraftForge.EVENT_BUS.register(events);
        content.oreRegistry();

        proxy.registerRenderer();
        proxy.registerTickHandler();
        proxy.addNames();
        proxy.readManuals();
        proxy.registerKeys();

        GameRegistry.registerWorldGenerator(new TBaseWorldGenerator());
        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGenEventHandler());
        GameRegistry.registerFuelHandler(content);
        GameRegistry.registerCraftingHandler(new TCraftingHandler());
        NetworkRegistry.instance().registerGuiHandler(instance, proxy);

        VillagerRegistry.instance().registerVillageTradeHandler(78943, new TVillageTrades());
        if (PHConstruct.addToVillages)
        {
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageToolStationHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageSmelteryHandler());
        }

        /*DimensionManager.registerProviderType(-7, TinkerWorldProvider.class, true);
        DimensionManager.registerDimension(-7, -7);*/
        playerTracker = new TPlayerHandler();
        GameRegistry.registerPlayerTracker(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
    	if(event.getSide() == Side.CLIENT){
    		MinecraftForge.EVENT_BUS.register(new EventCloakRender());
    	}
    	
        content.intermodCommunication();
        GameRegistry.registerWorldGenerator(new SlimeIslandGen(TContent.slimePool.blockID, 0));
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent evt)
    {
		Behavior.registerBuiltInBehaviors();
		SpecialStackHandler.registerBuiltInStackHandlers();
        content.modIntegration();
        content.createEntities();
        content.modRecipes();
    }

    public static LiquidCasting getTableCasting ()
    {
        return tableCasting;
    }

    public static LiquidCasting getBasinCasting ()
    {
        return basinCasting;
    }

    public static Detailing getChiselDetailing ()
    {
        return chiselDetailing;
    }

    public static TContent content;
    public static TEventHandler events;
    public static TPlayerHandler playerTracker;
    public static LiquidCasting tableCasting;
    public static LiquidCasting basinCasting;
    public static Detailing chiselDetailing;
}
