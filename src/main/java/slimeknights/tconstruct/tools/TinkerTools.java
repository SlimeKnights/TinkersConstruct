package slimeknights.tconstruct.tools;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.library.utils.SupplierItemGroup;
import slimeknights.tconstruct.tools.harvest.AxeTool;
import slimeknights.tconstruct.tools.harvest.HammerTool;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;
import slimeknights.tconstruct.tools.harvest.ShovelTool;
import slimeknights.tconstruct.tools.melee.BroadSword;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all complete tool items
 */
public final class TinkerTools extends TinkerModule {

  /** Creative tab for all tool items */
  public static final ItemGroup TAB_TOOLS = new SupplierItemGroup(TConstruct.modID, "tools", () -> {
    List<IMaterial> materials = new ArrayList<>(MaterialRegistry.getInstance().getMaterials());

    if (materials.isEmpty()) {
      return new ItemStack(TinkerTools.pickaxe);
    }

    return TinkerTools.pickaxe.get().buildToolForRendering();
  });

  /*
   * Items
   */
  public static final ItemObject<PickaxeTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.PICKAXE));
  public static final ItemObject<HammerTool> hammer = ITEMS.register("hammer", () -> new HammerTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.HAMMER));

  public static final ItemObject<ShovelTool> shovel = ITEMS.register("shovel", () -> new ShovelTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.SHOVEL));

  public static final ItemObject<AxeTool> axe = ITEMS.register("axe", () -> new AxeTool(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.AXE));

  public static final ItemObject<BroadSword> broadSword = ITEMS.register("broad_sword", () -> new BroadSword(new Item.Properties().group(TAB_TOOLS), ToolDefinitions.BROADSWORD));

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<IndestructibleEntityItem>> indestructibleItem = ENTITIES.register("indestructible_item", () -> {
    return EntityType.Builder.<IndestructibleEntityItem>create(IndestructibleEntityItem::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .immuneToFire();
  });
}
