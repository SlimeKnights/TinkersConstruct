package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.shared.block.OrientableBlock;

/**
 * Contains bommon blocks and items used in crafting materials
 */
@SuppressWarnings("unused")
public final class TinkerMaterials extends TinkerModule {
  // ores
  public static final MetalItemObject copper = BLOCKS.registerMetal("copper", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject cobalt = BLOCKS.registerMetal("cobalt", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  // tier 3
  public static final MetalItemObject slimesteel    = BLOCKS.registerMetal("slimesteel", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject tinkersBronze = BLOCKS.registerMetal("tinkers_bronze", "silicon_bronze", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject roseGold      = BLOCKS.registerMetal("rose_gold", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject pigIron       = BLOCKS.registerMetal("pig_iron", () -> new OrientableBlock(GENERIC_METAL_BLOCK), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  // tier 4
  private static final Block.Properties SOUL_STEEL = builder(Material.IRON, ToolType.PICKAXE, SoundType.METAL).setRequiresTool().hardnessAndResistance(5.0f).notSolid();
  public static final MetalItemObject queensSlime = BLOCKS.registerMetal("queens_slime", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject manyullyn   = BLOCKS.registerMetal("manyullyn", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject hepatizon   = BLOCKS.registerMetal("hepatizon", GENERIC_METAL_BLOCK, GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject soulsteel   = BLOCKS.registerMetal("soulsteel", SOUL_STEEL, HIDDEN_BLOCK_ITEM, HIDDEN_PROPS);
  public static final ItemObject<Item> netheriteNugget = ITEMS.register("netherite_nugget", GENERAL_PROPS);
  // tier 5
  public static final MetalItemObject knightslime = BLOCKS.registerMetal("knightslime", GENERIC_METAL_BLOCK, HIDDEN_BLOCK_ITEM, HIDDEN_PROPS);

  /*
   * Serializers
   */
  @SubscribeEvent
  void registerSerializers(RegistryEvent<IRecipeSerializer<?>> event) {
    CraftingHelper.register(MaterialIngredient.Serializer.ID, MaterialIngredient.Serializer.INSTANCE);
  }
}
