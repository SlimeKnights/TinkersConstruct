package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.data.recipe.CostTagAppender;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.minecraft.tags.ItemTags.CLUSTER_MAX_HARVESTABLES;
import static slimeknights.mantle.Mantle.commonResource;
import static slimeknights.tconstruct.common.TinkerTags.Items.AMMO;
import static slimeknights.tconstruct.common.TinkerTags.Items.ANCIENT_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.AOE;
import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.BALLISTAS;
import static slimeknights.tconstruct.common.TinkerTags.Items.BALLISTA_AMMO;
import static slimeknights.tconstruct.common.TinkerTags.Items.BASIC_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.BONUS_SLOTS;
import static slimeknights.tconstruct.common.TinkerTags.Items.BOOK_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.BOOTS;
import static slimeknights.tconstruct.common.TinkerTags.Items.BOWS;
import static slimeknights.tconstruct.common.TinkerTags.Items.BROAD_RANGED;
import static slimeknights.tconstruct.common.TinkerTags.Items.BROAD_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.CHESTPLATES;
import static slimeknights.tconstruct.common.TinkerTags.Items.CROSSBOWS;
import static slimeknights.tconstruct.common.TinkerTags.Items.DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Items.DYEABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.EMBELLISHMENT_SLIME;
import static slimeknights.tconstruct.common.TinkerTags.Items.EMBELLISHMENT_WOOD;
import static slimeknights.tconstruct.common.TinkerTags.Items.FANTASTIC_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.FISHING_RODS;
import static slimeknights.tconstruct.common.TinkerTags.Items.GADGETRY_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST_PRIMARY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HELD;
import static slimeknights.tconstruct.common.TinkerTags.Items.HELD_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.HELMETS;
import static slimeknights.tconstruct.common.TinkerTags.Items.HIDDEN_IN_RECIPE_VIEWERS;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_CHARGE;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_CHARGE_MODIFIER;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_DUAL;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_LEFT;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE_RIGHT;
import static slimeknights.tconstruct.common.TinkerTags.Items.LAUNCHERS;
import static slimeknights.tconstruct.common.TinkerTags.Items.LEGGINGS;
import static slimeknights.tconstruct.common.TinkerTags.Items.LONGBOWS;
import static slimeknights.tconstruct.common.TinkerTags.Items.LOOT_CAPABLE_TOOL;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_PRIMARY;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_WEAPON;
import static slimeknights.tconstruct.common.TinkerTags.Items.MIGHTY_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.MODIFIABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.MULTIPART_TOOL;
import static slimeknights.tconstruct.common.TinkerTags.Items.PARRY;
import static slimeknights.tconstruct.common.TinkerTags.Items.PUNY_ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.RANGED;
import static slimeknights.tconstruct.common.TinkerTags.Items.SHIELDS;
import static slimeknights.tconstruct.common.TinkerTags.Items.SINGLEPART_TOOL;
import static slimeknights.tconstruct.common.TinkerTags.Items.SINGLE_USE;
import static slimeknights.tconstruct.common.TinkerTags.Items.SMALL_RANGED;
import static slimeknights.tconstruct.common.TinkerTags.Items.SMALL_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.SPECIAL_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.STAFFS;
import static slimeknights.tconstruct.common.TinkerTags.Items.STONE_HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.SWORD;
import static slimeknights.tconstruct.common.TinkerTags.Items.THROWN_AMMO;
import static slimeknights.tconstruct.common.TinkerTags.Items.TOOL_PARTS;
import static slimeknights.tconstruct.common.TinkerTags.Items.TRADER_TOOLS;
import static slimeknights.tconstruct.common.TinkerTags.Items.TRIM;
import static slimeknights.tconstruct.common.TinkerTags.Items.UNARMED;
import static slimeknights.tconstruct.common.TinkerTags.Items.UNRECYCLABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.UNSALVAGABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.UNSWAPPABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.WORN_ARMOR;

@SuppressWarnings({"unchecked", "removal"})
public class ItemTagProvider extends ItemTagsProvider {
  /** Twlight forest uncrafting table blacklist */
  private static final TagKey<Item> BANNED_UNCRAFTABLE = ItemTags.create(new ResourceLocation("twilightforest", "banned_uncraftables"));
  private final Function<ResourceLocation,IntrinsicTagAppender<Item>> MAKE_TAG = tag -> tag(ItemTags.create(tag));

  public ItemTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
    super(output, lookupProvider, blockTagProvider, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(HolderLookup.Provider lookupProvider) {
    this.addCommon();
    this.addWorld();
    this.addSmeltery();
    this.addTools();
  }

  @SuppressWarnings("unchecked")
  private void addCommon() {
    this.tag(TinkerTags.Items.TINKERS_GUIDES)
        .add(TinkerCommons.materialsAndYou.get(), TinkerCommons.tinkersGadgetry.get(),
             TinkerCommons.punySmelting.get(), TinkerCommons.mightySmelting.get(),
             TinkerCommons.fantasticFoundry.get(), TinkerCommons.encyclopedia.get());
    this.tag(ItemTags.LECTERN_BOOKS).addTag(TinkerTags.Items.TINKERS_GUIDES);
    this.tag(ItemTags.BOOKSHELF_BOOKS).addTag(TinkerTags.Items.TINKERS_GUIDES);
    this.tag(TinkerTags.Items.GUIDEBOOKS).addTag(TinkerTags.Items.TINKERS_GUIDES);
    this.tag(TinkerTags.Items.BOOKS).addTag(TinkerTags.Items.GUIDEBOOKS);

    TagAppender<Item> slimeballs = this.tag(Tags.Items.SLIMEBALLS);
    for (SlimeType type : SlimeType.values()) {
      slimeballs.addTag(type.getSlimeballTag());
    }
    TinkerCommons.slimeball.forEach((type, ball) -> this.tag(type.getSlimeballTag()).add(ball));

    this.tag(Tags.Items.INGOTS).add(TinkerSmeltery.searedBrick.get(), TinkerSmeltery.scorchedBrick.get(), TinkerToolParts.fakeIngot.get()).addTag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP);
    this.tag(Tags.Items.NUGGETS).addTags(TinkerTags.Items.NUGGETS_COPPER, TinkerTags.Items.NUGGETS_NETHERITE, TinkerTags.Items.NUGGETS_NETHERITE_SCRAP);
    this.tag(TinkerTags.Items.BONES).add(Items.BONE);
    this.tag(TinkerTags.Items.WITHER_BONES).add(TinkerMaterials.necroticBone.get()).addTag(TinkerTags.Items.WEIRD_WITHER_BONES_TAG);
    this.tag(TinkerTags.Items.WEIRD_WITHER_BONES_TAG).add(TinkerMaterials.necroticBone.get());

    this.tag(TinkerTags.Items.NUGGETS_COPPER).add(TinkerMaterials.copperNugget.get());
    this.tag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP).add(Items.NETHERITE_SCRAP);
    this.tag(TinkerTags.Items.NUGGETS_NETHERITE).add(TinkerMaterials.netheriteNugget.get());
    this.tag(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP).add(TinkerMaterials.debrisNugget.get());

