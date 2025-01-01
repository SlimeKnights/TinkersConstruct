package slimeknights.tconstruct.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class TinkerTags {
  /** Called on mod construct to set up tags */
  public static void init() {
    Blocks.init();
    Items.init();
    Fluids.init();
    EntityTypes.init();
    TileEntityTypes.init();
    Biomes.init();
    Modifiers.init();
    Materials.init();
  }

  public static class Blocks {
    private static void init() {}
    public static final TagKey<Block> WORKBENCHES = forgeTag("workbenches");
    public static final TagKey<Block> TABLES = tag("tables");
    public static final TagKey<Block> GLASS_PANES_SILICA = forgeTag("glass_panes/silica");

    /** Compat: allows other mods to add normal stone variants which can be used to create stone tools.
     * Note this tag includes both stone and cobblestone, unlike the default forge/vanilla tags that include one or the other
     * Also does not include other items that can be used as stone in crafting */
    public static final TagKey<Block> STONE = forgeTag("normal_stone");
    /** Compat: allows other mods to add granite variants which can be used to create granite tools */
    public static final TagKey<Block> GRANITE = forgeTag("granite");
    /** Compat: allows other mods to add diorite variants which can be used to create diorite tools */
    public static final TagKey<Block> DIORITE = forgeTag("diorite");
    /** Compat: allows other mods to add andesite variants which can be used to create andesite tools */
    public static final TagKey<Block> ANDESITE = forgeTag("andesite");
    /** Compat: allows other mods to add blackstone variants which can be used to create blackstone tools */
    public static final TagKey<Block> BLACKSTONE = forgeTag("blackstone");
    /** Compat: allows other mods to add deepslate variants which can be used to create deepslate tools */
    public static final TagKey<Block> DEEPSLATE = forgeTag("deepslate");
    /** Compat: allows other mods to add basalt variants which can be used to create basalt tools */
    public static final TagKey<Block> BASALT = forgeTag("basalt");

    /** Blocks that render a transparent overlay when you are inside */
    public static final TagKey<Block> TRANSPARENT_OVERLAY = tag("transparent_overlay");

    /** Materials that can be used to craft wooden tool tables */
    public static final TagKey<Block> PLANKLIKE = tag("planklike");
    /** Metals that can be used to craft the anvil */
    public static final TagKey<Block> WORKSTATION_ROCK = tag("workstation_rock");
    /** Metals that can be used to craft the anvil */
    public static final TagKey<Block> ANVIL_METAL = tag("anvil_metal");
    /** Materials that can be used to craft the smeltery controller */
    public static final TagKey<Block> SMELTERY_BRICKS = tag("smeltery_bricks");
    /** Materials that can be used to craft the foundry controller */
    public static final TagKey<Block> FOUNDRY_BRICKS = tag("foundry_bricks");

    /** Things the platform connects to */
    public static final TagKey<Block> PLATFORM_CONNECTIONS = tag("platform_connections");
    /** Copper platform variants */
    public static final TagKey<Block> COPPER_PLATFORMS = tag("copper_platforms");

    // Slime spawn
    /** Blocks skyslimes can spawn on in the overworld */
    public static final TagKey<Block> SKY_SLIME_SPAWN = tag("slime_spawn/sky");
    /** Blocks earthslimes can spawn on in the overworld */
    public static final TagKey<Block> EARTH_SLIME_SPAWN = tag("slime_spawn/earth");
    /** Blocks enderslimes can spawn on in the end */
    public static final TagKey<Block> ENDER_SLIME_SPAWN = tag("slime_spawn/ender");

    public static final TagKey<Block> SLIME_BLOCK = tag("slime_block");
    public static final TagKey<Block> CONGEALED_SLIME = tag("congealed_slime");
    public static final TagKey<Block> SLIMY_LOGS = tag("slimy_logs");
    public static final TagKey<Block> SLIMY_PLANKS = tag("slimy_planks");
    public static final TagKey<Block> SLIMY_LEAVES = tag("slimy_leaves");
    public static final TagKey<Block> SLIMY_VINES = tag("slimy_vines");
    public static final TagKey<Block> SLIMY_SAPLINGS = tag("slimy_saplings");
    /** All variants of enderbark roots */
    public static final TagKey<Block> ENDERBARK_ROOTS = tag("enderbark/roots");
    /** Blocks that are slime grass on top of dirt */
    public static final TagKey<Block> SLIMY_GRASS = tag("slimy_grass");
    /** Blocks that are slime fungus on top of dirt */
    public static final TagKey<Block> SLIMY_NYLIUM = tag("slimy_nylium");
    /** Slime grass, slime fungus, or slime dirt */
    public static final TagKey<Block> SLIMY_SOIL = tag("slimy_soil");
    /** Blocks enderbark logs can replace */
    public static final TagKey<Block> ENDERBARK_LOGS_CAN_GROW_THROUGH = tag("enderbark/logs_can_grow_through");
    /** Blocks enderbark roots can replace */
    public static final TagKey<Block> ENDERBARK_ROOTS_CAN_GROW_THROUGH = tag("enderbark/roots_can_grow_through");

    public static final TagKey<Block> ORES_COBALT = forgeTag("ores/cobalt");
    public static final TagKey<Block> RAW_BLOCK_COBALT = forgeTag("storage_blocks/raw_cobalt");

    public static final TagKey<Block> SEARED_BLOCKS = tag("seared_blocks");
    public static final TagKey<Block> SEARED_BRICKS = tag("seared_bricks");
    public static final TagKey<Block> SEARED_TANKS = tag("seared_tanks");

    public static final TagKey<Block> SCORCHED_BLOCKS = tag("scorched_blocks");
    public static final TagKey<Block> SCORCHED_TANKS = tag("scorched_tanks");


    /** Blocks which make a heater part of a structure, when placed above a heater */
    public static final TagKey<Block> HEATER_CONTROLLERS = tag("heater_controllers");
    /** Blocks valid as a fuel tank for the melter or alloyer, should be item handlers with 1 slot or fluid handlers with 1 fluid */
    public static final TagKey<Block> FUEL_TANKS = tag("fuel_tanks");
    /** Tanks that serve as a valid input for the alloyer, should be fluid handlers with 1 fluid */
    public static final TagKey<Block> ALLOYER_TANKS = tag("alloyer_tanks");

    /** Blocks that make up the smeltery structure */
    public static final TagKey<Block> SMELTERY = tag("smeltery");
    /** Blocks valid as a smeltery tank, required for fuel */
    public static final TagKey<Block> SMELTERY_TANKS = tag("smeltery/tanks");
    /** Blocks valid as a smeltery floor */
    public static final TagKey<Block> SMELTERY_FLOOR = tag("smeltery/floor");
    /** Blocks valid in the smeltery wall */
    public static final TagKey<Block> SMELTERY_WALL = tag("smeltery/wall");

    /** Blocks that make up the foundry structure */
    public static final TagKey<Block> FOUNDRY = tag("foundry");
    /** Blocks valid as a foundry tank, required for fuel */
    public static final TagKey<Block> FOUNDRY_TANKS = tag("foundry/tanks");
    /** Blocks valid as a foundry floor */
    public static final TagKey<Block> FOUNDRY_FLOOR = tag("foundry/floor");
    /** Blocks valid in the foundry wall */
    public static final TagKey<Block> FOUNDRY_WALL = tag("foundry/wall");

    /** Blocks that the mattock is effective on */
    public static final TagKey<Block> MINABLE_WITH_MATTOCK = tag("mineable/mattock");
    /** Blocks that the mattock is effective on */
    public static final TagKey<Block> MINABLE_WITH_PICKADZE = tag("mineable/pickadze");
    /** Blocks that the hand axe is effective on */
    public static final TagKey<Block> MINABLE_WITH_HAND_AXE = tag("mineable/hand_axe");
    /** Blocks that the scythe or kama are effective on */
    public static final TagKey<Block> MINABLE_WITH_SCYTHE = tag("mineable/scythe");
    /** Blocks that the vanilla sword is effective on */
    public static final TagKey<Block> MINABLE_WITH_SWORD = forgeTag("mineable/sword");
    /** Blocks that the vanilla shears are effective on */
    public static final TagKey<Block> MINABLE_WITH_SHEARS = forgeTag("mineable/shears");
    /** Blocks that the dagger is effective on */
    public static final TagKey<Block> MINABLE_WITH_DAGGER = tag("mineable/dagger");
    /** Blocks that the melting pan cannot mine */
    public static final TagKey<Block> MINEABLE_MELTING_BLACKLIST = tag("mineable/melting_blacklist");

    /** Any block that can be harvested using a kama or scythe */
    public static final TagKey<Block> HARVESTABLE = tag("harvestable");
    /** Plants that are broken to drop produce and seeds */
    public static final TagKey<Block> HARVESTABLE_CROPS = tag("harvestable/crops");
    /** Plants that drop fruit on interaction */
    public static final TagKey<Block> HARVESTABLE_INTERACT = tag("harvestable/interact");
    /** Plants that grow by placing a copy on top */
    public static final TagKey<Block> HARVESTABLE_STACKABLE = tag("harvestable/stackable");
    /** Any block that counts as a tree trunk for the lumber axe. Note it must also be harvestable by axes to be effective */
    public static final TagKey<Block> TREE_LOGS = tag("tree_log");
    /** List of blocks that should produce bonus gold nugget drops from the chrysophilite modifier. Will only drop bonus if the block does not drop itself */
    public static final TagKey<Block> CHRYSOPHILITE_ORES = tag("chrysophilite_ores");

    // ceramics compat
    public static final TagKey<Block> CISTERN_CONNECTIONS = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("ceramics", "cistern_connections"));

    /** Makes a tag in the tinkers domain */
    public static TagKey<Block> tag(String name) {
      return TagKey.create(Registry.BLOCK_REGISTRY, TConstruct.getResource(name));
    }

    private static TagKey<Block> forgeTag(String name) {
      return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", name));
    }
  }

  public static class Items {
    private static void init() {}
    public static final TagKey<Item> WORKBENCHES = forgeTag("workbenches");
    public static final TagKey<Item> TABLES = tag("tables");
    public static final TagKey<Item> GLASS_PANES_SILICA = forgeTag("glass_panes/silica");

    /** Compat: allows other mods to add normal stone variants which can be used to create stone tools.
     * Note this tag includes both stone and cobblestone, unlike the default forge/vanilla tags that include one or the other
     * Also does not include other items that can be used as stone in crafting */
    public static final TagKey<Item> STONE = forgeTag("normal_stone");
    /** Compat: allows other mods to add granite variants which can be used to create granite tools */
    public static final TagKey<Item> GRANITE = forgeTag("granite");
    /** Compat: allows other mods to add diorite variants which can be used to create diorite tools */
    public static final TagKey<Item> DIORITE = forgeTag("diorite");
    /** Compat: allows other mods to add andesite variants which can be used to create andesite tools */
    public static final TagKey<Item> ANDESITE = forgeTag("andesite");
    /** Compat: allows other mods to add blackstone variants which can be used to create blackstone tools */
    public static final TagKey<Item> BLACKSTONE = forgeTag("blackstone");
    /** Compat: allows other mods to add deepslate variants which can be used to create deepslate tools */
    public static final TagKey<Item> DEEPSLATE = forgeTag("deepslate");
    /** Compat: allows other mods to add basalt variants which can be used to create basalt tools */
    public static final TagKey<Item> BASALT = forgeTag("basalt");

    /** Materials that can be used to craft wooden tool tables */
    public static final TagKey<Item> PLANKLIKE = tag("planklike");
    /** Metals that can be used to craft the anvil */
    public static final TagKey<Item> WORKSTATION_ROCK = tag("workstation_rock");
    /** Metals that can be used to craft the anvil */
    public static final TagKey<Item> ANVIL_METAL = tag("anvil_metal");
    /** Materials that can be used to craft the smeltery controller */
    public static final TagKey<Item> SMELTERY_BRICKS = tag("smeltery_bricks");
    /** Materials that can be used to craft the foundry controller */
    public static final TagKey<Item> FOUNDRY_BRICKS = tag("foundry_bricks");
    /** Copper platform variants */
    public static final TagKey<Item> COPPER_PLATFORMS = tag("copper_platforms");

    /** Planks in this tag are skipped in the default wood crafting recipe as they have their own variant. Tagging your planks here will allow you to add another wood variant */
    public static final TagKey<Item> VARIANT_PLANKS = tag("wood_variants/planks");
    /** Logs in this tag are skipped in the default wood crafting recipe as they have their own variant. Tagging your planks here will allow you to add another wood variant */
    public static final TagKey<Item> VARIANT_LOGS = tag("wood_variants/logs");

    public static final TagKey<Item> SLIME_BLOCK = tag("slime_block");
    public static final TagKey<Item> CONGEALED_SLIME = tag("congealed_slime");
    public static final TagKey<Item> SLIMY_LOGS = tag("slimy_logs");
    public static final TagKey<Item> SLIMY_PLANKS = tag("slimy_planks");
    public static final TagKey<Item> SLIMY_LEAVES = tag("slimy_leaves");
    public static final TagKey<Item> SLIMY_VINES = tag("slimy_vines");
    public static final TagKey<Item> SLIMY_SAPLINGS = tag("slimy_saplings");
    /** All variants of enderbark roots */
    public static final TagKey<Item> ENDERBARK_ROOTS = tag("enderbark/roots");

    public static final TagKey<Item> SEARED_BLOCKS = tag("seared_blocks");
    public static final TagKey<Item> SEARED_BRICKS = tag("seared_bricks");
    public static final TagKey<Item> SCORCHED_BLOCKS = tag("scorched_blocks");
    public static final TagKey<Item> SMELTERY = tag("smeltery");
    public static final TagKey<Item> FOUNDRY = tag("foundry");

    public static final TagKey<Item> ORES_COBALT = forgeTag("ores/cobalt");
    public static final TagKey<Item> RAW_BLOCK_COBALT = forgeTag("storage_blocks/raw_cobalt");
    public static final TagKey<Item> RAW_COBALT = forgeTag("raw_materials/cobalt");

    public static final TagKey<Item> NUGGETS_NETHERITE = forgeTag("nuggets/netherite");
    public static final TagKey<Item> INGOTS_NETHERITE_SCRAP = forgeTag("ingots/netherite_scrap");
    public static final TagKey<Item> NUGGETS_NETHERITE_SCRAP = forgeTag("nuggets/netherite_scrap");
    public static final TagKey<Item> NUGGETS_COPPER = forgeTag("nuggets/copper");

    public static final TagKey<Item> CASTS = tag("casts");
    public static final TagKey<Item> GOLD_CASTS = tag("casts/gold");
    public static final TagKey<Item> SAND_CASTS = tag("casts/sand");
    public static final TagKey<Item> RED_SAND_CASTS = tag("casts/red_sand");
    public static final TagKey<Item> SINGLE_USE_CASTS = tag("casts/single_use");
    public static final TagKey<Item> MULTI_USE_CASTS = tag("casts/multi_use");
    public static final TagKey<Item> BLANK_SINGLE_USE_CASTS = tag("casts/single_use/blank");
    /** Items that count as empty in a casting table */
    public static final TagKey<Item> TABLE_EMPTY_CASTS = tag("casts/empty/table");
    /** Items that count as empty in a casting basin */
    public static final TagKey<Item> BASIN_EMPTY_CASTS = tag("casts/empty/basin");

    /** Items that can be placed in the pattern slot in the part builder */
    public static final TagKey<Item> PATTERNS = tag("patterns");
    /** Items that work in all basic part builder recipes, when unspecified this tag is used for the input */
    public static final TagKey<Item> DEFAULT_PATTERNS = tag("patterns/default");
    /** Pattern items that are not consumed in the part builder */
    public static final TagKey<Item> REUSABLE_PATTERNS = tag("patterns/reusable");

    /** All basic tinkers tanks */
    public static final TagKey<Item> SEARED_TANKS = tag("seared_tanks");
    public static final TagKey<Item> SCORCHED_TANKS = tag("scorched_tanks");
    public static final TagKey<Item> TANKS = tag("tanks");

    public static final TagKey<Item> WITHER_BONES = forgeTag("wither_bones");
    public static final TagKey<Item> BOOKS = forgeTag("books");
    public static final TagKey<Item> GUIDEBOOKS = forgeTag("books/guide");
    public static final TagKey<Item> TINKERS_GUIDES = tag("guides");

    /** Any items in this tag will show the erroring block in smelteries and foundries when held or worn */
    public static final TagKey<Item> GENERAL_STRUCTURE_DEBUG = tag("structure_debug/general");
    /** Any items in this tag will show the erroring block in smelteries when held or worn */
    public static final TagKey<Item> SMELTERY_DEBUG = tag("structure_debug/smeltery");
    /** Any items in this tag will show the erroring block in foundries when held or worn */
    public static final TagKey<Item> FOUNDRY_DEBUG = tag("structure_debug/foundry");

    /** Containers that can be used in the duct */
    public static final TagKey<Item> DUCT_CONTAINERS = tag("duct_containers");

    /** Items that cannot be autosmelted */
    public static final TagKey<Item> AUTOSMELT_BLACKLIST = tag("autosmelt_blacklist");

    /** Items that are seeds for kama harvest */
    public static final TagKey<Item> SEEDS = tag("seeds");

    /** Seeds that produce slimy grass */
    public static final TagKey<Item> SLIMY_SEEDS = tag("slimy_grass_seeds");

    /** Stones that can be used for stoneshield */
    public static final TagKey<Item> STONESHIELDS = tag("stoneshields");
    /** Items that can be consumed for a blaze slimeskull to shoot a fireball */
    public static final TagKey<Item> FIREBALLS = tag("fireballs");
    /** Items in this tag cannot be placed inside tool inventories */
    public static final TagKey<Item> TOOL_INVENTORY_BLACKLIST = tag("inventory_blacklist");
    /** List of blocks that should produce bonus gold nugget drops from the chrysophilite modifier. Will only drop bonus if the block does not drop itself */
    public static final TagKey<Item> CHRYSOPHILITE_ORES = tag("chrysophilite_ores");

    /*
     * Tool tags
     */
    /** Anything that can be stored in the part chest */
    public static final TagKey<Item> CHEST_PARTS = tag("chest_parts");
    /** All material items, used to populate several lists such as books or subtypes in JEI */
    public static final TagKey<Item> TOOL_PARTS = tag("parts");

    /** Anything that can be modified in the tool station */
    public static final TagKey<Item> MODIFIABLE = tag("modifiable");
    /** Anything that supports modifiers that grant extra modifiers */
    public static final TagKey<Item> BONUS_SLOTS = tag("modifiable/bonus_slots");

    /** Modifiable items that contain multiple parts */
    public static final TagKey<Item> MULTIPART_TOOL = tag("modifiable/multipart");
    /** Modifiable items that can have range increased */
    public static final TagKey<Item> AOE = tag("modifiable/aoe");
    /** Tools that use durability and can be repaired. Items in this tag support the {@link ToolStats#DURABILITY} stat. */
    public static final TagKey<Item> DURABILITY = tag("modifiable/durability");

    /** Generally faster tools with lower damage, used for book display */
    public static final TagKey<Item> SMALL_TOOLS = tag("modifiable/small");
    /** Generally slower tools with higher damage, used for book display and to customize stat debuff on offhand attack */
    public static final TagKey<Item> BROAD_TOOLS = tag("modifiable/broad");
    /** Speciality tools that don't fit into either broad or small, notably includes staffs. Used in the books */
    public static final TagKey<Item> SPECIAL_TOOLS = tag("modifiable/special");
    /** Tools found through loot. Used in books and some modifiers */
    public static final TagKey<Item> ANCIENT_TOOLS = tag("modifiable/ancient");
    /** Tools traded by the wandering trader. By default, just ancient, but you can add other stuff. Just remember recycling exists. */
    public static final TagKey<Item> TRADER_TOOLS = tag("modifiable/wandering_trader");

    /** Tools that can adjust the loot context for {@link slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook} */
    public static final TagKey<Item> LOOT_CAPABLE_TOOL = tag("modifiable/loot_capable_tool");
    /** Anything that is used in the player's hand, mostly tools that support interaction, but other tools can be added directly */
    public static final TagKey<Item> HELD = tag("modifiable/held");
    /** Anything that can use interaction modifiers */
    public static final TagKey<Item> INTERACTABLE = tag("modifiable/interactable");
    /** Tools that can interact on right click */
    public static final TagKey<Item> INTERACTABLE_RIGHT = tag("modifiable/interactable/right");
    /** Tools that can interact on left click */
    public static final TagKey<Item> INTERACTABLE_LEFT = tag("modifiable/interactable/left");
    /** Tools that can interact when worn as armor */
    public static final TagKey<Item> INTERACTABLE_ARMOR = tag("modifiable/interactable/armor");
    /** Tools that can interact on left click or right click */
    public static final TagKey<Item> INTERACTABLE_DUAL = tag("modifiable/interactable/dual");

    /** Items in this tag support the @link ToolStats#ATTACK_DAMAGE} stat. Should not be added to directly typically, use one of the below tags instead. */
    public static final TagKey<Item> MELEE = tag("modifiable/melee");
    /** Modifiable items that support melee attacks. Items in this tag support the {@link ToolStats#ATTACK_SPEED} stat (plus those from {@link #MELEE}). */
    public static final TagKey<Item> MELEE_WEAPON = tag("modifiable/melee/weapon");
    /** Modifiable items that specifically are designed for melee, removes melee penalties. Primary melee items are assumed held. */
    public static final TagKey<Item> MELEE_PRIMARY = tag("modifiable/melee/primary");
    /** Modifiable items that are also swords, typically no use outside of combat. Swords are assumed held.  */
    public static final TagKey<Item> SWORD = tag("modifiable/melee/sword");
    /** Modifiable items that boost unarmed attack damage. By default this is just chestplates, but added as a tag to make it easier for adds to change. Unarmed tools cannot modify attack speed and typically don't take damage from melee. */
    public static final TagKey<Item> UNARMED = tag("modifiable/melee/unarmed");
    /** Modifiable items that can parry, cannot receive blocking */
    public static final TagKey<Item> PARRY = tag("modifiable/melee/parry");

    /** Modifiable items that can break blocks. Items in this tag support the {@link ToolStats#MINING_SPEED} and {@link ToolStats#HARVEST_TIER} stats. */
    public static final TagKey<Item> HARVEST = tag("modifiable/harvest");
    /** Modifiable items that are specifically designed for harvest, removes harvest penalties */
    public static final TagKey<Item> HARVEST_PRIMARY = tag("modifiable/harvest/primary");
    /** Modifiable items that can break stone blocks */
    public static final TagKey<Item> STONE_HARVEST = tag("modifiable/harvest/stone");

    /** Modifiable items that are worn as armor. Items in this tag support the {@link ToolStats#ARMOR}, {@link ToolStats#ARMOR_TOUGHNESS} and {@link ToolStats#KNOCKBACK_RESISTANCE} stats. */
    public static final TagKey<Item> ARMOR = tag("modifiable/armor");
    /** Modifiable items that are worn as boots */
    public static final TagKey<Item> BOOTS = tag("modifiable/armor/boots");
    /** Modifiable items that are worn as leggings */
    public static final TagKey<Item> LEGGINGS = tag("modifiable/armor/leggings");
    /** Modifiable items that are worn as chestplates */
    public static final TagKey<Item> CHESTPLATES = tag("modifiable/armor/chestplate");
    /** Modifiable items that are worn as helmets */
    public static final TagKey<Item> HELMETS = tag("modifiable/armor/helmets");
    /** Modifiable items that are worn on any of the main armor slots, likely is applicable for curio slots too */
    public static final TagKey<Item> WORN_ARMOR = tag("modifiable/armor/worn");
    /** Modifiable items that are held in either hand */
    public static final TagKey<Item> HELD_ARMOR = tag("modifiable/armor/held");
    /** Modifiable items that have innate shielding behavior */
    public static final TagKey<Item> SHIELDS = tag("modifiable/shields");
    /** Armor that supports being made golden */
    public static final TagKey<Item> GOLDEN_ARMOR = tag("modifiable/armor/golden");

    // armor book tags
    /** Full list of armor shown in the encyclopedia, can add to directly to show only in the encyclopedia */
    public static final TagKey<Item> BOOK_ARMOR = tag("modifiable/book_armor");
    /** Full list of armor shown in materials and you, automatically added first to encyclopedia */
    public static final TagKey<Item> BASIC_ARMOR = tag("modifiable/book_armor/materials_and_you");
    /** Full list of armor shown in puny smelting, automatically added second to encyclopedia */
    public static final TagKey<Item> PUNY_ARMOR = tag("modifiable/book_armor/puny_smelting");
    /** Full list of armor shown in mighty smelting, automatically added third to encyclopedia */
    public static final TagKey<Item> MIGHTY_ARMOR = tag("modifiable/book_armor/mighty_smelting");
    /** Full list of armor shown in fantastic foundrry, automatically added fourth to encyclopedia */
    public static final TagKey<Item> FANTASTIC_ARMOR = tag("modifiable/book_armor/fantastic_foundry");
    /** Full list of armor shown in tinkers gadgetry, automatically added last to encyclopedia */
    public static final TagKey<Item> GADGETRY_ARMOR = tag("modifiable/book_armor/tinkers_gadgetry");

    /** Modifiable items that support ranged attacks. Items in this tag support {@link ToolStats#DRAW_SPEED}, {@link ToolStats#VELOCITY}, {@link ToolStats#PROJECTILE_DAMAGE} and {@link ToolStats#ACCURACY} */
    public static final TagKey<Item> RANGED = tag("modifiable/ranged");
    /** Any modifiable ranged items that are a bow, includes crosbows and longbows */
    public static final TagKey<Item> BOWS = tag("modifiable/ranged/bows");
    /** Any modifiable bows that fire arrows on release */
    public static final TagKey<Item> LONGBOWS = tag("modifiable/ranged/longbows");
    /** Any modifiable bows that store an arrow then fire on next use */
    public static final TagKey<Item> CROSSBOWS = tag("modifiable/ranged/crossbows");
    /** Modifiable items support special staff modifiers, is a subtag of ranged. */
    public static final TagKey<Item> STAFFS = tag("modifiable/staffs");

    /** Tools that can receive wood based embellishments */
    public static final TagKey<Item> EMBELLISHMENT_WOOD = tag("modifiable/embellishment/wood");
    /** Tools that can receive slime based embellishments */
    public static final TagKey<Item> EMBELLISHMENT_SLIME = tag("modifiable/embellishment/slime");
    /** Tools that can be dyed */
    public static final TagKey<Item> DYEABLE = tag("modifiable/dyeable");
    /** Tools to blacklist from default salvage recipes. May still be salvagable in other recipes */
    public static final TagKey<Item> UNSALVAGABLE = tag("modifiable/unsalvageable");

    /** Tag so mods like thermal know our scyhtes can harvest */
    public static final TagKey<Item> SCYTHES = forgeTag("tools/scythe");

    /** Tag for others adding empty potion bottles */
    public static final TagKey<Item> SPLASH_BOTTLE = forgeTag("bottles/splash");
    public static final TagKey<Item> LINGERING_BOTTLE = forgeTag("bottles/lingering");

    /** Makes a tag in the tinkers domain */
    private static TagKey<Item> tag(String name) {
      return TagKey.create(Registry.ITEM_REGISTRY, TConstruct.getResource(name));
    }

    /** Makes a tag in the forge domain */
    public static TagKey<Item> forgeTag(String name) {
      return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", name));
    }
  }

  public static class Fluids {
    private static void init() {}
    public static final TagKey<Fluid> SLIME = tag("slime");
    /** Causes the fluid to be formatted like a metal in tooltips */
    public static final TagKey<Fluid> SLIME_TOOLTIPS = tag("tooltips/slime");
    /** Causes the fluid to be formatted like a clay in tooltips */
    public static final TagKey<Fluid> CLAY_TOOLTIPS = tag("tooltips/clay");
    /** Causes the fluid to be formatted like a metal in tooltips */
    public static final TagKey<Fluid> METAL_TOOLTIPS = tag("tooltips/metal");
    /** Causes the fluid to be formatted like gems, with 3x3 blocks */
    public static final TagKey<Fluid> LARGE_GEM_TOOLTIPS = tag("tooltips/gem_large");
    /** Causes the fluid to be formatted like gems, with 2x2 blocks */
    public static final TagKey<Fluid> SMALL_GEM_TOOLTIPS = tag("tooltips/gem_small");
    /** Causes the fluid to be formatted like glass in tooltips */
    public static final TagKey<Fluid> GLASS_TOOLTIPS = tag("tooltips/glass");
    /** Causes the fluid to be formatted like soup in tooltips, with bowls. Similar to slime, but no blocks */
    public static final TagKey<Fluid> SOUP_TOOLTIPS = tag("tooltips/soup");

    // spilling tags - used to reduce the number of spilling recipes
    public static final TagKey<Fluid> CLAY_SPILLING = tag("spilling/clay");
    public static final TagKey<Fluid> GLASS_SPILLING = tag("spilling/glass");
    public static final TagKey<Fluid> CHEAP_METAL_SPILLING = tag("spilling/metal/cheap");
    public static final TagKey<Fluid> AVERAGE_METAL_SPILLING = tag("spilling/metal/average");
    public static final TagKey<Fluid> EXPENSIVE_METAL_SPILLING = tag("spilling/metal/expensive");

    private static TagKey<Fluid> tag(String name) {
      return TagKey.create(Registry.FLUID_REGISTRY, TConstruct.getResource(name));
    }
  }

  public static class EntityTypes {
    private static void init() {}
    public static final TagKey<EntityType<?>> SLIMES = forgeTag("slimes");
    public static final TagKey<EntityType<?>> BACON_PRODUCER = tag("bacon_producer");

    public static final TagKey<EntityType<?>> MELTING_SHOW = tag("melting/show_in_default");
    public static final TagKey<EntityType<?>> MELTING_HIDE = tag("melting/hide_in_default");
    public static final TagKey<EntityType<?>> PIGGYBACKPACK_BLACKLIST = tag("piggybackpack_blacklist");

    /** Entities in this tag take more damage from bane of sssss */
    public static final TagKey<EntityType<?>> CREEPERS = forgeTag("creepers");
    public static final TagKey<EntityType<?>> VILLAGERS = forgeTag("villagers");
    public static final TagKey<EntityType<?>> ILLAGERS = forgeTag("illagers");
    /** Entities in this tag take more damage from killager */
    public static final TagKey<EntityType<?>> KILLAGERS = tag("killagers");
    /** Mobs that rarely spawn, boosts drop rate of severing */
    public static final TagKey<EntityType<?>> RARE_MOBS = tag("rare_mobs");
    /** Mobs that get the 4x protection boost due to only 1 armor piece */
    public static final TagKey<EntityType<?>> SMALL_ARMOR = forgeTag("small_armor");

    /** Projectiles with this tag cannot be reflected */
    public static final TagKey<EntityType<?>> REFLECTING_BLACKLIST = forgeTag("reflecting/blacklist");
    /** Projectiles with this tag cannot be reflected */
    public static final TagKey<EntityType<?>> REFLECTING_PRESERVE_OWNER = forgeTag("reflecting/preserve_owner");

    private static TagKey<EntityType<?>> tag(String name) {
      return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, TConstruct.getResource(name));
    }

    private static TagKey<EntityType<?>> forgeTag(String name) {
      return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("forge", name));
    }
  }

  public static class TileEntityTypes {
    private static void init() {}
    public static final TagKey<BlockEntityType<?>> CRAFTING_STATION_BLACKLIST = tag("crafting_station_blacklist");

    private static TagKey<BlockEntityType<?>> tag(String name) {
      return TagKey.create(Registry.BLOCK_ENTITY_TYPE_REGISTRY, TConstruct.getResource(name));
    }
  }

  public static class Biomes {
    private static void init() {}

    /** Biomes the earthslime islands can spawn in, typically overworld ocean */
    public static final TagKey<Biome> EARTHSLIME_ISLANDS = tag("islands/earthslime");
    /** Biomes the skyslime islands can spawn in, generally in most overworld locations */
    public static final TagKey<Biome> SKYSLIME_ISLANDS = tag("islands/skyslime");
    /** Biomes the clay islands can spawn in, generally non-forested overworld */
    public static final TagKey<Biome> CLAY_ISLANDS = tag("islands/clay");
    /** Biomes the blood islands can spawn in, generally anywhere in the nether */
    public static final TagKey<Biome> BLOOD_ISLANDS = tag("islands/blood");
    /** Biomes the enderslime island can spawn in, generally the outer end islands */
    public static final TagKey<Biome> ENDERSLIME_ISLANDS = tag("islands/enderslime");

    private static TagKey<Biome> tag(String name) {
      return TagKey.create(Registry.BIOME_REGISTRY, TConstruct.getResource(name));
    }
  }

  public static class Modifiers {
    private static void init() {}
    /** Gem modifiers, one of which is needed for netherite */
    public static final TagKey<Modifier> GEMS = tag("gems");
    /** Blacklist for modifiers that cannot be hidden with invisible ink */
    public static final TagKey<Modifier> INVISIBLE_INK_BLACKLIST = tag("invisible_ink_blacklist");
    /** Blacklist for modifiers that cannot be extracted via the general recipe */
    public static final TagKey<Modifier> EXTRACT_MODIFIER_BLACKLIST = tag("extract_blacklist/tools");
    /** Blacklist for modifiers that cannot be extracted via the slotless recipe */
    public static final TagKey<Modifier> EXTRACT_SLOTLESS_BLACKLIST = tag("extract_blacklist/slotless");
    /** Modifiers that support blocking while charging, for the sake of shields */
    public static final TagKey<Modifier> BLOCK_WHILE_CHARGING = tag("block_while_charging");
    /** Modifiers that can be used on both left and right click. Does not care about armor modifiers */
    public static final TagKey<Modifier> DUAL_INTERACTION = tag("dual_interaction");
    /** Common defense modifier types, used for skyslime armor */
    public static final TagKey<Modifier> SLIME_DEFENSE = tag("slime_defense");

    /** Modifiers in this tag prevent the overslime debuff */
    public static final TagKey<Modifier> OVERSLIME_FRIEND = tag("overslime_friend");

    // book tags - these are used to determine pages to load in resource packs
    // upgrades
    public static final TagKey<Modifier> UPGRADES = tag("upgrades");
    public static final TagKey<Modifier> GENERAL_UPGRADES = tag("upgrades/general");
    public static final TagKey<Modifier> MELEE_UPGRADES = tag("upgrades/melee");
    public static final TagKey<Modifier> DAMAGE_UPGRADES = tag("upgrades/damage");
    public static final TagKey<Modifier> HARVEST_UPGRADES = tag("upgrades/harvest");
    public static final TagKey<Modifier> RANGED_UPGRADES = tag("upgrades/ranged");
    // armor upgrades
    public static final TagKey<Modifier> ARMOR_UPGRADES = tag("upgrades/armor");
    public static final TagKey<Modifier> GENERAL_ARMOR_UPGRADES = tag("upgrades/armor/general");
    public static final TagKey<Modifier> HELMET_UPGRADES = tag("upgrades/armor/helmet");
    public static final TagKey<Modifier> CHESTPLATE_UPGRADES = tag("upgrades/armor/chestplate");
    public static final TagKey<Modifier> LEGGING_UPGRADES = tag("upgrades/armor/leggings");
    public static final TagKey<Modifier> BOOT_UPGRADES = tag("upgrades/armor/boots");
    // abilities
    public static final TagKey<Modifier> ABILITIES = tag("abilities");
    public static final TagKey<Modifier> GENERAL_ABILITIES = tag("abilities/general");
    public static final TagKey<Modifier> INTERACTION_ABILITIES = tag("abilities/interaction");
    public static final TagKey<Modifier> MELEE_ABILITIES = tag("abilities/melee");
    public static final TagKey<Modifier> HARVEST_ABILITIES = tag("abilities/harvest");
    public static final TagKey<Modifier> RANGED_ABILITIES = tag("abilities/ranged");
    // armor abilities
    public static final TagKey<Modifier> ARMOR_ABILITIES = tag("abilities/armor");
    public static final TagKey<Modifier> GENERAL_ARMOR_ABILITIES = tag("abilities/armor/general");
    public static final TagKey<Modifier> HELMET_ABILITIES = tag("abilities/armor/helmet");
    public static final TagKey<Modifier> CHESTPLATE_ABILITIES = tag("abilities/armor/chestplate");
    public static final TagKey<Modifier> LEGGING_ABILITIES = tag("abilities/armor/leggings");
    public static final TagKey<Modifier> BOOT_ABILITIES = tag("abilities/armor/boots");
    public static final TagKey<Modifier> SHIELD_ABILITIES = tag("abilities/armor/shield");
    // defense
    public static final TagKey<Modifier> DEFENSE = tag("defense");
    public static final TagKey<Modifier> PROTECTION_DEFENSE = tag("defense/protection");
    public static final TagKey<Modifier> SPECIAL_DEFENSE = tag("defense/special");
    // slotless
    public static final TagKey<Modifier> SLOTLESS = tag("slotless");
    public static final TagKey<Modifier> GENERAL_SLOTLESS = tag("slotless/general");
    public static final TagKey<Modifier> BONUS_SLOTLESS = tag("slotless/bonus");


    private static TagKey<Modifier> tag(String name) {
      return ModifierManager.getTag(TConstruct.getResource(name));
    }
  }

  public static class Materials {
    private static void init() {}
    /** Materials available in nether */
    public static final TagKey<IMaterial> NETHER = tag("nether");

    private static TagKey<IMaterial> tag(String name) {
      return MaterialManager.getTag(TConstruct.getResource(name));
    }
  }
}
