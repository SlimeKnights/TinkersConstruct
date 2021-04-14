package slimeknights.tconstruct.shared;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
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
  public static final MetalItemObject copper = BLOCKS.registerMetal("copper", getGenericMetalBlock(), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject cobalt = BLOCKS.registerMetal("cobalt", getGenericMetalBlock(), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  // tier 3
  public static final MetalItemObject slimesteel    = BLOCKS.registerMetal("slimesteel", getGenericMetalBlock(), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject tinkersBronze = BLOCKS.registerMetal("tinkers_bronze", "silicon_bronze", getGenericMetalBlock(), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject roseGold      = BLOCKS.registerMetal("rose_gold", getGenericMetalBlock(), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject pigIron       = BLOCKS.registerMetal("pig_iron", () -> new OrientableBlock(getGenericMetalBlock()), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  // tier 4
  private static final AbstractBlock.Settings SOUL_STEEL = builder(Material.METAL, FabricToolTags.PICKAXES, BlockSoundGroup.METAL).requiresTool().strength(5.0f).nonOpaque();
  public static final MetalItemObject queensSlime = BLOCKS.registerMetal("queens_slime", getGenericMetalBlock(), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject manyullyn   = BLOCKS.registerMetal("manyullyn", getGenericMetalBlock(), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject hepatizon   = BLOCKS.registerMetal("hepatizon", getGenericMetalBlock(), GENERAL_TOOLTIP_BLOCK_ITEM, GENERAL_PROPS);
  public static final MetalItemObject soulsteel   = BLOCKS.registerMetal("soulsteel", SOUL_STEEL, HIDDEN_BLOCK_ITEM, getHiddenProps());
  public static final ItemObject<Item> netheriteNugget = ITEMS.register("netherite_nugget", GENERAL_PROPS);
  // tier 5
  public static final MetalItemObject knightslime = BLOCKS.registerMetal("knightslime", getGenericMetalBlock(), HIDDEN_BLOCK_ITEM, getHiddenProps());

  @Override
  public void onInitialize() {
//    CraftingHelper.register(MaterialIngredient.Serializer.ID, MaterialIngredient.Serializer.INSTANCE);
  }
}
