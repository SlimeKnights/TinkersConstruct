package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

import static slimeknights.tconstruct.common.TinkerTags.Items.AOE;
import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.BOOTS;
import static slimeknights.tconstruct.common.TinkerTags.Items.CHESTPLATES;
import static slimeknights.tconstruct.common.TinkerTags.Items.DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST_PRIMARY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HELD;
import static slimeknights.tconstruct.common.TinkerTags.Items.HELMETS;
import static slimeknights.tconstruct.common.TinkerTags.Items.INTERACTABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.LEGGINGS;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_OR_HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_OR_UNARMED;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_PRIMARY;
import static slimeknights.tconstruct.common.TinkerTags.Items.MODIFIABLE;
import static slimeknights.tconstruct.common.TinkerTags.Items.MULTIPART_TOOL;
import static slimeknights.tconstruct.common.TinkerTags.Items.ONE_HANDED;
import static slimeknights.tconstruct.common.TinkerTags.Items.STONE_HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.SWORD;
import static slimeknights.tconstruct.common.TinkerTags.Items.TWO_HANDED;

@SuppressWarnings("unchecked")
public class ItemTagProvider extends ItemTagsProvider {

  public ItemTagProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
    super(generatorIn, blockTagProvider, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
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
    this.tag(TinkerTags.Items.GUIDEBOOKS).addTag(TinkerTags.Items.TINKERS_GUIDES);
    this.tag(TinkerTags.Items.BOOKS).addTag(TinkerTags.Items.GUIDEBOOKS);

    TagAppender<Item> slimeballs = this.tag(Tags.Items.SLIMEBALLS);
    for (SlimeType type : SlimeType.values()) {
      slimeballs.addTag(type.getSlimeballTag());
    }
    TinkerCommons.slimeball.forEach((type, ball) -> this.tag(type.getSlimeballTag()).add(ball));

    this.tag(Tags.Items.INGOTS).add(TinkerSmeltery.searedBrick.get(), TinkerSmeltery.scorchedBrick.get()).addTag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP);
    this.tag(Tags.Items.NUGGETS).addTags(TinkerTags.Items.NUGGETS_COPPER, TinkerTags.Items.NUGGETS_NETHERITE, TinkerTags.Items.NUGGETS_NETHERITE_SCRAP);
    this.tag(TinkerTags.Items.WITHER_BONES).add(TinkerMaterials.necroticBone.get());

