package slimeknights.tconstruct.common.data.tags;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.data.SmelteryCompat;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

public class BlockTagProvider extends BlockTagsProvider {

  public BlockTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
    super(generatorIn, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void registerTags() {
    this.addCommon();
    this.addTools();
    this.addWorld();
    this.addSmeltery();
    this.addFluids();
  }

  private void addCommon() {
    // ores
    addMetalTags(TinkerMaterials.copper, false);
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
    this.getOrCreateBuilder(Tags.Blocks.GLASS_COLORLESS).add(TinkerCommons.clearGlass.get());
    this.getOrCreateBuilder(Tags.Blocks.GLASS_PANES_COLORLESS).add(TinkerCommons.clearGlassPane.get());
    addGlass(TinkerCommons.clearStainedGlass, "glass/", getOrCreateBuilder(Tags.Blocks.STAINED_GLASS));
    addGlass(TinkerCommons.clearStainedGlassPane, "glass_panes/", getOrCreateBuilder(Tags.Blocks.STAINED_GLASS_PANES));
    // impermeable for all glass
    Builder<Block> impermeable = getOrCreateBuilder(BlockTags.IMPERMEABLE);
    impermeable.add(TinkerCommons.clearGlass.get(), TinkerCommons.soulGlass.get(), TinkerSmeltery.searedGlass.get());
    TinkerCommons.clearStainedGlass.forEach(impermeable::addItemEntry);

    // soul speed on glass
    this.getOrCreateBuilder(BlockTags.SOUL_SPEED_BLOCKS).add(TinkerCommons.soulGlass.get(), TinkerCommons.soulGlassPane.get());
    this.getOrCreateBuilder(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(TinkerCommons.soulGlass.get());

    TagsProvider.Builder<Block> builder = this.getOrCreateBuilder(TinkerTags.Blocks.ANVIL_METAL)
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
    this.getOrCreateBuilder(TinkerTags.Blocks.PLANKLIKE)
        .addTag(BlockTags.PLANKS)
        .add(TinkerCommons.lavawood.get(), TinkerCommons.blazewood.get(), TinkerMaterials.nahuatl.get());
  }

  private void addTools() {
    // vanilla is not tagged, so tag it
    this.getOrCreateBuilder(TinkerTags.Blocks.WORKBENCHES)
        .add(Blocks.CRAFTING_TABLE, TinkerTables.craftingStation.get())
        .addOptionalTag(new ResourceLocation("forge:workbench")); // some mods use a non-standard name here, so support it I guess
    this.getOrCreateBuilder(TinkerTags.Blocks.TABLES)
        .add(TinkerTables.craftingStation.get(), TinkerTables.partBuilder.get(), TinkerTables.tinkerStation.get());

    // can harvest crops and sugar cane
    this.getOrCreateBuilder(TinkerTags.Blocks.HARVESTABLE_STACKABLE)
        .add(Blocks.SUGAR_CANE, Blocks.KELP_PLANT);
    this.getOrCreateBuilder(TinkerTags.Blocks.HARVESTABLE_CROPS)
        .addTag(BlockTags.CROPS)
        .addOptionalTag(new ResourceLocation("forge", "crops"))
        .add(Blocks.NETHER_WART);
    this.getOrCreateBuilder(TinkerTags.Blocks.HARVESTABLE_INTERACT)
        .add(Blocks.SWEET_BERRY_BUSH);
    this.getOrCreateBuilder(TinkerTags.Blocks.HARVESTABLE)
        .add(Blocks.PUMPKIN, Blocks.BEEHIVE, Blocks.BEE_NEST)
        .addTag(TinkerTags.Blocks.HARVESTABLE_CROPS)
        .addTag(TinkerTags.Blocks.HARVESTABLE_INTERACT)
        .addTag(TinkerTags.Blocks.HARVESTABLE_STACKABLE);
    // just logs for lumber axe, but modpack makers can add more
    this.getOrCreateBuilder(TinkerTags.Blocks.TREE_LOGS).addTag(BlockTags.LOGS);
    // blocks that drop gold and should drop more gold
    this.getOrCreateBuilder(TinkerTags.Blocks.CHRYSOPHILITE_ORES).addTag(Tags.Blocks.ORES_GOLD).add(Blocks.GILDED_BLACKSTONE);
  }


  private void addWorld() {
    // ores
    this.getOrCreateBuilder(TinkerTags.Blocks.ORES_COBALT).add(TinkerWorld.cobaltOre.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.ORES_COPPER).add(TinkerWorld.copperOre.get());
    this.getOrCreateBuilder(Tags.Blocks.ORES)
        .addTag(TinkerTags.Blocks.ORES_COBALT)
        .addTag(TinkerTags.Blocks.ORES_COPPER);

    // allow the enderman to hold more blocks
    TagsProvider.Builder<Block> endermanHoldable = this.getOrCreateBuilder(BlockTags.ENDERMAN_HOLDABLE);
    endermanHoldable.addTag(TinkerTags.Blocks.CONGEALED_SLIME).add(TinkerSmeltery.grout.get(), TinkerSmeltery.netherGrout.get());

    // wood
    this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_LOGS)
        .addTag(TinkerWorld.greenheart.getLogBlockTag())
        .addTag(TinkerWorld.skyroot.getLogBlockTag())
        .addTag(TinkerWorld.bloodshroom.getLogBlockTag());
    this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_PLANKS).add(TinkerWorld.greenheart.get(), TinkerWorld.skyroot.get(), TinkerWorld.bloodshroom.get());
    this.getOrCreateBuilder(BlockTags.PLANKS).addTag(TinkerTags.Blocks.SLIMY_PLANKS);
    this.getOrCreateBuilder(BlockTags.LOGS).addTag(TinkerTags.Blocks.SLIMY_LOGS);
    this.addWoodTags(TinkerWorld.greenheart, true);
    this.addWoodTags(TinkerWorld.skyroot, true);
    this.addWoodTags(TinkerWorld.bloodshroom, false);

    // slime blocks
    TagsProvider.Builder<Block> slimeBlockBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SLIME_BLOCK);
    TagsProvider.Builder<Block> congealedBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.CONGEALED_SLIME);
    for (SlimeType type : SlimeType.values()) {
      slimeBlockBuilder.add(TinkerWorld.slime.get(type));
      congealedBuilder.add(TinkerWorld.congealedSlime.get(type));
    }
    // old world compat, make congealed support leaves
    this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_TREE_TRUNKS).addTag(TinkerTags.Blocks.SLIMY_LOGS).addTag(TinkerTags.Blocks.CONGEALED_SLIME);

    // foliage
    TagsProvider.Builder<Block> leavesBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_LEAVES);
    TagsProvider.Builder<Block> wartBuilder = this.getOrCreateBuilder(BlockTags.WART_BLOCKS);
    TagsProvider.Builder<Block> saplingBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_SAPLINGS);
    for (SlimeType type : SlimeType.values()) {
      if (type.isNether()) {
        wartBuilder.add(TinkerWorld.slimeLeaves.get(type));
        endermanHoldable.add(TinkerWorld.slimeSapling.get(type));
      } else {
        leavesBuilder.add(TinkerWorld.slimeLeaves.get(type));
        saplingBuilder.add(TinkerWorld.slimeSapling.get(type));
      }
    }
    this.getOrCreateBuilder(BlockTags.LEAVES).addTag(TinkerTags.Blocks.SLIMY_LEAVES);
    this.getOrCreateBuilder(BlockTags.SAPLINGS).addTag(TinkerTags.Blocks.SLIMY_SAPLINGS);

    Builder<Block> slimyGrass = this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_GRASS);
    Builder<Block> slimyNylium = this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_NYLIUM);
    Builder<Block> slimySoil = this.getOrCreateBuilder(TinkerTags.Blocks.SLIMY_SOIL);
    for (SlimeType type : SlimeType.values()) {
      (type.isNether() ? slimyNylium : slimyGrass).addTag(type.getGrassBlockTag());
      slimySoil.addTag(type.getDirtBlockTag());
    }
    TinkerWorld.slimeGrass.forEach((dirtType, blockObj) -> blockObj.forEach((grassType, block) -> {
      this.getOrCreateBuilder(grassType.getGrassBlockTag()).add(block);
      this.getOrCreateBuilder(dirtType.getDirtBlockTag()).add(block);
    }));
    TinkerWorld.slimeDirt.forEach((type, block) -> this.getOrCreateBuilder(type.getDirtBlockTag()).add(block));
    endermanHoldable.addTag(TinkerTags.Blocks.SLIMY_SOIL);

    this.getOrCreateBuilder(BlockTags.GUARDED_BY_PIGLINS)
        .add(TinkerTables.castChest.get(),
             // piglins do not appreciate you touching their corpses
             TinkerWorld.heads.get(TinkerHeadType.PIGLIN), TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE),
             TinkerWorld.wallHeads.get(TinkerHeadType.PIGLIN), TinkerWorld.wallHeads.get(TinkerHeadType.PIGLIN_BRUTE));
    // piglins are not a fan of zombie piglin corpses though
    this.getOrCreateBuilder(BlockTags.PIGLIN_REPELLENTS)
        .add(TinkerWorld.heads.get(TinkerHeadType.ZOMBIFIED_PIGLIN), TinkerWorld.wallHeads.get(TinkerHeadType.ZOMBIFIED_PIGLIN));
  }

  private void addSmeltery() {
    // seared
    this.getOrCreateBuilder(TinkerTags.Blocks.SEARED_BRICKS).add(
      TinkerSmeltery.searedBricks.get(),
      TinkerSmeltery.searedFancyBricks.get(),
      TinkerSmeltery.searedTriangleBricks.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.SEARED_BLOCKS)
        .add(TinkerSmeltery.searedStone.get(), TinkerSmeltery.searedCrackedBricks.get(), TinkerSmeltery.searedCobble.get(), TinkerSmeltery.searedPaver.get())
        .addTag(TinkerTags.Blocks.SEARED_BRICKS);
    this.getOrCreateBuilder(BlockTags.WALLS).add(TinkerSmeltery.searedBricks.getWall(), TinkerSmeltery.searedCobble.getWall());

    // scorched
    this.getOrCreateBuilder(TinkerTags.Blocks.SCORCHED_BLOCKS).add(
      TinkerSmeltery.scorchedStone.get(),
      TinkerSmeltery.polishedScorchedStone.get(),
      TinkerSmeltery.scorchedBricks.get(),
      TinkerSmeltery.scorchedRoad.get(),
      TinkerSmeltery.chiseledScorchedBricks.get());
    this.getOrCreateBuilder(BlockTags.FENCES).add(TinkerSmeltery.scorchedBricks.getFence(), TinkerMaterials.nahuatl.getFence());

    this.getOrCreateBuilder(TinkerTags.Blocks.CISTERN_CONNECTIONS)
        // cannot add channels as it requires a block state property to properly detect, look into a way to fix this later
        .add(TinkerSmeltery.searedFaucet.get(), TinkerSmeltery.scorchedFaucet.get());

    // tanks
    Builder<Block> searedTankBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SEARED_TANKS);
    TinkerSmeltery.searedTank.forEach(searedTankBuilder::addItemEntry);
    Builder<Block> scorchedTankBuilder = this.getOrCreateBuilder(TinkerTags.Blocks.SCORCHED_TANKS);
    TinkerSmeltery.scorchedTank.forEach(scorchedTankBuilder::addItemEntry);

    // structure tags
    // melter supports the heater as a tank
    this.getOrCreateBuilder(TinkerTags.Blocks.HEATER_CONTROLLERS)
        .add(TinkerSmeltery.searedMelter.get(), TinkerSmeltery.scorchedAlloyer.get());
    this.getOrCreateBuilder(TinkerTags.Blocks.FUEL_TANKS)
        .add(TinkerSmeltery.searedHeater.get())
        .addTag(TinkerTags.Blocks.SEARED_TANKS)
        .addTag(TinkerTags.Blocks.SCORCHED_TANKS);
    this.getOrCreateBuilder(TinkerTags.Blocks.SMELTERY_TANKS).addTag(TinkerTags.Blocks.SEARED_TANKS);
    this.getOrCreateBuilder(TinkerTags.Blocks.FOUNDRY_TANKS).addTag(TinkerTags.Blocks.SCORCHED_TANKS);
    this.getOrCreateBuilder(TinkerTags.Blocks.ALLOYER_TANKS)
        .add(TinkerSmeltery.scorchedAlloyer.get(), TinkerSmeltery.searedMelter.get())
        .addTag(TinkerTags.Blocks.SEARED_TANKS)
        .addTag(TinkerTags.Blocks.SCORCHED_TANKS);

    // smeltery blocks
    // floor allows any basic seared blocks and all IO blocks
    this.getOrCreateBuilder(TinkerTags.Blocks.SMELTERY_FLOOR)
        .addTag(TinkerTags.Blocks.SEARED_BLOCKS)
        .add(TinkerSmeltery.searedDrain.get(), TinkerSmeltery.searedChute.get(), TinkerSmeltery.searedDuct.get());
    // wall allows seared blocks, tanks, glass, and IO
    this.getOrCreateBuilder(TinkerTags.Blocks.SMELTERY_WALL)
        .addTag(TinkerTags.Blocks.SEARED_BLOCKS)
        .addTag(TinkerTags.Blocks.SMELTERY_TANKS)
        .add(TinkerSmeltery.searedGlass.get(), TinkerSmeltery.searedLadder.get(),
             TinkerSmeltery.searedDrain.get(), TinkerSmeltery.searedChute.get(), TinkerSmeltery.searedDuct.get());
    // smeltery allows any of the three
    this.getOrCreateBuilder(TinkerTags.Blocks.SMELTERY)
        .addTag(TinkerTags.Blocks.SMELTERY_WALL)
        .addTag(TinkerTags.Blocks.SMELTERY_FLOOR)
        .addTag(TinkerTags.Blocks.SMELTERY_TANKS);

    // foundry blocks
    // floor allows any basic seared blocks and all IO blocks
    this.getOrCreateBuilder(TinkerTags.Blocks.FOUNDRY_FLOOR)
        .addTag(TinkerTags.Blocks.SCORCHED_BLOCKS)
        .add(TinkerSmeltery.scorchedDrain.get(), TinkerSmeltery.scorchedChute.get(), TinkerSmeltery.scorchedDuct.get());
    // wall allows seared blocks, tanks, glass, and IO
    this.getOrCreateBuilder(TinkerTags.Blocks.FOUNDRY_WALL)
        .addTag(TinkerTags.Blocks.SCORCHED_BLOCKS)
        .addTag(TinkerTags.Blocks.FOUNDRY_TANKS)
        .add(TinkerSmeltery.scorchedGlass.get(), TinkerSmeltery.scorchedLadder.get(),
             TinkerSmeltery.scorchedDrain.get(), TinkerSmeltery.scorchedChute.get(), TinkerSmeltery.scorchedDuct.get());
    // foundry allows any of the three
    this.getOrCreateBuilder(TinkerTags.Blocks.FOUNDRY)
        .addTag(TinkerTags.Blocks.FOUNDRY_WALL)
        .addTag(TinkerTags.Blocks.FOUNDRY_FLOOR)
        .addTag(TinkerTags.Blocks.FOUNDRY_TANKS);

    // climb seared ladder
    this.getOrCreateBuilder(BlockTags.CLIMBABLE).add(TinkerSmeltery.searedLadder.get(), TinkerSmeltery.scorchedLadder.get());
    this.getOrCreateBuilder(BlockTags.DRAGON_IMMUNE).add(TinkerCommons.obsidianPane.get());
  }

  private void addFluids() {
    this.getOrCreateBuilder(BlockTags.STRIDER_WARM_BLOCKS).add(TinkerFluids.magma.getBlock(), TinkerFluids.blazingBlood.getBlock());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Block Tags";
  }

  /**
   * Adds relevant tags for a metal object
   * @param metal  Metal object
   */
  private void addMetalTags(MetalItemObject metal, boolean beacon) {
    this.getOrCreateBuilder(metal.getBlockTag()).add(metal.get());
    if (beacon) {
      this.getOrCreateBuilder(BlockTags.BEACON_BASE_BLOCKS).addTag(metal.getBlockTag());
    }
    this.getOrCreateBuilder(Tags.Blocks.STORAGE_BLOCKS).addTag(metal.getBlockTag());
  }

  /** Adds tags for a glass item object */
  private void addGlass(EnumObject<GlassColor,? extends Block> blockObj, String tagPrefix, Builder<Block> blockTag) {
    blockObj.forEach((color, block) -> {
      blockTag.add(block);
      this.getOrCreateBuilder(BlockTags.createOptional(new ResourceLocation("forge", tagPrefix + color))).add(block);
    });
  }

  /** Adds all tags relevant to the given wood object */
  private void addWoodTags(WoodBlockObject object, boolean doesBurn) {
    // planks, handled by slimy planks tag
    //this.getOrCreateBuilder(BlockTags.PLANKS).add(object.get());
    this.getOrCreateBuilder(BlockTags.WOODEN_SLABS).add(object.getSlab());
    this.getOrCreateBuilder(BlockTags.WOODEN_STAIRS).add(object.getStairs());
    // logs
    this.getOrCreateBuilder(object.getLogBlockTag()).add(object.getLog(), object.getStrippedLog(), object.getWood(), object.getStrippedWood());

    // doors
    this.getOrCreateBuilder(BlockTags.WOODEN_FENCES).add(object.getFence());
    this.getOrCreateBuilder(Tags.Blocks.FENCES_WOODEN).add(object.getFence());
    this.getOrCreateBuilder(BlockTags.FENCE_GATES).add(object.getFenceGate());
    this.getOrCreateBuilder(Tags.Blocks.FENCE_GATES_WOODEN).add(object.getFenceGate());
    this.getOrCreateBuilder(BlockTags.WOODEN_DOORS).add(object.getDoor());
    this.getOrCreateBuilder(BlockTags.WOODEN_TRAPDOORS).add(object.getTrapdoor());
    // redstone
    this.getOrCreateBuilder(BlockTags.WOODEN_BUTTONS).add(object.getButton());
    this.getOrCreateBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(object.getPressurePlate());

    if (doesBurn) {
      // regular logs is handled by slimy logs tag
      this.getOrCreateBuilder(BlockTags.LOGS_THAT_BURN).addTag(object.getLogBlockTag());
    } else {
      this.getOrCreateBuilder(BlockTags.NON_FLAMMABLE_WOOD)
          .add(object.get(), object.getSlab(), object.getStairs(),
               object.getFence(), object.getFenceGate(), object.getDoor(), object.getTrapdoor(),
               object.getPressurePlate(), object.getButton())
          .addTag(object.getLogBlockTag());
    }

    // signs
    this.getOrCreateBuilder(BlockTags.STANDING_SIGNS).add(object.getSign());
    this.getOrCreateBuilder(BlockTags.WALL_SIGNS).add(object.getWallSign());
  }
}
