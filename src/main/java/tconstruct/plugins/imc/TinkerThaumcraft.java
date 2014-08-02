package tconstruct.plugins.imc;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.TConstruct;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.world.TinkerWorld;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers Thaumcraft addon", description = "The Thaumcraft addon for Tinkers.", modsRequired = "Thaumcraft", forced = true)
public class TinkerThaumcraft {

    @Handler
    public void init(FMLInitializationEvent event)
    {
        TConstruct.logger.info("[Thaumcraft] Registering harvestables.");
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 13));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 14));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 15));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerrySecond, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerrySecond, 1, 13));
    }

}
