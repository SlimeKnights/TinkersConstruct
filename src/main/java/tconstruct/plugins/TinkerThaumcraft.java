package tconstruct.plugins;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import net.minecraft.item.ItemStack;
import tconstruct.TConstruct;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.modifiers.armor.AModThaumicVision;
import tconstruct.world.TinkerWorld;
import thaumcraft.api.ItemApi;

@ObjectHolder(TConstruct.modID)
@Pulse(
        id = "Tinkers Thaumcraft Compatibility",
        description = "Tinkers Construct compatibility for Thaumcraft",
        modsRequired = "Thaumcraft",
        pulsesRequired = "Tinkers' World",
        forced = true)
public class TinkerThaumcraft {

    @Handler
    public void init(FMLInitializationEvent event) {
        TConstruct.logger.info("Thaumcraft detected. Registering harvestables.");
        sendIMC();
        registerModifiers();
    }

    private void sendIMC() {
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 12));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 13));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 14));
        FMLInterModComms.sendMessage("Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerry, 1, 15));
        FMLInterModComms.sendMessage(
                "Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerrySecond, 1, 12));
        FMLInterModComms.sendMessage(
                "Thaumcraft", "harvestClickableCrop", new ItemStack(TinkerWorld.oreBerrySecond, 1, 13));
    }

    private void registerModifiers() {
        ItemStack thaumometer = ItemApi.getItem("itemThaumometer", 0);
        if (thaumometer != null) {
            // Thaumometer Vision!
            ModifyBuilder.registerModifier(new AModThaumicVision(thaumometer));
        }
    }
}
