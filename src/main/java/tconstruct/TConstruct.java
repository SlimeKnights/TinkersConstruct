package tconstruct;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import mantle.pulsar.config.ForgeCFG;
import mantle.pulsar.control.PulseManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tconstruct.achievements.AchievementEvents;
import tconstruct.achievements.TAchievements;
import tconstruct.api.TConstructAPI;
import tconstruct.armor.TinkerArmor;
import tconstruct.armor.player.TPlayerHandler;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.common.TProxyCommon;
import tconstruct.library.TConstructCreativeTab;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.Detailing;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.mechworks.TinkerMechworks;
import tconstruct.mechworks.landmine.behavior.Behavior;
import tconstruct.mechworks.landmine.behavior.stackCombo.SpecialStackHandler;
import tconstruct.plugins.gears.TinkerGears;
import tconstruct.plugins.fmp.TinkerFMP;
import tconstruct.plugins.ic2.TinkerIC2;
import tconstruct.plugins.imc.TinkerAE2;
import tconstruct.plugins.imc.TinkerBuildCraft;
import tconstruct.plugins.imc.TinkerMystcraft;
import tconstruct.plugins.TinkerThaumcraft;
import tconstruct.plugins.imc.TinkerRfTools;
import tconstruct.plugins.mfr.TinkerMFR;
import tconstruct.plugins.te4.TinkerTE4;
import tconstruct.plugins.te4.TinkersThermalFoundation;
import tconstruct.plugins.ubc.TinkerUBC;
import tconstruct.plugins.waila.TinkerWaila;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.util.EnvironmentChecks;
import tconstruct.util.IMCHandler;
import tconstruct.util.config.DimensionBlacklist;
import tconstruct.util.config.PHConstruct;
import tconstruct.util.network.PacketPipeline;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.world.TinkerWorld;
import tconstruct.world.gen.SlimeIslandGen;
import tconstruct.world.village.*;

import java.util.Map;
import java.util.Random;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the
 * original is gone!
 * 
 * @author mDiyo
 */

@Mod(modid = "TConstruct", name = "TConstruct", version = "${version}",
        dependencies = "required-after:Forge@[10.13.3.1384,11.14);" +
                "required-after:Mantle@[1.7.10-0.3.2,);" +
                "after:MineFactoryReloaded@[1.7.10R2.8.0RC7,);" +
                "after:ThermalExpansion@[1.7.10R4.0.0RC2,);" +
                "after:ThermalFoundation@[1.7.10R1.0.0RC3,);" +
                "after:armourersWorkshop@[1.7.10-0.28.0,);" +
                "after:CoFHAPI|energy;" +
                "after:CoFHCore;" +
                "after:battlegear2;" +
                "after:ZeldaItemAPI;" +
                "after:DynamicSkillsAPI;" +
                "after:NotEnoughItems;" +
                "after:Waila;" +
                "before:UndergroundBiomes")
public class TConstruct
{
    public static final String modVersion = "${version}";
    /** The value of one ingot in millibuckets */
    public static final int ingotLiquidValue = 144;
    public static final int oreLiquidValue = ingotLiquidValue * 2;
    public static final int blockLiquidValue = ingotLiquidValue * 9;
    public static final int chunkLiquidValue = ingotLiquidValue / 2;
    public static final int nuggetLiquidValue = ingotLiquidValue / 9;
    public static final int stoneLiquidValue = ingotLiquidValue/8;

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
        pulsar.registerPulse(new TinkerWeaponry());
        pulsar.registerPulse(new TinkerThaumcraft());
        pulsar.registerPulse(new TinkerWaila());
        pulsar.registerPulse(new TinkerBuildCraft());
        pulsar.registerPulse(new TinkerAE2());
        pulsar.registerPulse(new TinkerIC2());
        pulsar.registerPulse(new TinkerMystcraft());
        pulsar.registerPulse(new TinkerMFR());
        pulsar.registerPulse(new TinkerTE4());
        pulsar.registerPulse(new TinkersThermalFoundation());
        pulsar.registerPulse(new TinkerFMP());
        pulsar.registerPulse(new TinkerUBC());
        pulsar.registerPulse(new TinkerGears());
        pulsar.registerPulse(new TinkerRfTools());
        /*pulsar.registerPulse(new TinkerPrayers());
        pulsar.registerPulse(new TinkerCropify());*/

        TConstructRegistry.materialTab = new TConstructCreativeTab("TConstructMaterials");
        TConstructRegistry.toolTab = new TConstructCreativeTab("TConstructTools");
        TConstructRegistry.partTab = new TConstructCreativeTab("TConstructParts");
        TConstructRegistry.blockTab = new TConstructCreativeTab("TConstructBlocks");
        TConstructRegistry.equipableTab = new TConstructCreativeTab("TConstructEquipables");
        TConstructRegistry.weaponryTab = new TConstructCreativeTab("TConstructWeaponry");

        tableCasting = new LiquidCasting();
        basinCasting = new LiquidCasting();
        chiselDetailing = new Detailing();

        //GameRegistry.registerFuelHandler(content);

        playerTracker = new TPlayerHandler();
        // GameRegistry.registerPlayerTracker(playerTracker);
        FMLCommonHandler.instance().bus().register(playerTracker);
        MinecraftForge.EVENT_BUS.register(playerTracker);
        NetworkRegistry.INSTANCE.registerGuiHandler(TConstruct.instance, proxy);

        if (PHConstruct.globalDespawn != 6000)
            MinecraftForge.EVENT_BUS.register(new Spawntercepter());

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
            if(PHConstruct.allowVillagerTrading)
                VillagerRegistry.instance().registerVillageTradeHandler(78943, new TVillageTrades());

            VillagerRegistry.instance().registerVillageCreationHandler(new VillageToolStationHandler());
            MapGenStructureIO.func_143031_a(ComponentToolWorkshop.class, "TConstruct:ToolWorkshopStructure");
            if(pulsar.isPulseLoaded("Tinkers' Smeltery")) {
                VillagerRegistry.instance().registerVillageCreationHandler(new VillageSmelteryHandler());
                MapGenStructureIO.func_143031_a(ComponentSmeltery.class, "TConstruct:SmelteryStructure");
            }
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

    /* IMC Mod Support */
    @EventHandler
    public void handleIMC(FMLInterModComms.IMCEvent e)
    {
        IMCHandler.processIMC(e.getMessages());
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent evt)
    {
        IMCHandler.processIMC(FMLInterModComms.fetchRuntimeMessages(this));
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

    @Mod.EventHandler
    public void missingMapping(FMLMissingMappingsEvent event) {
        // this will be called because the air-block got removed
        for(FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
            if(mapping.name.equals("TConstruct:TankAir"))
                mapping.ignore();
        }
    }

    public static class Spawntercepter {
        @SubscribeEvent
        public void onEntitySpawn(EntityJoinWorldEvent event)
        {
            if(event.entity instanceof EntityItem)
            {
                EntityItem ourGuy = (EntityItem)event.entity;
                if (ourGuy.lifespan == 6000) {
                    ourGuy.lifespan = PHConstruct.globalDespawn;
                }
            }
        }
    }
}
