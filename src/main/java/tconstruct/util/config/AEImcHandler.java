package tconstruct.util.config;

import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.event.FMLInterModComms;

public class AEImcHandler
{
    private static List<String> spatialIOLogics = Arrays.asList("AdaptiveSmelteryLogic", "AdvancedDrawbridgeLogic", "AqueductLogic", "CastingBasinLogic", "CastingChannelLogic", "CastingTableLogic",
            "CraftingStationLogic", "DrawbridgeLogic", "DryingRackLogic", "EssenceExtractorLogic", "FaucetLogic", "FirestarterLogic", "FrypanLogic", "GolemPedestalLogic", "LavaTankLogic",
            "PartBuilderLogic", "PatternChestLogic", "RedwireLogic", "SmelteryDrainLogic", "SmelteryLogic", "StencilTableLogic", "TankAirLogic", "TileEntityLandmine", "ToolForgeLogic",
            "ToolStationLogic", "TowerFurnaceLogic", "MultiServantLogic");

    public static void registerForSpatialIO ()
    {
        for (String s : spatialIOLogics)
        {
            FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile", "tconstruct.blocks.logic." + s);
        }
    }
}
