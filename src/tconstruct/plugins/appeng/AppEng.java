package tconstruct.plugins.appeng;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkMod;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Mod(modid = "TConstruct|CompatAE", name = "TConstruct Compat: AE", version = "0.0.1", dependencies = "after:AppliedEnergistics;required-after:TConstruct")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class AppEng {

    public static Logger logger = Logger.getLogger("TConstruct AE");
    private static List<String> spatialIOLogics = Arrays.asList(
        "AdaptiveSmelteryLogic",
        "AdvancedDrawbridgeLogic",
        "AqueductLogic",
        "CastingBasinLogic",
        "CastingChannelLogic",
        "CastingTableLogic",
        "CraftingStationLogic",
        "DrawbridgeLogic",
        "DryingRackLogic",
        "EssenceExtractorLogic",
        "FaucetLogic",
        "FirestarterLogic",
        "FrypanLogic",
        "GolemPedestalLogic",
        "LavaTankLogic",
        "PartBuilderLogic",
        "PatternChestLogic",
        "RedwireLogic",
        "SmelteryDrainLogic",
        "SmelteryLogic",
        "StencilTableLogic",
        "TankAirLogic",
        "TileEntityLandmine",
        "ToolForgeLogic",
        "ToolStationLogic",
        "TowerFurnaceLogic",
        "MultiServantLogic"
    );

    @EventHandler
    public static void load (FMLInitializationEvent ev)
    {
        logger.setParent(FMLCommonHandler.instance().getFMLLogger());

        if (!Loader.isModLoaded("AppliedEnergistics")) {
            logger.warning("Applied Energistics missing - TConstruct Compat: AE not loading.");
            return;
        } try {
            logger.info("Applied Energistics detected. Registering for Spatial IO.");
            registerForSpatialIO();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registerForSpatialIO() {
        for (int i = 0; i < spatialIOLogics.size(); i++) {
            FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile", "tconstruct.blocks.logic." + spatialIOLogics.get(i));
        }
    }

}
