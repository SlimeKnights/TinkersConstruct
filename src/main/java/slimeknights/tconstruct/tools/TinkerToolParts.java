package slimeknights.tconstruct.tools;

import net.minecraft.item.Item;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.library.tinkering.ToolPart;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

public final class TinkerToolParts extends TinkerModule {

  private static final Item.Properties PARTS_PROPS = new Item.Properties().group(TinkerRegistry.tabParts);

  public static final ItemObject<ToolPart> pickaxeHead = ITEMS.register("pickaxe_head", () -> new ToolPart(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPart> shovelHead = ITEMS.register("shovel_head", () -> new ToolPart(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPart> swordBlade = ITEMS.register("sword_blade", () -> new ToolPart(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPart> smallBinding = ITEMS.register("small_binding", () -> new ToolPart(PARTS_PROPS, ExtraMaterialStats.ID));
  public static final ItemObject<ToolPart> wideGuard = ITEMS.register("wide_guard", () -> new ToolPart(PARTS_PROPS, ExtraMaterialStats.ID));
  public static final ItemObject<ToolPart> toolRod = ITEMS.register("tool_rod", () -> new ToolPart(PARTS_PROPS, HandleMaterialStats.ID));
}