    this.tag(TinkerTags.Items.STEEL_SHARD).add(TinkerWorld.steelShard.get());
    this.tag(TinkerTags.Items.KNIGHTMETAL_SHARD).add(TinkerWorld.knightmetalShard.get());

    // ores
    addMetalTags(TinkerMaterials.steel);
    addMetalTags(TinkerMaterials.cobalt);
    // tier 3
    addMetalTags(TinkerMaterials.slimesteel);
    addMetalTags(TinkerMaterials.amethystBronze);
    addMetalTags(TinkerMaterials.roseGold);
    addMetalTags(TinkerMaterials.pigIron);
    // tier 4
    addMetalTags(TinkerMaterials.cinderslime);
    addMetalTags(TinkerMaterials.queensSlime);
    addMetalTags(TinkerMaterials.manyullyn);
    addMetalTags(TinkerMaterials.hepatizon);
    addMetalTags(TinkerMaterials.soulsteel);
    // tier 5
    addMetalTags(TinkerMaterials.knightmetal);
    addMetalTags(TinkerMaterials.knightslime);
    this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

    // glass
    copy(Tags.Blocks.GLASS_SILICA, Tags.Items.GLASS_SILICA);
    copy(Tags.Blocks.GLASS_TINTED, Tags.Items.GLASS_TINTED);
    copy(TinkerTags.Blocks.GLASS_PANES_SILICA, TinkerTags.Items.GLASS_PANES_SILICA);
    copy(Tags.Blocks.GLASS_COLORLESS, Tags.Items.GLASS_COLORLESS);
    copy(Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Items.GLASS_PANES_COLORLESS);
    copy(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
    copy(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
    for (DyeColor color : DyeColor.values()) {
      ResourceLocation name = commonResource("glass/" + color.getSerializedName());
      copy(TagKey.create(Registries.BLOCK, name), TagKey.create(Registries.ITEM, name));
      name = commonResource("glass_panes/" + color.getSerializedName());
      copy(TagKey.create(Registries.BLOCK, name), TagKey.create(Registries.ITEM, name));
    }

    copy(TinkerTags.Blocks.WORKBENCHES, TinkerTags.Items.WORKBENCHES);
    copy(TinkerTags.Blocks.TABLES, TinkerTags.Items.TABLES);
    copy(TinkerTags.Blocks.WORKSTATION_ROCK, TinkerTags.Items.WORKSTATION_ROCK);
    copy(TinkerTags.Blocks.ANVIL_METAL, TinkerTags.Items.ANVIL_METAL);
    copy(TinkerTags.Blocks.PLANKLIKE, TinkerTags.Items.PLANKLIKE);

    // piglins like gold and dislike zombie piglin heads
    this.tag(ItemTags.PIGLIN_LOVED)
        .add(TinkerModifiers.goldReinforcement.get(), TinkerGadgets.itemFrame.get(FrameType.GOLD), TinkerGadgets.itemFrame.get(FrameType.REVERSED_GOLD), TinkerFluids.moltenGold.asItem(), TinkerCommons.goldBars.asItem(), TinkerCommons.goldPlatform.asItem())
        .addTag(TinkerTags.Items.GOLD_CASTS);
    this.tag(ItemTags.PIGLIN_REPELLENTS).add(TinkerWorld.headItems.get(TinkerHeadType.ZOMBIFIED_PIGLIN));

    // beacons are happy to accept any expensive ingots
    this.tag(ItemTags.BEACON_PAYMENT_ITEMS)
        .addTags(TinkerMaterials.steel.getIngotTag(), TinkerMaterials.cobalt.getIngotTag(), TinkerMaterials.queensSlime.getIngotTag(),
                 TinkerMaterials.manyullyn.getIngotTag(), TinkerMaterials.hepatizon.getIngotTag(), TinkerMaterials.cinderslime.getIngotTag());

    this.copy(TinkerTags.Blocks.COPPER_PLATFORMS, TinkerTags.Items.COPPER_PLATFORMS);

    this.tag(TinkerTags.Items.SPLASH_BOTTLE).add(TinkerFluids.splashBottle.get());
    this.tag(TinkerTags.Items.LINGERING_BOTTLE).add(TinkerFluids.lingeringBottle.get());

    // trim materials
    this.tag(ItemTags.TRIM_MATERIALS).add(
      TinkerMaterials.slimesteel.getIngot(), TinkerMaterials.amethystBronze.getIngot(), TinkerMaterials.pigIron.getIngot(), TinkerMaterials.roseGold.getIngot(),
      TinkerMaterials.steel.getIngot(), TinkerMaterials.cobalt.getIngot(), TinkerMaterials.manyullyn.getIngot(), TinkerMaterials.hepatizon.getIngot(), TinkerMaterials.cinderslime.getIngot(), TinkerMaterials.queensSlime.getIngot(),
      TinkerMaterials.knightmetal.getIngot(),
      TinkerWorld.earthGeode.asItem(), TinkerWorld.skyGeode.asItem(), TinkerWorld.ichorGeode.asItem(), TinkerWorld.enderGeode.asItem()
    );

    // items to fully hide from JEI
    IntrinsicTagAppender<Item> hidden = tag(HIDDEN_IN_RECIPE_VIEWERS);
    hidden.add(
      // internal item for modifiers
      TinkerTools.crystalshotItem.asItem(),
      // unused future fluids
      TinkerFluids.moltenSoulsteel.asItem(), TinkerFluids.moltenKnightslime.asItem()
    );
    // unused future material items
    TinkerMaterials.soulsteel.forEach(item -> hidden.add(item.asItem()));
    TinkerMaterials.knightslime.forEach(item -> hidden.add(item.asItem()));
    // ichor foliage
    hidden.add(
      TinkerWorld.slimeLeaves.get(FoliageType.ICHOR).asItem(),
      TinkerWorld.slimeTallGrass.get(FoliageType.ICHOR).asItem(),
      TinkerWorld.slimeFern.get(FoliageType.ICHOR).asItem(),
      TinkerWorld.slimeSapling.get(FoliageType.ICHOR).asItem(),
      TinkerWorld.slimeGrassSeeds.get(FoliageType.ICHOR).asItem()
    );
    for (DirtType dirtType : DirtType.values()) {
      hidden.add(TinkerWorld.slimeGrass.get(dirtType).get(FoliageType.ICHOR).asItem());
    }
  }

  private void addWorld() {
    IntrinsicTagAppender<Item> heads = this.tag(Tags.Items.HEADS);
    heads.add(Items.PIGLIN_HEAD);
    TinkerWorld.heads.forEach(head -> heads.add(head.asItem()));

    this.copy(TinkerTags.Blocks.SLIME_BLOCK, TinkerTags.Items.SLIME_BLOCK);
    this.copy(TinkerTags.Blocks.CONGEALED_SLIME, TinkerTags.Items.CONGEALED_SLIME);
    this.copy(TinkerTags.Blocks.SLIMY_LOGS, TinkerTags.Items.SLIMY_LOGS);
    this.copy(TinkerTags.Blocks.SLIMY_PLANKS, TinkerTags.Items.SLIMY_PLANKS);
    this.copy(TinkerTags.Blocks.SLIMY_LEAVES, TinkerTags.Items.SLIMY_LEAVES);
    this.copy(TinkerTags.Blocks.SLIMY_VINES, TinkerTags.Items.SLIMY_VINES);
    this.copy(TinkerTags.Blocks.SLIMY_SAPLINGS, TinkerTags.Items.SLIMY_SAPLINGS);
    this.copy(TinkerTags.Blocks.ENDERBARK_ROOTS, TinkerTags.Items.ENDERBARK_ROOTS);
    this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
    this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);

    this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
    this.copy(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Items.ORE_RATES_SINGULAR);
    this.copy(Tags.Blocks.ORES_IN_GROUND_NETHERRACK, Tags.Items.ORES_IN_GROUND_NETHERRACK);
    this.copy(TinkerTags.Blocks.ORES_COBALT, TinkerTags.Items.ORES_COBALT);
    this.copy(TinkerTags.Blocks.RAW_BLOCK_COBALT, TinkerTags.Items.RAW_BLOCK_COBALT);
    this.tag(TinkerTags.Items.RAW_COBALT).add(TinkerWorld.rawCobalt.get());
    this.tag(Tags.Items.RAW_MATERIALS).addTag(TinkerTags.Items.RAW_COBALT);

    // wood
    this.addNonFlammableTag(TinkerWorld.greenheart);
    this.addNonFlammableTag(TinkerWorld.skyroot);
    this.addNonFlammableTag(TinkerWorld.bloodshroom);
    this.addNonFlammableTag(TinkerWorld.enderbark);
    // planks
    this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
    this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
    this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
    // logs
    this.copy(TinkerWorld.greenheart.getLogBlockTag(), TinkerWorld.greenheart.getLogItemTag());
    this.copy(TinkerWorld.skyroot.getLogBlockTag(), TinkerWorld.skyroot.getLogItemTag());
    this.copy(TinkerWorld.bloodshroom.getLogBlockTag(), TinkerWorld.bloodshroom.getLogItemTag());
    this.copy(TinkerWorld.enderbark.getLogBlockTag(), TinkerWorld.enderbark.getLogItemTag());
    this.copy(BlockTags.LOGS, ItemTags.LOGS);
    // no burnable woods presently
    //this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
    // doors
    this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
    this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
    this.copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
    this.copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
    this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
    this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
    // redstone
    this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
    this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
    this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
    this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
  }

