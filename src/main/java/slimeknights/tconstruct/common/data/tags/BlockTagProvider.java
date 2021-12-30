package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.MetalItemObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.data.SmelteryCompat;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Supplier;

import static net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE;
import static net.minecraft.tags.BlockTags.MINEABLE_WITH_HOE;
import static net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE;
import static net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL;
import static net.minecraft.tags.BlockTags.NEEDS_DIAMOND_TOOL;
import static net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL;
import static net.minecraft.tags.BlockTags.NEEDS_STONE_TOOL;
import static net.minecraftforge.common.Tags.Blocks.NEEDS_NETHERITE_TOOL;

@SuppressWarnings("unchecked")
public class BlockTagProvider extends BlockTagsProvider {

  public BlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
    super(generatorIn, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    this.addCommon();
    this.addTools();
    this.addWorld();
    this.addSmeltery();
    this.addFluids();
    this.addHarvest();
  }

  private void addCommon() {
    tag(TinkerTags.Blocks.BLOCKS_COPPER).add(Blocks.COPPER_BLOCK, Blocks.CUT_COPPER);
    tag(Tags.Blocks.STORAGE_BLOCKS).addTag(TinkerTags.Blocks.BLOCKS_COPPER);
    // ores
    addMetalTags(TinkerMaterials.cobalt, true);
    // tier 3
    addMetalTags(TinkerMaterials.slimesteel, false);
    addMetalTags(TinkerMaterials.tinkersBronze, false);
    addMetalTags(TinkerMaterials.roseGold, false);
    addMetalTags(TinkerMaterials.pigIron, false);
    // tier 4
    addMetalTags(TinkerMaterials.queensSlime, true);
    addMetalTags(TinkerMaterials.manyullyn, true);
    addMetalTags(TinkerMaterials.hepatizon, true);
    addMetalTags(TinkerMaterials.soulsteel, true);
    // tier 5
    addMetalTags(TinkerMaterials.knightslime, false);

    // glass
    this.tag(Tags.Blocks.GLASS_COLORLESS).add(TinkerCommons.clearGlass.get());
    this.tag(Tags.Blocks.GLASS_PANES_COLORLESS).add(TinkerCommons.clearGlassPane.get());
    addGlass(TinkerCommons.clearStainedGlass, "glass/", tag(Tags.Blocks.STAINED_GLASS));
    addGlass(TinkerCommons.clearStainedGlassPane, "glass_panes/", tag(Tags.Blocks.STAINED_GLASS_PANES));
    // impermeable for all glass
    TagAppender<Block> impermeable = tag(BlockTags.IMPERMEABLE);
    impermeable.add(TinkerCommons.clearGlass.get(), TinkerCommons.soulGlass.get(), TinkerSmeltery.searedGlass.get());
    TinkerCommons.clearStainedGlass.values().forEach(impermeable::add);

    // soul speed on glass
    this.tag(BlockTags.SOUL_SPEED_BLOCKS).add(TinkerCommons.soulGlass.get(), TinkerCommons.soulGlassPane.get());
    this.tag(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(TinkerCommons.soulGlass.get());

    TagsProvider.TagAppender<Block> builder = this.tag(TinkerTags.Blocks.ANVIL_METAL)
        // tier 3
        .addTag(TinkerMaterials.slimesteel.getBlockTag())
        .addTag(TinkerMaterials.tinkersBronze.getBlockTag())
        .addTag(TinkerMaterials.roseGold.getBlockTag())
        .addTag(TinkerMaterials.pigIron.getBlockTag())
        // tier 4
        .addTag(TinkerMaterials.queensSlime.getBlockTag())
        .addTag(TinkerMaterials.manyullyn.getBlockTag())
        .addTag(TinkerMaterials.hepatizon.getBlockTag())
        .addTag(Tags.Blocks.STORAGE_BLOCKS_NETHERITE);
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      if (!compat.isOre()) {
        builder.addOptionalTag(new ResourceLocation("forge", "storage_blocks/" + compat.getName()));
      }
    }

    // allow using wood variants to make tables
    this.tag(TinkerTags.Blocks.PLANKLIKE)
        .addTag(BlockTags.PLANKS)
        .add(TinkerCommons.lavawood.get(), TinkerCommons.blazewood.get(), TinkerMaterials.nahuatl.get());
  }

