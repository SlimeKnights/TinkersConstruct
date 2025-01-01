package slimeknights.tconstruct.library;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.tconstruct.TConstruct;

import java.util.Locale;

/** Custom transform types used for tinkers item rendering */
public class TinkerItemDisplays {
  private TinkerItemDisplays() {}

  public static void init() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(TinkerItemDisplays::registerDisplay);
  }

  /** Used by the melter and smeltery for display of items its melting */
  public static ItemDisplayContext MELTER = create("melter", ItemDisplayContext.NONE);
  /** Used by the part builder, crafting station, tinkers station, and tinker anvil */
  public static ItemDisplayContext TABLE = create("table", ItemDisplayContext.NONE);
  /** Used by the casting table for item rendering */
  public static ItemDisplayContext CASTING_TABLE = create("casting_table", ItemDisplayContext.FIXED);
  /** Used by the casting basin for item rendering */
  public static ItemDisplayContext CASTING_BASIN = create("casting_basin", ItemDisplayContext.NONE);

  /** Creates a transform type */
  private static ItemDisplayContext create(String name, ItemDisplayContext fallback) {
    String key = "TCONSTRUCT_" + name.toUpperCase(Locale.ROOT);
    if (fallback == ItemDisplayContext.NONE) {
      return ItemDisplayContext.create(key, TConstruct.getResource(name), null);
    }
    return ItemDisplayContext.create(key, TConstruct.getResource(name), fallback);
  }

  /** Registers all item display types */
  private static void registerDisplay(RegisterEvent event) {
    if (event.getRegistryKey() == ForgeRegistries.Keys.DISPLAY_CONTEXTS) {
      IForgeRegistry<ItemDisplayContext> registry = ForgeRegistries.DISPLAY_CONTEXTS.get();
      register(registry, MELTER);
      register(registry, TABLE);
      register(registry, CASTING_TABLE);
      register(registry, CASTING_BASIN);
    }
  }

  /** Registers a display type */
  private static void register(IForgeRegistry<ItemDisplayContext> registry, ItemDisplayContext context) {
    registry.register(new ResourceLocation(context.getSerializedName()), context);
  }
}
