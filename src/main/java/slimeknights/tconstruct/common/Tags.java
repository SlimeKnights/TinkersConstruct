package slimeknights.tconstruct.common;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class Tags {

  public static class Blocks {

    public static final Tag<Block> SLIMY_LOGS = tag("slimy_logs");
    public static final Tag<Block> SLIMY_LEAVES = tag("slimy_leaves");
    public static final Tag<Block> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final Tag<Block> STORAGE_BLOCKS_COBALT = forgeTag("storage_blocks/cobalt");
    public static final Tag<Block> STORAGE_BLOCKS_ARDITE = forgeTag("storage_blocks/ardite");
    public static final Tag<Block> STORAGE_BLOCKS_MANYULLYN = forgeTag("storage_blocks/manyullyn");
    public static final Tag<Block> STORAGE_BLOCKS_KNIGHTSLIME = forgeTag("storage_blocks/knightslime");
    public static final Tag<Block> STORAGE_BLOCKS_PIGIRON = forgeTag("storage_blocks/pigiron");
    public static final Tag<Block> STORAGE_BLOCKS_ALUBRASS = forgeTag("storage_blocks/alubrass");

    public static final Tag<Block> GLASS = forgeTag("glass");
    public static final Tag<Block> GLASS_BLACK = forgeTag("glass/black");
    public static final Tag<Block> GLASS_BLUE = forgeTag("glass/blue");
    public static final Tag<Block> GLASS_BROWN = forgeTag("glass/brown");
    public static final Tag<Block> GLASS_COLORLESS = forgeTag("glass/colorless");
    public static final Tag<Block> GLASS_CYAN = forgeTag("glass/cyan");
    public static final Tag<Block> GLASS_GRAY = forgeTag("glass/gray");
    public static final Tag<Block> GLASS_GREEN = forgeTag("glass/green");
    public static final Tag<Block> GLASS_LIGHT_BLUE = forgeTag("glass/light_blue");
    public static final Tag<Block> GLASS_LIGHT_GRAY = forgeTag("glass/light_gray");
    public static final Tag<Block> GLASS_LIME = forgeTag("glass/lime");
    public static final Tag<Block> GLASS_MAGENTA = forgeTag("glass/magenta");
    public static final Tag<Block> GLASS_ORANGE = forgeTag("glass/orange");
    public static final Tag<Block> GLASS_PINK = forgeTag("glass/pink");
    public static final Tag<Block> GLASS_PURPLE = forgeTag("glass/purple");
    public static final Tag<Block> GLASS_RED = forgeTag("glass/red");
    public static final Tag<Block> GLASS_WHITE = forgeTag("glass/white");
    public static final Tag<Block> GLASS_YELLOW = forgeTag("glass/yellow");

    private static Tag<Block> tag(String name) {
      return new BlockTags.Wrapper(new ResourceLocation("tconstruct", name));
    }

    private static Tag<Block> forgeTag(String name) {
      return new BlockTags.Wrapper(new ResourceLocation("forge", name));
    }
  }

  public static class Items {

    public static final Tag<Item> SLIMY_LOGS = tag("slimy_logs");
    public static final Tag<Item> SLIMY_LEAVES = tag("slimy_leaves");
    public static final Tag<Item> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final Tag<Item> STORAGE_BLOCKS_COBALT = forgeTag("storage_blocks/cobalt");
    public static final Tag<Item> STORAGE_BLOCKS_ARDITE = forgeTag("storage_blocks/ardite");
    public static final Tag<Item> STORAGE_BLOCKS_MANYULLYN = forgeTag("storage_blocks/manyullyn");
    public static final Tag<Item> STORAGE_BLOCKS_KNIGHTSLIME = forgeTag("storage_blocks/knightslime");
    public static final Tag<Item> STORAGE_BLOCKS_PIGIRON = forgeTag("storage_blocks/pigiron");
    public static final Tag<Item> STORAGE_BLOCKS_ALUBRASS = forgeTag("storage_blocks/alubrass");

    public static final Tag<Item> GREEN_SLIMEBALL = forgeTag("slimeball/green");
    public static final Tag<Item> BLUE_SLIMEBALL = forgeTag("slimeball/blue");
    public static final Tag<Item> PURPLE_SLIMEBALL = forgeTag("slimeball/purple");
    public static final Tag<Item> BLOOD_SLIMEBALL = forgeTag("slimeball/blood");
    public static final Tag<Item> MAGMA_SLIMEBALL = forgeTag("slimeball/magma");

    public static final Tag<Item> INGOTS_COBALT = forgeTag("ingots/cobalt");
    public static final Tag<Item> INGOTS_ARDITE = forgeTag("ingots/ardite");
    public static final Tag<Item> INGOTS_MANYULLYN = forgeTag("ingots/manyullyn");
    public static final Tag<Item> INGOTS_KNIGHTSLIME = forgeTag("ingots/knightslime");
    public static final Tag<Item> INGOTS_PIGIRON = forgeTag("ingots/pigiron");
    public static final Tag<Item> INGOTS_ALUBRASS = forgeTag("ingots/alubrass");

    public static final Tag<Item> NUGGETS_COBALT = forgeTag("nuggets/cobalt");
    public static final Tag<Item> NUGGETS_ARDITE = forgeTag("nuggets/ardite");
    public static final Tag<Item> NUGGETS_MANYULLYN = forgeTag("nuggets/manyullyn");
    public static final Tag<Item> NUGGETS_KNIGHTSLIME = forgeTag("nuggets/knightslime");
    public static final Tag<Item> NUGGETS_PIGIRON = forgeTag("nuggets/pigiron");
    public static final Tag<Item> NUGGETS_ALUBRASS = forgeTag("nuggets/alubrass");

    public static final Tag<Item> DUSTS_SULFUR = forgeTag("dusts/sulfur");

    public static final Tag<Item> RODS_STONE = forgeTag("rods/stone");

    public static final Tag<Item> GLASS = forgeTag("glass");
    public static final Tag<Item> GLASS_BLACK = forgeTag("glass/black");
    public static final Tag<Item> GLASS_BLUE = forgeTag("glass/blue");
    public static final Tag<Item> GLASS_BROWN = forgeTag("glass/brown");
    public static final Tag<Item> GLASS_COLORLESS = forgeTag("glass/colorless");
    public static final Tag<Item> GLASS_CYAN = forgeTag("glass/cyan");
    public static final Tag<Item> GLASS_GRAY = forgeTag("glass/gray");
    public static final Tag<Item> GLASS_GREEN = forgeTag("glass/green");
    public static final Tag<Item> GLASS_LIGHT_BLUE = forgeTag("glass/light_blue");
    public static final Tag<Item> GLASS_LIGHT_GRAY = forgeTag("glass/light_gray");
    public static final Tag<Item> GLASS_LIME = forgeTag("glass/lime");
    public static final Tag<Item> GLASS_MAGENTA = forgeTag("glass/magenta");
    public static final Tag<Item> GLASS_ORANGE = forgeTag("glass/orange");
    public static final Tag<Item> GLASS_PINK = forgeTag("glass/pink");
    public static final Tag<Item> GLASS_PURPLE = forgeTag("glass/purple");
    public static final Tag<Item> GLASS_RED = forgeTag("glass/red");
    public static final Tag<Item> GLASS_WHITE = forgeTag("glass/white");
    public static final Tag<Item> GLASS_YELLOW = forgeTag("glass/yellow");

    private static Tag<Item> tag(String name) {
      return new ItemTags.Wrapper(new ResourceLocation("tconstruct", name));
    }

    private static Tag<Item> forgeTag(String name) {
      return new ItemTags.Wrapper(new ResourceLocation("forge", name));
    }
  }

  public static class Fluids {

    public static final Tag<Fluid> BLUE_SLIME = tag("blue_slime");
    public static final Tag<Fluid> PINK_SLIME = tag("pink_slime");
    public static final Tag<Fluid> SLIME = tag("slime");

    private static Tag<Fluid> tag(String name) {
      return new FluidTags.Wrapper(new ResourceLocation("tconstruct", name));
    }

    private static Tag<Fluid> forgeTag(String name) {
      return new FluidTags.Wrapper(new ResourceLocation("forge", name));
    }
  }

  public static class EntityTypes {

    public static final Tag<EntityType<?>> SLIMES = forgeTag("slimes");

    private static Tag<EntityType<?>> tag(String name) {
      return new EntityTypeTags.Wrapper(new ResourceLocation("tconstruct", name));
    }

    private static Tag<EntityType<?>> forgeTag(String name) {
      return new EntityTypeTags.Wrapper(new ResourceLocation("forge", name));
    }
  }
}
