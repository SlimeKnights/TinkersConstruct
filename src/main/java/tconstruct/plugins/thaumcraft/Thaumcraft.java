package tconstruct.plugins.thaumcraft;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.plugins.ICompatPlugin;

public class Thaumcraft implements ICompatPlugin {

    @Override
    public String getModId() {
        return "Thaumcraft";
    }

    @Override
    public void preInit() {

    }

    @Override
    public void init() {
        TConstruct.logger.info("[Thaumcraft] Registering harvestables.");
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TContent.oreBerry, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TContent.oreBerry, 1, 13));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TContent.oreBerry, 1, 14));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TContent.oreBerry, 1, 15));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TContent.oreBerrySecond, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TContent.oreBerrySecond, 1, 13));
    }

    @Override
    public void postInit() {

    }

}
