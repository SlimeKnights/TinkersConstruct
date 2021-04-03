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
    public static final IOptionalNamedTag<Block> WORKBENCHES = forgeTag("workbenches");
    public static final IOptionalNamedTag<Block> TABLES = tag("tables");

    public static final IOptionalNamedTag<Block> ANVIL_METAL = tag("anvil_metal");

    public static final IOptionalNamedTag<Block> CONGEALED_SLIME = tag("congealed_slime");
    public static final IOptionalNamedTag<Block> SLIMY_LOGS = tag("slimy_logs");
    public static final IOptionalNamedTag<Block> SLIMY_LEAVES = tag("slimy_leaves");
    public static final IOptionalNamedTag<Block> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final IOptionalNamedTag<Block> ORES_COBALT = forgeTag("ores/cobalt");
    public static final IOptionalNamedTag<Block> ORES_COPPER = forgeTag("ores/copper");

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
    public static final IOptionalNamedTag<Item> WORKBENCHES = forgeTag("workbenches");
    public static final IOptionalNamedTag<Item> TABLES = tag("tables");

    public static final IOptionalNamedTag<Item> ANVIL_METAL = tag("anvil_metal");

    public static final IOptionalNamedTag<Item> CONGEALED_SLIME = tag("congealed_slime");
    public static final IOptionalNamedTag<Item> SLIMY_LOGS = tag("slimy_logs");
    public static final IOptionalNamedTag<Item> SLIMY_LEAVES = tag("slimy_leaves");
    public static final IOptionalNamedTag<Item> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final IOptionalNamedTag<Item> ORES_COBALT = forgeTag("ores/cobalt");
    public static final IOptionalNamedTag<Item> ORES_COPPER = forgeTag("ores/copper");

    public static final IOptionalNamedTag<Item> SEARED_BLOCKS = tag("seared_blocks");

    public static final IOptionalNamedTag<Item> SEARED_BRICKS = tag("seared_bricks");
    public static final IOptionalNamedTag<Item> EARTH_SLIMEBALL = forgeTag("slimeball/green");
    public static final IOptionalNamedTag<Item> SKY_SLIMEBALL = forgeTag("slimeball/sky");
    public static final IOptionalNamedTag<Item> ICHOR_SLIMEBALL = forgeTag("slimeball/ichor");
    public static final IOptionalNamedTag<Item> ENDER_SLIMEBALL = forgeTag("slimeball/ender");
    public static final IOptionalNamedTag<Item> BLOOD_SLIMEBALL = forgeTag("slimeball/blood");

    public static final IOptionalNamedTag<Item> NUGGETS_NETHERITE = forgeTag("nuggets/netherite");
    public static final IOptionalNamedTag<Item> INGOTS_NETHERITE_SCRAP = forgeTag("ingots/netherite_scrap");

    public static final IOptionalNamedTag<Item> CASTS = tag("casts");
    public static final IOptionalNamedTag<Item> GOLD_CASTS = tag("casts/gold");
    public static final IOptionalNamedTag<Item> SAND_CASTS = tag("casts/sand");
    public static final IOptionalNamedTag<Item> RED_SAND_CASTS = tag("casts/red_sand");

    public static final IOptionalNamedTag<Item> RODS_STONE = forgeTag("rods/stone");
    public static final IOptionalNamedTag<Item> WITHER_BONES = forgeTag("wither_bones");

    /** Containers that can be used in the duct */
    public static final IOptionalNamedTag<Item> DUCT_CONTAINERS = tag("duct_containers");

    /** Items that cannot be autosmelted */
    public static final IOptionalNamedTag<Item> AUTOSMELT_BLACKLIST = tag("autosmelt_blacklist");

    /*
     * Tool tags
     */
    /** Anything that can be modified in the tool station */
    public static final IOptionalNamedTag<Item> MODIFIABLE = tag("modifiable");
    /** Modifiable items that contain multiple parts */
    public static final IOptionalNamedTag<Item> MULTIPART_TOOL = tag("modifiable/multipart");
    /** Modifiable items that support melee attacks */
    public static final IOptionalNamedTag<Item> MELEE = tag("modifiable/melee");
    /** Modifiable items that specifically are designed for combat */
    public static final IOptionalNamedTag<Item> COMBAT = tag("modifiable/combat");
    /** This is a common combination for modifiers, so figured it is worth a tag. Should not be added to directly typically */
    public static final IOptionalNamedTag<Item> MELEE_OR_HARVEST = tag("modifiable/melee_or_harvest");
    /** Modifiable items that can break blocks */
    public static final IOptionalNamedTag<Item> HARVEST = tag("modifiable/harvest");
    /** Modifiable items that can have range increased */
    public static final IOptionalNamedTag<Item> AOE = tag("modifiable/aoe");
    // /** Modifiable items that support ranged attacks, such as bows */
    // public static final IOptionalNamedTag<Item> RANGED = tag("modifiable/ranged");


    private static IOptionalNamedTag<Item> tag(String name) {
      return ItemTags.createOptional(Util.getResource(name));
    }

    private static IOptionalNamedTag<Item> forgeTag(String name) {
      return ItemTags.createOptional(new ResourceLocation("forge", name));
    }
  }

  public static class Fluids {

    public static final IOptionalNamedTag<Fluid> SLIMELIKE = tag("slimelike");
    public static final IOptionalNamedTag<Fluid> SLIME = tag("slime");
    public static final IOptionalNamedTag<Fluid> EARTH_SLIME = tag("earth_slime");
    public static final IOptionalNamedTag<Fluid> SKY_SLIME = tag("sky_slime");
    public static final IOptionalNamedTag<Fluid> ENDER_SLIME = tag("ender_slime");

    private static IOptionalNamedTag<Fluid> tag(String name) {
      return FluidTags.createOptional(Util.getResource(name));
    }

    private static IOptionalNamedTag<Fluid> forgeTag(String name) {
      return FluidTags.createOptional(new ResourceLocation("forge", name));
    }
  }

  public static class EntityTypes {

    public static final IOptionalNamedTag<EntityType<?>> SLIMES = forgeTag("slimes");
    public static final IOptionalNamedTag<EntityType<?>> MELTING_SHOW = tag("melting/show_in_default");
    public static final IOptionalNamedTag<EntityType<?>> MELTING_HIDE = tag("melting/hide_in_default");

    private static IOptionalNamedTag<EntityType<?>> tag(String name) {
      return EntityTypeTags.createOptional(Util.getResource(name));
    }

    private static IOptionalNamedTag<EntityType<?>> forgeTag(String name) {
      return EntityTypeTags.createOptional(new ResourceLocation("forge", name));
    }
  }
}
