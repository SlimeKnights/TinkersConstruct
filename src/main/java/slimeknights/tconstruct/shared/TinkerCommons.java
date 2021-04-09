package slimeknights.tconstruct.shared;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemEnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.conditions.ConfigEnabledCondition;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.common.recipe.BlockOrEntityCondition;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.ClearGlassPaneBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.ClearStainedGlassPaneBlock;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.SlimeType;

/**
 * Contains items and blocks and stuff that is shared by multiple modules, but might be required individually
 */
@SuppressWarnings("unused")
public final class TinkerCommons extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_commons");

  /*
   * Blocks
   */
  public static final GlowBlock glow = (GlowBlock) BLOCKS.registerNoItem("glow", () -> new GlowBlock(builder(Material.SUPPORTED, NO_TOOL, BlockSoundGroup.WOOL).strength(0.0F).luminance(s -> 14).nonOpaque()));
  public static final BuildingBlockObject mudBricks = BLOCKS.registerBuilding("mud_bricks", builder(Material.SOIL, FabricToolTags.SHOVELS, BlockSoundGroup.GRAVEL).requiresTool().strength(2.0F), GENERAL_BLOCK_ITEM);
  // clay
  // TODO: moving to natura
  private static final AbstractBlock.Settings DRIED_CLAY = builder(Material.STONE, FabricToolTags.PICKAXES, BlockSoundGroup.STONE).requiresTool().strength(1.5F, 20.0F);
  public static final BuildingBlockObject driedClay = BLOCKS.registerBuilding("dried_clay", DRIED_CLAY, HIDDEN_BLOCK_ITEM);
  public static final BuildingBlockObject driedClayBricks = BLOCKS.registerBuilding("dried_clay_bricks", DRIED_CLAY, HIDDEN_BLOCK_ITEM);
  // glass
  public static final ItemObject<Block> clearGlass = BLOCKS.register("clear_glass", () -> new GlassBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final ItemObject<Block> clearGlassPane = BLOCKS.register("clear_glass_pane", () -> new ClearGlassPaneBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor, Block> clearStainedGlass = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass", (color) -> new ClearStainedGlassBlock(GENERIC_GLASS_BLOCK, color), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor, Block> clearStainedGlassPane = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass_pane", (color) -> new ClearStainedGlassPaneBlock(GENERIC_GLASS_BLOCK, color), GENERAL_BLOCK_ITEM);
  public static final ItemObject<Block> soulGlass = BLOCKS.register("soul_glass", () -> new GlassBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final ItemObject<Block> soulGlassPane = BLOCKS.register("soul_glass_pane", () -> new ClearGlassPaneBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  // wood
  private static final AbstractBlock.Settings WOOD = builder(Material.WOOD, FabricToolTags.AXES, BlockSoundGroup.WOOD).requiresTool().strength(2.0F, 7.0F).luminance(s -> 7);
  public static final BuildingBlockObject lavawood = BLOCKS.registerBuilding("lavawood", WOOD, GENERAL_BLOCK_ITEM);
  public static final BuildingBlockObject blazewood = BLOCKS.registerBuilding("blazewood", WOOD, GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<EdibleItem> bacon = ITEMS.register("bacon", () -> new EdibleItem(TinkerFood.BACON, TAB_GENERAL));
  public static final ItemObject<TinkerBookItem> book = ITEMS.register("book", () -> new TinkerBookItem(new Item.Settings().group(TAB_GENERAL).maxCount(1)));
  // TODO: move to natura
  public static final ItemObject<Item> driedBrick = ITEMS.register("dried_brick", HIDDEN_PROPS);

  /* Loot conditions */
  public static LootConditionType lootConfig;
  public static LootConditionType lootBlockOrEntity;

  /* Slime Balls are edible, believe it or not */
  public static final ItemEnumObject<SlimeType, Item> slimeball = new ItemEnumObject.Builder<>(SlimeType.class)
    .put(SlimeType.EARTH, () -> Items.SLIME_BALL)
    .putAll(ITEMS.registerEnum(SlimeType.TINKER, "slime_ball", (type) -> new EdibleItem(type.getSlimeFood(type), TAB_GENERAL)))
    .build();

  @Override
  public void onInitialize() {
    CraftingHelper.register(ConfigEnabledCondition.SERIALIZER);
    lootConfig = Registry.register(Registry.LOOT_CONDITION_TYPE, ConfigEnabledCondition.ID, new LootConditionType(ConfigEnabledCondition.SERIALIZER));
    lootBlockOrEntity = Registry.register(Registry.LOOT_CONDITION_TYPE, BlockOrEntityCondition.ID, new LootConditionType(BlockOrEntityCondition.SERIALIZER));
  }
}
