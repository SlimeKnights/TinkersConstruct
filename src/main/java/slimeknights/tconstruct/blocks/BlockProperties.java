package slimeknights.tconstruct.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.utils.HarvestLevels;

import javax.annotation.Nullable;

public final class BlockProperties {
  /* Properties should be set in the following order for consistency:
   * material/tooltype/soundtype via constructor.
   * harvestlevel if required
   * hardness and resistance always
   * other
   */

  private static final ToolType NO_TOOL = null;

  static Block.Properties GENERIC_METAL_BLOCK = builder(Material.IRON, ToolType.PICKAXE, SoundType.METAL).hardnessAndResistance(5.0f);
  static Block.Properties GENERIC_GEM_BLOCK = GENERIC_METAL_BLOCK;
  static Block.Properties GENERIC_SAND_BLOCK = builder(Material.SAND, ToolType.SHOVEL, SoundType.SAND).hardnessAndResistance(3.0f).slipperiness(0.8F);
  static Block.Properties GENERIC_EARTH_BLOCK = builder(Material.EARTH, ToolType.SHOVEL, SoundType.GROUND).hardnessAndResistance(2.0F);
  static Block.Properties GENERIC_GLASS_BLOCK = builder(Material.GLASS, ToolType.PICKAXE, SoundType.GLASS).hardnessAndResistance(0.3F).notSolid();

  static Block.Properties DECO_GROUND_SLAB = builder(Material.ROCK, ToolType.SHOVEL, SoundType.GROUND).hardnessAndResistance(2.0F);// ?????

  static Block.Properties FIREWOOD = builder(Material.WOOD, ToolType.AXE, SoundType.WOOD).hardnessAndResistance(2.0F, 7.0F).lightValue(7);
  static Block.Properties LAVAWOOD = FIREWOOD;

  static Block.Properties MUD_BRICKS = builder(Material.EARTH, ToolType.SHOVEL, SoundType.GROUND).hardnessAndResistance(2.0F);
  static Block.Properties DRIED_CLAY = builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).hardnessAndResistance(1.5F, 20.0F);
  static Block.Properties DRIED_CLAY_BRICKS = DRIED_CLAY;

  // WORLD
  static Block.Properties ORE = builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).harvestLevel(HarvestLevels.COBALT).hardnessAndResistance(10.0F).notSolid();
  static Block.Properties SLIME = Block.Properties.create(Material.CLAY, MaterialColor.GRASS).sound(SoundType.SLIME).hardnessAndResistance(0.0f).slipperiness(0.8F);
  static Block.Properties CONGEALED_SLIME = builder(Material.CLAY, NO_TOOL, SoundType.SLIME).hardnessAndResistance(0.5F).slipperiness(0.5F);
  static Block.Properties SLIME_DIRT = builder(Material.ORGANIC, NO_TOOL, SoundType.SLIME).hardnessAndResistance(0.55F);
  static Block.Properties SLIME_GRASS = SLIME_DIRT;
  static Block.Properties SLIME_LEAVES = builder(Material.LEAVES, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.3F).tickRandomly().notSolid();

  static Block.Properties SAPLING = builder(Material.PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.1F).doesNotBlockMovement().tickRandomly();
  static Block.Properties TALL_GRASS = builder(Material.PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.1F).doesNotBlockMovement().tickRandomly();
  static Block.Properties VINE = builder(Material.TALL_PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(0.3F).doesNotBlockMovement().tickRandomly();

  // MISC
  static Block.Properties GLOW = builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.CLOTH).hardnessAndResistance(0.0F).lightValue(14).notSolid();
  static Block.Properties STONE_TORCH = builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.STONE).doesNotBlockMovement().hardnessAndResistance(0.0F).lightValue(14);
  static Block.Properties STONE_LADDER = builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.STONE).hardnessAndResistance(0.1F).notSolid();
  static Block.Properties WOODEN_RAIL = builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.WOOD).doesNotBlockMovement().hardnessAndResistance(0.2F);
  static Block.Properties PUNJI = builder(Material.PLANTS, NO_TOOL, SoundType.PLANT).hardnessAndResistance(3.0F);

  // SMELTERY
  static Block.Properties SMELTERY = builder(Material.ROCK, NO_TOOL, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F);
  static Block.Properties SMELTERY_GLASS = builder(Material.ROCK, NO_TOOL, SoundType.METAL).hardnessAndResistance(3.0F, 9.0F).notSolid();

  /**
   * We use this builder to ensure that our blocks all have the most important properties set.
   * This way it'll stick out if a block doesn't have a tooltype or sound set.
   * It may be a bit less clear at first, since the actual builder methods tell you what each value means,
   * but as long as we don't statically import the enums it should be just as readable.
   */
  private static Block.Properties builder(Material material, @Nullable ToolType toolType, SoundType soundType) {
    //noinspection ConstantConditions
    return Block.Properties.create(material).harvestTool(toolType).sound(soundType);
  }

  private BlockProperties() {}
}
