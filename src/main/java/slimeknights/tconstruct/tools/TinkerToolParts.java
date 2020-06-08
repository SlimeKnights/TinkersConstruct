package slimeknights.tconstruct.tools;

import net.minecraft.item.Item;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.library.tinkering.MaterialItem;

public final class TinkerToolParts extends TinkerModule {

  private static final Item.Properties PARTS_PROPS = new Item.Properties().group(TinkerRegistry.tabParts);

  public static final ItemObject<MaterialItem> pickaxeHead = ITEMS.register("pickaxe_head", () -> new MaterialItem(PARTS_PROPS));
  public static final ItemObject<MaterialItem> smallBinding = ITEMS.register("small_binding", () -> new MaterialItem(PARTS_PROPS));
  public static final ItemObject<MaterialItem> toolRod = ITEMS.register("tool_rod", () -> new MaterialItem(PARTS_PROPS));
}
