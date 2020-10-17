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
    public static final IOptionalNamedTag<Block> ORES_COPPER = forgeTag("ores/copper");

    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_COBALT = forgeTag("storage_blocks/cobalt");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_ARDITE = forgeTag("storage_blocks/ardite");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_MANYULLYN = forgeTag("storage_blocks/manyullyn");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_KNIGHTSLIME = forgeTag("storage_blocks/knightslime");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_PIG_IRON = forgeTag("storage_blocks/pig_iron");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_ROSE_GOLD = forgeTag("storage_blocks/rose_gold");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_RAVAGER_STEEL = forgeTag("storage_blocks/ravager_steel");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SOUL_STEEL = forgeTag("storage_blocks/soul_steel");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_HEPTAZION = forgeTag("storage_blocks/heptazion");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SLIME_BRONZE = forgeTag("storage_blocks/slime_bronze");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_SLIME_STEEL = forgeTag("storage_blocks/slime_steel");
    public static final IOptionalNamedTag<Block> STORAGE_BLOCKS_KNIGHT_METAL = forgeTag("storage_blocks/knight_metal");

    public static final IOptionalNamedTag<Block> SEARED_BLOCKS = tag("seared_blocks");
    public static final IOptionalNamedTag<Block> SEARED_BRICKS = tag("seared_bricks");
    public static final IOptionalNamedTag<Block> MAGMASTONE_BLOCKS = tag("magmastone_blocks");
    public static final IOptionalNamedTag<Block> MAGMASTONE_BRICKS = tag("magmastone_bricks");
    public static final IOptionalNamedTag<Block> DRAGONSTONE_BLOCKS = tag("dragonstone_blocks");
    public static final IOptionalNamedTag<Block> DRAGONSTONE_BRICKS = tag("dragonstone_bricks");

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
    public static final IOptionalNamedTag<Item> ORES_COPPER = forgeTag("ores/copper");

    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_COBALT = forgeTag("storage_blocks/cobalt");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_ARDITE = forgeTag("storage_blocks/ardite");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_MANYULLYN = forgeTag("storage_blocks/manyullyn");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_KNIGHTSLIME = forgeTag("storage_blocks/knightslime");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_PIG_IRON = forgeTag("storage_blocks/pig_iron");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_ROSE_GOLD = forgeTag("storage_blocks/rose_gold");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_BRONZE = forgeTag("storage_blocks/bronze");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_RAVAGER_STEEL = forgeTag("storage_blocks/ravager_steel");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_SOUL_STEEL = forgeTag("storage_blocks/soul_steel");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_HEPTAZION = forgeTag("storage_blocks/heptazion");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_SLIME_BRONZE = forgeTag("storage_blocks/slime_bronze");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_SLIME_STEEL = forgeTag("storage_blocks/slime_steel");
    public static final IOptionalNamedTag<Item> STORAGE_BLOCKS_KNIGHT_METAL = forgeTag("storage_blocks/knight_metal");

    public static final IOptionalNamedTag<Item> SEARED_BLOCKS = tag("seared_blocks");
    public static final IOptionalNamedTag<Item> SEARED_BRICKS = tag("seared_bricks");
    public static final IOptionalNamedTag<Item> MAGMASTONE_BLOCKS = tag("magmastone_blocks");
    public static final IOptionalNamedTag<Item> MAGMASTONE_BRICKS = tag("magmastone_bricks");
    public static final IOptionalNamedTag<Item> DRAGONSTONE_BLOCKS = tag("dragonstone_blocks");
    public static final IOptionalNamedTag<Item> DRAGONSTONE_BRICKS = tag("dragonstone_bricks");

    public static final IOptionalNamedTag<Item> BLOOD_SLIMEBALL = forgeTag("slimeball/blood");
    public static final IOptionalNamedTag<Item> GREEN_SLIMEBALL = forgeTag("slimeball/green");
    public static final IOptionalNamedTag<Item> BLUE_SLIMEBALL = forgeTag("slimeball/blue");
    public static final IOptionalNamedTag<Item> MAGMA_SLIMEBALL = forgeTag("slimeball/magma");
    public static final IOptionalNamedTag<Item> PURPLE_SLIMEBALL = forgeTag("slimeball/purple");
    public static final IOptionalNamedTag<Item> RAINBOW_SLIMEBALL = forgeTag("slimeball/rainbow");
    //public static final IOptionalNamedTag<Item> GOLD_SLIMEBALL = forgeTag("slimeball/gold");

    public static final IOptionalNamedTag<Item> ALEXANDRITE_PEARL = forgeTag("ingots/alexandrite");

    public static final IOptionalNamedTag<Item> INGOTS_ARDITE = forgeTag("ingots/ardite");
    public static final IOptionalNamedTag<Item> INGOTS_COBALT = forgeTag("ingots/cobalt");
    public static final IOptionalNamedTag<Item> INGOTS_MANYULLYN = forgeTag("ingots/manyullyn");
    public static final IOptionalNamedTag<Item> INGOTS_KNIGHTSLIME = forgeTag("ingots/knightslime");
    public static final IOptionalNamedTag<Item> INGOTS_PIG_IRON = forgeTag("ingots/pig_iron");
    public static final IOptionalNamedTag<Item> INGOTS_COPPER = forgeTag("ingots/copper");
    public static final IOptionalNamedTag<Item> INGOTS_ROSE_GOLD = forgeTag("ingots/rose_gold");
    public static final IOptionalNamedTag<Item> INGOTS_BRONZE = forgeTag("ingots/bronze");
    public static final IOptionalNamedTag<Item> INGOTS_RAVAGER_STEEL = forgeTag("ingots/ravager_steel");
    public static final IOptionalNamedTag<Item> INGOTS_SOUL_STEEL = forgeTag("ingots/soul_steel");
    public static final IOptionalNamedTag<Item> INGOTS_HEPTAZION = forgeTag("ingots/heptazion");
    public static final IOptionalNamedTag<Item> INGOTS_SLIME_BRONZE = forgeTag("ingots/slime_bronze");
    public static final IOptionalNamedTag<Item> INGOTS_SLIME_STEEL = forgeTag("ingots/slime_steel");
    public static final IOptionalNamedTag<Item> INGOTS_KNIGHT_METAL = forgeTag("ingots/knight_metal");

    public static final IOptionalNamedTag<Item> NUGGETS_COBALT = forgeTag("nuggets/cobalt");
    public static final IOptionalNamedTag<Item> NUGGETS_ARDITE = forgeTag("nuggets/ardite");
    public static final IOptionalNamedTag<Item> NUGGETS_MANYULLYN = forgeTag("nuggets/manyullyn");
    public static final IOptionalNamedTag<Item> NUGGETS_KNIGHTSLIME = forgeTag("nuggets/knightslime");
    public static final IOptionalNamedTag<Item> NUGGETS_PIG_IRON = forgeTag("nuggets/pig_iron");
    public static final IOptionalNamedTag<Item> NUGGETS_COPPER = forgeTag("nuggets/copper");
    public static final IOptionalNamedTag<Item> NUGGETS_ROSE_GOLD = forgeTag("nuggets/rose_gold");
    public static final IOptionalNamedTag<Item> NUGGETS_BRONZE = forgeTag("nuggets/bronze");
    public static final IOptionalNamedTag<Item> NUGGETS_RAVAGER_STEEL = forgeTag("nuggets/ravager_steel");
    public static final IOptionalNamedTag<Item> NUGGETS_SOUL_STEEL = forgeTag("nuggets/soul_steel");
    public static final IOptionalNamedTag<Item> NUGGETS_HEPTAZION = forgeTag("nuggets/heptazion");
    public static final IOptionalNamedTag<Item> NUGGETS_SLIME_BRONZE = forgeTag("nuggets/slime_bronze");
    public static final IOptionalNamedTag<Item> NUGGETS_SLIME_STEEL = forgeTag("nuggets/slime_steel");
    public static final IOptionalNamedTag<Item> NUGGETS_KNIGHT_METAL = forgeTag("nuggets/knight_metal");

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
    public static final IOptionalNamedTag<Fluid> MAGMA_SLIME = tag("magma_slime");
    public static final IOptionalNamedTag<Fluid> PURPLE_SLIME = tag("purple_slime");
    public static final IOptionalNamedTag<Fluid> RAINBOW_SLIME = tag("rainbow_slime");
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
