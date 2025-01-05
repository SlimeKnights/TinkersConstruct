package slimeknights.tconstruct.tools;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierCreativeTab;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.part.ToolPartItem;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.tools.item.RepairKitItem;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

public final class TinkerToolParts extends TinkerModule {
  /** Tab for all tool parts */
  public static final CreativeModeTab TAB_TOOL_PARTS = new SupplierCreativeTab(TConstruct.MOD_ID, "tool_parts", () -> {
    MaterialVariantId material;
    if (MaterialRegistry.isFullyLoaded()) {
      material = ToolBuildHandler.RANDOM.getMaterial(HeadMaterialStats.ID, RandomSource.create());
    } else {
      material = ToolBuildHandler.getRenderMaterial(0);
    }
    return TinkerToolParts.pickHead.get().withMaterialForDisplay(material);
  });
  private static final Item.Properties PARTS_PROPS = new Item.Properties().tab(TAB_TOOL_PARTS);

  // repair kit, technically a head so it filters to things useful for repair
  public static final ItemObject<RepairKitItem> repairKit = ITEMS.register("repair_kit", () -> new RepairKitItem(PARTS_PROPS));

  // rock
  public static final ItemObject<ToolPartItem> pickHead = ITEMS.register("pick_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> hammerHead = ITEMS.register("hammer_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  // axe
  public static final ItemObject<ToolPartItem> smallAxeHead = ITEMS.register("small_axe_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> broadAxeHead = ITEMS.register("broad_axe_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  // blades
  public static final ItemObject<ToolPartItem> smallBlade = ITEMS.register("small_blade", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> broadBlade = ITEMS.register("broad_blade", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  // plates
  public static final ItemObject<ToolPartItem> roundPlate = ITEMS.register("round_plate", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> largePlate = ITEMS.register("large_plate", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  // bows
  public static final ItemObject<ToolPartItem> bowLimb = ITEMS.register("bow_limb", () -> new ToolPartItem(PARTS_PROPS, LimbMaterialStats.ID));
  public static final ItemObject<ToolPartItem> bowGrip = ITEMS.register("bow_grip", () -> new ToolPartItem(PARTS_PROPS, GripMaterialStats.ID));
  public static final ItemObject<ToolPartItem> bowstring = ITEMS.register("bowstring", () -> new ToolPartItem(PARTS_PROPS, StatlessMaterialStats.BOWSTRING.getIdentifier()));
  // other parts
  public static final ItemObject<ToolPartItem> toolBinding = ITEMS.register("tool_binding", () -> new ToolPartItem(PARTS_PROPS, StatlessMaterialStats.BINDING.getIdentifier()));
  public static final ItemObject<ToolPartItem> toolHandle = ITEMS.register("tool_handle", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
  public static final ItemObject<ToolPartItem> toughHandle = ITEMS.register("tough_handle", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
  // armor
  public static final EnumObject<ArmorSlotType,ToolPartItem> plating = ITEMS.registerEnum(ArmorSlotType.values(), "plating", type -> new ToolPartItem(PARTS_PROPS, PlatingMaterialStats.TYPES.get(type.getIndex()).getId()));
  public static final ItemObject<ToolPartItem> maille = ITEMS.register("maille", () -> new ToolPartItem(PARTS_PROPS, StatlessMaterialStats.MAILLE.getIdentifier()));
  public static final ItemObject<ToolPartItem> shieldCore = ITEMS.register("shield_core", () -> new ToolPartItem(PARTS_PROPS, StatlessMaterialStats.SHIELD_CORE.getIdentifier()));

}