  private void addTools() {
    // vanilla is not tagged, so tag it
    this.tag(TinkerTags.Blocks.WORKBENCHES)
        .add(Blocks.CRAFTING_TABLE, TinkerTables.craftingStation.get())
        .addOptionalTag(new ResourceLocation("forge:workbench")); // some mods use a non-standard name here, so support it I guess
    this.tag(TinkerTags.Blocks.TABLES)
        .add(TinkerTables.craftingStation.get(), TinkerTables.partBuilder.get(), TinkerTables.tinkerStation.get());

    // can harvest crops and sugar cane
    this.tag(TinkerTags.Blocks.HARVESTABLE_STACKABLE)
        .add(Blocks.SUGAR_CANE, Blocks.KELP_PLANT);
    this.tag(TinkerTags.Blocks.HARVESTABLE_CROPS)
        .addTag(BlockTags.CROPS)
        .addOptionalTag(new ResourceLocation("forge", "crops"))
        .add(Blocks.NETHER_WART);
    this.tag(TinkerTags.Blocks.HARVESTABLE_INTERACT)
        .add(Blocks.SWEET_BERRY_BUSH);
    this.tag(TinkerTags.Blocks.HARVESTABLE)
        .add(Blocks.PUMPKIN, Blocks.BEEHIVE, Blocks.BEE_NEST)
        .addTag(TinkerTags.Blocks.HARVESTABLE_CROPS)
        .addTag(TinkerTags.Blocks.HARVESTABLE_INTERACT)
        .addTag(TinkerTags.Blocks.HARVESTABLE_STACKABLE);
    // just logs for lumber axe, but modpack makers can add more
    this.tag(TinkerTags.Blocks.TREE_LOGS).addTag(BlockTags.LOGS);
    // blocks that drop gold and should drop more gold
    this.tag(TinkerTags.Blocks.CHRYSOPHILITE_ORES).addTag(Tags.Blocks.ORES_GOLD).add(Blocks.GILDED_BLACKSTONE);
  }


