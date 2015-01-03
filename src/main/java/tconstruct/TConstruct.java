package tconstruct;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

import tconstruct.tools.TinkerMaterials;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the
 * original is gone!
 * 
 * @author mDiyo
 */


@Mod(modid = TConstruct.modID, name = "Tinkers' Construct", version = TConstruct.modVersion,
        dependencies = "required-after:Forge@[11.14.,);required-after:Mantle@[1.8-0.4,)")
        //dependencies = "required-after:Forge@[10.13.1.1217,);required-after:Mantle@[1.7.10-0.3.2,);after:MineFactoryReloaded;after:NotEnoughItems;after:Waila;after:ThermalExpansion;after:ThermalFoundation")
public class TConstruct
{
    public static final String modID = "TConstruct";
    public static final String modVersion = "${version}";
    /** The value of one ingot in millibuckets */
    public static final int ingotLiquidValue = 144;
    public static final int oreLiquidValue = ingotLiquidValue * 2;
    public static final int blockLiquidValue = ingotLiquidValue * 9;
    public static final int chunkLiquidValue = ingotLiquidValue / 2;
    public static final int nuggetLiquidValue = ingotLiquidValue / 9;
    public static final int stoneLiquidValue = ingotLiquidValue/8;

    public static final int liquidUpdateAmount = 6;
    


    /* Instance of this mod, used for grabbing prototype fields */
    @Mod.Instance(modID)
    public static TConstruct instance;
    /* Proxies for sides, used for graphics processing and client controls */
    //@SidedProxy(clientSide = "tconstruct.client.TProxyClient", serverSide = "tconstruct.common.TProxyCommon")
    //public static TProxyCommon proxy;

    /* Loads modules in a way that doesn't clutter the @Mod list */
    //public static PulseManager pulsar = new PulseManager(modID, new ForgeCFG("TinkersModules", "Modules: Disabling these will disable a chunk of the mod"));

    public TConstruct()
    {
        if (Loader.isModLoaded("Natura"))
        {
            LogManager.getLogger(modID).info("Natura, what are we going to do tomorrow night?");
            LogManager.getLogger("Natura").info("TConstruct, we're going to take over the world!");
        }
        else
        {
            LogManager.getLogger(modID).info("Preparing to take over the world");
        }
    }

    //Force the client and server to have or not have this mod
    @NetworkCheckHandler()
    public boolean matchModVersions (Map<String, String> remoteVersions, Side side)
    {
        return remoteVersions.containsKey(modID) && modVersion.equals(remoteVersions.get(modID));
    }

    @Mod.EventHandler
    public void preInit (FMLPreInitializationEvent event)
    {
        Util.logger = event.getModLog();

        TinkerMaterials.registerToolMaterials();
    }

}
