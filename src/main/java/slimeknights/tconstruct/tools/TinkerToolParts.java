package slimeknights.tconstruct.tools;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;
import slimeknights.tconstruct.tools.item.RepairKitItem;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class TinkerToolParts extends TinkerModule {
  /** Tab for all tool parts or tool components with many variants */
  public static final RegistryObject<CreativeModeTab> tabToolParts = CREATIVE_TABS.register(
    "tool_parts", () -> CreativeModeTab.builder().title(TConstruct.makeTranslation("itemGroup", "tool_parts"))
                                       .icon(() -> {
                                         MaterialVariantId material;
                                         if (MaterialRegistry.isFullyLoaded()) {
                                           material = ToolBuildHandler.RANDOM.getMaterial(HeadMaterialStats.ID, RandomSource.create());
                                         } else {
                                           material = ToolBuildHandler.getRenderMaterial(0);
                                         }
                                         return TinkerToolParts.pickHead.get().withMaterialForDisplay(material);
                                       })
                                       .displayItems(TinkerToolParts::addTabItems)
                                       .withTabsBefore(TinkerTools.tabTools.getId())
                                       .withSearchBar()
                                       .build());

  // repair kit, technically a head so it filters to things useful for repair
  public static final ItemObject<RepairKitItem> repairKit = ITEMS.register("repair_kit", () -> new RepairKitItem(ITEM_PROPS));

  // rock
  public static final ItemObject<ToolPartItem> pickHead = ITEMS.register("pick_head", () -> new ToolPartItem(ITEM_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> hammerHead = ITEMS.register("hammer_head", () -> new ToolPartItem(ITEM_PROPS, HeadMaterialStats.ID));
  // axe
  public static final ItemObject<ToolPartItem> smallAxeHead = ITEMS.register("small_axe_head", () -> new ToolPartItem(ITEM_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> broadAxeHead = ITEMS.register("broad_axe_head", () -> new ToolPartItem(ITEM_PROPS, HeadMaterialStats.ID));
  // blades
  public static final ItemObject<ToolPartItem> smallBlade = ITEMS.register("small_blade", () -> new ToolPartItem(ITEM_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> broadBlade = ITEMS.register("broad_blade", () -> new ToolPartItem(ITEM_PROPS, HeadMaterialStats.ID));
  // plates
  public static final ItemObject<ToolPartItem> adzeHead = ITEMS.register("adze_head", () -> new ToolPartItem(ITEM_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> largePlate = ITEMS.register("large_plate", () -> new ToolPartItem(ITEM_PROPS, HeadMaterialStats.ID));
  // bows
  public static final ItemObject<ToolPartItem> bowLimb = ITEMS.register("bow_limb", () -> new ToolPartItem(ITEM_PROPS, LimbMaterialStats.ID));
  public static final ItemObject<ToolPartItem> bowGrip = ITEMS.register("bow_grip", () -> new ToolPartItem(ITEM_PROPS, GripMaterialStats.ID));
  public static final ItemObject<ToolPartItem> bowstring = ITEMS.register("bowstring", () -> new ToolPartItem(ITEM_PROPS, StatlessMaterialStats.BOWSTRING.getIdentifier()));
  // other parts
  public static final ItemObject<ToolPartItem> toolBinding = ITEMS.register("tool_binding", () -> new ToolPartItem(ITEM_PROPS, StatlessMaterialStats.BINDING.getIdentifier()));
  public static final ItemObject<ToolPartItem> toughBinding = ITEMS.register("tough_binding", () -> new ToolPartItem(ITEM_PROPS, StatlessMaterialStats.BINDING.getIdentifier()));
  public static final ItemObject<ToolPartItem> toolHandle = ITEMS.register("tool_handle", () -> new ToolPartItem(ITEM_PROPS, HandleMaterialStats.ID));
  public static final ItemObject<ToolPartItem> toughHandle = ITEMS.register("tough_handle", () -> new ToolPartItem(ITEM_PROPS, HandleMaterialStats.ID));
  // armor
  public static final EnumObject<ArmorItem.Type,ToolPartItem> plating = ITEMS.registerEnum(ArmorItem.Type.values(), "plating", type -> new ToolPartItem(ITEM_PROPS, PlatingMaterialStats.TYPES.get(type.ordinal()).getId()));
  public static final ItemObject<ToolPartItem> maille = ITEMS.register("maille", () -> new ToolPartItem(ITEM_PROPS, StatlessMaterialStats.MAILLE.getIdentifier()));
  public static final ItemObject<ToolPartItem> shieldCore = ITEMS.register("shield_core", () -> new ToolPartItem(ITEM_PROPS, StatlessMaterialStats.SHIELD_CORE.getIdentifier()));

  /** Adds all relevant items to the creative tab */
  private static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output tab) {
    Consumer<ItemStack> output = tab::accept;
    accept(output, repairKit);
    // small heads
    accept(output, pickHead);
    accept(output, smallAxeHead);
    accept(output, smallBlade);
    accept(output, adzeHead);
    // large heads
    accept(output, hammerHead);
    accept(output, broadAxeHead);
    accept(output, broadBlade);
    accept(output, largePlate);
    // binding and rods
    accept(output, toolHandle);
    accept(output, toolBinding);
    accept(output, toughHandle);
    accept(output, toughBinding);
    // ranged
    accept(output, bowLimb);
    accept(output, bowGrip);
    accept(output, bowstring);
    // plating, pair each one with the dummy plating item
    for (ArmorItem.Type type : ArmorItem.Type.values()) {
      tab.accept(TinkerSmeltery.dummyPlating.get(type));
      plating.get(type).addVariants(output, "");
    }
    accept(output, maille);
    accept(output, shieldCore);

    // end with modifier crystal dynamic listing
    ModifierCrystalItem.addVariants(output);
  }

  /** Adds a tool part to the tab */
  private static void accept(Consumer<ItemStack> output, Supplier<? extends IMaterialItem> item) {
    item.get().addVariants(output, "");
  }
}
