package tconstruct.plugins.imc;

import java.util.Arrays;
import java.util.List;

import mantle.module.ILoadableModule;
import tconstruct.TConstruct;
import cpw.mods.fml.common.event.FMLInterModComms;

public class AppEng implements ILoadableModule
{

    private static List<String> spatialIOLogics = Arrays.asList("AdaptiveSmelteryLogic", "AqueductLogic", "CastingBasinLogic", "CastingChannelLogic", "CastingTableLogic", "CraftingStationLogic",
            "DryingRackLogic", "EssenceExtractorLogic", "FaucetLogic", "FrypanLogic", "GolemPedestalLogic", "LavaTankLogic", "PartBuilderLogic", "PatternChestLogic", "SmelteryDrainLogic",
            "SmelteryLogic", "StencilTableLogic", "TankAirLogic", "TileEntityLandmine", "ToolForgeLogic", "ToolStationLogic", "TowerFurnaceLogic", "MultiServantLogic");

    @SuppressWarnings("unused")
    public static String modId = "AppliedEnergistics";

    @Override
    public void preInit ()
    {

    }

    @Override
    public void init ()
    {
        TConstruct.logger.info("[AppEng] Registering for Spatial IO.");
        for (String s : spatialIOLogics)
        {
            FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile", "tconstruct.blocks.logic." + s);
        }
    }

    @Override
    public void postInit ()
    {

    }

}
