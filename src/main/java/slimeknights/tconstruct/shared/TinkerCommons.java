package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.crafting.ShapedFallbackRecipe;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.ClearStainedGlassPaneBlock;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;

/**
 * Contains items and blocks and stuff that is shared by multiple modules, but might be required individually
 */
@SuppressWarnings("unused")
public final class TinkerCommons extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_commons");

  /*
   * Blocks
   */
  public static final RegistryObject<GlowBlock> glow = BLOCKS.registerNoItem("glow", () -> new GlowBlock(builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.CLOTH).hardnessAndResistance(0.0F).setLightLevel(s -> 14).notSolid()));
  public static final BuildingBlockObject mudBricks = BLOCKS.registerBuilding("mud_bricks", builder(Material.EARTH, ToolType.SHOVEL, SoundType.GROUND).hardnessAndResistance(2.0F), GENERAL_BLOCK_ITEM);
  // clay
  private static final Block.Properties DRIED_CLAY = builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).hardnessAndResistance(1.5F, 20.0F);
  public static final BuildingBlockObject driedClay = BLOCKS.registerBuilding("dried_clay", DRIED_CLAY, GENERAL_BLOCK_ITEM);
  public static final BuildingBlockObject driedClayBricks = BLOCKS.registerBuilding("dried_clay_bricks", DRIED_CLAY, GENERAL_BLOCK_ITEM);
  // glass
  public static final ItemObject<GlassBlock> clearGlass = BLOCKS.register("clear_glass", () -> new GlassBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final ItemObject<ClearGlassPaneBlock> clearGlassPane = BLOCKS.register("clear_glass_pane", () -> new ClearGlassPaneBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassBlock> clearStainedGlass = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass", (color) -> new ClearStainedGlassBlock(GENERIC_GLASS_BLOCK, color), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassPaneBlock> clearStainedGlassPane = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass_pane", (color) -> new ClearStainedGlassPaneBlock(GENERIC_GLASS_BLOCK, color), GENERAL_BLOCK_ITEM);
  // wood
  private static final Block.Properties WOOD = builder(Material.WOOD, ToolType.AXE, SoundType.WOOD).hardnessAndResistance(2.0F, 7.0F).setLightLevel(s -> 7);
  public static final BuildingBlockObject lavawood = BLOCKS.registerBuilding("lavawood", WOOD, GENERAL_BLOCK_ITEM);
  public static final BuildingBlockObject firewood = BLOCKS.registerBuilding("firewood", WOOD, GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<EdibleItem> bacon = ITEMS.register("bacon", () -> new EdibleItem(TinkerFood.BACON, TAB_GENERAL));
  public static final ItemObject<TinkerBookItem> book = ITEMS.register("book", () -> new TinkerBookItem(new Item.Properties().group(TAB_GENERAL).maxStackSize(1)));
  public static final ItemObject<Item> mudBrick = ITEMS.register("mud_brick", GENERAL_PROPS);
  public static final ItemObject<Item> driedBrick = ITEMS.register("dried_brick", GENERAL_PROPS);

  /* Slime Balls are edible, believe it or not */
  public static final EnumObject<SlimeType, Item> slimeball = new EnumObject.Builder<SlimeType, Item>(SlimeType.class)
    .put(SlimeType.GREEN, Items.SLIME_BALL.delegate)
    .putAll(ITEMS.registerEnum(SlimeType.TINKER, "slime_ball", (type) -> new EdibleItem(type.getSlimeFood(type), TAB_GENERAL)))
    .build();

  /*
   * Recipe serializers
   */
  public static final RegistryObject<IRecipeSerializer<ShapedRecipe>> shapedFallbackRecipe = RECIPE_SERIALIZERS.register("crafting_shaped_fallback", ShapedFallbackRecipe.Serializer::new);

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    CraftingHelper.register(ConfigOptionEnabledCondition.Serializer.INSTANCE);
  }
}
