package mods.tinker.tconstruct;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.common.TProxyCommon;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.crafting.Detailing;
import mods.tinker.tconstruct.library.crafting.LiquidCasting;
import mods.tinker.tconstruct.library.util.TabTools;
import mods.tinker.tconstruct.skill.SkillRegistry;
import mods.tinker.tconstruct.util.PHConstruct;
import mods.tinker.tconstruct.util.TCraftingHandler;
import mods.tinker.tconstruct.util.TEventHandler;
import mods.tinker.tconstruct.util.player.TPlayerHandler;
import mods.tinker.tconstruct.worldgen.TBaseWorldGenerator;
import mods.tinker.tconstruct.worldgen.TerrainGenEventHandler;
import mods.tinker.tconstruct.worldgen.village.TVillageTrades;
import mods.tinker.tconstruct.worldgen.village.VillageSmelteryHandler;
import mods.tinker.tconstruct.worldgen.village.VillageToolStationHandler;
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
import cpw.mods.fml.common.registry.VillagerRegistry;

/** TConstruct, the tool mod.
 * Craft your tools with style, then modify until the original is gone!
 * @author: mDiyo
 * @dependencies: IC2 API, MFR API
 */

@Mod(modid = "TConstruct", name = "TConstruct", version = "1.5.1_1.4.dev.8", dependencies = "required-after:Forge@[7.7.1.675,)")
@NetworkMod(serverSideRequired = false, clientSideRequired = true, channels = { "TConstruct" }, packetHandler = mods.tinker.tconstruct.util.network.TPacketHandler.class)
public class TConstruct
{
    /** The value of one ingot in millibuckets */
    public static final int ingotLiquidValue = 144;
    public static final int liquidUpdateAmount = 6;

    /* Instance of this mod, used for grabbing prototype fields */
    @Instance("TConstruct")
    public static TConstruct instance;
    /* Proxies for sides, used for graphics processing */
    @SidedProxy(clientSide = "mods.tinker.tconstruct.client.TProxyClient", serverSide = "mods.tinker.tconstruct.common.TProxyCommon")
    public static TProxyCommon proxy;
    
    public TConstruct()
    {
        System.out.println("[TConstruct] Preparing to take over the world");
    }

    @PreInit
    public void preInit (FMLPreInitializationEvent evt)
    {
        PHConstruct.initProps();
        TConstructRegistry.materialTab = new TabTools("TConstructMaterials");
        TConstructRegistry.toolTab = new TabTools("TConstructTools");
        TConstructRegistry.blockTab = new TabTools("TConstructBlocks");

        tableCasting = new LiquidCasting();
        basinCasting = new LiquidCasting();
        chiselDetailing = new Detailing();
        
        content = new TContent();

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

        VillagerRegistry.instance().registerVillagerType(78943, "/mods/tinker/textures/mob/villagertools.png");
        VillagerRegistry.instance().registerVillageTradeHandler(78943, new TVillageTrades());
        if (PHConstruct.addToVillages)
        {
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageToolStationHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageSmelteryHandler());
        }
        
        /*DimensionManager.registerProviderType(-7, TinkerWorldProvider.class, true);
        DimensionManager.registerDimension(-7, -7);*/
    }
    
    @Init
    public void init (FMLInitializationEvent event)
    {
        content.intermodCommunication();
    }

    @PostInit
    public void postInit (FMLPostInitializationEvent evt)
    {
        playerTracker = new TPlayerHandler();
        GameRegistry.registerPlayerTracker(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);

        content.modIntegration();
        content.createEntities();
        /*SkillRegistry.registerSkill("Wall Building", new WallBuilding());
        SkillRegistry.registerSkill("Jump", new Jump());*/
    }
    
    public static LiquidCasting getTableCasting()
    {
        return tableCasting;
    }
    
    public static LiquidCasting getBasinCasting()
    {
        return basinCasting;
    }
    
    public static Detailing getChiselDetailing()
    {
        return chiselDetailing;
    }

    public static TContent content;
    public static TEventHandler events;
    public static TPlayerHandler playerTracker;
    public static LiquidCasting tableCasting;
    public static LiquidCasting basinCasting;
    public static Detailing chiselDetailing;
    public static SkillRegistry skillRegistry;
}