    this.tag(TinkerTags.Items.NUGGETS_COPPER).add(TinkerMaterials.copperNugget.get());
    this.tag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP).add(Items.NETHERITE_SCRAP);
    this.tag(TinkerTags.Items.NUGGETS_NETHERITE).add(TinkerMaterials.netheriteNugget.get());
    this.tag(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP).add(TinkerMaterials.debrisNugget.get());
    // vanilla made cut copper dumb, so untag it in forge
    this.tag(Tags.Items.STORAGE_BLOCKS_COPPER).remove(Blocks.CUT_COPPER.asItem());

    // ores
    addMetalTags(TinkerMaterials.cobalt);
    // tier 3
    addMetalTags(TinkerMaterials.slimesteel);
    addMetalTags(TinkerMaterials.tinkersBronze);
    addMetalTags(TinkerMaterials.roseGold);
    addMetalTags(TinkerMaterials.pigIron);
    // tier 4
    addMetalTags(TinkerMaterials.queensSlime);
    addMetalTags(TinkerMaterials.manyullyn);
    addMetalTags(TinkerMaterials.hepatizon);
    addMetalTags(TinkerMaterials.soulsteel);
    // tier 5
    addMetalTags(TinkerMaterials.knightslime);
    this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);

    // glass
    copy(Tags.Blocks.GLASS_COLORLESS, Tags.Items.GLASS_COLORLESS);
    copy(Tags.Blocks.GLASS_PANES_COLORLESS, Tags.Items.GLASS_PANES_COLORLESS);
    copy(Tags.Blocks.STAINED_GLASS, Tags.Items.STAINED_GLASS);
    copy(Tags.Blocks.STAINED_GLASS_PANES, Tags.Items.STAINED_GLASS_PANES);
    for (DyeColor color : DyeColor.values()) {
      ResourceLocation name = new ResourceLocation("forge", "glass/" + color.getSerializedName());
      copy(BlockTags.createOptional(name), ItemTags.createOptional(name));
      name = new ResourceLocation("forge", "glass_panes/" + color.getSerializedName());
      copy(BlockTags.createOptional(name), ItemTags.createOptional(name));
    }

    copy(TinkerTags.Blocks.WORKBENCHES, TinkerTags.Items.WORKBENCHES);
    copy(TinkerTags.Blocks.TABLES, TinkerTags.Items.TABLES);
    copy(TinkerTags.Blocks.ANVIL_METAL, TinkerTags.Items.ANVIL_METAL);
    copy(TinkerTags.Blocks.PLANKLIKE, TinkerTags.Items.PLANKLIKE);

    TagAppender<Item> slimeslings = this.tag(TinkerTags.Items.SLIMESLINGS);
    TinkerGadgets.slimeSling.values().forEach(slimeslings::add);

    // piglins like gold and dislike zombie piglin heads
    this.tag(ItemTags.PIGLIN_LOVED)
        .add(TinkerModifiers.goldReinforcement.get(), TinkerGadgets.itemFrame.get(FrameType.GOLD), TinkerGadgets.itemFrame.get(FrameType.REVERSED_GOLD), TinkerFluids.moltenGold.asItem(), TinkerCommons.goldBars.asItem(), TinkerCommons.goldPlatform.asItem())
        .addTag(TinkerTags.Items.GOLD_CASTS);
    this.tag(ItemTags.PIGLIN_REPELLENTS).add(TinkerWorld.headItems.get(TinkerHeadType.ZOMBIFIED_PIGLIN));

    // beacons are happy to accept any expensive ingots
    this.tag(ItemTags.BEACON_PAYMENT_ITEMS)
        .addTags(TinkerMaterials.cobalt.getIngotTag(), TinkerMaterials.queensSlime.getIngotTag(),
                 TinkerMaterials.manyullyn.getIngotTag(), TinkerMaterials.hepatizon.getIngotTag());

    this.copy(TinkerTags.Blocks.COPPER_PLATFORMS, TinkerTags.Items.COPPER_PLATFORMS);
  }

  private void addWorld() {
    TagAppender<Item> heads = this.tag(Tags.Items.HEADS);
    TinkerWorld.heads.forEach(head -> heads.add(head.asItem()));

    this.copy(TinkerTags.Blocks.SLIME_BLOCK, TinkerTags.Items.SLIME_BLOCK);
    this.copy(TinkerTags.Blocks.CONGEALED_SLIME, TinkerTags.Items.CONGEALED_SLIME);
    this.copy(TinkerTags.Blocks.SLIMY_LOGS, TinkerTags.Items.SLIMY_LOGS);
    this.copy(TinkerTags.Blocks.SLIMY_PLANKS, TinkerTags.Items.SLIMY_PLANKS);
    this.copy(TinkerTags.Blocks.SLIMY_LEAVES, TinkerTags.Items.SLIMY_LEAVES);
    this.copy(TinkerTags.Blocks.SLIMY_SAPLINGS, TinkerTags.Items.SLIMY_SAPLINGS);
    this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
    this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);

    this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
    this.copy(TinkerTags.Blocks.ORES_COBALT, TinkerTags.Items.ORES_COBALT);
    this.copy(TinkerTags.Blocks.RAW_BLOCK_COBALT, TinkerTags.Items.RAW_BLOCK_COBALT);
    this.tag(TinkerTags.Items.RAW_COBALT).add(TinkerWorld.rawCobalt.get());

    // wood
    this.copy(BlockTags.NON_FLAMMABLE_WOOD, ItemTags.NON_FLAMMABLE_WOOD);
    // planks
    this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
    this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
    this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
    // logs
    this.copy(TinkerWorld.greenheart.getLogBlockTag(), TinkerWorld.greenheart.getLogItemTag());
    this.copy(TinkerWorld.skyroot.getLogBlockTag(), TinkerWorld.skyroot.getLogItemTag());
    this.copy(TinkerWorld.bloodshroom.getLogBlockTag(), TinkerWorld.bloodshroom.getLogItemTag());
    this.copy(BlockTags.LOGS, ItemTags.LOGS);
    this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
    // doors
    this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
    this.copy(Tags.Blocks.FENCES_WOODEN, Tags.Items.FENCES_WOODEN);
    this.copy(Tags.Blocks.FENCE_GATES_WOODEN, Tags.Items.FENCE_GATES_WOODEN);
    this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
    this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
    // redstone
    this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
    this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
    this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
  }


  private void addTools() {
    // stone
    addToolTags(TinkerTools.pickaxe,      MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE,         ONE_HANDED, AOE);
    addToolTags(TinkerTools.sledgeHammer, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE_PRIMARY, TWO_HANDED, AOE);
    addToolTags(TinkerTools.veinHammer,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, STONE_HARVEST, MELEE,         TWO_HANDED, AOE);
    // dirtD
    addToolTags(TinkerTools.mattock,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE, ONE_HANDED, AOE);
    addToolTags(TinkerTools.pickadze,  MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE, ONE_HANDED, AOE, STONE_HARVEST);
    addToolTags(TinkerTools.excavator, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE, TWO_HANDED, AOE);
    // wood
    addToolTags(TinkerTools.handAxe,  MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, ONE_HANDED, AOE);
    addToolTags(TinkerTools.broadAxe, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, TWO_HANDED, AOE);
    // plants
    addToolTags(TinkerTools.kama,   MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE,         ONE_HANDED, AOE);
    addToolTags(TinkerTools.scythe, MULTIPART_TOOL, DURABILITY, HARVEST_PRIMARY, MELEE_PRIMARY, TWO_HANDED, AOE);
    // sword
    addToolTags(TinkerTools.dagger,  MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, ONE_HANDED);
    addToolTags(TinkerTools.sword,   MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, ONE_HANDED, SWORD, AOE);
    addToolTags(TinkerTools.cleaver, MULTIPART_TOOL, DURABILITY, HARVEST, MELEE_PRIMARY, TWO_HANDED, SWORD, AOE);
    // specialized
    addToolTags(TinkerTools.flintAndBronze, DURABILITY, MELEE, ONE_HANDED, AOE);

    // armor
    addArmorTags(TinkerTools.travelersGear, DURABILITY);
    addArmorTags(TinkerTools.plateArmor,    DURABILITY);
    addArmorTags(TinkerTools.slimesuit,     DURABILITY);
    addToolTags(TinkerTools.slimesuit.get(ArmorSlotType.HELMET), MULTIPART_TOOL);

    // add tags to other tags
    // harvest primary and stone harvest are both automatically harvest
    this.tag(TinkerTags.Items.HARVEST).addTag(HARVEST_PRIMARY).addTag(STONE_HARVEST);
    // melee primary and swords
    this.tag(MELEE).addTag(MELEE_PRIMARY).addTag(SWORD);
    // modifier helper tags
    this.tag(MELEE_OR_HARVEST).addTag(MELEE).addTag(HARVEST);
    this.tag(MELEE_OR_UNARMED).addTag(MELEE).addTag(CHESTPLATES);
    this.tag(HELD).addTag(ONE_HANDED).addTag(TWO_HANDED);
    this.tag(INTERACTABLE).addTag(HELD).addTag(CHESTPLATES);
    this.tag(ARMOR).addTag(BOOTS).addTag(LEGGINGS).addTag(CHESTPLATES).addTag(HELMETS);
    this.tag(AOE).addTag(BOOTS); // boot walk modifiers

    // general
    this.tag(MODIFIABLE)
        .addTag(MULTIPART_TOOL).addTag(DURABILITY)
        .addTag(MELEE_OR_HARVEST).addTag(AOE)
        .addTag(HELD);

    // kamas are a shear type, when broken we don't pass it to loot tables
    this.tag(Tags.Items.SHEARS).add(TinkerTools.kama.get());
    // mark kama and scythe for mods like thermal to use
    this.tag(TinkerTags.Items.SCYTHES).add(TinkerTools.kama.get(), TinkerTools.scythe.get());
    // nothing to blacklist, just want the empty tag so it appears in datapacks
    this.tag(TinkerTags.Items.AUTOSMELT_BLACKLIST);

    // carrots and potatoes are not seeds in vanilla, so make a tag with them
    this.tag(TinkerTags.Items.SEEDS)
        .addTag(Tags.Items.SEEDS)
        .add(Items.CARROT, Items.POTATO, Items.NETHER_WART);

    // tags for modifiers
    copy(TinkerTags.Blocks.CHRYSOPHILITE_ORES, TinkerTags.Items.CHRYSOPHILITE_ORES);

    // tag for tool parts, mostly used by JEI right now
    this.tag(TinkerTags.Items.TOOL_PARTS)
        .add(TinkerToolParts.pickHead.get(), TinkerToolParts.hammerHead.get(),
						 TinkerToolParts.smallAxeHead.get(), TinkerToolParts.broadAxeHead.get(),
						 TinkerToolParts.smallBlade.get(), TinkerToolParts.broadBlade.get(),
						 TinkerToolParts.toolBinding.get(), TinkerToolParts.roundPlate.get(), TinkerToolParts.largePlate.get(),
						 TinkerToolParts.toolHandle.get(), TinkerToolParts.toughHandle.get(),
						 TinkerToolParts.repairKit.get()); // repair kit is not strictly a tool part, but this list just helps out JEI

    TagAppender<Item> slimySeeds = this.tag(TinkerTags.Items.SLIMY_SEEDS);
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
        .add(Items.SHULKER_BOX,
             Items.WHITE_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX,
             Items.YELLOW_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.GRAY_SHULKER_BOX,
             Items.LIGHT_GRAY_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.BLUE_SHULKER_BOX,
             Items.BROWN_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.BLACK_SHULKER_BOX);

    this.tag(TinkerTags.Items.VARIANT_PLANKS)
        .add(Items.OAK_PLANKS, Items.SPRUCE_PLANKS, Items.BIRCH_PLANKS, Items.JUNGLE_PLANKS, Items.DARK_OAK_PLANKS, Items.ACACIA_PLANKS, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS)
        .addTag(TinkerTags.Items.SLIMY_PLANKS);
    this.tag(TinkerTags.Items.VARIANT_LOGS).addTags(ItemTags.OAK_LOGS, ItemTags.SPRUCE_LOGS, ItemTags.BIRCH_LOGS, ItemTags.JUNGLE_LOGS, ItemTags.DARK_OAK_LOGS, ItemTags.ACACIA_LOGS, ItemTags.CRIMSON_STEMS, ItemTags.WARPED_STEMS, TinkerTags.Items.SLIMY_LOGS);

    // part builder
    this.tag(TinkerTags.Items.DEFAULT_PATTERNS).add(TinkerTables.pattern.get());
    this.tag(TinkerTags.Items.REUSABLE_PATTERNS).addTag(TinkerTags.Items.GOLD_CASTS);
    this.tag(TinkerTags.Items.PATTERNS).addTags(TinkerTags.Items.DEFAULT_PATTERNS, TinkerTags.Items.REUSABLE_PATTERNS);
  }

  private void addSmeltery() {
    this.copy(TinkerTags.Blocks.SEARED_BRICKS, TinkerTags.Items.SEARED_BRICKS);
    this.copy(TinkerTags.Blocks.SEARED_BLOCKS, TinkerTags.Items.SEARED_BLOCKS);
    this.copy(TinkerTags.Blocks.SCORCHED_BLOCKS, TinkerTags.Items.SCORCHED_BLOCKS);
    this.copy(BlockTags.SOUL_FIRE_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);

    // smeltery and foundry structure blocks
    this.tag(TinkerTags.Items.SMELTERY)
        .addTag(TinkerTags.Items.SEARED_BLOCKS)
        .addTag(TinkerTags.Items.SEARED_TANKS)
        .add(TinkerSmeltery.smelteryController.asItem(), TinkerSmeltery.searedDrain.asItem(), TinkerSmeltery.searedChute.asItem(), TinkerSmeltery.searedDuct.asItem(), TinkerSmeltery.searedGlass.asItem(), TinkerSmeltery.searedLadder.asItem());
    this.tag(TinkerTags.Items.FOUNDRY)
        .addTag(TinkerTags.Items.SCORCHED_BLOCKS)
        .addTag(TinkerTags.Items.SCORCHED_TANKS)
        .add(TinkerSmeltery.foundryController.asItem(), TinkerSmeltery.scorchedDrain.asItem(), TinkerSmeltery.scorchedChute.asItem(), TinkerSmeltery.scorchedDuct.asItem(), TinkerSmeltery.scorchedGlass.asItem(), TinkerSmeltery.scorchedLadder.asItem());
    // structure debug
    this.tag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG);
    this.tag(TinkerTags.Items.SMELTERY_DEBUG).addTag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG).addTag(TinkerTags.Items.SMELTERY);
    this.tag(TinkerTags.Items.FOUNDRY_DEBUG).addTag(TinkerTags.Items.GENERAL_STRUCTURE_DEBUG).addTag(TinkerTags.Items.FOUNDRY);

    // tag each type of cast
    TagAppender<Item> goldCasts = this.tag(TinkerTags.Items.GOLD_CASTS);
    TagAppender<Item> sandCasts = this.tag(TinkerTags.Items.SAND_CASTS);
    TagAppender<Item> redSandCasts = this.tag(TinkerTags.Items.RED_SAND_CASTS);
    TagAppender<Item> singleUseCasts = this.tag(TinkerTags.Items.SINGLE_USE_CASTS);
    TagAppender<Item> multiUseCasts = this.tag(TinkerTags.Items.MULTI_USE_CASTS);
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
    // large heads
    addCast.accept(TinkerSmeltery.hammerHeadCast);
    addCast.accept(TinkerSmeltery.broadAxeHeadCast);
    addCast.accept(TinkerSmeltery.broadBladeCast);
    // bindings
    addCast.accept(TinkerSmeltery.toolBindingCast);
    addCast.accept(TinkerSmeltery.roundPlateCast);
    addCast.accept(TinkerSmeltery.largePlateCast);
    // tool rods
    addCast.accept(TinkerSmeltery.toolHandleCast);
    addCast.accept(TinkerSmeltery.toughHandleCast);

    // add all casts to a common tag
    this.tag(TinkerTags.Items.CASTS)
        .addTag(TinkerTags.Items.GOLD_CASTS)
        .addTag(TinkerTags.Items.SAND_CASTS)
        .addTag(TinkerTags.Items.RED_SAND_CASTS);
    this.tag(TinkerTags.Items.TABLE_EMPTY_CASTS).add(TinkerCommons.goldBars.asItem());
    this.tag(TinkerTags.Items.BASIN_EMPTY_CASTS).add(TinkerCommons.goldPlatform.asItem());

    this.tag(TinkerTags.Items.DUCT_CONTAINERS).add(Items.BUCKET, TinkerSmeltery.copperCan.get(), TinkerSmeltery.searedLantern.asItem(), TinkerSmeltery.scorchedLantern.asItem());

    // tank tag
    this.copy(TinkerTags.Blocks.SEARED_TANKS, TinkerTags.Items.SEARED_TANKS);
    this.copy(TinkerTags.Blocks.SCORCHED_TANKS, TinkerTags.Items.SCORCHED_TANKS);
    this.tag(TinkerTags.Items.TANKS)
        .addTag(TinkerTags.Items.SEARED_TANKS)
        .addTag(TinkerTags.Items.SCORCHED_TANKS);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Item Tags";
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
  private void addToolTags(ItemLike tool, Tag.Named<Item>... tags) {
    Item item = tool.asItem();
    for (Tag.Named<Item> tag : tags) {
      this.tag(tag).add(item);
    }
  }

  private Tag.Named<Item> getArmorTag(ArmorSlotType slotType) {
    return switch (slotType) {
      case BOOTS -> BOOTS;
      case LEGGINGS -> LEGGINGS;
      case CHESTPLATE -> CHESTPLATES;
      case HELMET -> HELMETS;
    };
  }

  @SafeVarargs
  private void addArmorTags(EnumObject<ArmorSlotType,? extends Item> armor, Tag.Named<Item>... tags) {
    armor.forEach((type, item) -> {
      for (Tag.Named<Item> tag : tags) {
        this.tag(tag).add(item);
      }
      this.tag(getArmorTag(type)).add(item);
    });
  }
}
