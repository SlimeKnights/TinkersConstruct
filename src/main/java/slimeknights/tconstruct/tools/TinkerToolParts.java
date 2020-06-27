package slimeknights.tconstruct.tools;

import net.minecraft.item.Item;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.library.tinkering.ToolPartItem;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class TinkerToolParts extends TinkerModule {

  private static final Item.Properties PARTS_PROPS = new Item.Properties().group(TinkerRegistry.tabParts);

  public static final ItemObject<ToolPartItem> pickaxeHead = ITEMS.register("pickaxe_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> hammerHead = ITEMS.register("hammer_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> shovelHead = ITEMS.register("shovel_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> swordBlade = ITEMS.register("sword_blade", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> smallBinding = ITEMS.register("small_binding", () -> new ToolPartItem(PARTS_PROPS, ExtraMaterialStats.ID));
  public static final ItemObject<ToolPartItem> wideGuard = ITEMS.register("wide_guard", () -> new ToolPartItem(PARTS_PROPS, ExtraMaterialStats.ID));
  public static final ItemObject<ToolPartItem> largePlate = ITEMS.register("large_plate", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> toolRod = ITEMS.register("tool_rod", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
  public static final ItemObject<ToolPartItem> toughToolRod = ITEMS.register("tough_tool_rod", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
}
