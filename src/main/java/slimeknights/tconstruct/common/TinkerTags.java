package slimeknights.tconstruct.common;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import slimeknights.tconstruct.library.Util;

public class TinkerTags {

  public static class Blocks {

    public static final IOptionalNamedTag<Block> CONGEALED_SLIME = tag("congealed_slime");
    public static final IOptionalNamedTag<Block> SLIMY_LOGS = tag("slimy_logs");
    public static final IOptionalNamedTag<Block> SLIMY_LEAVES = tag("slimy_leaves");
    public static final IOptionalNamedTag<Block> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final IOptionalNamedTag<Block> ORES_COBALT = forgeTag("ores/cobalt");
    public static final IOptionalNamedTag<Block> ORES_ARDITE = forgeTag("ores/ardite");
    public static final IOptionalNamedTag<Block> ORES_COPPER = forgeTag("ores/copper");

    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_COBALT = forgeTag("storage_blocks/cobalt");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_ARDITE = forgeTag("storage_blocks/ardite");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_MANYULLYN = forgeTag("storage_blocks/manyullyn");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_KNIGHTSLIME = forgeTag("storage_blocks/knightslime");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_PIG_IRON = forgeTag("storage_blocks/pig_iron");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_ROSE_GOLD = forgeTag("storage_blocks/rose_gold");

    public static final IOptionalNamedTag<Block> SEARED_BLOCKS = tag("seared_blocks");
    public static final IOptionalNamedTag<Block> SEARED_BRICKS = tag("seared_bricks");

    /** Blocks valid as a smeltery tank, required for fuel */
    public static final IOptionalNamedTag<Block> MELTER_TANKS = tag("melter_tanks");
    /** Blocks valid as a smeltery tank, required for fuel */
    public static final IOptionalNamedTag<Block> SMELTERY_TANKS = tag("smeltery/tanks");
    /** Blocks valid as a smeltery floor */
    public static final IOptionalNamedTag<Block> SMELTERY_FLOOR = tag("smeltery/floor");
    /** Blocks valid in any area of the smeltery */
    public static final IOptionalNamedTag<Block> SMELTERY_WALL = tag("smeltery/wall");


    private static IOptionalNamedTag<Block> tag(String name) {
      return BlockTags.createOptional(Util.getResource(name));
    }

    private static IOptionalNamedTag<Block> forgeTag(String name) {
      return BlockTags.createOptional(new ResourceLocation("forge", name));
    }
  }

  public static class Items {

    public static final IOptionalNamedTag<Item> CONGEALED_SLIME = tag("congealed_slime");
    public static final IOptionalNamedTag<Item> SLIMY_LOGS = tag("slimy_logs");
    public static final IOptionalNamedTag<Item> SLIMY_LEAVES = tag("slimy_leaves");
    public static final IOptionalNamedTag<Item> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final IOptionalNamedTag<Item> ORES_COBALT = forgeTag("ores/cobalt");
    public static final IOptionalNamedTag<Item> ORES_ARDITE = forgeTag("ores/ardite");
    public static final IOptionalNamedTag<Item> ORES_COPPER = forgeTag("ores/copper");

    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_COBALT = forgeTag("storage_blocks/cobalt");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_ARDITE = forgeTag("storage_blocks/ardite");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_MANYULLYN = forgeTag("storage_blocks/manyullyn");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_KNIGHTSLIME = forgeTag("storage_blocks/knightslime");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_PIG_IRON = forgeTag("storage_blocks/pig_iron");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_ROSE_GOLD = forgeTag("storage_blocks/rose_gold");

    public static final IOptionalNamedTag<Item> SEARED_BLOCKS = tag("seared_blocks");

    public static final IOptionalNamedTag<Item> SEARED_BRICKS = tag("seared_bricks");
    public static final IOptionalNamedTag<Item> GREEN_SLIMEBALL = forgeTag("slimeball/green");
    public static final IOptionalNamedTag<Item> BLUE_SLIMEBALL = forgeTag("slimeball/blue");
    public static final IOptionalNamedTag<Item> PURPLE_SLIMEBALL = forgeTag("slimeball/purple");
    public static final IOptionalNamedTag<Item> BLOOD_SLIMEBALL = forgeTag("slimeball/blood");

    public static final IOptionalNamedTag<Item> MAGMA_SLIMEBALL = forgeTag("slimeball/magma");
    public static final IOptionalNamedTag<Item> INGOTS_ARDITE = forgeTag("ingots/ardite");
    public static final IOptionalNamedTag<Item> INGOTS_COBALT = forgeTag("ingots/cobalt");
    public static final IOptionalNamedTag<Item> INGOTS_MANYULLYN = forgeTag("ingots/manyullyn");
    public static final IOptionalNamedTag<Item> INGOTS_KNIGHTSLIME = forgeTag("ingots/knightslime");
    public static final IOptionalNamedTag<Item> INGOTS_PIG_IRON = forgeTag("ingots/pig_iron");
    public static final IOptionalNamedTag<Item> INGOTS_COPPER = forgeTag("ingots/copper");
    public static final IOptionalNamedTag<Item> INGOTS_ROSE_GOLD = forgeTag("ingots/rose_gold");

    public static final IOptionalNamedTag<Item> NUGGETS_COBALT = forgeTag("nuggets/cobalt");
    public static final IOptionalNamedTag<Item> NUGGETS_ARDITE = forgeTag("nuggets/ardite");
    public static final IOptionalNamedTag<Item> NUGGETS_MANYULLYN = forgeTag("nuggets/manyullyn");
    public static final IOptionalNamedTag<Item> NUGGETS_KNIGHTSLIME = forgeTag("nuggets/knightslime");
    public static final IOptionalNamedTag<Item> NUGGETS_PIG_IRON = forgeTag("nuggets/pig_iron");
    public static final IOptionalNamedTag<Item> NUGGETS_COPPER = forgeTag("nuggets/copper");
    public static final IOptionalNamedTag<Item> NUGGETS_ROSE_GOLD = forgeTag("nuggets/rose_gold");

    public static final IOptionalNamedTag<Item> CASTS = tag("casts");

    public static final IOptionalNamedTag<Item> RODS_STONE = forgeTag("rods/stone");


    private static IOptionalNamedTag<Item> tag(String name) {
      return ItemTags.createOptional(Util.getResource(name));
    }

    private static IOptionalNamedTag<Item> forgeTag(String name) {
      return ItemTags.createOptional(new ResourceLocation("forge", name));
    }
  }

  public static class Fluids {

    public static final IOptionalNamedTag<Fluid> BLUE_SLIME = tag("blue_slime");
    public static final IOptionalNamedTag<Fluid> PURPLE_SLIME = tag("purple_slime");
    public static final IOptionalNamedTag<Fluid> SLIME = tag("slime");
    public static final IOptionalNamedTag<Fluid> MILK = forgeTag("milk");

    private static IOptionalNamedTag<Fluid> tag(String name) {
      return FluidTags.createOptional(Util.getResource(name));
    }

    private static IOptionalNamedTag<Fluid> forgeTag(String name) {
      return FluidTags.createOptional(new ResourceLocation("forge", name));
    }
  }

  public static class EntityTypes {

    public static final IOptionalNamedTag<EntityType<?>> SLIMES = forgeTag("slimes");

    private static IOptionalNamedTag<EntityType<?>> tag(String name) {
      return EntityTypeTags.createOptional(Util.getResource(name));
    }

    private static IOptionalNamedTag<EntityType<?>> forgeTag(String name) {
      return EntityTypeTags.createOptional(new ResourceLocation("forge", name));
    }
  }
}
