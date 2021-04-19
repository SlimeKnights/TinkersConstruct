package slimeknights.tconstruct.common;

import net.fabricmc.fabric.api.tag.TagRegistry;
import slimeknights.tconstruct.library.Util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TinkerTags {
  /** Called on mod construct to set up tags */
  public static void init() {
    Blocks.init();
    Items.init();
    Fluids.init();
    EntityTypes.init();
  }

  public static class Blocks {
    private static void init() {}
    public static final Tag.Identified<Block> WORKBENCHES = forgeTag("workbenches");
    public static final Tag.Identified<Block> TABLES = tag("tables");

    public static final Tag.Identified<Block> ANVIL_METAL = tag("anvil_metal");

    public static final Tag.Identified<Block> SLIME_BLOCK = tag("slime_block");
    public static final Tag.Identified<Block> CONGEALED_SLIME = tag("congealed_slime");
    public static final Tag.Identified<Block> SLIMY_LOGS = tag("slimy_logs");
    public static final Tag.Identified<Block> SLIMY_LEAVES = tag("slimy_leaves");
    public static final Tag.Identified<Block> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final Tag.Identified<Block> ORES_COBALT = forgeTag("ores/cobalt");
    public static final Tag.Identified<Block> ORES_COPPER = forgeTag("ores/copper");

    public static final Tag.Identified<Block> SEARED_BLOCKS = tag("seared_blocks");
    public static final Tag.Identified<Block> SEARED_BRICKS = tag("seared_bricks");

    /** Blocks valid as a smeltery tank, required for fuel */
    public static final Tag.Identified<Block> MELTER_TANKS = tag("melter_tanks");
    /** Blocks that make up the smeltery structure */
    public static final Tag.Identified<Block> SMELTERY = tag("smeltery");
    /** Blocks valid as a smeltery tank, required for fuel */
    public static final Tag.Identified<Block> SMELTERY_TANKS = tag("smeltery/tanks");
    /** Blocks valid as a smeltery floor */
    public static final Tag.Identified<Block> SMELTERY_FLOOR = tag("smeltery/floor");
    /** Blocks valid in any area of the smeltery */
    public static final Tag.Identified<Block> SMELTERY_WALL = tag("smeltery/wall");

    /** Any block that can be harvested using a kama or scythe */
    public static final Tag.Identified<Block> HARVESTABLE = tag("harvestable");
    /** Plants that are broken to drop produce and seeds */
    public static final Tag.Identified<Block> HARVESTABLE_CROPS = tag("harvestable/crops");
    /** Plants that drop fruit on interaction */
    public static final Tag.Identified<Block> HARVESTABLE_INTERACT = tag("harvestable/interact");
    /** Plants that grow by placing a copy on top */
    public static final Tag.Identified<Block> HARVESTABLE_STACKABLE = tag("harvestable/stackable");

    private static Tag.Identified<Block> tag(String name) {
      return (Tag.Identified<Block>) TagRegistry.block(Util.getResource(name));
    }

    private static Tag.Identified<Block> forgeTag(String name) {
      return (Tag.Identified<Block>) TagRegistry.block(new Identifier("c", name));
    }
  }

  public static class Items {
    private static void init() {}
    public static final Tag.Identified<Item> WORKBENCHES = forgeTag("workbenches");
    public static final Tag.Identified<Item> TABLES = tag("tables");

    public static final Tag.Identified<Item> ANVIL_METAL = tag("anvil_metal");

    public static final Tag.Identified<Item> SLIME_BLOCK = tag("slime_block");
    public static final Tag.Identified<Item> CONGEALED_SLIME = tag("congealed_slime");
    public static final Tag.Identified<Item> SLIMY_LOGS = tag("slimy_logs");
    public static final Tag.Identified<Item> SLIMY_LEAVES = tag("slimy_leaves");
    public static final Tag.Identified<Item> SLIMY_SAPLINGS = tag("slimy_saplings");

    public static final Tag.Identified<Item> ORES_COBALT = forgeTag("ores/cobalt");
    public static final Tag.Identified<Item> ORES_COPPER = forgeTag("ores/copper");

    public static final Tag.Identified<Item> SEARED_BLOCKS = tag("seared_blocks");

    public static final Tag.Identified<Item> SEARED_BRICKS = tag("seared_bricks");
    public static final Tag.Identified<Item> EARTH_SLIMEBALL = forgeTag("slimeball/green");
    public static final Tag.Identified<Item> SKY_SLIMEBALL = forgeTag("slimeball/sky");
    public static final Tag.Identified<Item> ICHOR_SLIMEBALL = forgeTag("slimeball/ichor");
    public static final Tag.Identified<Item> ENDER_SLIMEBALL = forgeTag("slimeball/ender");
    public static final Tag.Identified<Item> BLOOD_SLIMEBALL = forgeTag("slimeball/blood");

    public static final Tag.Identified<Item> NUGGETS_NETHERITE = forgeTag("nuggets/netherite");
    public static final Tag.Identified<Item> INGOTS_NETHERITE_SCRAP = forgeTag("ingots/netherite_scrap");

    public static final Tag.Identified<Item> CASTS = tag("casts");
    public static final Tag.Identified<Item> GOLD_CASTS = tag("casts/gold");
    public static final Tag.Identified<Item> SAND_CASTS = tag("casts/sand");
    public static final Tag.Identified<Item> RED_SAND_CASTS = tag("casts/red_sand");
    public static final Tag.Identified<Item> SINGLE_USE_CASTS = tag("casts/single_use");
    public static final Tag.Identified<Item> MULTI_USE_CASTS = tag("casts/multi_use");

    public static final Tag.Identified<Item> RODS_STONE = forgeTag("rods/stone");
    public static final Tag.Identified<Item> WITHER_BONES = forgeTag("wither_bones");

    /** Containers that can be used in the duct */
    public static final Tag.Identified<Item> DUCT_CONTAINERS = tag("duct_containers");

    /** Items that cannot be autosmelted */
    public static final Tag.Identified<Item> AUTOSMELT_BLACKLIST = tag("autosmelt_blacklist");

    /** Items that are seeds for kama harvest */
    public static final Tag.Identified<Item> SEEDS = tag("seeds");

    /*
     * Tool tags
     */
    /** Anything that can be modified in the tool station */
    public static final Tag.Identified<Item> TOOL_PARTS = tag("parts");
    /** Anything that can be modified in the tool station */
    public static final Tag.Identified<Item> MODIFIABLE = tag("modifiable");
    /** Modifiable items that contain multiple parts */
    public static final Tag.Identified<Item> MULTIPART_TOOL = tag("modifiable/multipart");
    /** Modifiable items that support melee attacks */
    public static final Tag.Identified<Item> MELEE = tag("modifiable/melee");
    /** Modifiable items that specifically are designed for combat */
    public static final Tag.Identified<Item> COMBAT = tag("modifiable/combat");
    /** This is a common combination for modifiers, so figured it is worth a tag. Should not be added to directly typically */
    public static final Tag.Identified<Item> MELEE_OR_HARVEST = tag("modifiable/melee_or_harvest");
    /** Modifiable items that can break blocks */
    public static final Tag.Identified<Item> HARVEST = tag("modifiable/harvest");
    /** Modifiable items that can break stone blocks */
    public static final Tag.Identified<Item> STONE_HARVEST = tag("modifiable/harvest/stone");
    /** Modifiable items that can have range increased */
    public static final Tag.Identified<Item> AOE = tag("modifiable/aoe");
    // /** Modifiable items that support ranged attacks, such as bows */
    // public static final Tag<Item> RANGED = tag("modifiable/ranged");
    /** Tag so mods like thermal know our scyhtes can harvest */
    public static final Tag.Identified<Item> SCYTHES = forgeTag("tools/scythe");


    private static Tag.Identified<Item> tag(String name) {
      return (Tag.Identified<Item>) TagRegistry.item(Util.getResource(name));
    }

    private static Tag.Identified<Item> forgeTag(String name) {
      return (Tag.Identified<Item>) TagRegistry.item(new Identifier("c", name));
    }
  }

  public static class Fluids {
    private static void init() {}
    public static final Tag.Identified<Fluid> SLIMELIKE = tag("slimelike");
    public static final Tag.Identified<Fluid> SLIME = tag("slime");

    private static Tag.Identified<Fluid> tag(String name) {
      return (Tag.Identified<Fluid>) TagRegistry.fluid(Util.getResource(name));
    }

    private static Tag.Identified<Fluid> forgeTag(String name) {
      return (Tag.Identified<Fluid>) TagRegistry.fluid(new Identifier("c", name));
    }
  }

  public static class EntityTypes {
    private static void init() {}
    public static final Tag.Identified<EntityType<?>> BOUNCY = tag("bouncy");
    public static final Tag.Identified<EntityType<?>> SLIMES = forgeTag("slimes");
    public static final Tag.Identified<EntityType<?>> MELTING_SHOW = tag("melting/show_in_default");
    public static final Tag.Identified<EntityType<?>> MELTING_HIDE = tag("melting/hide_in_default");

    private static Tag.Identified<EntityType<?>> tag(String name) {
      return (Tag.Identified<EntityType<?>>) TagRegistry.entityType(Util.getResource(name));
    }

    private static Tag.Identified<EntityType<?>> forgeTag(String name) {
      return (Tag.Identified<EntityType<?>>) TagRegistry.entityType(new Identifier("c", name));
    }
  }
}
