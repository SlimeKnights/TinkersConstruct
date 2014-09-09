package tconstruct;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.relauncher.Side;
import java.util.*;
import mantle.pulsar.config.ForgeCFG;
import mantle.pulsar.control.PulseManager;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.*;
import tconstruct.achievements.*;
import tconstruct.api.TConstructAPI;
import tconstruct.armor.TinkerArmor;
import tconstruct.armor.player.*;
import tconstruct.common.TProxyCommon;
import tconstruct.library.*;
import tconstruct.library.crafting.*;
import tconstruct.mechworks.TinkerMechworks;
import tconstruct.mechworks.landmine.behavior.Behavior;
import tconstruct.mechworks.landmine.behavior.stackCombo.SpecialStackHandler;
import tconstruct.plugins.ic2.TinkerIC2;
import tconstruct.plugins.imc.*;
import tconstruct.plugins.mfr.TinkerMFR;
import tconstruct.plugins.nei.TinkerNEI;
import tconstruct.plugins.te4.TinkerTE4;
import tconstruct.plugins.waila.TinkerWaila;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.util.EnvironmentChecks;
import tconstruct.util.config.*;
import tconstruct.util.network.PacketPipeline;
import tconstruct.world.TinkerWorld;
import tconstruct.world.gen.SlimeIslandGen;
import tconstruct.world.village.*;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the
 * original is gone!
 * 
 * @author mDiyo
 */

@Mod(modid = "TConstruct", name = "TConstruct", version = "${version}", dependencies = "required-after:Forge@[10.13,);required-after:Mantle;after:MineFactoryReloaded;after:NotEnoughItems;after:Waila;after:ThermalExpansion")
public class TConstruct
{
    public static final String modVersion = "${version}";
    /** The value of one ingot in millibuckets */
    public static final int ingotLiquidValue = 144;
    public static final int oreLiquidValue = ingotLiquidValue * 2;
    public static final int blockLiquidValue = ingotLiquidValue * 9;
    public static final int chunkLiquidValue = ingotLiquidValue / 2;
    public static final int nuggetLiquidValue = ingotLiquidValue / 9;

    public static final int liquidUpdateAmount = 6;
    public static final String modID = "TConstruct";
    public static final Logger logger = LogManager.getLogger(modID);
    public static final PacketPipeline packetPipeline = new PacketPipeline();
    public static Random random = new Random();

    /* Instance of this mod, used for grabbing prototype fields */
    @Instance(modID)
    public static TConstruct instance;
    /* Proxies for sides, used for graphics processing and client controls */
    @SidedProxy(clientSide = "tconstruct.client.TProxyClient", serverSide = "tconstruct.common.TProxyCommon")
    public static TProxyCommon proxy;

    /* Loads modules in a way that doesn't clutter the @Mod list */
    public static PulseManager pulsar = new PulseManager(modID, new ForgeCFG("TinkersModules", "Modules: Disabling these will disable a chunk of the mod"));

    public TConstruct()
    {
        if (Loader.isModLoaded("Natura"))
        {
            logger.info("Natura, what are we going to do tomorrow night?");
            LogManager.getLogger("Natura").info("TConstruct, we're going to take over the world!");
        }
        else
        {
            logger.info("Preparing to take over the world");
        }

        EnvironmentChecks.verifyEnvironmentSanity();
    }

    //Force the client and server to have or not have this mod
    @NetworkCheckHandler()
    public boolean matchModVersions (Map<String, String> remoteVersions, Side side)
    {
        return remoteVersions.containsKey("TConstruct") && modVersion.equals(remoteVersions.get("TConstruct"));
    }

    @EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        PHConstruct.initProps(event.getModConfigurationDirectory());

        pulsar.registerPulse(new TinkerWorld());
        pulsar.registerPulse(new TinkerTools());
        pulsar.registerPulse(new TinkerSmeltery());
        pulsar.registerPulse(new TinkerMechworks());
        pulsar.registerPulse(new TinkerArmor());
        pulsar.registerPulse(new TinkerNEI());
        pulsar.registerPulse(new TinkerThaumcraft());
        pulsar.registerPulse(new TinkerWaila());
        pulsar.registerPulse(new TinkerBuildCraft());
        pulsar.registerPulse(new TinkerAE2());
        pulsar.registerPulse(new TinkerIC2());
        pulsar.registerPulse(new TinkerMystcraft());
        pulsar.registerPulse(new TinkerMFR());
        pulsar.registerPulse(new TinkerTE4());
        /*pulsar.registerPulse(new TinkerPrayers());
        pulsar.registerPulse(new TinkerCropify());*/

        TConstructRegistry.materialTab = new TConstructCreativeTab("TConstructMaterials");
        TConstructRegistry.toolTab = new TConstructCreativeTab("TConstructTools");
        TConstructRegistry.partTab = new TConstructCreativeTab("TConstructParts");
        TConstructRegistry.blockTab = new TConstructCreativeTab("TConstructBlocks");
        TConstructRegistry.equipableTab = new TConstructCreativeTab("TConstructEquipables");

        tableCasting = new LiquidCasting();
        basinCasting = new LiquidCasting();
        chiselDetailing = new Detailing();

        //GameRegistry.registerFuelHandler(content);

        playerTracker = new TPlayerHandler();
        // GameRegistry.registerPlayerTracker(playerTracker);
        FMLCommonHandler.instance().bus().register(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);
        NetworkRegistry.INSTANCE.registerGuiHandler(TConstruct.instance, proxy);

        pulsar.preInit(event);

        if (PHConstruct.achievementsEnabled)
        {
            TAchievements.addDefaultAchievements();
        }

        if (PHConstruct.addToVillages)
        {
            // adds to the villager spawner egg
            VillagerRegistry.instance().registerVillagerId(78943);
            // moved down, not needed if 'addToVillages' is false
            VillagerRegistry.instance().registerVillageTradeHandler(78943, new TVillageTrades());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageToolStationHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageSmelteryHandler());
            MapGenStructureIO.func_143031_a(ComponentToolWorkshop.class, "TConstruct:ToolWorkshopStructure");
            MapGenStructureIO.func_143031_a(ComponentSmeltery.class, "TConstruct:SmelteryStructure");
        }

        TConstructAPI.PROP_NAME = TPlayerStats.PROP_NAME;
    }

    @EventHandler
    public void init (FMLInitializationEvent event)
    {
        packetPipeline.initalise();
        if (event.getSide() == Side.CLIENT)
        {
            //MinecraftForge.EVENT_BUS.register(new EventCloakRender());
        }

        DimensionBlacklist.getBadBimensions();
        GameRegistry.registerWorldGenerator(new SlimeIslandGen(TinkerWorld.slimePool, 2), 2);

        pulsar.init(event);
    }

    @EventHandler
    public void postInit (FMLPostInitializationEvent event)
    {
        packetPipeline.postInitialise();
        Behavior.registerBuiltInBehaviors();
        SpecialStackHandler.registerBuiltInStackHandlers();

        proxy.initialize();
        pulsar.postInit(event);

        if (PHConstruct.achievementsEnabled)
        {
            TAchievements.registerAchievementPane();
            MinecraftForge.EVENT_BUS.register(new AchievementEvents());
        }
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

    public static TPlayerHandler playerTracker;
    public static LiquidCasting tableCasting;
    public static LiquidCasting basinCasting;
    public static Detailing chiselDetailing;
}
