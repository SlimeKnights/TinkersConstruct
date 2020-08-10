package slimeknights.tconstruct.common;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import slimeknights.tconstruct.library.Util;

public class TinkerTags {

  public static class Blocks {

    public static final INamedTag<Block> CONGEALED_SLIME = tag("congealed_slime");
    public static final INamedTag<Block> SLIMY_LOGS = tag("slimy_logs");
    public static final INamedTag<Block> SLIMY_LEAVES = tag("slimy_leaves");
    public static final INamedTag<Block> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final INamedTag<Block> ORES_COBALT = forgeTag("ores/cobalt");
    public static final INamedTag<Block> ORES_ARDITE = forgeTag("ores/ardite");
    public static final INamedTag<Block> ORES_COPPER = forgeTag("ores/copper");

    public static final INamedTag<Block> STORAGE_BLOCKS_COBALT = forgeTag("storage_blocks/cobalt");
    public static final INamedTag<Block> STORAGE_BLOCKS_ARDITE = forgeTag("storage_blocks/ardite");
    public static final INamedTag<Block> STORAGE_BLOCKS_MANYULLYN = forgeTag("storage_blocks/manyullyn");
    public static final INamedTag<Block> STORAGE_BLOCKS_KNIGHTSLIME = forgeTag("storage_blocks/knightslime");
    public static final INamedTag<Block> STORAGE_BLOCKS_PIG_IRON = forgeTag("storage_blocks/pig_iron");
    public static final INamedTag<Block> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
    public static final INamedTag<Block> STORAGE_BLOCKS_ROSE_GOLD = forgeTag("storage_blocks/rose_gold");

    public static final INamedTag<Block> SEARED_BLOCKS = tag("seared_blocks");
    public static final INamedTag<Block> SMOOTH_SEARED_BLOCKS = tag("smooth_seared_blocks");
    public static final INamedTag<Block> SEARED_BRICKS = tag("seared_bricks");


    private static INamedTag<Block> tag(String name) {
      return BlockTags.makeWrapperTag(Util.resource(name));
    }

    private static INamedTag<Block> forgeTag(String name) {
      return BlockTags.makeWrapperTag("forge:" + name);
    }
  }

  public static class Items {

    public static final INamedTag<Item> CONGEALED_SLIME = tag("congealed_slime");
    public static final INamedTag<Item> SLIMY_LOGS = tag("slimy_logs");
    public static final INamedTag<Item> SLIMY_LEAVES = tag("slimy_leaves");
    public static final INamedTag<Item> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final INamedTag<Item> ORES_COBALT = forgeTag("ores/cobalt");
    public static final INamedTag<Item> ORES_ARDITE = forgeTag("ores/ardite");
    public static final INamedTag<Item> ORES_COPPER = forgeTag("ores/copper");

    public static final INamedTag<Item> STORAGE_BLOCKS_COBALT = forgeTag("storage_blocks/cobalt");
    public static final INamedTag<Item> STORAGE_BLOCKS_ARDITE = forgeTag("storage_blocks/ardite");
    public static final INamedTag<Item> STORAGE_BLOCKS_MANYULLYN = forgeTag("storage_blocks/manyullyn");
    public static final INamedTag<Item> STORAGE_BLOCKS_KNIGHTSLIME = forgeTag("storage_blocks/knightslime");
    public static final INamedTag<Item> STORAGE_BLOCKS_PIG_IRON = forgeTag("storage_blocks/pig_iron");
    public static final INamedTag<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
    public static final INamedTag<Item> STORAGE_BLOCKS_ROSE_GOLD = forgeTag("storage_blocks/rose_gold");

    public static final INamedTag<Item> SEARED_BLOCKS = tag("seared_blocks");
    public static final INamedTag<Item> SMOOTH_SEARED_BLOCKS = tag("smooth_seared_blocks");

    public static final INamedTag<Item> SEARED_BRICKS = tag("seared_bricks");
    public static final INamedTag<Item> GREEN_SLIMEBALL = forgeTag("slimeball/green");
    public static final INamedTag<Item> BLUE_SLIMEBALL = forgeTag("slimeball/blue");
    public static final INamedTag<Item> PURPLE_SLIMEBALL = forgeTag("slimeball/purple");
    public static final INamedTag<Item> BLOOD_SLIMEBALL = forgeTag("slimeball/blood");

    public static final INamedTag<Item> MAGMA_SLIMEBALL = forgeTag("slimeball/magma");
    public static final INamedTag<Item> INGOTS_ARDITE = forgeTag("ingots/ardite");
    public static final INamedTag<Item> INGOTS_COBALT = forgeTag("ingots/cobalt");
    public static final INamedTag<Item> INGOTS_MANYULLYN = forgeTag("ingots/manyullyn");
    public static final INamedTag<Item> INGOTS_KNIGHTSLIME = forgeTag("ingots/knightslime");
    public static final INamedTag<Item> INGOTS_PIG_IRON = forgeTag("ingots/pig_iron");
    public static final INamedTag<Item> INGOTS_COPPER = forgeTag("ingots/copper");
    public static final INamedTag<Item> INGOTS_ROSE_GOLD = forgeTag("ingots/rose_gold");

    public static final INamedTag<Item> NUGGETS_COBALT = forgeTag("nuggets/cobalt");
    public static final INamedTag<Item> NUGGETS_ARDITE = forgeTag("nuggets/ardite");
    public static final INamedTag<Item> NUGGETS_MANYULLYN = forgeTag("nuggets/manyullyn");
    public static final INamedTag<Item> NUGGETS_KNIGHTSLIME = forgeTag("nuggets/knightslime");
    public static final INamedTag<Item> NUGGETS_PIG_IRON = forgeTag("nuggets/pig_iron");
    public static final INamedTag<Item> NUGGETS_COPPER = forgeTag("nuggets/copper");
    public static final INamedTag<Item> NUGGETS_ROSE_GOLD = forgeTag("nuggets/rose_gold");

    public static final INamedTag<Item> CASTS = tag("casts");

    public static final INamedTag<Item> RODS_STONE = forgeTag("rods/stone");


    private static INamedTag<Item> tag(String name) {
      return ItemTags.makeWrapperTag(Util.resource(name));
    }

    private static INamedTag<Item> forgeTag(String name) {
      return ItemTags.makeWrapperTag("forge:" + name);
    }
  }

  public static class Fluids {

    public static final INamedTag<Fluid> BLUE_SLIME = tag("blue_slime");
    public static final INamedTag<Fluid> PURPLE_SLIME = tag("purple_slime");
    public static final INamedTag<Fluid> SLIME = tag("slime");
    public static final INamedTag<Fluid> MILK = forgeTag("milk");

    private static INamedTag<Fluid> tag(String name) {
      return FluidTags.makeWrapperTag(Util.resource(name));
    }

    private static INamedTag<Fluid> forgeTag(String name) {
      return FluidTags.makeWrapperTag("forge:" + name);
    }
  }

  public static class EntityTypes {

    public static final INamedTag<EntityType<?>> SLIMES = forgeTag("slimes");

    private static INamedTag<EntityType<?>> tag(String name) {
      return EntityTypeTags.func_232896_a_(Util.resource(name));
    }

    private static INamedTag<EntityType<?>> forgeTag(String name) {
      return EntityTypeTags.func_232896_a_("forge:" + name);
    }
  }
}
