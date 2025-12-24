package slimeknights.tconstruct.common;

import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static slimeknights.mantle.Mantle.commonResource;
import static slimeknights.tconstruct.TConstruct.getResource;

public class TinkerTags {
  /** Checks if tags have been loaded on this instance. Used to prevent certain NBT operations that depend on tags from happening client side when tags are missing. */
  @Getter
  static boolean tagsLoaded = false;

  /** Called on mod construct to set up tags */
  public static void init() {
    // TODO: is this really needed anymore?
    Blocks.init();
    Items.init();
    Fluids.init();
    EntityTypes.init();
    TileEntityTypes.init();
    Biomes.init();
    Modifiers.init();
    Materials.init();
    DamageTypes.init();
    MenuTypes.init();
    Potions.init();
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TagsUpdatedEvent.class, event -> tagsLoaded = true);
  }

  /** Resource location of the hidden from recipe tags used in JEI. */
  @SuppressWarnings("removal")
  public static final ResourceLocation HIDDEN_FROM_RECIPE_VIEWERS = new ResourceLocation("c", "hidden_from_recipe_viewers");

  /** Creates a tag that hides things from JEI */
  @SuppressWarnings("SameParameterValue") // there really is no benefit to migrating to new constructors early; just lose Neo compat
  private static <R> TagKey<R> hiddenFromRecipeViewers(ResourceKey<? extends Registry<R>> registry) {
    return TagKey.create(registry, HIDDEN_FROM_RECIPE_VIEWERS);
  }

  public static class Blocks {
    private static void init() {}
    public static final TagKey<Block> WORKBENCHES = common("workbenches");
    public static final TagKey<Block> TABLES = local("tables");
    public static final TagKey<Block> GLASS_PANES_SILICA = common("glass_panes/silica");

    /** Compat: allows other mods to add normal stone variants which can be used to create stone tools.
     * Note this tag includes both stone and cobblestone, unlike the default forge/vanilla tags that include one or the other
     * Also does not include other items that can be used as stone in crafting */
    public static final TagKey<Block> STONE = common("normal_stone");
    /** Compat: allows other mods to add granite variants which can be used to create granite tools */
    public static final TagKey<Block> GRANITE = common("granite");
    /** Compat: allows other mods to add diorite variants which can be used to create diorite tools */
    public static final TagKey<Block> DIORITE = common("diorite");
    /** Compat: allows other mods to add andesite variants which can be used to create andesite tools */
    public static final TagKey<Block> ANDESITE = common("andesite");
    /** Compat: allows other mods to add blackstone variants which can be used to create blackstone tools */
    public static final TagKey<Block> BLACKSTONE = common("blackstone");
    /** Compat: allows other mods to add deepslate variants which can be used to create deepslate tools */
    public static final TagKey<Block> DEEPSLATE = common("deepslate");
    /** Compat: allows other mods to add basalt variants which can be used to create basalt tools */
    public static final TagKey<Block> BASALT = common("basalt");

    /** Blocks that render a transparent overlay when you are inside */
    public static final TagKey<Block> TRANSPARENT_OVERLAY = local("transparent_overlay");

    /** Materials that can be used to craft wooden tool tables */
    public static final TagKey<Block> PLANKLIKE = local("planklike");
    /** Metals that can be used to craft the anvil */
    public static final TagKey<Block> WORKSTATION_ROCK = local("workstation_rock");
    /** Metals that can be used to craft the anvil */
    public static final TagKey<Block> ANVIL_METAL = local("anvil_metal");
    /** Materials that can be used to craft the smeltery controller */
    public static final TagKey<Block> SMELTERY_BRICKS = local("smeltery_bricks");
    /** Materials that can be used to craft the foundry controller */
    public static final TagKey<Block> FOUNDRY_BRICKS = local("foundry_bricks");

    /** Things the platform connects to */
    public static final TagKey<Block> PLATFORM_CONNECTIONS = local("platform_connections");
    /** Copper platform variants */
    public static final TagKey<Block> COPPER_PLATFORMS = local("copper_platforms");

    // Slime spawn
    /** Blocks skyslimes can spawn on in the overworld */
    public static final TagKey<Block> SKY_SLIME_SPAWN = local("slime_spawn/sky");
    /** Blocks earthslimes can spawn on in the overworld */
    public static final TagKey<Block> EARTH_SLIME_SPAWN = local("slime_spawn/earth");
    /** Blocks enderslimes can spawn on in the end */
    public static final TagKey<Block> ENDER_SLIME_SPAWN = local("slime_spawn/ender");

    public static final TagKey<Block> SLIME_BLOCK = local("slime_block");
    public static final TagKey<Block> CONGEALED_SLIME = local("congealed_slime");
    public static final TagKey<Block> SLIMY_LOGS = local("slimy_logs");
    public static final TagKey<Block> SLIMY_PLANKS = local("slimy_planks");
    public static final TagKey<Block> SLIMY_LEAVES = local("slimy_leaves");
    public static final TagKey<Block> SLIMY_VINES = local("slimy_vines");
    public static final TagKey<Block> SLIMY_SAPLINGS = local("slimy_saplings");
    /** All variants of enderbark roots */
    public static final TagKey<Block> ENDERBARK_ROOTS = local("enderbark/roots");
    /** Blocks that are slime grass on top of dirt */
    public static final TagKey<Block> SLIMY_GRASS = local("slimy_grass");
    /** Blocks that are slime fungus on top of dirt */
    public static final TagKey<Block> SLIMY_NYLIUM = local("slimy_nylium");
    /** Slime grass, slime fungus, or slime dirt */
    public static final TagKey<Block> SLIMY_SOIL = local("slimy_soil");
    /** Blocks enderbark logs can replace */
    public static final TagKey<Block> ENDERBARK_LOGS_CAN_GROW_THROUGH = local("enderbark/logs_can_grow_through");
    /** Blocks enderbark roots can replace */
    public static final TagKey<Block> ENDERBARK_ROOTS_CAN_GROW_THROUGH = local("enderbark/roots_can_grow_through");
    /** Blocks slimy fungus (any of the three types) can replace */
    public static final TagKey<Block> SLIMY_FUNGUS_CAN_GROW_THROUGH = local("slimy_fungus_can_grow_through");

    public static final TagKey<Block> ORES_COBALT = common("ores/cobalt");
    public static final TagKey<Block> RAW_BLOCK_COBALT = common("storage_blocks/raw_cobalt");

    public static final TagKey<Block> SEARED_BLOCKS = local("seared_blocks");
    public static final TagKey<Block> SEARED_BRICKS = local("seared_bricks");
    public static final TagKey<Block> SEARED_TANKS = local("seared_tanks");

    public static final TagKey<Block> SCORCHED_BLOCKS = local("scorched_blocks");
    public static final TagKey<Block> SCORCHED_TANKS = local("scorched_tanks");


    /** Blocks which make a heater part of a structure, when placed above a heater */
    public static final TagKey<Block> HEATER_CONTROLLERS = local("heater_controllers");
    /** Blocks valid as a fuel tank for the melter or alloyer, should be item handlers with 1 slot or fluid handlers with 1 fluid */
    public static final TagKey<Block> FUEL_TANKS = local("fuel_tanks");
    /** Tanks that serve as a valid input for the alloyer, should be fluid handlers with 1 fluid */
    public static final TagKey<Block> ALLOYER_TANKS = local("alloyer_tanks");

    /** Blocks that are treated as equivelent to air in structure detection. Used primarily for invisible light blocks */
    public static final TagKey<Block> STRUCTURE_AIR = local("structure_air");

    /** Blocks that make up the smeltery structure */
    public static final TagKey<Block> SMELTERY = local("smeltery");
    /** Blocks valid as a smeltery tank, required for fuel */
    public static final TagKey<Block> SMELTERY_TANKS = local("smeltery/tanks");
    /** Blocks valid as a smeltery floor */
    public static final TagKey<Block> SMELTERY_FLOOR = local("smeltery/floor");
    /** Blocks valid in the smeltery wall */
    public static final TagKey<Block> SMELTERY_WALL = local("smeltery/wall");

    /** Blocks that make up the foundry structure */
    public static final TagKey<Block> FOUNDRY = local("foundry");
    /** Blocks valid as a foundry tank, required for fuel */
    public static final TagKey<Block> FOUNDRY_TANKS = local("foundry/tanks");
    /** Blocks valid as a foundry floor */
    public static final TagKey<Block> FOUNDRY_FLOOR = local("foundry/floor");
    /** Blocks valid in the foundry wall */
    public static final TagKey<Block> FOUNDRY_WALL = local("foundry/wall");

    /** Blocks that the mattock is effective on */
    public static final TagKey<Block> MINABLE_WITH_MATTOCK = local("mineable/mattock");
    /** Blocks that the mattock is effective on */
    public static final TagKey<Block> MINABLE_WITH_PICKADZE = local("mineable/pickadze");
    /** Blocks that the hand axe is effective on */
    public static final TagKey<Block> MINABLE_WITH_HAND_AXE = local("mineable/hand_axe");
    /** Blocks that the scythe or kama are effective on */
    public static final TagKey<Block> MINABLE_WITH_SCYTHE = local("mineable/scythe");
    /** Blocks that the vanilla sword is effective on */
    public static final TagKey<Block> MINABLE_WITH_SWORD = common("mineable/sword");
    /** Blocks that the vanilla shears are effective on */
    public static final TagKey<Block> MINABLE_WITH_SHEARS = common("mineable/shears");
    /** Blocks that the dagger is effective on */
    public static final TagKey<Block> MINABLE_WITH_DAGGER = local("mineable/dagger");
    /** Blocks that the melting pan cannot mine */
    public static final TagKey<Block> MINEABLE_MELTING_BLACKLIST = local("mineable/melting_blacklist");
    /** Blocks that are not replaced by liquid despite not blocking motion, used for water mining list */
    public static final TagKey<Block> UNREPLACABLE_BY_LIQUID = local("unreplacable_by_liquid");

    /** Any block that can be harvested using a kama or scythe */
    public static final TagKey<Block> HARVESTABLE = local("harvestable");
    /** Plants that are broken to drop produce and seeds */
    public static final TagKey<Block> HARVESTABLE_CROPS = local("harvestable/crops");
    /** Plants that drop fruit on interaction */
    public static final TagKey<Block> HARVESTABLE_INTERACT = local("harvestable/interact");
    /** Plants that grow by placing a copy on top */
    public static final TagKey<Block> HARVESTABLE_STACKABLE = local("harvestable/stackable");
    /** Any block that counts as a tree trunk for the lumber axe. Note it must also be harvestable by axes to be effective */
    public static final TagKey<Block> TREE_LOGS = local("tree_log");
    /** List of blocks that should produce bonus gold nugget drops from the chrysophilite modifier. Will only drop bonus if the block does not drop itself */
    public static final TagKey<Block> CHRYSOPHILITE_ORES = local("chrysophilite_ores");

    // misc compat
    public static final TagKey<Block> BUDDING = common("budding");
    // ceramics compat
    @SuppressWarnings("removal")
    public static final TagKey<Block> CISTERN_CONNECTIONS = TagKey.create(Registries.BLOCK, new ResourceLocation("ceramics", "cistern_connections"));

    /** Makes a tag in the tinkers domain */
    private static TagKey<Block> local(String name) {
      return TagKey.create(Registries.BLOCK, getResource(name));
    }

    private static TagKey<Block> common(String name) {
      return TagKey.create(Registries.BLOCK, commonResource(name));
    }
  }

  public static class Items {
    private static void init() {}
    public static final TagKey<Item> WORKBENCHES = common("workbenches");
    public static final TagKey<Item> TABLES = local("tables");
    public static final TagKey<Item> GLASS_PANES_SILICA = common("glass_panes/silica");

    /** Compat: allows other mods to add normal stone variants which can be used to create stone tools.
     * Note this tag includes both stone and cobblestone, unlike the default forge/vanilla tags that include one or the other
     * Also does not include other items that can be used as stone in crafting */
    public static final TagKey<Item> STONE = common("normal_stone");
    /** Compat: allows other mods to add granite variants which can be used to create granite tools */
    public static final TagKey<Item> GRANITE = common("granite");
    /** Compat: allows other mods to add diorite variants which can be used to create diorite tools */
    public static final TagKey<Item> DIORITE = common("diorite");
    /** Compat: allows other mods to add andesite variants which can be used to create andesite tools */
    public static final TagKey<Item> ANDESITE = common("andesite");
    /** Compat: allows other mods to add blackstone variants which can be used to create blackstone tools */
    public static final TagKey<Item> BLACKSTONE = common("blackstone");
    /** Compat: allows other mods to add deepslate variants which can be used to create deepslate tools */
    public static final TagKey<Item> DEEPSLATE = common("deepslate");
    /** Compat: allows other mods to add basalt variants which can be used to create basalt tools */
    public static final TagKey<Item> BASALT = common("basalt");

    /** Materials that can be used to craft wooden tool tables */
    public static final TagKey<Item> PLANKLIKE = local("planklike");
    /** Metals that can be used to craft the anvil */
    public static final TagKey<Item> WORKSTATION_ROCK = local("workstation_rock");
    /** Metals that can be used to craft the anvil */
    public static final TagKey<Item> ANVIL_METAL = local("anvil_metal");
    /** Materials that can be used to craft the smeltery controller */
    public static final TagKey<Item> SMELTERY_BRICKS = local("smeltery_bricks");
    /** Materials that can be used to craft the foundry controller */
    public static final TagKey<Item> FOUNDRY_BRICKS = local("foundry_bricks");
    /** Copper platform variants */
    public static final TagKey<Item> COPPER_PLATFORMS = local("copper_platforms");

    /** Planks in this tag are skipped in the default wood crafting recipe as they have their own variant. Tagging your planks here will allow you to add another wood variant */
    public static final TagKey<Item> VARIANT_PLANKS = local("wood_variants/planks");
    /** Logs in this tag are skipped in the default wood crafting recipe as they have their own variant. Tagging your logs here will allow you to add another wood variant */
    public static final TagKey<Item> VARIANT_LOGS = local("wood_variants/logs");

    public static final TagKey<Item> SLIME_BLOCK = local("slime_block");
    public static final TagKey<Item> CONGEALED_SLIME = local("congealed_slime");
    public static final TagKey<Item> SLIMY_LOGS = local("slimy_logs");
    public static final TagKey<Item> SLIMY_PLANKS = local("slimy_planks");
    public static final TagKey<Item> SLIMY_LEAVES = local("slimy_leaves");
    public static final TagKey<Item> SLIMY_VINES = local("slimy_vines");
    public static final TagKey<Item> SLIMY_SAPLINGS = local("slimy_saplings");
    /** All variants of enderbark roots */
    public static final TagKey<Item> ENDERBARK_ROOTS = local("enderbark/roots");

    public static final TagKey<Item> SEARED_BLOCKS = local("seared_blocks");
    public static final TagKey<Item> SEARED_BRICKS = local("seared_bricks");
    public static final TagKey<Item> SCORCHED_BLOCKS = local("scorched_blocks");
    public static final TagKey<Item> SMELTERY = local("smeltery");
    public static final TagKey<Item> FOUNDRY = local("foundry");

    public static final TagKey<Item> ORES_COBALT = common("ores/cobalt");
    public static final TagKey<Item> RAW_BLOCK_COBALT = common("storage_blocks/raw_cobalt");
    public static final TagKey<Item> RAW_COBALT = common("raw_materials/cobalt");

    // non-standard prefix for items that smelt into 1 nugget but are not in fact nuggets
    public static final TagKey<Item> STEEL_SHARD = common("raw_nuggets/steel");
    public static final TagKey<Item> KNIGHTMETAL_SHARD = common("raw_nuggets/knightmetal");

    public static final TagKey<Item> NUGGETS_NETHERITE = common("nuggets/netherite");
    public static final TagKey<Item> INGOTS_NETHERITE_SCRAP = common("ingots/netherite_scrap");
    public static final TagKey<Item> NUGGETS_NETHERITE_SCRAP = common("nuggets/netherite_scrap");
    public static final TagKey<Item> NUGGETS_COPPER = common("nuggets/copper");
    /** Ingots in this tag will make the whitestone composite variant show in the books. */
    public static final TagKey<Item> WHITESTONE_INGOTS = local("whitestone_ingots");

    public static final TagKey<Item> CASTS = local("casts");
    public static final TagKey<Item> GOLD_CASTS = local("casts/gold");
    public static final TagKey<Item> SAND_CASTS = local("casts/sand");
    public static final TagKey<Item> RED_SAND_CASTS = local("casts/red_sand");
    public static final TagKey<Item> SINGLE_USE_CASTS = local("casts/single_use");
    public static final TagKey<Item> MULTI_USE_CASTS = local("casts/multi_use");
    public static final TagKey<Item> BLANK_SINGLE_USE_CASTS = local("casts/single_use/blank");
    /** Items that count as empty in a casting table */
    public static final TagKey<Item> TABLE_EMPTY_CASTS = local("casts/empty/table");
    /** Items that count as empty in a casting basin */
    public static final TagKey<Item> BASIN_EMPTY_CASTS = local("casts/empty/basin");
    /** Tag of fluid container items disallowed in the proxy tank */
    public static final TagKey<Item> PROXY_TANK_BLACKLIST = local("proxy_tank_blacklist");

    /** Items that can be placed in the pattern slot in the part builder */
    public static final TagKey<Item> PATTERNS = local("patterns");
    /** Items that work in all basic part builder recipes, when unspecified this tag is used for the input */
    public static final TagKey<Item> DEFAULT_PATTERNS = local("patterns/default");
    /** Pattern items that are not consumed in the part builder */
    public static final TagKey<Item> REUSABLE_PATTERNS = local("patterns/reusable");

    /** All basic tinkers tanks */
    public static final TagKey<Item> SEARED_TANKS = local("seared_tanks");
    public static final TagKey<Item> SCORCHED_TANKS = local("scorched_tanks");
    public static final TagKey<Item> TANKS = local("tanks");

    /** Bones that drop from normal skeletons or some equivalent. Intentionally does not use {@link net.minecraftforge.common.Tags.Items#BONES} as that includes many weird bones. */
    public static final TagKey<Item> BONES = local("bones");
    /** Bones that drop from wither skeletons */
    public static final TagKey<Item> WITHER_BONES = common("wither_bones");
    /** Weird inconsistent alternative tag for bones that drop from wither skeletons */
    public static final TagKey<Item> WEIRD_WITHER_BONES_TAG = common("bones/wither");
    public static final TagKey<Item> BOOKS = common("books");
    public static final TagKey<Item> GUIDEBOOKS = common("books/guide");
    public static final TagKey<Item> TINKERS_GUIDES = local("guides");

    /** Any items in this tag will show the erroring block in smelteries and foundries when held or worn */
    public static final TagKey<Item> GENERAL_STRUCTURE_DEBUG = local("structure_debug/general");
    /** Any items in this tag will show the erroring block in smelteries when held or worn */
    public static final TagKey<Item> SMELTERY_DEBUG = local("structure_debug/smeltery");
    /** Any items in this tag will show the erroring block in foundries when held or worn */
    public static final TagKey<Item> FOUNDRY_DEBUG = local("structure_debug/foundry");

    /** Containers that can be used in the duct */
    public static final TagKey<Item> DUCT_CONTAINERS = local("duct_containers");


    /** Items that are seeds for kama harvest */
    public static final TagKey<Item> SEEDS = local("seeds");

    /** Seeds that produce slimy grass */
    public static final TagKey<Item> SLIMY_SEEDS = local("slimy_grass_seeds");

    /** Stones that can be used for stoneshield */
    public static final TagKey<Item> STONESHIELDS = local("stoneshields");
    /** Items that can be consumed for a blaze slimeskull to shoot a fireball */
    public static final TagKey<Item> FIREBALLS = local("fireballs");
    /** Items in this tag cannot be placed inside tool inventories */
    public static final TagKey<Item> TOOL_INVENTORY_BLACKLIST = local("inventory_blacklist");
    /** List of blocks that should produce bonus gold nugget drops from the chrysophilite modifier. Will only drop bonus if the block does not drop itself */
    public static final TagKey<Item> CHRYSOPHILITE_ORES = local("chrysophilite_ores");
    /** All ore rates that are not {@link net.minecraftforge.common.Tags.Items#ORE_RATES_SINGULAR}. Used for recipe conditioning. */
    public static final TagKey<Item> NON_SINGULAR_ORE_RATES = local("non_singular_ore_rates");
    /** Items that cannot be autosmelted */
    public static final TagKey<Item> AUTOSMELT_BLACKLIST = local("autosmelt_blacklist");
    /** Items which should not be duplicated from higher levels of autosmelt */
    public static final TagKey<Item> AUTOSMELT_PLUS_BLACKLIST = common("autosmelt_plus_blacklist");
    /** Items that can be thrown from sleeves. Item must implement {@link Item#use(Level, Player, InteractionHand)} */
    public static final TagKey<Item> THROWABLE = local("throwable");

    /*
     * Tool tags
     */
    /** Anything that can be stored in the part chest */
    public static final TagKey<Item> CHEST_PARTS = local("chest_parts");
    /** All material items, used to populate several lists such as books or subtypes in JEI */
    public static final TagKey<Item> TOOL_PARTS = local("parts");
    /** Tool parts that piglins may barter to the player. Will contain random nether materials from {@link Materials#BARTERED} */
    public static final TagKey<Item> BARTERED_PARTS = local("parts/bartered");

    /** Anything that can be modified in the tool station */
    public static final TagKey<Item> MODIFIABLE = local("modifiable");
    /** Anything that supports modifiers that grant extra modifiers */
    public static final TagKey<Item> BONUS_SLOTS = local("modifiable/bonus_slots");

    /** Modifiable items that contain multiple parts. TODO 1.21: rename to modifiable/parts? */
    public static final TagKey<Item> MULTIPART_TOOL = local("modifiable/multipart");
    /** Modifiable items that contain exactly 1 tool part, special cased in JEI */
    public static final TagKey<Item> SINGLEPART_TOOL = local("modifiable/multipart/single");
    /** Modifiable items that can have range increased */
    public static final TagKey<Item> AOE = local("modifiable/aoe");
    /** Tools that use durability and can be repaired. Items in this tag support the {@link ToolStats#DURABILITY} stat. */
    public static final TagKey<Item> DURABILITY = local("modifiable/durability");

    /** Generally faster tools with lower damage, used for book display */
    public static final TagKey<Item> SMALL_TOOLS = local("modifiable/small");
    /** Generally slower tools with higher damage, used for book display and to customize stat debuff on offhand attack */
    public static final TagKey<Item> BROAD_TOOLS = local("modifiable/broad");
    /** Speciality tools that don't fit into either broad or small, notably includes staffs. Used in the books */
    public static final TagKey<Item> SPECIAL_TOOLS = local("modifiable/special");
    /** Tools found through loot. Used in books and some modifiers */
    public static final TagKey<Item> ANCIENT_TOOLS = local("modifiable/ancient");
    /** Tools traded by the wandering trader. By default, just ancient, but you can add other stuff. Just remember recycling exists. */
    public static final TagKey<Item> TRADER_TOOLS = local("modifiable/wandering_trader");

    /** Tools that can adjust the loot context for {@link slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook} */
    public static final TagKey<Item> LOOT_CAPABLE_TOOL = local("modifiable/loot_capable_tool");
    /** Anything that is used in the player's hand, mostly tools that support interaction, but other tools can be added directly */
    public static final TagKey<Item> HELD = local("modifiable/held");
    /** Anything that can use interaction modifiers */
    public static final TagKey<Item> INTERACTABLE = local("modifiable/interactable");
    /** Tools that can interact on right click */
    public static final TagKey<Item> INTERACTABLE_RIGHT = local("modifiable/interactable/right");
    /** Tools that can charge up interaction. Includes anything in {@link #INTERACTABLE_RIGHT}, {@link #BOWS}, or {@link #SHIELDS} */
    public static final TagKey<Item> INTERACTABLE_CHARGE = local("modifiable/interactable/charge");
    /** Tools that can charge up interaction, using a modifier for their main action. Like {@link #INTERACTABLE_CHARGE} but excludes bows. */
    public static final TagKey<Item> INTERACTABLE_CHARGE_MODIFIER = local("modifiable/interactable/charge/modifier");
    /** Tools that can interact on left click */
    public static final TagKey<Item> INTERACTABLE_LEFT = local("modifiable/interactable/left");
    /** Tools that can interact when worn as armor */
    public static final TagKey<Item> INTERACTABLE_ARMOR = local("modifiable/interactable/armor");
    /** Tools that can interact on left click or right click */
    public static final TagKey<Item> INTERACTABLE_DUAL = local("modifiable/interactable/dual");

    /** Items in this tag support the @link ToolStats#ATTACK_DAMAGE} stat. Should not be added to directly typically, use one of the below tags instead. */
    public static final TagKey<Item> MELEE = local("modifiable/melee");
    /** Modifiable items that support melee attacks. Items in this tag support the {@link ToolStats#ATTACK_SPEED} stat (plus those from {@link #MELEE}). */
    public static final TagKey<Item> MELEE_WEAPON = local("modifiable/melee/weapon");
    /** Modifiable items that specifically are designed for melee, removes melee penalties. Primary melee items are assumed held. */
    public static final TagKey<Item> MELEE_PRIMARY = local("modifiable/melee/primary");
    /** Modifiable items that are also swords, typically no use outside of combat. Swords are assumed held.  */
    public static final TagKey<Item> SWORD = local("modifiable/melee/sword");
    /** Modifiable items that boost unarmed attack damage. By default this is just chestplates, but added as a tag to make it easier for adds to change. Unarmed tools cannot modify attack speed and typically don't take damage from melee. */
    public static final TagKey<Item> UNARMED = local("modifiable/melee/unarmed");
    /** Modifiable items that can parry, cannot receive blocking */
    public static final TagKey<Item> PARRY = local("modifiable/melee/parry");

    /** Modifiable items that can break blocks. Items in this tag support the {@link ToolStats#MINING_SPEED} and {@link ToolStats#HARVEST_TIER} stats. */
    public static final TagKey<Item> HARVEST = local("modifiable/harvest");
    /** Modifiable items that are specifically designed for harvest, removes harvest penalties */
    public static final TagKey<Item> HARVEST_PRIMARY = local("modifiable/harvest/primary");
    /** Modifiable items that can break stone blocks */
    public static final TagKey<Item> STONE_HARVEST = local("modifiable/harvest/stone");

    /** Modifiable items that are worn as armor. Items in this tag support the {@link ToolStats#ARMOR}, {@link ToolStats#ARMOR_TOUGHNESS} and {@link ToolStats#KNOCKBACK_RESISTANCE} stats. */
    public static final TagKey<Item> ARMOR = local("modifiable/armor");
    /** Modifiable items that are worn as boots */
    public static final TagKey<Item> BOOTS = local("modifiable/armor/boots");
    /** Modifiable items that are worn as leggings */
    public static final TagKey<Item> LEGGINGS = local("modifiable/armor/leggings");
    /** Modifiable items that are worn as chestplates */
    public static final TagKey<Item> CHESTPLATES = local("modifiable/armor/chestplate");
    /** Modifiable items that are worn as helmets */
    public static final TagKey<Item> HELMETS = local("modifiable/armor/helmets");
    /** Modifiable items that are worn on any of the main armor slots, likely is applicable for curio slots too */
    public static final TagKey<Item> WORN_ARMOR = local("modifiable/armor/worn");
    /** Modifiable items that are held in either hand */
    public static final TagKey<Item> HELD_ARMOR = local("modifiable/armor/held");
    /** Modifiable items that have innate shielding behavior */
    public static final TagKey<Item> SHIELDS = local("modifiable/shields");
    /** @deprecated After migrating travelers to have a material, doing away with golden armor on slimesuit. If you still want this on your armor, we recommend adding a new recipe */
    @Deprecated(forRemoval = true)
    public static final TagKey<Item> GOLDEN_ARMOR = local("modifiable/armor/golden");

    // armor book tags
    /** Full list of armor shown in the encyclopedia, can add to directly to show only in the encyclopedia */
    public static final TagKey<Item> BOOK_ARMOR = local("modifiable/book_armor");
    /** Full list of armor shown in materials and you, automatically added first to encyclopedia */
    public static final TagKey<Item> BASIC_ARMOR = local("modifiable/book_armor/materials_and_you");
    /** Full list of armor shown in puny smelting, automatically added second to encyclopedia */
    public static final TagKey<Item> PUNY_ARMOR = local("modifiable/book_armor/puny_smelting");
    /** Full list of armor shown in mighty smelting, automatically added third to encyclopedia */
    public static final TagKey<Item> MIGHTY_ARMOR = local("modifiable/book_armor/mighty_smelting");
    /** Full list of armor shown in fantastic foundrry, automatically added fourth to encyclopedia */
    public static final TagKey<Item> FANTASTIC_ARMOR = local("modifiable/book_armor/fantastic_foundry");
    /** Full list of armor shown in tinkers gadgetry, automatically added last to encyclopedia */
    public static final TagKey<Item> GADGETRY_ARMOR = local("modifiable/book_armor/tinkers_gadgetry");

    /** Modifiable items that support ranged attacks. Items in this tag support {@link ToolStats#DRAW_SPEED}, {@link ToolStats#VELOCITY} and {@link ToolStats#ACCURACY} */
    public static final TagKey<Item> RANGED = local("modifiable/ranged");
    /** Modifiable items that launch a projectile, as opposed to being the projectile. Additionally includes {@link ToolStats#PROJECTILE_DAMAGE} for its launch power. */
    public static final TagKey<Item> LAUNCHERS = local("modifiable/ranged/launcher");
    /** Any modifiable ranged items that are a bow, includes crosbows and longbows */
    public static final TagKey<Item> BOWS = local("modifiable/ranged/bows");
    /** Any modifiable bows that fire arrows on release */
    public static final TagKey<Item> LONGBOWS = local("modifiable/ranged/longbows");
    /** Bows supporting the ballista modifier. In code, only {@link slimeknights.tconstruct.library.tools.item.ranged.ModifiableBowItem} implements this functionality. */
    public static final TagKey<Item> BALLISTAS = local("modifiable/ranged/ballistas");
    /** Any modifiable bows that store an arrow then fire on next use */
    public static final TagKey<Item> CROSSBOWS = local("modifiable/ranged/crossbows");
    /** Modifiable items support special staff modifiers, is a subtag of ranged. */
    public static final TagKey<Item> STAFFS = local("modifiable/staffs");
    /** Modifiable items that support fishing modifiers. */
    public static final TagKey<Item> FISHING_RODS = local("modifiable/fishing_rods");
    /** Ranged tools to show in materials and you and the encyclopedia. */
    public static final TagKey<Item> SMALL_RANGED = local("modifiable/ranged/small");
    /** Ranged tools to show in mighty smelting and the encyclopedia. */
    public static final TagKey<Item> BROAD_RANGED = local("modifiable/ranged/broad");

    /** Items in this tag have a primary purpose of being ammo */
    public static final TagKey<Item> AMMO = local("modifiable/ammo");
    /** Ammo that is thrown directly, instead of using a launcher. */
    public static final TagKey<Item> THROWN_AMMO = local("modifiable/ammo/thrown");
    /** Tools that support being fired using bows with the ballisa modifier. */
    public static final TagKey<Item> BALLISTA_AMMO = local("modifiable/ballista_ammo");
    /** Items in this tag have some cheaper modifier recipes since they are not reusable */
    public static final TagKey<Item> SINGLE_USE = local("modifiable/single_use");

    /** Tools that can receive wood based embellishments */
    public static final TagKey<Item> EMBELLISHMENT_WOOD = local("modifiable/embellishment/wood");
    /** Tools that can receive slime based embellishments */
    public static final TagKey<Item> EMBELLISHMENT_SLIME = local("modifiable/embellishment/slime");
    /** Tools that can be dyed */
    public static final TagKey<Item> DYEABLE = local("modifiable/dyeable");
    /** Armor items that support trim */
    public static final TagKey<Item> TRIM = local("modifiable/armor/trim");
    /** Tools to blacklist from default part recycling recipes. May still be recyclable in other recipes */
    public static final TagKey<Item> UNRECYCLABLE = local("modifiable/unrecyclable");
    /** Tools to blacklist from default salvage recipes. May still be salvagable in other recipes */
    public static final TagKey<Item> UNSALVAGABLE = local("modifiable/unsalvageable");
    /** Tools to blacklist from part swapping */
    public static final TagKey<Item> UNSWAPPABLE = local("modifiable/unswappable");

    /** Tag so mods like thermal know our scyhtes can harvest */
    public static final TagKey<Item> SCYTHES = common("tools/scythe");

    /** Tag for others adding empty potion bottles */
    public static final TagKey<Item> SPLASH_BOTTLE = common("bottles/splash");
    public static final TagKey<Item> LINGERING_BOTTLE = common("bottles/lingering");

    // compat tags
    /** Tag meaning necronium is available */
    public static final TagKey<Item> URANIUM_INGOTS = common("ingots/uranium");
    /** Tag of trophies from bosses, to grant an additional upgrade slot to tools. Meant for Twilight Forest boss trophies, but suppose you can add other bosses that are not easily farmed. */
    public static final TagKey<Item> BOSS_TROPHIES = local("boss_trophies");

    /** Fluids in this tag won't show in JEI */
    public static final TagKey<Item> HIDDEN_IN_RECIPE_VIEWERS = hiddenFromRecipeViewers(Registries.ITEM);

    /** Makes a tag in the tinkers domain */
    private static TagKey<Item> local(String name) {
      return TagKey.create(Registries.ITEM, getResource(name));
    }

    /** Makes a tag in the forge domain */
    private static TagKey<Item> common(String name) {
      return TagKey.create(Registries.ITEM, commonResource(name));
    }
  }

  public static class Fluids {
    private static void init() {}
    public static final TagKey<Fluid> SLIME = local("slime");
    /** Causes the fluid to be formatted like a metal in tooltips */
    public static final TagKey<Fluid> SLIME_TOOLTIPS = local("tooltips/slime");
    /** Causes the fluid to be formatted with buckets, bottles, and drops in the tooltip, like venom */
    public static final TagKey<Fluid> BOTTLE_TOOLTIPS = local("tooltips/bottle");
    /** Causes the fluid to be formatted like a clay in tooltips */
    public static final TagKey<Fluid> CLAY_TOOLTIPS = local("tooltips/clay");
    /** Causes the fluid to be formatted like a metal in tooltips */
    public static final TagKey<Fluid> METAL_TOOLTIPS = local("tooltips/metal");
    /** Causes the fluid to be formatted like gems, with 3x3 blocks */
    public static final TagKey<Fluid> LARGE_GEM_TOOLTIPS = local("tooltips/gem_large");
    /** Causes the fluid to be formatted like gems, with 2x2 blocks */
    public static final TagKey<Fluid> SMALL_GEM_TOOLTIPS = local("tooltips/gem_small");
    /** Causes the fluid to be formatted like glass in tooltips */
    public static final TagKey<Fluid> GLASS_TOOLTIPS = local("tooltips/glass");
    /** @deprecated use {@link slimeknights.mantle.datagen.MantleTags.Fluids#SOUP} */
    @Deprecated(forRemoval = true)
    public static final TagKey<Fluid> SOUP_TOOLTIPS = local("tooltips/soup");

    /** Fluids found in swashers the hands of drowned */
    public static final TagKey<Fluid> DROWNED_SWASHER = local("swasher/drowned");
    /** Fluids found in swashers the hands of wither skeletons */
    public static final TagKey<Fluid> WITHER_SKELETON_SWASHER = local("swasher/wither_skeleton");

    /** @deprecated Fluids have all been given unique effects, use {@link slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider} */
    @Deprecated(forRemoval = true)
    public static final TagKey<Fluid> CLAY_SPILLING = local("spilling/clay");
    /** @deprecated Fluids have all been given unique effects, use {@link slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider} */
    @Deprecated(forRemoval = true)
    public static final TagKey<Fluid> GLASS_SPILLING = local("spilling/glass");
    /** @deprecated Fluids have all been given unique effects, use {@link slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider} */
    @Deprecated(forRemoval = true)
    public static final TagKey<Fluid> CHEAP_METAL_SPILLING = local("spilling/metal/cheap");
    /** @deprecated Fluids have all been given unique effects, use {@link slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider} */
    @Deprecated(forRemoval = true)
    public static final TagKey<Fluid> AVERAGE_METAL_SPILLING = local("spilling/metal/average");
    /** @deprecated Fluids have all been given unique effects, use {@link slimeknights.tconstruct.library.data.tinkering.AbstractFluidEffectProvider} */
    @Deprecated(forRemoval = true)
    public static final TagKey<Fluid> EXPENSIVE_METAL_SPILLING = local("spilling/metal/expensive");

    /** Fluids in this tag won't show in the creative filled tanks */
    public static final TagKey<Fluid> HIDE_IN_CREATIVE_TANKS = local("hide_in_creative_tanks");
    /** Fluids in this tag won't show in JEI */
    public static final TagKey<Fluid> HIDDEN_IN_RECIPE_VIEWERS = hiddenFromRecipeViewers(Registries.FLUID);

    /** Any fluids in this tag will have block {@link slimeknights.tconstruct.library.modifiers.fluid.FluidEffects} run when fired using the chem thrower */
    public static final TagKey<Fluid> CHEMTHROWER_BLOCK_EFFECTS = local("chemthrower_effects/block");
    /** Any fluids in this tag will have entity {@link slimeknights.tconstruct.library.modifiers.fluid.FluidEffects} run when fired using the chem thrower */
    public static final TagKey<Fluid> CHEMTHROWER_ENTITY_EFFECTS = local("chemthrower_effects/entity");
    /** Any fluids in this tag will have both block and entity {@link slimeknights.tconstruct.library.modifiers.fluid.FluidEffects} run when fired using the chem thrower */
    public static final TagKey<Fluid> CHEMTHROWER_BOTH_EFFECTS = local("chemthrower_effects/both");

    private static TagKey<Fluid> local(String name) {
      return TagKey.create(Registries.FLUID, getResource(name));
    }
  }

  public static class EntityTypes {
    private static void init() {}
    public static final TagKey<EntityType<?>> SLIMES = common("slimes");
    public static final TagKey<EntityType<?>> BACON_PRODUCER = local("bacon_producer");

    /**
     * Entities in this tag either run proper hooks to use a melee weapon on left click or cause issues with our melee modifier logic.
     * Anything not in this tag will attempt the fallback behavior which apply effects during damage events.
     */
    public static final TagKey<EntityType<?>> DAMAGE_MODIFIER_BLACKLIST = local("damage_modifier_blacklist");

    public static final TagKey<EntityType<?>> MELTING_SHOW = local("melting/show_in_default");
    public static final TagKey<EntityType<?>> MELTING_HIDE = local("melting/hide_in_default");
    public static final TagKey<EntityType<?>> PIGGYBACKPACK_BLACKLIST = local("piggybackpack_blacklist");

    /** Entities in this tag take more damage from bane of sssss */
    public static final TagKey<EntityType<?>> CREEPERS = common("creepers");
    public static final TagKey<EntityType<?>> VILLAGERS = common("villagers");
    public static final TagKey<EntityType<?>> ILLAGERS = common("illagers");
    /** Entities in this tag may spawn with battle signs */
    public static final TagKey<EntityType<?>> PIGLINS = common("piglins");
    /** Entities in this tag take more damage from killager */
    public static final TagKey<EntityType<?>> KILLAGERS = local("killagers");
    /** Mobs that rarely spawn, boosts drop rate of severing */
    public static final TagKey<EntityType<?>> RARE_MOBS = local("rare_mobs");
    /** Mobs that get the 4x protection boost due to only 1 armor piece */
    public static final TagKey<EntityType<?>> SMALL_ARMOR = common("small_armor");

    /** Things that can be collected using {@link net.minecraft.world.entity.Entity#playerTouch(Player)} using a fishing rod. */
    public static final TagKey<EntityType<?>> COLLECTABLES = common("collectables");

    /** Projectiles with this tag will not be discarded by any relevant modifiers. */
    public static final TagKey<EntityType<?>> REUSABLE_AMMO = common("reusable_ammo");
    /** Projectiles with this tag cannot be reflected */
    public static final TagKey<EntityType<?>> REFLECTING_BLACKLIST = common("reflecting/blacklist");
    /** Projectiles with this tag cannot be reflected */
    public static final TagKey<EntityType<?>> REFLECTING_PRESERVE_OWNER = common("reflecting/preserve_owner");

    /** Entities that will not heal you using necrotic */
    public static final TagKey<EntityType<?>> NECROTIC_BLACKLIST = common("necrotic_blacklist");

    private static TagKey<EntityType<?>> local(String name) {
      return TagKey.create(Registries.ENTITY_TYPE, getResource(name));
    }

    private static TagKey<EntityType<?>> common(String name) {
      return TagKey.create(Registries.ENTITY_TYPE, commonResource(name));
    }
  }

  public static class TileEntityTypes {
    private static void init() {}

    /** Block entities in this tag can be used as a side inventory for the crafting station and alike */
    public static final TagKey<BlockEntityType<?>> SIDE_INVENTORIES = local("side_inventories");

    @SuppressWarnings("SameParameterValue")  // may want more tags later
    private static TagKey<BlockEntityType<?>> local(String name) {
      return TagKey.create(Registries.BLOCK_ENTITY_TYPE, getResource(name));
    }
  }

  public static class Biomes {
    private static void init() {}

    /** Biomes the earthslime islands can spawn in, typically overworld ocean */
    public static final TagKey<Biome> EARTHSLIME_ISLANDS = local("islands/earthslime");
    /** Biomes the skyslime islands can spawn in, generally in most overworld locations */
    public static final TagKey<Biome> SKYSLIME_ISLANDS = local("islands/skyslime");
    /** Biomes the clay islands can spawn in, generally non-forested overworld */
    public static final TagKey<Biome> CLAY_ISLANDS = local("islands/clay");
    /** Biomes the blood islands can spawn in, generally anywhere in the nether */
    public static final TagKey<Biome> BLOOD_ISLANDS = local("islands/blood");
    /** Biomes the enderslime island can spawn in, generally the outer end islands */
    public static final TagKey<Biome> ENDERSLIME_ISLANDS = local("islands/enderslime");

    private static TagKey<Biome> local(String name) {
      return TagKey.create(Registries.BIOME, getResource(name));
    }
  }

  public static class Modifiers {
    private static void init() {}
    /** Gem modifiers, one of which is needed for netherite */
    public static final TagKey<Modifier> GEMS = local("gems");
    /** Blacklist for modifiers that cannot be hidden with invisible ink */
    public static final TagKey<Modifier> INVISIBLE_INK_BLACKLIST = local("invisible_ink_blacklist");
    /** Blacklist for modifiers that cannot be removed via the general recipe */
    public static final TagKey<Modifier> REMOVE_MODIFIER_BLACKLIST = local("remove_blacklist");
    /** Blacklist for modifiers that cannot be extracted via the general recipe */
    public static final TagKey<Modifier> EXTRACT_MODIFIER_BLACKLIST = local("extract_blacklist/tools");
    /** Blacklist for modifiers that cannot be extracted via the slotless recipe */
    public static final TagKey<Modifier> EXTRACT_SLOTLESS_BLACKLIST = local("extract_blacklist/slotless");
    /** Blacklist for modifiers that cannot be extracted via the upgrade recipe */
    public static final TagKey<Modifier> EXTRACT_UPGRADE_BLACKLIST = local("extract_blacklist/upgrade");
    /** Modifiers that support blocking while charging, for the sake of shields */
    public static final TagKey<Modifier> BLOCK_WHILE_CHARGING = local("block_while_charging");
    /** Modifiers that can be used on both left and right click. Does not care about armor modifiers */
    public static final TagKey<Modifier> DUAL_INTERACTION = local("dual_interaction");
    /** Common defense modifier types, used for skyslime armor */
    public static final TagKey<Modifier> SLIME_DEFENSE = local("slime_defense");

    /** Modifiers in this tag prevent the overslime debuff */
    public static final TagKey<Modifier> OVERSLIME_FRIEND = local("overslime_friend");
    /** Modifiers in this tag will show the wireframe hitbox on all blocks */
    public static final TagKey<Modifier> AOE_INTERACTION = local("aoe_interaction");
    /** Modifiers in this tag will allow charging a bow that has no ammo, making the bow charge up */
    public static final TagKey<Modifier> CHARGE_EMPTY_BOW_WITH_DRAWTIME = local("charge_empty_bow/with_drawtime");
    /** Modifiers in this tag will allow charging a bow that has no ammo, but won't charge the bow */
    public static final TagKey<Modifier> CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME = local("charge_empty_bow/without_drawtime");
    /** Movement modifiers that can activate the drill attack */
    public static final TagKey<Modifier> DRILL_ATTACKS = local("drill_attacks");

    // book tags - these are used to determine pages to load in resource packs
    // upgrades
    public static final TagKey<Modifier> UPGRADES = local("upgrades");
    public static final TagKey<Modifier> GENERAL_UPGRADES = local("upgrades/general");
    public static final TagKey<Modifier> MELEE_UPGRADES = local("upgrades/melee");
    public static final TagKey<Modifier> DAMAGE_UPGRADES = local("upgrades/damage");
    public static final TagKey<Modifier> HARVEST_UPGRADES = local("upgrades/harvest");
    public static final TagKey<Modifier> RANGED_UPGRADES = local("upgrades/ranged");
    // armor upgrades
    public static final TagKey<Modifier> ARMOR_UPGRADES = local("upgrades/armor");
    public static final TagKey<Modifier> GENERAL_ARMOR_UPGRADES = local("upgrades/armor/general");
    public static final TagKey<Modifier> HELMET_UPGRADES = local("upgrades/armor/helmet");
    public static final TagKey<Modifier> CHESTPLATE_UPGRADES = local("upgrades/armor/chestplate");
    public static final TagKey<Modifier> LEGGING_UPGRADES = local("upgrades/armor/leggings");
    public static final TagKey<Modifier> BOOT_UPGRADES = local("upgrades/armor/boots");
    // abilities
    public static final TagKey<Modifier> ABILITIES = local("abilities");
    public static final TagKey<Modifier> GENERAL_ABILITIES = local("abilities/general");
    public static final TagKey<Modifier> INTERACTION_ABILITIES = local("abilities/interaction");
    public static final TagKey<Modifier> MELEE_ABILITIES = local("abilities/melee");
    public static final TagKey<Modifier> HARVEST_ABILITIES = local("abilities/harvest");
    public static final TagKey<Modifier> RANGED_ABILITIES = local("abilities/ranged");
    // armor abilities
    public static final TagKey<Modifier> ARMOR_ABILITIES = local("abilities/armor");
    public static final TagKey<Modifier> GENERAL_ARMOR_ABILITIES = local("abilities/armor/general");
    public static final TagKey<Modifier> HELMET_ABILITIES = local("abilities/armor/helmet");
    public static final TagKey<Modifier> CHESTPLATE_ABILITIES = local("abilities/armor/chestplate");
    public static final TagKey<Modifier> LEGGING_ABILITIES = local("abilities/armor/leggings");
    public static final TagKey<Modifier> BOOT_ABILITIES = local("abilities/armor/boots");
    public static final TagKey<Modifier> SHIELD_ABILITIES = local("abilities/armor/shield");
    // defense
    public static final TagKey<Modifier> DEFENSE = local("defense");
    public static final TagKey<Modifier> PROTECTION_DEFENSE = local("defense/protection");
    public static final TagKey<Modifier> SPECIAL_DEFENSE = local("defense/special");
    // slotless
    public static final TagKey<Modifier> SLOTLESS = local("slotless");
    public static final TagKey<Modifier> GENERAL_SLOTLESS = local("slotless/general");
    public static final TagKey<Modifier> BONUS_SLOTLESS = local("slotless/bonus");
    public static final TagKey<Modifier> COSMETIC_SLOTLESS = local("slotless/cosmetic");

    // JEI
    public static final TagKey<Modifier> HIDDEN_FROM_RECIPE_VIEWERS = hiddenFromRecipeViewers(ModifierManager.REGISTRY_KEY);


    private static TagKey<Modifier> local(String name) {
      return ModifierManager.getTag(getResource(name));
    }
  }

  public static class Materials {
    private static void init() {}
    /** Materials available in nether. */
    public static final TagKey<IMaterial> NETHER = local("nether");
    /** Materials that cannot be obtained without going to the nether. */
    public static final TagKey<IMaterial> NETHER_GATED = local("nether/gated");

    /** Materials bartered from piglins */
    public static final TagKey<IMaterial> BARTERED = local("bartered");
    /** Materials not found on ancient tools or other loot sources */
    public static final TagKey<IMaterial> EXCLUDE_FROM_LOOT = local("exclude_from_loot");

    /** Materials acting as compatability metals. Will allow them to use {@link slimeknights.tconstruct.tools.TinkerToolParts#fakeIngot} assuming they lack an ingot. */
    public static final TagKey<IMaterial> COMPATABILITY_METALS = local("compatibility_metals");
    /** Materials acting as compatability alloys. Will allow them to use {@link slimeknights.tconstruct.tools.TinkerToolParts#fakeStorageBlock}. */
    public static final TagKey<IMaterial> COMPATABILITY_ALLOYS = local("compatibility_metals/alloys");

    // tags for book material lists
    /** Ammo materials to show in materials and you. Used instead of tiers due to non-standard ammo behavior. */
    public static final TagKey<IMaterial> BASIC_AMMO = local("book/basic_ammo");
    /** Materials gated behind blazing blood, typically tier 4. Will show in Fantastic Foundry. */
    public static final TagKey<IMaterial> BLAZING_BLOOD = local("book/blazing_blood");
    /** Materials found from late game exploration such as the end. */
    public static final TagKey<IMaterial> DISTANT = local("book/distant");

    // material categories
    // melee harvest
    /** Materials that work well at both melee and harvest tasks, often durability focused or all around */
    public static final TagKey<IMaterial> GENERAL = local("melee_harvest/general");
    /** Materials that work best on melee tools */
    public static final TagKey<IMaterial> MELEE = local("melee_harvest/melee");
    /** Materials that work well on harvest tools */
    public static final TagKey<IMaterial> HARVEST = local("melee_harvest/harvest");

    // ranged
    /** Ranged materials with average drawspeed and velocity */
    public static final TagKey<IMaterial> BALANCED = local("ranged/balanced");
    /** Ranged materials that prioritize high speed or DPS */
    public static final TagKey<IMaterial> LIGHT = local("ranged/light");
    /** Ranged materials that maximize damage */
    public static final TagKey<IMaterial> HEAVY = local("ranged/heavy");

    // slimeskull
    /** Materials that are a slimeskull. Mostly used for a sort order in books rather than having gameplay function. */
    public static final TagKey<IMaterial> SLIMESKULL = local("slimeskull");

    @SuppressWarnings("SameParameterValue")  // may want more tags later
    private static TagKey<IMaterial> local(String name) {
      return MaterialManager.getTag(getResource(name));
    }
  }

  public static class DamageTypes {
    private static void init() {}
    /** Damage types reduced by the melee protection modifier */
    public static final TagKey<DamageType> MELEE_PROTECTION = local("protection/melee");
    /** Damage types reduced by the projectile protection modifier */
    public static final TagKey<DamageType> PROJECTILE_PROTECTION = local("protection/projectile");
    /** Damage types reduced by the fire protection modifier */
    public static final TagKey<DamageType> FIRE_PROTECTION = local("protection/fire");
    /** Damage types reduced by the blast protection modifier */
    public static final TagKey<DamageType> BLAST_PROTECTION = local("protection/blast");
    /** Damage types reduced by the magic protection modifier */
    public static final TagKey<DamageType> MAGIC_PROTECTION = local("protection/magic");
    /** Damage types reduced by the feather falling modifier */
    public static final TagKey<DamageType> FALL_PROTECTION = local("protection/fall");

    /** Damage types that can use modifiers. */
    public static final TagKey<DamageType> MODIFIER_WHITELIST = local("modifier_whitelist");

    private static TagKey<DamageType> local(String name) {
      return TagKey.create(Registries.DAMAGE_TYPE, getResource(name));
    }
  }

  public static class MenuTypes {
    private static void init() {}

    /** Any menus that support being closed in favor of the tool inventory */
    public static final TagKey<MenuType<?>> TOOL_INVENTORY_REPLACEMENTS = TagKey.create(Registries.MENU, getResource("tool_inventory_replacements"));
  }

  public static class Potions {
    private static void init() {}

    /** Any potion variants in this tag will be hidden from the variants of the potion fluid shown in JEI. */
    public static final TagKey<Potion> HIDDEN_FLUID = TagKey.create(Registries.POTION, getResource("hide_in_fluid"));
  }
}
