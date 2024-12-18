package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tiers;
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
import slimeknights.tconstruct.common.registration.GeodeItemObject;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
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
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE;
import static net.minecraft.tags.BlockTags.MINEABLE_WITH_HOE;
import static net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE;
import static net.minecraft.tags.BlockTags.MINEABLE_WITH_SHOVEL;
import static net.minecraft.tags.BlockTags.NEEDS_DIAMOND_TOOL;
import static net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL;
import static net.minecraft.tags.BlockTags.NEEDS_STONE_TOOL;
import static net.minecraftforge.common.Tags.Blocks.NEEDS_GOLD_TOOL;
import static net.minecraftforge.common.Tags.Blocks.NEEDS_NETHERITE_TOOL;
import static slimeknights.tconstruct.common.TinkerTags.Blocks.MINEABLE_MELTING_BLACKLIST;

@SuppressWarnings({"unchecked", "SameParameterValue"})
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
    // ores
    addMetalTags(TinkerMaterials.cobalt, true);
    // tier 3
    addMetalTags(TinkerMaterials.slimesteel, false);
    addMetalTags(TinkerMaterials.amethystBronze, false);
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
    TagAppender<Block> silicaPanes = tag(TinkerTags.Blocks.GLASS_PANES_SILICA);
    silicaPanes.add(
      Blocks.GLASS_PANE, TinkerCommons.clearGlassPane.get(),
      Blocks.BLACK_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE,
      Blocks.GRAY_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE,
      Blocks.LIME_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS_PANE,
      Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE, Blocks.WHITE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE);
    this.tag(Tags.Blocks.GLASS_COLORLESS).add(TinkerCommons.clearGlass.get());
    this.tag(Tags.Blocks.GLASS_PANES_COLORLESS).add(TinkerCommons.clearGlassPane.get());
    addGlass(TinkerCommons.clearStainedGlass, "glass/", tag(Tags.Blocks.STAINED_GLASS));
    addGlass(TinkerCommons.clearStainedGlassPane, "glass_panes/", tag(Tags.Blocks.STAINED_GLASS_PANES));
    TinkerCommons.clearStainedGlassPane.forEach(pane -> silicaPanes.add(pane));

    // impermeable for all glass
    TagAppender<Block> impermeable = tag(BlockTags.IMPERMEABLE);
    TagAppender<Block> silicaGlass = tag(Tags.Blocks.GLASS_SILICA);
    impermeable.add(TinkerCommons.clearGlass.get(), TinkerCommons.soulGlass.get(), TinkerCommons.clearTintedGlass.get(),
                    TinkerSmeltery.searedGlass.get(), TinkerSmeltery.searedSoulGlass.get(), TinkerSmeltery.searedTintedGlass.get(),
                    TinkerSmeltery.scorchedGlass.get(), TinkerSmeltery.scorchedSoulGlass.get(), TinkerSmeltery.scorchedTintedGlass.get());
    silicaGlass.add(TinkerCommons.clearGlass.get());
    TinkerCommons.clearStainedGlass.values().forEach(impermeable::add);
    TinkerCommons.clearStainedGlass.values().forEach(silicaGlass::add);
    tag(Tags.Blocks.GLASS_TINTED).add(TinkerCommons.clearTintedGlass.get());

    // soul speed on glass
    this.tag(BlockTags.SOUL_SPEED_BLOCKS).add(TinkerCommons.soulGlass.get(), TinkerCommons.soulGlassPane.get(),
                                              TinkerSmeltery.searedSoulGlass.get(), TinkerSmeltery.searedSoulGlassPane.get(),
                                              TinkerSmeltery.scorchedSoulGlass.get(), TinkerSmeltery.scorchedSoulGlassPane.get());
    this.tag(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(TinkerCommons.soulGlass.get(), TinkerSmeltery.searedSoulGlass.get(), TinkerSmeltery.scorchedSoulGlass.get());
    this.tag(TinkerTags.Blocks.TRANSPARENT_OVERLAY).add(TinkerCommons.soulGlass.get(), TinkerCommons.soulGlassPane.get(),
                                                        TinkerSmeltery.searedSoulGlass.get(), TinkerSmeltery.searedSoulGlassPane.get(),
                                                        TinkerSmeltery.scorchedSoulGlass.get(), TinkerSmeltery.scorchedSoulGlassPane.get());
    this.tag(TinkerTags.Blocks.WORKSTATION_ROCK)
      .addTags(TinkerTags.Blocks.STONE, TinkerTags.Blocks.BLACKSTONE, TinkerTags.Blocks.GRANITE, TinkerTags.Blocks.DIORITE, TinkerTags.Blocks.ANDESITE, TinkerTags.Blocks.DEEPSLATE, TinkerTags.Blocks.BASALT);

    TagsProvider.TagAppender<Block> builder = this.tag(TinkerTags.Blocks.ANVIL_METAL)
        // tier 3
        .addTag(TinkerMaterials.slimesteel.getBlockTag())
        .addTag(TinkerMaterials.amethystBronze.getBlockTag())
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
        .add(TinkerMaterials.blazewood.get(), TinkerMaterials.nahuatl.get());
    // things the platform connects to on the sides
    this.tag(TinkerTags.Blocks.PLATFORM_CONNECTIONS)
        .add(Blocks.LEVER, Blocks.LADDER, Blocks.IRON_BARS, TinkerCommons.goldBars.get(), Blocks.TRIPWIRE_HOOK, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.REDSTONE_WIRE)
        .addTags(Tags.Blocks.GLASS_PANES, BlockTags.BUTTONS, Tags.Blocks.FENCES, BlockTags.WALLS, BlockTags.WALL_SIGNS);

    // copper platforms
    TagAppender<Block> copperPlatforms = this.tag(TinkerTags.Blocks.COPPER_PLATFORMS);
    TinkerCommons.copperPlatform.forEach(block -> copperPlatforms.add(block));
    TinkerCommons.waxedCopperPlatform.forEach(block -> copperPlatforms.add(block));
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
    this.tag(Tags.Blocks.ORES).addTag(TinkerTags.Blocks.ORES_COBALT);
    this.tag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK).add(TinkerWorld.cobaltOre.get());
    this.tag(Tags.Blocks.ORE_RATES_SINGULAR).add(TinkerWorld.cobaltOre.get());
    this.tag(TinkerTags.Blocks.RAW_BLOCK_COBALT).add(TinkerWorld.rawCobaltBlock.get());
    this.tag(Tags.Blocks.STORAGE_BLOCKS).addTag(TinkerTags.Blocks.RAW_BLOCK_COBALT);

    // allow the enderman to hold more blocks
    TagsProvider.TagAppender<Block> endermanHoldable = this.tag(BlockTags.ENDERMAN_HOLDABLE);
    endermanHoldable.addTag(TinkerTags.Blocks.CONGEALED_SLIME).add(TinkerSmeltery.grout.get(), TinkerSmeltery.netherGrout.get());

    // wood
    this.tag(TinkerTags.Blocks.SLIMY_LOGS)
        .addTags(TinkerWorld.greenheart.getLogBlockTag(), TinkerWorld.skyroot.getLogBlockTag(), TinkerWorld.bloodshroom.getLogBlockTag(), TinkerWorld.enderbark.getLogBlockTag());
    this.tag(TinkerTags.Blocks.SLIMY_PLANKS).add(TinkerWorld.greenheart.get(), TinkerWorld.skyroot.get(), TinkerWorld.bloodshroom.get(), TinkerWorld.enderbark.get());
    this.tag(BlockTags.PLANKS).addTag(TinkerTags.Blocks.SLIMY_PLANKS);
    this.tag(BlockTags.LOGS).addTag(TinkerTags.Blocks.SLIMY_LOGS);
    this.addWoodTags(TinkerWorld.greenheart, false);
    this.addWoodTags(TinkerWorld.skyroot, false);
    this.addWoodTags(TinkerWorld.bloodshroom, false);
    this.addWoodTags(TinkerWorld.enderbark, false);

    // slime blocks
    TagsProvider.TagAppender<Block> slimeBlockTagAppender = this.tag(TinkerTags.Blocks.SLIME_BLOCK);
    TagsProvider.TagAppender<Block> congealedTagAppender = this.tag(TinkerTags.Blocks.CONGEALED_SLIME);
    for (SlimeType type : SlimeType.values()) {
      slimeBlockTagAppender.add(TinkerWorld.slime.get(type));
      congealedTagAppender.add(TinkerWorld.congealedSlime.get(type));
    }

    // foliage
    this.tag(TinkerTags.Blocks.SLIMY_VINES).add(TinkerWorld.skySlimeVine.get(), TinkerWorld.enderSlimeVine.get());
    TagsProvider.TagAppender<Block> leavesTagAppender = this.tag(TinkerTags.Blocks.SLIMY_LEAVES);
    TagsProvider.TagAppender<Block> wartTagAppender = this.tag(BlockTags.WART_BLOCKS);
    TagsProvider.TagAppender<Block> saplingTagAppender = this.tag(TinkerTags.Blocks.SLIMY_SAPLINGS);
    for (FoliageType type : FoliageType.values()) {
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
    for (FoliageType type : FoliageType.values()) {
      (type.isNether() ? slimyNylium : slimyGrass).addTag(type.getGrassBlockTag());
    }
    for (DirtType type : DirtType.values()) {
      slimySoil.addTag(type.getBlockTag());
    }
    TinkerWorld.slimeGrass.forEach((dirtType, blockObj) -> blockObj.forEach((grassType, block) -> {
      this.tag(grassType.getGrassBlockTag()).add(block);
      this.tag(dirtType.getBlockTag()).add(block);
    }));
    TinkerWorld.slimeDirt.forEach((type, block) -> this.tag(type.getBlockTag()).add(block));
    TagAppender<Block> enderBarkRoots = this.tag(TinkerTags.Blocks.ENDERBARK_ROOTS).add(TinkerWorld.enderbarkRoots.get());
    TinkerWorld.slimyEnderbarkRoots.forEach((type, block) -> {
      this.tag(type.getDirtType().getBlockTag()).add(block);
      enderBarkRoots.add(block);
    });
    endermanHoldable.addTag(TinkerTags.Blocks.SLIMY_SOIL);
    tagBlocks(BlockTags.REPLACEABLE_PLANTS, TinkerWorld.slimeTallGrass, TinkerWorld.slimeFern);

    Consumer<Block> flowerPotAppender = this.tag(BlockTags.FLOWER_POTS)::add;
    TinkerWorld.pottedSlimeFern.forEach(flowerPotAppender);
    TinkerWorld.pottedSlimeSapling.forEach(flowerPotAppender);

    this.tag(TinkerTags.Blocks.ENDERBARK_LOGS_CAN_GROW_THROUGH)
        .addTags(TinkerTags.Blocks.SLIMY_VINES, TinkerTags.Blocks.SLIMY_SAPLINGS, TinkerTags.Blocks.CONGEALED_SLIME, TinkerTags.Blocks.ENDERBARK_ROOTS, TinkerTags.Blocks.SLIMY_LEAVES, TinkerTags.Blocks.SLIMY_LOGS);
    this.tag(TinkerTags.Blocks.ENDERBARK_ROOTS_CAN_GROW_THROUGH)
        .addTags(TinkerTags.Blocks.SLIMY_VINES, TinkerTags.Blocks.SLIMY_SAPLINGS, TinkerTags.Blocks.CONGEALED_SLIME, TinkerTags.Blocks.ENDERBARK_ROOTS)
        .add(Blocks.SNOW);


    // slime spawns
    this.tag(TinkerTags.Blocks.SKY_SLIME_SPAWN).add(TinkerWorld.earthGeode.getBlock(), TinkerWorld.earthGeode.getBudding()).addTag(FoliageType.SKY.getGrassBlockTag());
    this.tag(TinkerTags.Blocks.EARTH_SLIME_SPAWN).add(TinkerWorld.skyGeode.getBlock(), TinkerWorld.skyGeode.getBudding()).addTag(FoliageType.EARTH.getGrassBlockTag());
    this.tag(TinkerTags.Blocks.ENDER_SLIME_SPAWN).add(TinkerWorld.enderGeode.getBlock(), TinkerWorld.enderGeode.getBudding()).addTag(FoliageType.ENDER.getGrassBlockTag());

    this.tag(BlockTags.GUARDED_BY_PIGLINS)
        .add(TinkerTables.castChest.get(), TinkerCommons.goldBars.get(), TinkerCommons.goldPlatform.get(),
             // piglins do not appreciate you touching their corpses
             TinkerWorld.heads.get(TinkerHeadType.PIGLIN), TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE),
             TinkerWorld.wallHeads.get(TinkerHeadType.PIGLIN), TinkerWorld.wallHeads.get(TinkerHeadType.PIGLIN_BRUTE));
    // piglins are not a fan of zombie piglin corpses though
    this.tag(BlockTags.PIGLIN_REPELLENTS)
        .add(TinkerWorld.heads.get(TinkerHeadType.ZOMBIFIED_PIGLIN), TinkerWorld.wallHeads.get(TinkerHeadType.ZOMBIFIED_PIGLIN));

    // stone variants
    this.tag(TinkerTags.Blocks.STONE).add(Blocks.STONE, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE);
    this.tag(TinkerTags.Blocks.GRANITE).add(Blocks.GRANITE);
    this.tag(TinkerTags.Blocks.DIORITE).add(Blocks.DIORITE);
    this.tag(TinkerTags.Blocks.ANDESITE).add(Blocks.ANDESITE);
    this.tag(TinkerTags.Blocks.BLACKSTONE).add(Blocks.BLACKSTONE);
    this.tag(TinkerTags.Blocks.DEEPSLATE).add(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
    this.tag(TinkerTags.Blocks.BASALT).add(Blocks.BASALT);
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
    this.tag(TinkerTags.Blocks.SMELTERY_BRICKS).addTag(TinkerTags.Blocks.SEARED_BLOCKS);
    this.tag(BlockTags.WALLS).add(TinkerSmeltery.searedBricks.getWall(), TinkerSmeltery.searedCobble.getWall());

    // scorched
    this.tag(TinkerTags.Blocks.SCORCHED_BLOCKS).add(
      TinkerSmeltery.scorchedStone.get(),
      TinkerSmeltery.polishedScorchedStone.get(),
      TinkerSmeltery.scorchedBricks.get(),
      TinkerSmeltery.scorchedRoad.get(),
      TinkerSmeltery.chiseledScorchedBricks.get());
    this.tag(TinkerTags.Blocks.FOUNDRY_BRICKS).addTag(TinkerTags.Blocks.SCORCHED_BLOCKS);
    this.tag(BlockTags.FENCES).add(TinkerSmeltery.scorchedBricks.getFence(), TinkerMaterials.blazewood.getFence(), TinkerMaterials.nahuatl.getFence());

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
        .add(TinkerSmeltery.searedGlass.get(), TinkerSmeltery.searedSoulGlass.get(), TinkerSmeltery.searedTintedGlass.get(),
             TinkerSmeltery.searedLadder.get(),
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
        .add(TinkerSmeltery.scorchedGlass.get(), TinkerSmeltery.scorchedSoulGlass.get(), TinkerSmeltery.scorchedTintedGlass.get(),
             TinkerSmeltery.scorchedLadder.get(),
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
    tagBlocks(MINEABLE_WITH_SHOVEL, TinkerCommons.cheeseBlock);
    tagBlocks(MINEABLE_WITH_AXE, TinkerGadgets.punji);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, TinkerCommons.obsidianPane);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_STONE_TOOL, TinkerCommons.ironPlatform);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, TinkerCommons.goldBars, TinkerCommons.goldPlatform, TinkerCommons.cobaltPlatform);
    this.tag(MINEABLE_WITH_PICKAXE).addTag(TinkerTags.Blocks.COPPER_PLATFORMS);
    this.tag(NEEDS_STONE_TOOL).addTag(TinkerTags.Blocks.COPPER_PLATFORMS);

    // materials
    tagBlocks(MINEABLE_WITH_AXE, NEEDS_IRON_TOOL, TinkerMaterials.blazewood);
    tagBlocks(MINEABLE_WITH_AXE, NEEDS_DIAMOND_TOOL, TinkerMaterials.nahuatl);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, TinkerWorld.cobaltOre, TinkerWorld.rawCobaltBlock, TinkerMaterials.cobalt, TinkerMaterials.slimesteel, TinkerMaterials.amethystBronze, TinkerMaterials.roseGold, TinkerMaterials.pigIron);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, TinkerMaterials.queensSlime, TinkerMaterials.manyullyn, TinkerMaterials.hepatizon, TinkerMaterials.soulsteel, TinkerModifiers.silkyJewelBlock);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_NETHERITE_TOOL, TinkerMaterials.knightslime);

    // slime
    tagBlocks(MINEABLE_WITH_SHOVEL, TinkerWorld.congealedSlime, TinkerWorld.slimeDirt, TinkerWorld.vanillaSlimeGrass, TinkerWorld.earthSlimeGrass, TinkerWorld.skySlimeGrass, TinkerWorld.enderSlimeGrass, TinkerWorld.ichorSlimeGrass);
    // harvest tiers on shovel blocks
    TinkerWorld.slimeDirt.forEach((type, block) -> this.tag(Objects.requireNonNull(type.getHarvestTier().getTag())).add(block));
    for (DirtType dirt : DirtType.values()) {
      for (FoliageType grass : FoliageType.values()) {
        Tiers dirtTier = dirt.getHarvestTier();
        Tiers grassTier = grass.getHarvestTier();
        // cannot use tier sorting registry as its not init during datagen, stuck comparing levels and falling back to ordinal for gold
        Tiers tier;
        if (dirtTier.getLevel() == grassTier.getLevel()) {
          tier = dirtTier.ordinal() > grassTier.ordinal() ? dirtTier : grassTier;
        } else {
          tier = dirtTier.getLevel() > grassTier.getLevel() ? dirtTier : grassTier;
        }
        this.tag(Objects.requireNonNull(tier.getTag())).add(TinkerWorld.slimeGrass.get(dirt).get(grass));
      }
    }

    tagBlocks(MINEABLE_WITH_HOE, TinkerWorld.slimeLeaves);
    tagLogs(MINEABLE_WITH_AXE, NEEDS_GOLD_TOOL, TinkerWorld.skyroot);
    tagLogs(MINEABLE_WITH_AXE, NEEDS_STONE_TOOL, TinkerWorld.greenheart);
    tagLogs(MINEABLE_WITH_AXE, NEEDS_IRON_TOOL, TinkerWorld.bloodshroom);
    tagLogs(MINEABLE_WITH_AXE, NEEDS_DIAMOND_TOOL, TinkerWorld.enderbark);
    tagPlanks(MINEABLE_WITH_SHOVEL, TinkerWorld.greenheart, TinkerWorld.skyroot, TinkerWorld.bloodshroom, TinkerWorld.enderbark);
    tagBlocks(MINEABLE_WITH_SHOVEL, TinkerWorld.slimyEnderbarkRoots);
    tagBlocks(MINEABLE_WITH_AXE, TinkerWorld.skySlimeVine, TinkerWorld.enderSlimeVine, TinkerWorld.enderbarkRoots);
    tagBlocks(MINEABLE_WITH_AXE, TinkerWorld.slimeTallGrass, TinkerWorld.slimeFern);
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerWorld.earthGeode, TinkerWorld.skyGeode, TinkerWorld.ichorGeode, TinkerWorld.enderGeode);
    tagBlocks(NEEDS_DIAMOND_TOOL, TinkerWorld.enderbarkRoots);
    tagBlocks(NEEDS_DIAMOND_TOOL, TinkerWorld.slimyEnderbarkRoots);


    // smeltery
    tagBlocks(MINEABLE_WITH_SHOVEL, TinkerSmeltery.grout, TinkerSmeltery.netherGrout);
    // seared
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_GOLD_TOOL, TinkerSmeltery.searedStone, TinkerSmeltery.searedPaver, TinkerSmeltery.searedCobble, TinkerSmeltery.searedBricks);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_GOLD_TOOL, TinkerSmeltery.searedCrackedBricks, TinkerSmeltery.searedFancyBricks, TinkerSmeltery.searedTriangleBricks, TinkerSmeltery.searedLadder, TinkerSmeltery.searedGlass, TinkerSmeltery.searedSoulGlass, TinkerSmeltery.searedTintedGlass, TinkerSmeltery.searedGlassPane, TinkerSmeltery.searedSoulGlassPane);
    // scorched
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_GOLD_TOOL, TinkerSmeltery.scorchedBricks, TinkerSmeltery.scorchedRoad);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_GOLD_TOOL, TinkerSmeltery.scorchedStone, TinkerSmeltery.polishedScorchedStone, TinkerSmeltery.chiseledScorchedBricks, TinkerSmeltery.scorchedLadder, TinkerSmeltery.scorchedGlass, TinkerSmeltery.scorchedSoulGlass, TinkerSmeltery.scorchedTintedGlass, TinkerSmeltery.scorchedGlassPane, TinkerSmeltery.scorchedSoulGlassPane);
    // fluids
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_GOLD_TOOL, TinkerSmeltery.searedTank, TinkerSmeltery.scorchedTank);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_GOLD_TOOL, TinkerSmeltery.searedLantern,   TinkerSmeltery.searedFaucet,   TinkerSmeltery.searedChannel,   TinkerSmeltery.searedBasin,   TinkerSmeltery.searedTable);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_GOLD_TOOL, TinkerSmeltery.scorchedLantern, TinkerSmeltery.scorchedFaucet, TinkerSmeltery.scorchedChannel, TinkerSmeltery.scorchedBasin, TinkerSmeltery.scorchedTable);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_GOLD_TOOL, TinkerSmeltery.searedHeater, TinkerSmeltery.searedMelter, TinkerSmeltery.scorchedAlloyer);
    // tough seared + scorched
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_STONE_TOOL, TinkerSmeltery.searedDrain, TinkerSmeltery.searedChute, TinkerSmeltery.smelteryController);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, TinkerSmeltery.searedDuct, TinkerSmeltery.scorchedDuct);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_DIAMOND_TOOL, TinkerSmeltery.scorchedDrain, TinkerSmeltery.scorchedChute, TinkerSmeltery.foundryController);

    // tables
    tagBlocks(MINEABLE_WITH_AXE, TinkerTables.craftingStation, TinkerTables.tinkerStation, TinkerTables.partBuilder, TinkerTables.tinkersChest, TinkerTables.partChest);
    tagBlocks(MINEABLE_WITH_PICKAXE, TinkerTables.modifierWorktable);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_STONE_TOOL, TinkerTables.castChest);
    tagBlocks(MINEABLE_WITH_PICKAXE, NEEDS_IRON_TOOL, TinkerTables.tinkersAnvil, TinkerTables.scorchedAnvil);

    // custom tool harvest
    // mattock works on all shovel and natural axe
    tag(TinkerTags.Blocks.MINABLE_WITH_MATTOCK).addTags(MINEABLE_WITH_SHOVEL, BlockTags.LOGS).add(
      Blocks.AZALEA, Blocks.BAMBOO, Blocks.GLOW_LICHEN, Blocks.VINE,
      Blocks.BEE_NEST, Blocks.BEEHIVE,
      Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN, Blocks.PUMPKIN,
      Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.COCOA,
      Blocks.BROWN_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM, Blocks.RED_MUSHROOM_BLOCK);
    // pickadze is shovel or pickaxe
    tag(TinkerTags.Blocks.MINABLE_WITH_PICKADZE).addTags(MINEABLE_WITH_SHOVEL, MINEABLE_WITH_PICKAXE);
    // hand axe has a leaf bonus
    tag(TinkerTags.Blocks.MINABLE_WITH_HAND_AXE).addTags(MINEABLE_WITH_AXE, BlockTags.LEAVES);
    // scythe/kama does hoe or shear blocks
    tag(TinkerTags.Blocks.MINABLE_WITH_SHEARS)
      .add(Blocks.AZALEA, Blocks.COBWEB, Blocks.DRIED_KELP_BLOCK, Blocks.GLOW_LICHEN, Blocks.LILY_PAD, Blocks.REDSTONE_WIRE,
           Blocks.TRIPWIRE, Blocks.TWISTING_VINES_PLANT, Blocks.TWISTING_VINES, Blocks.VINE, Blocks.WEEPING_VINES_PLANT, Blocks.WEEPING_VINES)
      .addTags(BlockTags.CAVE_VINES, BlockTags.LEAVES, BlockTags.WOOL,BlockTags.SAPLINGS, BlockTags.FLOWERS, BlockTags.REPLACEABLE_PLANTS, BlockTags.CORAL_PLANTS);
    // scythe/kama does hoe or shear blocks
    tag(TinkerTags.Blocks.MINABLE_WITH_SCYTHE)
      .add(Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM, Blocks.BIG_DRIPLEAF_STEM, Blocks.BIG_DRIPLEAF, Blocks.KELP, Blocks.KELP_PLANT, Blocks.NETHER_WART, Blocks.SMALL_DRIPLEAF, Blocks.SPORE_BLOSSOM, Blocks.SUGAR_CANE, Blocks.SWEET_BERRY_BUSH)
      .addTags(MINEABLE_WITH_HOE, TinkerTags.Blocks.MINABLE_WITH_SHEARS, BlockTags.CROPS);
    // sword list is filled to best ability, but will be a bit inexact as vanilla uses materials, hopefully putting this tag under forge will get people to tag their blocks
    tag(TinkerTags.Blocks.MINABLE_WITH_SWORD).add(Blocks.COBWEB)
      .add(Blocks.COCOA, Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER, Blocks.SWEET_BERRY_BUSH, Blocks.VINE, Blocks.MOSS_CARPET, Blocks.MOSS_BLOCK,
           Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM, Blocks.GLOW_LICHEN, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.MELON)
      .addTags(BlockTags.LEAVES, BlockTags.SAPLINGS, BlockTags.FLOWERS, BlockTags.CROPS);
    // dagger does hoe or sword blocks
    tag(TinkerTags.Blocks.MINABLE_WITH_DAGGER).addTags(MINEABLE_WITH_HOE, TinkerTags.Blocks.MINABLE_WITH_SWORD);

    // melting pan blacklist, basically anything that feels gross due to unsupported melting recipe
    tagBlocks(MINEABLE_MELTING_BLACKLIST, TinkerSmeltery.searedMelter, TinkerSmeltery.smelteryController, TinkerSmeltery.foundryController, TinkerSmeltery.searedLantern, TinkerSmeltery.scorchedLantern);
    tagBlocks(MINEABLE_MELTING_BLACKLIST, TinkerSmeltery.searedTank, TinkerSmeltery.scorchedTank);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Block Tags";
  }

  /** Applies a tag to a set of suppliers */
  @SafeVarargs
  private void tagBlocks(TagKey<Block> tag, Supplier<? extends Block>... blocks) {
    TagAppender<Block> appender = this.tag(tag);
    for (Supplier<? extends Block> block : blocks) {
      appender.add(block.get());
    }
  }

  /** Applies a tag to a set of suppliers */
  private void tagBlocks(TagKey<Block> tag, GeodeItemObject... blocks) {
    TagAppender<Block> appender = this.tag(tag);
    for (GeodeItemObject geode : blocks) {
      appender.add(geode.getBlock());
      appender.add(geode.getBudding());
      for (BudSize size : BudSize.values()) {
        appender.add(geode.getBud(size));
      }
    }
  }

  /** Applies a set of tags to a block */
  @SuppressWarnings("SameParameterValue")
  private void tagBlocks(TagKey<Block> tag1, TagKey<Block> tag2, Supplier<? extends Block>... blocks) {
    tagBlocks(tag1, blocks);
    tagBlocks(tag2, blocks);
  }

  /** Applies a tag to a set of blocks */
  @SafeVarargs
  private void tagBlocks(TagKey<Block> tag, EnumObject<?,? extends Block>... blocks) {
    TagAppender<Block> appender = this.tag(tag);
    for (EnumObject<?,? extends Block> block : blocks) {
      block.forEach(b -> appender.add(b));
    }
  }

  /** Applies a tag to a set of blocks */
  @SafeVarargs
  private void tagBlocks(TagKey<Block> tag1, TagKey<Block> tag2, EnumObject<?,? extends Block>... blocks) {
    tagBlocks(tag1, blocks);
    tagBlocks(tag2, blocks);
  }

  /** Applies a set of tags to a block */
  private void tagBlocks(TagKey<Block> tag, BuildingBlockObject... blocks) {
    TagAppender<Block> appender = this.tag(tag);
    for (BuildingBlockObject block : blocks) {
      block.values().forEach(appender::add);
    }
  }

  /** Applies a set of tags to a block */
  @SuppressWarnings("SameParameterValue")
  private void tagBlocks(TagKey<Block> tag1, TagKey<Block> tag2, BuildingBlockObject... blocks) {
    tagBlocks(tag1, blocks);
    tagBlocks(tag2, blocks);
  }

  /** Applies a set of tags to either wood or logs from a block */
  @SuppressWarnings("SameParameterValue")
  private void tagLogs(TagKey<Block> tag1, TagKey<Block> tag2, WoodBlockObject... blocks) {
    for (WoodBlockObject block : blocks) {
      tag(tag1).add(block.getLog(), block.getWood());
      tag(tag2).add(block.getLog(), block.getWood());
    }
  }

  /** Applies a set of tags to either wood or logs from a block */
  @SuppressWarnings("SameParameterValue")
  private void tagPlanks(TagKey<Block> tag, WoodBlockObject... blocks) {
    for (WoodBlockObject block : blocks) {
      tag(tag).add(block.get(), block.getSlab(), block.getStairs(), block.getFence(),
                   block.getStrippedLog(), block.getStrippedWood(), block.getFenceGate(), block.getDoor(), block.getTrapdoor(),
                   block.getPressurePlate(), block.getButton(), block.getSign(), block.getWallSign());
    }
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
      this.tag(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("forge", tagPrefix + color.getSerializedName()))).add(block);
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
