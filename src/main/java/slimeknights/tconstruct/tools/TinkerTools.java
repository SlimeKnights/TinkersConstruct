package slimeknights.tconstruct.tools;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;
import slimeknights.tconstruct.tools.melee.BroadSword;

/**
 * Contains all complete tool items
 */
public final class TinkerTools extends TinkerModule {
  /*
   * Items
   */
  public static final ItemObject<PickaxeTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(new Item.Properties().group(TinkerRegistry.tabTools), ToolDefinitions.PICKAXE));
  public static final ItemObject<BroadSword> broadSword = ITEMS.register("broad_sword", () -> new BroadSword(new Item.Properties().group(TinkerRegistry.tabTools), ToolDefinitions.BROADSWORD));

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<IndestructibleEntityItem>> indestructibleItem = ENTITIES.register("indestructible_item", () -> {
    return EntityType.Builder.<IndestructibleEntityItem>create(IndestructibleEntityItem::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .immuneToFire();
  });
}
