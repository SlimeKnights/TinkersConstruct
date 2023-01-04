package tconstruct.gadgets;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Locale;
import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tconstruct.TConstruct;
import tconstruct.gadgets.item.ItemSlimeBoots;
import tconstruct.gadgets.item.ItemSlimeSling;
import tconstruct.library.SlimeBounceHandler;
import tconstruct.library.TConstructRegistry;

@Pulse(id = "Tinkers' Gadgets", description = "All the fun toys.", forced = true)
public class TinkerGadgets {

    public static final String PulseId = "TinkerGadgets";
    public static final Logger log = LogManager.getLogger(PulseId);

    // Gadgets
    public static ItemSlimeSling slimeSling;
    public static ItemSlimeBoots slimeBoots;

    @Handler
    public void preInit(FMLPreInitializationEvent event) {
        log.info("Pre Init");
        SlimeBounceHandler.init();
        slimeSling = registerItem(new ItemSlimeSling(), "slimesling");
        slimeBoots = registerItem(new ItemSlimeBoots(), "slime_boots");
    }

    @Handler
    public void init(FMLInitializationEvent event) {
        log.info("Init");
        String ore = "blockSlime";

        GameRegistry.addRecipe(
                new ShapedOreRecipe(new ItemStack(slimeBoots), "   ", "s s", "b b", 's', "slimeball", 'b', ore));
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(slimeSling), "fbf", "s s", " s ", 'f', Items.string, 's', "slimeball", 'b', ore));
        TConstructRegistry.gadgetsTab.init(new ItemStack(slimeBoots));
    }

    @Handler
    public void postInit(FMLPostInitializationEvent evt) {}

    public static String resource(String res) {
        return String.format("%s:%s", "tinker", res);
    }

    public static ResourceLocation getResource(String res) {
        return new ResourceLocation("tinker", res);
    }

    public static String prefix(String name) {
        return String.format("%s.%s", TConstruct.modID, name.toLowerCase(Locale.US));
    }

    protected static <T extends Item> T registerItem(T item, String name) {
        if (!name.equals(name.toLowerCase(Locale.US))) {
            throw new IllegalArgumentException(
                    String.format("Unlocalized names need to be all lowercase! Item: %s", name));
        }
        item.setUnlocalizedName(prefix(name));
        GameRegistry.registerItem(item, name);
        return item;
    }
}