  private void addWorld() {
    // ores
    this.tag(TinkerTags.Blocks.ORES_COBALT).add(TinkerWorld.cobaltOre.get());
    this.tag(TinkerTags.Blocks.ORES_COPPER).addTag(BlockTags.COPPER_ORES);
    this.tag(Tags.Blocks.ORES).addTags(TinkerTags.Blocks.ORES_COBALT, TinkerTags.Blocks.ORES_COPPER);

    // allow the enderman to hold more blocks
    TagsProvider.TagAppender<Block> endermanHoldable = this.tag(BlockTags.ENDERMAN_HOLDABLE);
    endermanHoldable.addTag(TinkerTags.Blocks.CONGEALED_SLIME).add(TinkerSmeltery.grout.get(), TinkerSmeltery.netherGrout.get());

    // wood
    this.tag(TinkerTags.Blocks.SLIMY_LOGS)
        .addTag(TinkerWorld.greenheart.getLogBlockTag())
        .addTag(TinkerWorld.skyroot.getLogBlockTag())
        .addTag(TinkerWorld.bloodshroom.getLogBlockTag());
    this.tag(TinkerTags.Blocks.SLIMY_PLANKS).add(TinkerWorld.greenheart.get(), TinkerWorld.skyroot.get(), TinkerWorld.bloodshroom.get());
    this.tag(BlockTags.PLANKS).addTag(TinkerTags.Blocks.SLIMY_PLANKS);
    this.tag(BlockTags.LOGS).addTag(TinkerTags.Blocks.SLIMY_LOGS);
    this.addWoodTags(TinkerWorld.greenheart, true);
    this.addWoodTags(TinkerWorld.skyroot, true);
    this.addWoodTags(TinkerWorld.bloodshroom, false);

    // slime blocks
    TagsProvider.TagAppender<Block> slimeBlockTagAppender = this.tag(TinkerTags.Blocks.SLIME_BLOCK);
    TagsProvider.TagAppender<Block> congealedTagAppender = this.tag(TinkerTags.Blocks.CONGEALED_SLIME);
    for (SlimeType type : SlimeType.values()) {
      slimeBlockTagAppender.add(TinkerWorld.slime.get(type));
      congealedTagAppender.add(TinkerWorld.congealedSlime.get(type));
    }

    // foliage
    TagsProvider.TagAppender<Block> leavesTagAppender = this.tag(TinkerTags.Blocks.SLIMY_LEAVES);
    TagsProvider.TagAppender<Block> wartTagAppender = this.tag(BlockTags.WART_BLOCKS);
    TagsProvider.TagAppender<Block> saplingTagAppender = this.tag(TinkerTags.Blocks.SLIMY_SAPLINGS);
    for (SlimeType type : SlimeType.values()) {
      if (type.isNether()) {
        wartTagAppender.add(TinkerWorld.slimeLeaves.get(type));
        endermanHoldable.add(TinkerWorld.slimeSapling.get(type));
      } else {
        leavesTagAppender.add(TinkerWorld.slimeLeaves.get(type));
        saplingTagAppender.add(TinkerWorld.slimeSapling.get(type));
      }
    }
    this.tag(BlockTags.LEAVES).addTag(TinkerTags.Blocks.SLIMY_LEAVES);
    this.tag(BlockTags.SAPLINGS).addTag(TinkerTags.Blocks.SLIMY_SAPLINGS);

    TagAppender<Block> slimyGrass = this.tag(TinkerTags.Blocks.SLIMY_GRASS);
    TagAppender<Block> slimyNylium = this.tag(TinkerTags.Blocks.SLIMY_NYLIUM);
    TagAppender<Block> slimySoil = this.tag(TinkerTags.Blocks.SLIMY_SOIL);
    for (SlimeType type : SlimeType.values()) {
      (type.isNether() ? slimyNylium : slimyGrass).addTag(type.getGrassBlockTag());
      slimySoil.addTag(type.getDirtBlockTag());
    }
    TinkerWorld.slimeGrass.forEach((dirtType, blockObj) -> blockObj.forEach((grassType, block) -> {
      this.tag(grassType.getGrassBlockTag()).add(block);
      this.tag(dirtType.getDirtBlockTag()).add(block);
    }));
    TinkerWorld.slimeDirt.forEach((type, block) -> this.tag(type.getDirtBlockTag()).add(block));
    endermanHoldable.addTag(TinkerTags.Blocks.SLIMY_SOIL);

    this.tag(BlockTags.GUARDED_BY_PIGLINS)
        .add(TinkerTables.castChest.get(),
             // piglins do not appreciate you touching their corpses
             TinkerWorld.heads.get(TinkerHeadType.PIGLIN), TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE),
             TinkerWorld.wallHeads.get(TinkerHeadType.PIGLIN), TinkerWorld.wallHeads.get(TinkerHeadType.PIGLIN_BRUTE));
    // piglins are not a fan of zombie piglin corpses though
    this.tag(BlockTags.PIGLIN_REPELLENTS)
        .add(TinkerWorld.heads.get(TinkerHeadType.ZOMBIFIED_PIGLIN), TinkerWorld.wallHeads.get(TinkerHeadType.ZOMBIFIED_PIGLIN));
  }

  private void addSmeltery() {
    // seared
    this.tag(TinkerTags.Blocks.SEARED_BRICKS).add(
      TinkerSmeltery.searedBricks.get(),
      TinkerSmeltery.searedFancyBricks.get(),
      TinkerSmeltery.searedTriangleBricks.get());
    this.tag(TinkerTags.Blocks.SEARED_BLOCKS)
        .add(TinkerSmeltery.searedStone.get(), TinkerSmeltery.searedCrackedBricks.get(), TinkerSmeltery.searedCobble.get(), TinkerSmeltery.searedPaver.get())
        .addTag(TinkerTags.Blocks.SEARED_BRICKS);
    this.tag(BlockTags.WALLS).add(TinkerSmeltery.searedBricks.getWall(), TinkerSmeltery.searedCobble.getWall());

    // scorched
    this.tag(TinkerTags.Blocks.SCORCHED_BLOCKS).add(
      TinkerSmeltery.scorchedStone.get(),
      TinkerSmeltery.polishedScorchedStone.get(),
      TinkerSmeltery.scorchedBricks.get(),
      TinkerSmeltery.scorchedRoad.get(),
      TinkerSmeltery.chiseledScorchedBricks.get());
    this.tag(BlockTags.FENCES).add(TinkerSmeltery.scorchedBricks.getFence(), TinkerMaterials.nahuatl.getFence());

    this.tag(TinkerTags.Blocks.CISTERN_CONNECTIONS)
        // cannot add channels as it requires a block state property to properly detect, look into a way to fix this later
        .add(TinkerSmeltery.searedFaucet.get(), TinkerSmeltery.scorchedFaucet.get());

    // tanks
    TagAppender<Block> searedTankTagAppender = this.tag(TinkerTags.Blocks.SEARED_TANKS);
    TinkerSmeltery.searedTank.values().forEach(searedTankTagAppender::add);
    TagAppender<Block> scorchedTankTagAppender = this.tag(TinkerTags.Blocks.SCORCHED_TANKS);
    TinkerSmeltery.scorchedTank.values().forEach(scorchedTankTagAppender::add);

    // structure tags
    // melter supports the heater as a tank
    this.tag(TinkerTags.Blocks.HEATER_CONTROLLERS)
        .add(TinkerSmeltery.searedMelter.get(), TinkerSmeltery.scorchedAlloyer.get());
    this.tag(TinkerTags.Blocks.FUEL_TANKS)
        .add(TinkerSmeltery.searedHeater.get())
        .addTag(TinkerTags.Blocks.SEARED_TANKS)
        .addTag(TinkerTags.Blocks.SCORCHED_TANKS);
    this.tag(TinkerTags.Blocks.SMELTERY_TANKS).addTag(TinkerTags.Blocks.SEARED_TANKS);
    this.tag(TinkerTags.Blocks.FOUNDRY_TANKS).addTag(TinkerTags.Blocks.SCORCHED_TANKS);
    this.tag(TinkerTags.Blocks.ALLOYER_TANKS)
        .add(TinkerSmeltery.scorchedAlloyer.get(), TinkerSmeltery.searedMelter.get())
        .addTag(TinkerTags.Blocks.SEARED_TANKS)
        .addTag(TinkerTags.Blocks.SCORCHED_TANKS);

    // smeltery blocks
    // floor allows any basic seared blocks and all IO blocks
    this.tag(TinkerTags.Blocks.SMELTERY_FLOOR)
        .addTag(TinkerTags.Blocks.SEARED_BLOCKS)
        .add(TinkerSmeltery.searedDrain.get(), TinkerSmeltery.searedChute.get(), TinkerSmeltery.searedDuct.get());
    // wall allows seared blocks, tanks, glass, and IO
    this.tag(TinkerTags.Blocks.SMELTERY_WALL)
        .addTag(TinkerTags.Blocks.SEARED_BLOCKS)
        .addTag(TinkerTags.Blocks.SMELTERY_TANKS)
        .add(TinkerSmeltery.searedGlass.get(), TinkerSmeltery.searedLadder.get(),
             TinkerSmeltery.searedDrain.get(), TinkerSmeltery.searedChute.get(), TinkerSmeltery.searedDuct.get());
    // smeltery allows any of the three
    this.tag(TinkerTags.Blocks.SMELTERY)
        .addTag(TinkerTags.Blocks.SMELTERY_WALL)
        .addTag(TinkerTags.Blocks.SMELTERY_FLOOR)
        .addTag(TinkerTags.Blocks.SMELTERY_TANKS);

    // foundry blocks
    // floor allows any basic seared blocks and all IO blocks
    this.tag(TinkerTags.Blocks.FOUNDRY_FLOOR)
        .addTag(TinkerTags.Blocks.SCORCHED_BLOCKS)
        .add(TinkerSmeltery.scorchedDrain.get(), TinkerSmeltery.scorchedChute.get(), TinkerSmeltery.scorchedDuct.get());
    // wall allows seared blocks, tanks, glass, and IO
    this.tag(TinkerTags.Blocks.FOUNDRY_WALL)
        .addTag(TinkerTags.Blocks.SCORCHED_BLOCKS)
        .addTag(TinkerTags.Blocks.FOUNDRY_TANKS)
        .add(TinkerSmeltery.scorchedGlass.get(), TinkerSmeltery.scorchedLadder.get(),
             TinkerSmeltery.scorchedDrain.get(), TinkerSmeltery.scorchedChute.get(), TinkerSmeltery.scorchedDuct.get());
    // foundry allows any of the three
    this.tag(TinkerTags.Blocks.FOUNDRY)
        .addTag(TinkerTags.Blocks.FOUNDRY_WALL)
        .addTag(TinkerTags.Blocks.FOUNDRY_FLOOR)
        .addTag(TinkerTags.Blocks.FOUNDRY_TANKS);

    // climb seared ladder
    this.tag(BlockTags.CLIMBABLE).add(TinkerSmeltery.searedLadder.get(), TinkerSmeltery.scorchedLadder.get());
    this.tag(BlockTags.DRAGON_IMMUNE).add(TinkerCommons.obsidianPane.get());
  }

  private void addFluids() {
    this.tag(BlockTags.STRIDER_WARM_BLOCKS).add(TinkerFluids.magma.getBlock(), TinkerFluids.blazingBlood.getBlock());
  }

  private void addHarvest() {
    // commons
    tagBlocks(MINEABLE_WITH_SHOVEL, TinkerCommons.mudBricks);
    tagBlocks(MINEABLE_WITH_AXE, TinkerCommons.lavawood, TinkerCommons.blazewood);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, TinkerCommons.obsidianPane);

    // materials
    tagBlocks(MINEABLE_WITH_AXE, NEEDS_DIAMOND_TOOL, TinkerMaterials.nahuatl);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, TinkerWorld.cobaltOre, TinkerMaterials.cobalt, TinkerMaterials.slimesteel, TinkerMaterials.tinkersBronze, TinkerMaterials.roseGold, TinkerMaterials.pigIron);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, TinkerMaterials.queensSlime, TinkerMaterials.manyullyn, TinkerMaterials.hepatizon, TinkerMaterials.soulsteel, TinkerModifiers.silkyJewelBlock);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_NETHERITE_TOOL, TinkerMaterials.knightslime);

    // slime
    tagBlocks(MINEABLE_WITH_SHOVEL, TinkerWorld.congealedSlime, TinkerWorld.slimeDirt, TinkerWorld.vanillaSlimeGrass, TinkerWorld.earthSlimeGrass, TinkerWorld.skySlimeGrass, TinkerWorld.enderSlimeGrass, TinkerWorld.ichorSlimeGrass);
    tagBlocks(MINEABLE_WITH_HOE, TinkerWorld.slimeLeaves);
    tagBlocks(MINEABLE_WITH_AXE, TinkerWorld.greenheart, TinkerWorld.skyroot, TinkerWorld.bloodshroom);
    tagBlocks(MINEABLE_WITH_AXE, TinkerWorld.skySlimeVine, TinkerWorld.enderSlimeVine);

    // smeltery
    tagBlocks(MINEABLE_WITH_SHOVEL, TinkerSmeltery.grout, TinkerSmeltery.netherGrout);
    // seared
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerSmeltery.searedStone, TinkerSmeltery.searedPaver, TinkerSmeltery.searedCobble, TinkerSmeltery.searedBricks);
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerSmeltery.searedCrackedBricks, TinkerSmeltery.searedFancyBricks, TinkerSmeltery.searedLadder, TinkerSmeltery.searedGlass, TinkerSmeltery.searedGlassPane);
    // scorched
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerSmeltery.scorchedBricks, TinkerSmeltery.scorchedRoad);
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerSmeltery.scorchedStone, TinkerSmeltery.polishedScorchedStone, TinkerSmeltery.chiseledScorchedBricks, TinkerSmeltery.scorchedLadder, TinkerSmeltery.scorchedGlass, TinkerSmeltery.scorchedGlassPane);
    // fluids
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerSmeltery.searedTank, TinkerSmeltery.scorchedTank);
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerSmeltery.searedLantern,   TinkerSmeltery.searedFaucet,   TinkerSmeltery.searedChannel,   TinkerSmeltery.searedBasin,   TinkerSmeltery.searedTable);
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerSmeltery.scorchedLantern, TinkerSmeltery.scorchedFaucet, TinkerSmeltery.scorchedChannel, TinkerSmeltery.scorchedBasin, TinkerSmeltery.scorchedTable);
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerSmeltery.searedHeater, TinkerSmeltery.searedMelter, TinkerSmeltery.scorchedAlloyer);
    // tough seared + scorched
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_STONE_TOOL, TinkerSmeltery.searedDrain, TinkerSmeltery.searedChute, TinkerSmeltery.smelteryController);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, TinkerSmeltery.searedDuct, TinkerSmeltery.scorchedDuct);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, TinkerSmeltery.scorchedDrain, TinkerSmeltery.scorchedDuct, TinkerSmeltery.foundryController);

    // tables
    tagBlocks(MINEABLE_WITH_AXE, TinkerTables.craftingStation, TinkerTables.tinkerStation, TinkerTables.partBuilder, TinkerTables.tinkersChest, TinkerTables.partChest);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_STONE_TOOL, TinkerTables.castChest);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, TinkerTables.tinkersAnvil, TinkerTables.scorchedAnvil);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Block Tags";
  }

  /** Applies a tag to a set of suppliers */
  @SafeVarargs
  private void tagBlocks(Tag.Named<Block> tag, Supplier<? extends Block>... blocks) {
    TagAppender<Block> appender = this.tag(tag);
    for (Supplier<? extends Block> block : blocks) {
      appender.add(block.get());
    }
  }

  /** Applies a set of tags to a block */
  private void tagBlocks(Tag.Named<Block> tag1, Tag.Named<Block> tag2, Supplier<? extends Block>... blocks) {
    tagBlocks(tag1, blocks);
    tagBlocks(tag2, blocks);
  }

  /** Applies a tag to a set of blocks */
  @SafeVarargs
  private void tagBlocks(Tag.Named<Block> tag, EnumObject<?,? extends Block>... blocks) {
    TagAppender<Block> appender = this.tag(tag);
    for (EnumObject<?,? extends Block> block : blocks) {
      block.forEach(b -> appender.add(b));
    }
  }

  /** Applies a set of tags to a block */
  private void tagBlocks(Tag.Named<Block> tag, BuildingBlockObject... blocks) {
    TagAppender<Block> appender = this.tag(tag);
    for (BuildingBlockObject block : blocks) {
      block.values().forEach(appender::add);
    }
  }

  /** Applies a set of tags to a block */
  private void tagBlocks(Tag.Named<Block> tag1, Tag.Named<Block> tag2, BuildingBlockObject... blocks) {
    tagBlocks(tag1, blocks);
    tagBlocks(tag2, blocks);
  }

  /**
   * Adds relevant tags for a metal object
   * @param metal  Metal object
   */
  private void addMetalTags(MetalItemObject metal, boolean beacon) {
    this.tag(metal.getBlockTag()).add(metal.get());
    if (beacon) {
      this.tag(BlockTags.BEACON_BASE_BLOCKS).addTag(metal.getBlockTag());
    }
    this.tag(Tags.Blocks.STORAGE_BLOCKS).addTag(metal.getBlockTag());
  }

  /** Adds tags for a glass item object */
  private void addGlass(EnumObject<GlassColor,? extends Block> blockObj, String tagPrefix, TagAppender<Block> blockTag) {
    blockObj.forEach((color, block) -> {
      blockTag.add(block);
      this.tag(BlockTags.createOptional(new ResourceLocation("forge", tagPrefix + color.getSerializedName()))).add(block);
    });
  }

  /** Adds all tags relevant to the given wood object */
  private void addWoodTags(WoodBlockObject object, boolean doesBurn) {
    // planks, handled by slimy planks tag
    //this.tag(BlockTags.PLANKS).add(object.get());
    this.tag(BlockTags.WOODEN_SLABS).add(object.getSlab());
    this.tag(BlockTags.WOODEN_STAIRS).add(object.getStairs());
    // logs
    this.tag(object.getLogBlockTag()).add(object.getLog(), object.getStrippedLog(), object.getWood(), object.getStrippedWood());

    // doors
    this.tag(BlockTags.WOODEN_FENCES).add(object.getFence());
    this.tag(Tags.Blocks.FENCES_WOODEN).add(object.getFence());
    this.tag(BlockTags.FENCE_GATES).add(object.getFenceGate());
    this.tag(Tags.Blocks.FENCE_GATES_WOODEN).add(object.getFenceGate());
    this.tag(BlockTags.WOODEN_DOORS).add(object.getDoor());
    this.tag(BlockTags.WOODEN_TRAPDOORS).add(object.getTrapdoor());
    // redstone
    this.tag(BlockTags.WOODEN_BUTTONS).add(object.getButton());
    this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(object.getPressurePlate());

    if (doesBurn) {
      // regular logs is handled by slimy logs tag
      this.tag(BlockTags.LOGS_THAT_BURN).addTag(object.getLogBlockTag());
    } else {
      this.tag(BlockTags.NON_FLAMMABLE_WOOD)
          .add(object.get(), object.getSlab(), object.getStairs(),
               object.getFence(), object.getFenceGate(), object.getDoor(), object.getTrapdoor(),
               object.getPressurePlate(), object.getButton())
          .addTag(object.getLogBlockTag());
    }

    // signs
    this.tag(BlockTags.STANDING_SIGNS).add(object.getSign());
    this.tag(BlockTags.WALL_SIGNS).add(object.getWallSign());
  }
}
