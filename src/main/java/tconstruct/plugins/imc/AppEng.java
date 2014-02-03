package tconstruct.plugins.imc;

import cpw.mods.fml.common.event.FMLInterModComms;
import tconstruct.TConstruct;
import tconstruct.plugins.ICompatPlugin;

import java.util.Arrays;
import java.util.List;

public class AppEng implements ICompatPlugin {

    private static List<String> spatialIOLogics = Arrays.asList("AdaptiveSmelteryLogic", "AqueductLogic", "CastingBasinLogic", "CastingChannelLogic", "CastingTableLogic",
            "CraftingStationLogic", "DryingRackLogic", "EssenceExtractorLogic", "FaucetLogic", "FrypanLogic", "GolemPedestalLogic", "LavaTankLogic", "PartBuilderLogic",
            "PatternChestLogic", "SmelteryDrainLogic", "SmelteryLogic", "StencilTableLogic", "TankAirLogic", "TileEntityLandmine", "ToolForgeLogic", "ToolStationLogic",
            "TowerFurnaceLogic", "MultiServantLogic");

    @Override
    public String getModId() {
        return "AppliedEnergistics";
    }

    @Override
    public void preInit() {

    }

    @Override
    public void init() {
        TConstruct.logger.info("[AppEng] Registering for Spatial IO.");
        for (String s : spatialIOLogics)
        {
            FMLInterModComms.sendMessage("AppliedEnergistics", "movabletile", "tconstruct.blocks.logic." + s);
        }
    }

    @Override
    public void postInit() {

    }

}
