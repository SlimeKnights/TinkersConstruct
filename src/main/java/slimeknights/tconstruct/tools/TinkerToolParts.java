package slimeknights.tconstruct.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierItemGroup;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tools.item.ToolPartItem;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.ArrayList;
import java.util.List;

public final class TinkerToolParts extends TinkerModule {
  /** Tab for all tool parts */
  public static final ItemGroup TAB_TOOL_PARTS = new SupplierItemGroup(TConstruct.modID, "tool_parts", () -> {
    List<IMaterial> materials = new ArrayList<>(MaterialRegistry.getInstance().getMaterials());
    if (materials.isEmpty()) {
      return new ItemStack(TinkerToolParts.pickaxeHead);
    }
    return TinkerToolParts.pickaxeHead.get().getItemstackWithMaterial(materials.get(TConstruct.random.nextInt(materials.size())));
  });
  private static final Item.Properties PARTS_PROPS = new Item.Properties().group(TAB_TOOL_PARTS);

  public static final ItemObject<ToolPartItem> pickaxeHead = ITEMS.register("pickaxe_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> hammerHead = ITEMS.register("hammer_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> axeHead = ITEMS.register("axe_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> kamaHead = ITEMS.register("kama_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> swordBlade = ITEMS.register("sword_blade", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> toolBinding = ITEMS.register("tool_binding", () -> new ToolPartItem(PARTS_PROPS, ExtraMaterialStats.ID));
  public static final ItemObject<ToolPartItem> largePlate = ITEMS.register("large_plate", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> toolRod = ITEMS.register("tool_rod", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
  public static final ItemObject<ToolPartItem> toughToolRod = ITEMS.register("tough_tool_rod", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
}
