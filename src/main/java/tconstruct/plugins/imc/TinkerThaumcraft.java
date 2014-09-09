package tconstruct.plugins.imc;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.world.TinkerWorld;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers Thaumcraft Compatibility", description = "Tinkers Construct compatibility for Thaumcraft", modsRequired = "Thaumcraft", pulsesRequired = "Tinkers' World")
public class TinkerThaumcraft
{

    @Handler
    public void init (FMLInitializationEvent event)
    {
        TConstruct.logger.info("Thaumcraft detected. Registering harvestables.");
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 13));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 14));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 15));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerrySecond, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerrySecond, 1, 13));
    }

}