  private void addTools() {
    // stone
    addToolTags(TinkerTools.pickaxe,      MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE_WEAPON,  INTERACTABLE_RIGHT, AOE, CLUSTER_MAX_HARVESTABLES, SMALL_TOOLS, BONUS_SLOTS, ItemTags.PICKAXES);
    addToolTags(TinkerTools.sledgeHammer, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, CLUSTER_MAX_HARVESTABLES, BROAD_TOOLS, BONUS_SLOTS, ItemTags.PICKAXES);
    addToolTags(TinkerTools.veinHammer,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE_WEAPON,  INTERACTABLE_RIGHT, AOE, CLUSTER_MAX_HARVESTABLES, BROAD_TOOLS, BONUS_SLOTS, ItemTags.PICKAXES);
    // dirtD
    addToolTags(TinkerTools.mattock,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_WEAPON, INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS, ItemTags.SHOVELS, ItemTags.AXES);
    addToolTags(TinkerTools.pickadze,  MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_WEAPON, INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS, ItemTags.SHOVELS, STONE_HARVEST, ItemTags.PICKAXES);
    addToolTags(TinkerTools.excavator, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_WEAPON, INTERACTABLE_RIGHT, AOE, BROAD_TOOLS, BONUS_SLOTS, ItemTags.SHOVELS);
    // wood
    addToolTags(TinkerTools.handAxe,  MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS, ItemTags.AXES);
    addToolTags(TinkerTools.broadAxe, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, BROAD_TOOLS, BONUS_SLOTS, ItemTags.AXES);
    // plants
    addToolTags(TinkerTools.kama,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_WEAPON,  INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS, ItemTags.HOES);
    addToolTags(TinkerTools.scythe, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, BROAD_TOOLS, BONUS_SLOTS, ItemTags.HOES);
    // sword
    addToolTags(TinkerTools.dagger,  MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, PARRY, SMALL_TOOLS, BONUS_SLOTS, ItemTags.SWORDS, UNSALVAGABLE);
    addToolTags(TinkerTools.sword,   MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, SWORD, SMALL_TOOLS, BONUS_SLOTS, ItemTags.SWORDS, AOE);
    addToolTags(TinkerTools.cleaver, MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, INTERACTABLE_RIGHT, SWORD, BROAD_TOOLS, BONUS_SLOTS, ItemTags.SWORDS, AOE);
    // ranged
    addToolTags(TinkerTools.crossbow,   MULTIPART_TOOL, DURABILITY, MELEE_WEAPON, CROSSBOWS,    INTERACTABLE_LEFT,  SMALL_RANGED, BONUS_SLOTS, Tags.Items.TOOLS_CROSSBOWS);
    addToolTags(TinkerTools.longbow,    MULTIPART_TOOL, DURABILITY, MELEE_WEAPON, LONGBOWS,     INTERACTABLE_LEFT,  BROAD_RANGED, BONUS_SLOTS, Tags.Items.TOOLS_BOWS, BALLISTAS);
    addToolTags(TinkerTools.fishingRod, MULTIPART_TOOL, DURABILITY, MELEE_WEAPON, FISHING_RODS, INTERACTABLE_DUAL,  SMALL_RANGED, BONUS_SLOTS, Tags.Items.TOOLS_FISHING_RODS);
    addToolTags(TinkerTools.javelin,    MULTIPART_TOOL, DURABILITY, MELEE_PRIMARY, RANGED,      INTERACTABLE_RIGHT, BROAD_RANGED, BONUS_SLOTS, Tags.Items.TOOLS_TRIDENTS);
    addToolTags(TinkerTools.arrow,       MULTIPART_TOOL, AMMO,        UNSALVAGABLE, UNSWAPPABLE, SINGLE_USE, DYEABLE, ItemTags.ARROWS);
    addToolTags(TinkerTools.shuriken,    MULTIPART_TOOL, THROWN_AMMO, UNSALVAGABLE, UNSWAPPABLE, SINGLE_USE);
    addToolTags(TinkerTools.throwingAxe, MULTIPART_TOOL, THROWN_AMMO, UNSALVAGABLE, UNSWAPPABLE, SINGLE_USE);
    // specialized
    addToolTags(TinkerTools.flintAndBrick, DURABILITY, MELEE_WEAPON, INTERACTABLE_RIGHT, AOE, SMALL_TOOLS, BONUS_SLOTS);
    addToolTags(TinkerTools.skyStaff,      DURABILITY, STAFFS, SPECIAL_TOOLS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, DYEABLE, EMBELLISHMENT_WOOD, BONUS_SLOTS);
    addToolTags(TinkerTools.earthStaff,    DURABILITY, STAFFS, SPECIAL_TOOLS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, DYEABLE, EMBELLISHMENT_WOOD, BONUS_SLOTS);
    addToolTags(TinkerTools.ichorStaff,    DURABILITY, STAFFS, SPECIAL_TOOLS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, DYEABLE, EMBELLISHMENT_WOOD, BONUS_SLOTS);
    addToolTags(TinkerTools.enderStaff,    DURABILITY, STAFFS, SPECIAL_TOOLS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, DYEABLE, EMBELLISHMENT_WOOD, BONUS_SLOTS);
    // ancient
    addToolTags(TinkerTools.meltingPan, MULTIPART_TOOL, DURABILITY, ANCIENT_TOOLS, TRADER_TOOLS, HARVEST_PRIMARY, STAFFS, HELD_ARMOR, INTERACTABLE_DUAL, AOE, BONUS_SLOTS);
    addToolTags(TinkerTools.warPick,    MULTIPART_TOOL, DURABILITY, ANCIENT_TOOLS, TRADER_TOOLS, HARVEST_PRIMARY, STONE_HARVEST, MELEE_WEAPON, HELD, AOE, CLUSTER_MAX_HARVESTABLES, CROSSBOWS, BONUS_SLOTS, ItemTags.PICKAXES, Tags.Items.TOOLS_CROSSBOWS);
    addToolTags(TinkerTools.battlesign, MULTIPART_TOOL, DURABILITY, ANCIENT_TOOLS, TRADER_TOOLS, MELEE_PRIMARY, SHIELDS, BONUS_SLOTS, Tags.Items.TOOLS_SHIELDS);
    addToolTags(TinkerTools.swasher,    MULTIPART_TOOL, DURABILITY, ANCIENT_TOOLS, TRADER_TOOLS, HARVEST, MELEE_PRIMARY, LAUNCHERS, HELD, BONUS_SLOTS, ItemTags.SWORDS, STAFFS, INTERACTABLE_CHARGE_MODIFIER);
    optionalToolTags(TinkerTools.minotaurAxe, MULTIPART_TOOL, DURABILITY, ANCIENT_TOOLS, HARVEST_PRIMARY, MELEE_PRIMARY, INTERACTABLE_RIGHT, AOE, BONUS_SLOTS, ItemTags.AXES);

    // armor
    addArmorTags(TinkerTools.travelersGear, SINGLEPART_TOOL, DURABILITY, BONUS_SLOTS, DYEABLE, ItemTags.FREEZE_IMMUNE_WEARABLES);
    // no trim for travelers helmet, not enough texture
    tag(TRIM).add(TinkerTools.travelersGear.get(ArmorItem.Type.CHESTPLATE), TinkerTools.travelersGear.get(ArmorItem.Type.LEGGINGS), TinkerTools.travelersGear.get(ArmorItem.Type.BOOTS));
    addArmorTags(TinkerTools.plateArmor,    MULTIPART_TOOL, DURABILITY, BONUS_SLOTS, TRIM);
    addArmorTags(TinkerTools.slimesuit,     DURABILITY, BONUS_SLOTS, TRIM, EMBELLISHMENT_SLIME);
    addToolTags(TinkerTools.slimesuit.get(ArmorItem.Type.HELMET), SINGLEPART_TOOL);

    // shields
    addToolTags(TinkerTools.travelersShield, DURABILITY, BONUS_SLOTS, SHIELDS, INTERACTABLE_LEFT, Tags.Items.TOOLS_SHIELDS, SINGLEPART_TOOL, UNRECYCLABLE, DYEABLE);
    addToolTags(TinkerTools.plateShield,     DURABILITY, BONUS_SLOTS, SHIELDS, INTERACTABLE_LEFT, Tags.Items.TOOLS_SHIELDS, SINGLEPART_TOOL, UNRECYCLABLE);

    // care about order for armor in the book
    tag(BASIC_ARMOR);
    IntrinsicTagAppender<Item> bookArmor = tag(PUNY_ARMOR);
    for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
      bookArmor.add(TinkerTools.travelersGear.get(slotType));
    }
    bookArmor.add(TinkerTools.travelersShield.get());
    for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
      bookArmor.add(TinkerTools.plateArmor.get(slotType));
    }
    bookArmor.add(TinkerTools.plateShield.get());
    tag(MIGHTY_ARMOR);
    tag(FANTASTIC_ARMOR);
    bookArmor = tag(GADGETRY_ARMOR);
    for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
      bookArmor.add(TinkerTools.slimesuit.get(slotType));
    }
    tag(BOOK_ARMOR).addTags(BASIC_ARMOR, PUNY_ARMOR, MIGHTY_ARMOR, FANTASTIC_ARMOR, GADGETRY_ARMOR);


    // add tags to other tags
    // harvest primary and stone harvest are both automatically harvest
    this.tag(TinkerTags.Items.HARVEST).addTags(HARVEST_PRIMARY, STONE_HARVEST);
    // melee nesting - currently most all sub-tags are held exclusive as they revolve around tool damage or having an item in hand
    this.tag(MELEE_WEAPON).addTags(MELEE_PRIMARY, SWORD, PARRY);
    this.tag(AMMO).addTag(THROWN_AMMO);
    // by default, this tag just redirects to melee weapon, but you can reconfigure it to suit your pack
    this.tag(BALLISTA_AMMO).addTags(MELEE_WEAPON, HARVEST);
    this.tag(MELEE).addTags(MELEE_WEAPON, UNARMED);
    // modifier helper tags
    this.tag(LOOT_CAPABLE_TOOL).addTags(MELEE, HARVEST, FISHING_RODS);
    this.tag(UNARMED).addTag(CHESTPLATES);
    this.tag(INTERACTABLE_RIGHT).addTags(INTERACTABLE_DUAL);
    this.tag(INTERACTABLE_LEFT).addTag(INTERACTABLE_DUAL);
    this.tag(INTERACTABLE_CHARGE_MODIFIER).addTags(INTERACTABLE_RIGHT, SHIELDS);
    this.tag(INTERACTABLE_CHARGE).addTags(INTERACTABLE_CHARGE_MODIFIER, BOWS);
    // interactable armor is mostly so some mod could disable all chestplate interactions in one swing
    this.tag(INTERACTABLE_ARMOR).addTag(CHESTPLATES);
    // left and right handed are held, but not armor
    this.tag(HELD).addTags(INTERACTABLE_RIGHT, INTERACTABLE_LEFT, HELD_ARMOR);
    this.tag(INTERACTABLE).addTags(INTERACTABLE_LEFT, INTERACTABLE_RIGHT, INTERACTABLE_ARMOR);
    this.tag(WORN_ARMOR).addTags(BOOTS, LEGGINGS, CHESTPLATES, HELMETS);
    this.tag(HELD_ARMOR).addTag(SHIELDS);
    this.tag(ARMOR).addTags(WORN_ARMOR, HELD_ARMOR);
    this.tag(AOE).addTag(BOOTS); // boot walk modifiers
    this.tag(LAUNCHERS).addTags(BOWS, STAFFS, FISHING_RODS);
    this.tag(RANGED).addTags(LAUNCHERS, SMALL_RANGED, BROAD_RANGED);
    this.tag(BOWS).addTags(LONGBOWS, CROSSBOWS);
    // TODO 1.21: consider dropping unsalvagable from this tag
    this.tag(UNRECYCLABLE).addTags(UNSALVAGABLE, ANCIENT_TOOLS); // ancient tools lack tool parts, but may have special override recipes to salvage
    // headlight support
    this.tag(ItemTags.create(new ResourceLocation("headlight", "headlight_helmets"))).addTag(HELMETS);

    // general
    this.tag(MULTIPART_TOOL).addTag(SINGLEPART_TOOL);
    this.tag(MODIFIABLE).addTags(MULTIPART_TOOL, DURABILITY, MELEE, HARVEST, RANGED, AMMO, AOE, HELD, BONUS_SLOTS);
    // disable parry mod on our items, we have our own modifier for that
    this.tag(TagKey.create(Registries.ITEM, new ResourceLocation("parry", "excluded_shields"))).addTag(HELD);

    // kamas are a shear type, when broken we don't pass it to loot tables
    this.tag(Tags.Items.SHEARS).add(TinkerTools.kama.get());
    // mark kama and scythe for mods like thermal to use
    this.tag(TinkerTags.Items.SCYTHES).add(TinkerTools.kama.get(), TinkerTools.scythe.get());
    // nothing to blacklist, just want the empty tag so it appears in datapacks
    this.tag(TinkerTags.Items.AUTOSMELT_BLACKLIST);
    this.tag(TinkerTags.Items.AUTOSMELT_PLUS_BLACKLIST);

    // carrots and potatoes are not seeds in vanilla, so make a tag with them
    this.tag(TinkerTags.Items.SEEDS)
        .addTag(Tags.Items.SEEDS)
        .add(Items.CARROT, Items.POTATO, Items.NETHER_WART);

    // tags for modifiers
    copy(TinkerTags.Blocks.CHRYSOPHILITE_ORES, TinkerTags.Items.CHRYSOPHILITE_ORES);

    // tag for tool parts, mostly used by JEI right now
    this.tag(TinkerTags.Items.TOOL_PARTS).add(
      // arrow part bartering is weird as they have such low tiers
      TinkerToolParts.arrowHead.get(), TinkerToolParts.arrowShaft.get(), TinkerToolParts.fletching.get(),
      // repair kit is not strictly a tool part, but this list just helps out JEI
      TinkerToolParts.repairKit.get(), TinkerToolParts.fakeIngot.get(), TinkerToolParts.fakeStorageBlock.asItem()
    ).addTag(TinkerTags.Items.BARTERED_PARTS); // all bartered parts must be tool parts
    this.tag(TinkerTags.Items.BARTERED_PARTS)
        .add(
          TinkerToolParts.pickHead.get(), TinkerToolParts.hammerHead.get(),
          TinkerToolParts.smallAxeHead.get(), TinkerToolParts.broadAxeHead.get(),
          TinkerToolParts.smallBlade.get(), TinkerToolParts.broadBlade.get(),
          TinkerToolParts.adzeHead.get(), TinkerToolParts.largePlate.get(),
          TinkerToolParts.toolBinding.get(), TinkerToolParts.toughBinding.get(),
          TinkerToolParts.toolHandle.get(), TinkerToolParts.toughHandle.get(),
          TinkerToolParts.bowLimb.get(), TinkerToolParts.bowGrip.get(), TinkerToolParts.bowstring.get(),
          TinkerToolParts.maille.get(), TinkerToolParts.shieldCore.get())
        .add(TinkerToolParts.plating.values().toArray(new Item[0]));
    // tag for the part chest items
    this.tag(TinkerTags.Items.CHEST_PARTS).addTag(TinkerTags.Items.TOOL_PARTS).add(TinkerSmeltery.dummyPlating.values().toArray(new Item[0]));

    IntrinsicTagAppender<Item> slimySeeds = this.tag(TinkerTags.Items.SLIMY_SEEDS);
    TinkerWorld.slimeGrassSeeds.values().forEach(slimySeeds::add);

    // contains any ground stones
    this.tag(TinkerTags.Items.STONESHIELDS)
        .addTag(Tags.Items.STONE)
        .addTag(Tags.Items.COBBLESTONE)
        .addTag(Tags.Items.SANDSTONE)
        .addTag(Tags.Items.END_STONES)
        .addTag(Tags.Items.GRAVEL) // for shovels and axes to use
        .add(Items.NETHERRACK, Items.BASALT, Items.POLISHED_BASALT, Items.BLACKSTONE, Items.POLISHED_BLACKSTONE);
    this.tag(TinkerTags.Items.FIREBALLS).add(Items.FIRE_CHARGE);
    this.tag(TinkerTags.Items.TOOL_INVENTORY_BLACKLIST)
        .add(Items.BUNDLE, Items.SHULKER_BOX,
             Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX,
             Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
             Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.BLUE_SHULKER_BOX,
             Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX);
    this.tag(TinkerTags.Items.THROWABLE)
      .add(Items.SNOWBALL, Items.EGG, Items.ENDER_PEARL, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.EXPERIENCE_BOTTLE, Items.ENDER_EYE, Items.FIREWORK_ROCKET)
      .add(TinkerGadgets.efln.get(), TinkerGadgets.flintShuriken.get(), TinkerGadgets.quartzShuriken.get(), TinkerGadgets.glowBall.get())
      .addTag(THROWN_AMMO);
    this.tag(TinkerTags.Items.WHITESTONE_INGOTS)
      .addOptionalTag(commonResource("ingots/aluminum"))
      .addOptionalTag(commonResource("ingots/tin"))
      .addOptionalTag(commonResource("ingots/zinc"))
      .addOptionalTag(commonResource("ingots/nickel"))
      .addOptionalTag(commonResource("ingots/chromium"))
      .addOptionalTag(commonResource("ingots/cadmium"));

    this.tag(TinkerTags.Items.VARIANT_PLANKS)
        .add(Items.CRIMSON_PLANKS, Items.WARPED_PLANKS)
        .addTag(TinkerTags.Items.SLIMY_PLANKS);
    // the logs have "variants" as they have their own recipes
    this.tag(TinkerTags.Items.VARIANT_LOGS).addTags(ItemTags.OAK_LOGS, ItemTags.SPRUCE_LOGS, ItemTags.BIRCH_LOGS, ItemTags.JUNGLE_LOGS, ItemTags.DARK_OAK_LOGS, ItemTags.ACACIA_LOGS, ItemTags.MANGROVE_LOGS, ItemTags.CHERRY_LOGS, ItemTags.CRIMSON_STEMS, ItemTags.WARPED_STEMS, TinkerTags.Items.SLIMY_LOGS);

    // part builder
    this.tag(TinkerTags.Items.DEFAULT_PATTERNS).add(TinkerTables.pattern.get());
    this.tag(TinkerTags.Items.REUSABLE_PATTERNS).addTag(TinkerTags.Items.GOLD_CASTS);
    this.tag(TinkerTags.Items.PATTERNS)
        .addTags(TinkerTags.Items.DEFAULT_PATTERNS, TinkerTags.Items.REUSABLE_PATTERNS, TinkerTags.Items.SAND_CASTS, TinkerTags.Items.RED_SAND_CASTS)
        .add(Items.SAND, Items.RED_SAND, TinkerFluids.venomBottle.get());

    // stone
    this.copy(TinkerTags.Blocks.STONE,      TinkerTags.Items.STONE);
    this.copy(TinkerTags.Blocks.GRANITE,    TinkerTags.Items.GRANITE);
    this.copy(TinkerTags.Blocks.DIORITE,    TinkerTags.Items.DIORITE);
    this.copy(TinkerTags.Blocks.ANDESITE,   TinkerTags.Items.ANDESITE);
    this.copy(TinkerTags.Blocks.BLACKSTONE, TinkerTags.Items.BLACKSTONE);
    this.copy(TinkerTags.Blocks.DEEPSLATE,  TinkerTags.Items.DEEPSLATE);
    this.copy(TinkerTags.Blocks.BASALT,     TinkerTags.Items.BASALT);

    // twilight forest
    this.tag(BANNED_UNCRAFTABLE).addTag(MODIFIABLE).addTag(TOOL_PARTS).add(
      TinkerTables.tinkersAnvil.asItem(), TinkerTables.scorchedAnvil.asItem(), TinkerTables.modifierWorktable.asItem()
    );
    String tf = "twilightforest";
    Function<String,ResourceLocation> trophy = name -> new ResourceLocation(tf, name + "_trophy");
    this.tag(TinkerTags.Items.BOSS_TROPHIES)
      .addOptional(trophy.apply("naga"))
      .addOptional(trophy.apply("lich"))
      .addOptional(trophy.apply("minoshroom"))
      .addOptional(trophy.apply("hydra"))
      .addOptional(trophy.apply("knight_phantom"))
      .addOptional(trophy.apply("ur_ghast"))
      .addOptional(trophy.apply("alpha_yeti"))
      .addOptional(trophy.apply("snow_queen"))
      .addOptional(trophy.apply("quest_ram"));
    this.tag(TinkerTags.Items.THROWABLE)
      .addOptional(new ResourceLocation(tf, "ice_bomb"));
    this.tag(TinkerTags.Items.KNIGHTMETAL_SHARD).addOptional(new ResourceLocation(tf, "armor_shard"));
  }

  private void addSmeltery() {
    this.copy(TinkerTags.Blocks.SEARED_BRICKS, TinkerTags.Items.SEARED_BRICKS);
    this.copy(TinkerTags.Blocks.SEARED_BLOCKS, TinkerTags.Items.SEARED_BLOCKS);
    this.copy(TinkerTags.Blocks.SMELTERY_BRICKS, TinkerTags.Items.SMELTERY_BRICKS);
    this.copy(TinkerTags.Blocks.SCORCHED_BLOCKS, TinkerTags.Items.SCORCHED_BLOCKS);
    this.copy(TinkerTags.Blocks.FOUNDRY_BRICKS, TinkerTags.Items.FOUNDRY_BRICKS);
    this.copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);

    this.tag(TinkerTags.Items.NON_SINGULAR_ORE_RATES).addTags(Tags.Items.ORE_RATES_DENSE, Tags.Items.ORE_RATES_SPARSE);

    // smeltery and foundry structure blocks
    this.tag(TinkerTags.Items.SMELTERY)
        .addTag(TinkerTags.Items.SEARED_BLOCKS)
        .addTag(TinkerTags.Items.SEARED_TANKS)
        .add(TinkerSmeltery.smelteryController.asItem(), TinkerSmeltery.searedLadder.asItem(),
             TinkerSmeltery.searedDrain.asItem(), TinkerSmeltery.searedChute.asItem(), TinkerSmeltery.searedDuct.asItem(),
             TinkerSmeltery.searedGlass.asItem(), TinkerSmeltery.searedSoulGlass.asItem(), TinkerSmeltery.searedTintedGlass.asItem());
    this.tag(TinkerTags.Items.FOUNDRY)
        .addTag(TinkerTags.Items.SCORCHED_BLOCKS)
        .addTag(TinkerTags.Items.SCORCHED_TANKS)
        .add(TinkerSmeltery.foundryController.asItem(), TinkerSmeltery.scorchedLadder.asItem(),
             TinkerSmeltery.scorchedDrain.asItem(), TinkerSmeltery.scorchedChute.asItem(), TinkerSmeltery.scorchedDuct.asItem(),
             TinkerSmeltery.scorchedGlass.asItem(), TinkerSmeltery.scorchedSoulGlass.asItem(), TinkerSmeltery.scorchedTintedGlass.asItem());
    // structure debug
    this.tag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG);
    this.tag(TinkerTags.Items.SMELTERY_DEBUG).addTag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG).addTag(TinkerTags.Items.SMELTERY);
    this.tag(TinkerTags.Items.FOUNDRY_DEBUG).addTag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG).addTag(TinkerTags.Items.FOUNDRY);

    // tag each type of cast
    IntrinsicTagAppender<Item> goldCasts = this.tag(TinkerTags.Items.GOLD_CASTS);
    IntrinsicTagAppender<Item> sandCasts = this.tag(TinkerTags.Items.SAND_CASTS);
    IntrinsicTagAppender<Item> redSandCasts = this.tag(TinkerTags.Items.RED_SAND_CASTS);
    IntrinsicTagAppender<Item> singleUseCasts = this.tag(TinkerTags.Items.SINGLE_USE_CASTS);
    IntrinsicTagAppender<Item> multiUseCasts = this.tag(TinkerTags.Items.MULTI_USE_CASTS);
    Consumer<CastItemObject> addCast = cast -> {
      // tag based on material
      goldCasts.add(cast.get());
      sandCasts.add(cast.getSand());
      redSandCasts.add(cast.getRedSand());
      // tag based on usage
      singleUseCasts.addTag(cast.getSingleUseTag());
      this.tag(cast.getSingleUseTag()).add(cast.getSand(), cast.getRedSand());
      multiUseCasts.addTag(cast.getMultiUseTag());
      this.tag(cast.getMultiUseTag()).add(cast.get());
    };
    // blank sand casts, no blank gold or this would use the helper
    sandCasts.add(TinkerSmeltery.blankSandCast.get());
    redSandCasts.add(TinkerSmeltery.blankRedSandCast.get());
    singleUseCasts.addTag(TinkerTags.Items.BLANK_SINGLE_USE_CASTS);
    this.tag(TinkerTags.Items.BLANK_SINGLE_USE_CASTS).add(TinkerSmeltery.blankSandCast.get(), TinkerSmeltery.blankRedSandCast.get());
    // basic
    addCast.accept(TinkerSmeltery.ingotCast);
    addCast.accept(TinkerSmeltery.nuggetCast);
    addCast.accept(TinkerSmeltery.gemCast);
    addCast.accept(TinkerSmeltery.rodCast);
    addCast.accept(TinkerSmeltery.repairKitCast);
    // compatibility
    addCast.accept(TinkerSmeltery.plateCast);
    addCast.accept(TinkerSmeltery.gearCast);
    addCast.accept(TinkerSmeltery.coinCast);
    addCast.accept(TinkerSmeltery.wireCast);
    // small heads
    addCast.accept(TinkerSmeltery.pickHeadCast);
    addCast.accept(TinkerSmeltery.smallAxeHeadCast);
    addCast.accept(TinkerSmeltery.smallBladeCast);
    addCast.accept(TinkerSmeltery.adzeHeadCast);
    // large heads
    addCast.accept(TinkerSmeltery.hammerHeadCast);
    addCast.accept(TinkerSmeltery.broadAxeHeadCast);
    addCast.accept(TinkerSmeltery.broadBladeCast);
    addCast.accept(TinkerSmeltery.largePlateCast);
    // bindings
    addCast.accept(TinkerSmeltery.toolBindingCast);
    addCast.accept(TinkerSmeltery.toughBindingCast);
    // tool rods
    addCast.accept(TinkerSmeltery.toolHandleCast);
    addCast.accept(TinkerSmeltery.toughHandleCast);
    // bow
    addCast.accept(TinkerSmeltery.bowLimbCast);
    addCast.accept(TinkerSmeltery.bowGripCast);
    // armor
    addCast.accept(TinkerSmeltery.helmetPlatingCast);
    addCast.accept(TinkerSmeltery.chestplatePlatingCast);
    addCast.accept(TinkerSmeltery.leggingsPlatingCast);
    addCast.accept(TinkerSmeltery.bootsPlatingCast);
    addCast.accept(TinkerSmeltery.mailleCast);

    // arrow patterns are basically a gold cast
    goldCasts.add(TinkerSmeltery.arrowCast.get());

    // add all casts to a common tag
    this.tag(TinkerTags.Items.CASTS)
        .addTags(TinkerTags.Items.GOLD_CASTS, TinkerTags.Items.SAND_CASTS, TinkerTags.Items.RED_SAND_CASTS, TinkerTags.Items.TABLE_EMPTY_CASTS, TinkerTags.Items.BASIN_EMPTY_CASTS);
    this.tag(TinkerTags.Items.TABLE_EMPTY_CASTS).add(TinkerCommons.goldBars.asItem());
    this.tag(TinkerTags.Items.BASIN_EMPTY_CASTS).add(TinkerCommons.goldPlatform.asItem());

    this.tag(TinkerTags.Items.DUCT_CONTAINERS).add(Items.BUCKET, TinkerSmeltery.copperCan.get(), TinkerSmeltery.searedLantern.asItem(), TinkerSmeltery.scorchedLantern.asItem());

    // tank tag
    this.copy(TinkerTags.Blocks.SEARED_TANKS, TinkerTags.Items.SEARED_TANKS);
    this.copy(TinkerTags.Blocks.SCORCHED_TANKS, TinkerTags.Items.SCORCHED_TANKS);
    this.tag(TinkerTags.Items.TANKS)
        .addTag(TinkerTags.Items.SEARED_TANKS)
        .addTag(TinkerTags.Items.SCORCHED_TANKS);

    // blacklist for proxy tank - mostly to encourage you to use the better suited casting tank
    this.tag(TinkerTags.Items.PROXY_TANK_BLACKLIST)
      .add(Items.BUCKET, Items.GLASS_BOTTLE, Items.BOWL, TinkerSmeltery.copperCan.get())
      .addTag(TinkerTags.Items.AMMO); // ammo has exact size tanks, unlike other modifiable items that have variable sized

    // melting tags //
    // ores
    Function<String,ResourceLocation> ie = path -> new ResourceLocation("immersiveengineering", path);
    String tf = "twilightforest";
    moltenTools(TinkerFluids.moltenCopper).add(1, Items.BRUSH).toolTags().toolsComplement();
    moltenTools(TinkerFluids.moltenIron).minecraft()
      .add(1, Items.FLINT_AND_STEEL, Items.SHIELD).fdKnife()
      .add(2, Items.SHEARS)
      .add(2, true, ie.apply("hammer"))
      .crowbar().excavatorSpikeMaul();
    moltenTools(TinkerFluids.moltenGold).minecraft("golden")
      .add(1, true,  new ResourceLocation("farmers_delight", "golden_knife"))
      .add(4, false, new ResourceLocation("golden_boots"))
      .add(4, true,  new ResourceLocation(tf, "gold_minotaur_axe"));
    moltenTools(TinkerFluids.moltenSteel).toolTags().leggingsPaxel().crowbar()
      .toolTag(1, "shovel")
      .add(1, true, ie.apply("shovel_steel"))
      .add(2, true, ie.apply("sword_steel")).add(2, true, ie.apply("hoe_steel"))
      .add(3, true, ie.apply("axe_steel")).add(3, true, ie.apply("pickaxe_steel"))
      .armorTag(5, "helmets"    ).add(5, true, ie.apply("armor_steel_helmet"))
      .armorTag(8, "chestplates").add(8, true, ie.apply("armor_steel_chestplate"))
                                              .add(7, true, ie.apply("armor_steel_leggings"))
      .armorTag(4, "boots"      ).add(4, true, ie.apply("armor_steel_boots"));
    moltenTools(TinkerFluids.moltenNetherite).minecraft().fdKnife();
    moltenTools(TinkerFluids.moltenKnightmetal)
      .optionalMetal(3, tf, "axe", "pickaxe")
      .optionalMetal(7, tf, "leggings", "shield");
    // gems
    moltenTools(TinkerFluids.moltenDiamond).minecraft().excavatorSpikeMaul().crowbar().fdKnife()
      .add(4, false, new ResourceLocation("diamond_boots"))
      .add(4, true,  new ResourceLocation(tf, "diamond_minotaur_axe"));
    // mod ores
    moltenTools(TinkerFluids.moltenTin).toolTags().toolsComplement();
    moltenTools(TinkerFluids.moltenLead).toolTags().toolsComplement();
    moltenTools(TinkerFluids.moltenSilver).toolTags().toolsComplement();
    moltenTools(TinkerFluids.moltenNickel).toolTags().toolsComplement();
    moltenTools(TinkerFluids.moltenOsmium).toolTags().leggingsPaxel();
    // mod alloys
    moltenTools(TinkerFluids.moltenBronze).toolTags().toolsComplement().leggingsPaxel();
    moltenTools(TinkerFluids.moltenElectrum).toolTags().toolsComplement();
    moltenTools(TinkerFluids.moltenInvar).toolTags().toolsComplement();
    moltenTools(TinkerFluids.moltenConstantan).toolTags().toolsComplement();
    // special alloys
    moltenTools(TinkerFluids.moltenRefinedGlowstone).toolTags().leggingsPaxel();
    moltenTools(TinkerFluids.moltenRefinedObsidian).toolTags().leggingsPaxel();
    // twilight forest
    CostTagAppender.moltenToolMelting(TConstruct.MOD_ID, "ironwood", MAKE_TAG)
      .optionalMetal(2, tf, "sword", "hoe")
      .optionalMetal(3, tf, "axe", "pickaxe");
    moltenTools(TinkerFluids.moltenSteeleaf)
      .optionalMetal(2, tf, "sword", "hoe")
      .optionalMetal(3, tf, "axe", "pickaxe");
  }

  @Override
  public String getName() {
    return "Tinkers Construct Item Tags";
  }


  /** Adds the non-flammable wood tag to all relevant wood in the object */
  private void addNonFlammableTag(WoodBlockObject object) {
    this.tag(ItemTags.NON_FLAMMABLE_WOOD)
        .add(object.asItem(), object.getSlab().asItem(), object.getStairs().asItem(),
             object.getFence().asItem(), object.getFenceGate().asItem(), object.getDoor().asItem(), object.getTrapdoor().asItem(),
             object.getPressurePlate().asItem(), object.getButton().asItem())
        .addTag(object.getLogItemTag());
  }

  /**
   * Adds relevant tags for a metal object
   * @param metal  Metal object
   */
  private void addMetalTags(MetalItemObject metal) {
    this.tag(metal.getIngotTag()).add(metal.getIngot());
    this.tag(Tags.Items.INGOTS).addTag(metal.getIngotTag());
    this.tag(metal.getNuggetTag()).add(metal.getNugget());
    this.tag(Tags.Items.NUGGETS).addTag(metal.getNuggetTag());
    this.copy(metal.getBlockTag(), metal.getBlockItemTag());
  }

  @SafeVarargs
  private void addToolTags(ItemLike tool, TagKey<Item>... tags) {
    Item item = tool.asItem();
    for (TagKey<Item> tag : tags) {
      this.tag(tag).add(item);
    }
  }

  @SafeVarargs
  private void optionalToolTags(IdAwareObject tool, TagKey<Item>... tags) {
    ResourceLocation id = tool.getId();
    for (TagKey<Item> tag : tags) {
      this.tag(tag).addOptional(id);
    }
  }

  private TagKey<Item> getArmorTag(ArmorItem.Type slotType) {
    return switch (slotType) {
      case BOOTS -> BOOTS;
      case LEGGINGS -> LEGGINGS;
      case CHESTPLATE -> CHESTPLATES;
      case HELMET -> HELMETS;
    };
  }

  private TagKey<Item> getForgeArmorTag(ArmorItem.Type slotType) {
    return switch (slotType) {
      case BOOTS -> Tags.Items.ARMORS_BOOTS;
      case LEGGINGS -> Tags.Items.ARMORS_LEGGINGS;
      case CHESTPLATE -> Tags.Items.ARMORS_CHESTPLATES;
      case HELMET -> Tags.Items.ARMORS_HELMETS;
    };
  }

  @SafeVarargs
  private void addArmorTags(EnumObject<ArmorItem.Type,? extends Item> armor, TagKey<Item>... tags) {
    armor.forEach((type, item) -> {
      for (TagKey<Item> tag : tags) {
        this.tag(tag).add(item);
      }
      this.tag(getArmorTag(type)).add(item);
      this.tag(getForgeArmorTag(type)).add(item);
    });
  }

  /** Creates a builder for a melting tag with a molten fluid */
  protected CostTagAppender moltenTools(FluidObject<?> fluid) {
    return CostTagAppender.moltenToolMelting(fluid, MAKE_TAG);
  }
}
