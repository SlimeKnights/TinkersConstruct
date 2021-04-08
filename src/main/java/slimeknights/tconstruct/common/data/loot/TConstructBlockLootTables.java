package slimeknights.tconstruct.common.data.loot;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyNameLootFunction;
import net.minecraft.loot.function.CopyNbtLootFunction;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.loot.RetexturedLootFunction;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import org.jetbrains.annotations.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TConstructBlockLootTables extends BlockLootTableGenerator {

  private final Map<Identifier, LootTable.Builder> loot_tables = Maps.newHashMap();

  @Nonnull
  @Override
  protected Iterable<Block> getKnownBlocks() {
    return ForgeRegistries.BLOCKS.getValues().stream()
                                 .filter((block) -> TConstruct.modID.equals(Objects.requireNonNull(block.getRegistryName()).getNamespace()))
                                 .collect(Collectors.toList());
  }

  @Override
  protected void addTables() {
    this.addCommon();
    this.addDecorative();
    this.addGadgets();
    this.addWorld();
    this.addTools();
    this.addSmeltery();
  }

  private void addCommon() {
    this.addDrop(TinkerSmeltery.grout.get());
    this.registerBuildingLootTables(TinkerCommons.blazewood);
    this.registerBuildingLootTables(TinkerCommons.lavawood);

    this.addDrop(TinkerModifiers.silkyJewelBlock.get());

    // ores
    this.addDrop(TinkerMaterials.copper.get());
    this.addDrop(TinkerMaterials.cobalt.get());
    // tier 3
    this.addDrop(TinkerMaterials.slimesteel.get());
    this.addDrop(TinkerMaterials.tinkersBronze.get());
    this.addDrop(TinkerMaterials.roseGold.get());
    this.addDrop(TinkerMaterials.pigIron.get());
    // tier 4
    this.addDrop(TinkerMaterials.manyullyn.get());
    this.addDrop(TinkerMaterials.hepatizon.get());
    this.addDrop(TinkerMaterials.queensSlime.get());
    this.addDrop(TinkerMaterials.soulsteel.get());
    // tier 5
    this.addDrop(TinkerMaterials.knightslime.get());
  }

  private void addDecorative() {
    this.addDrop(TinkerCommons.clearGlass.get());
    this.addDrop(TinkerCommons.clearGlassPane.get());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      this.addDrop(TinkerCommons.clearStainedGlass.get(color));
      this.addDrop(TinkerCommons.clearStainedGlassPane.get(color));
    }
    this.addDrop(TinkerCommons.soulGlass.get());
    this.addDrop(TinkerCommons.soulGlassPane.get());

    this.registerBuildingLootTables(TinkerCommons.mudBricks);
    this.registerBuildingLootTables(TinkerCommons.driedClay);
    this.registerBuildingLootTables(TinkerCommons.driedClayBricks);
  }

  private void addTools() {
    this.addDrop(TinkerTables.craftingStation.get());

    // chests
    Function<Block, LootTable.Builder> addChest = block -> droppingWithFunctions(block, (builder) ->
      builder.apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
                    .apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation("Items", "TinkerData.Items")));
    this.addDrop(TinkerTables.modifierChest.get(), addChest);
    this.addDrop(TinkerTables.partChest.get(), addChest);
    this.addDrop(TinkerTables.castChest.get(), addChest);

    // tables with legs
    Function<Block, LootTable.Builder> addTable = block -> droppingWithFunctions(block, (builder) ->
      builder.apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY)).apply(RetexturedLootFunction::new));
    this.addDrop(TinkerTables.partBuilder.get(), addTable);
    this.addDrop(TinkerTables.tinkerStation.get(), addTable);
    this.addDrop(TinkerTables.tinkersAnvil.get(), addTable);

    // normal tables
    this.addDrop(TinkerTables.craftingStation.get(), block ->
      droppingWithFunctions(block, (builder) -> builder.apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))));
  }

  private void addWorld() {
    this.addDrop(TinkerWorld.cobaltOre.get());
    this.addDrop(TinkerWorld.copperOre.get());

    // Only make loot table for our modded slime blocks
    for (SlimeType slime : SlimeType.TINKER) {
      this.addDrop(TinkerWorld.slime.get(slime));
    }
    // Make loot table for all congealed_slime variants
    for (SlimeType slime : SlimeType.values()) {
      this.addDrop(TinkerWorld.congealedSlime.get(slime));
    }

    for (SlimeType type : SlimeType.TRUE_SLIME) {
      this.addDrop(TinkerWorld.slimeDirt.get(type));
    }

    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      this.addDrop(TinkerWorld.vanillaSlimeGrass.get(type), (block) -> drops(block, Blocks.DIRT));
      this.addDrop(TinkerWorld.earthSlimeGrass.get(type), (block) -> drops(block, TinkerWorld.slimeDirt.get(SlimeType.EARTH)));
      this.addDrop(TinkerWorld.skySlimeGrass.get(type), (block) -> drops(block, TinkerWorld.slimeDirt.get(SlimeType.SKY)));
      this.addDrop(TinkerWorld.enderSlimeGrass.get(type), (block) -> drops(block, TinkerWorld.slimeDirt.get(SlimeType.ENDER)));
      this.addDrop(TinkerWorld.ichorSlimeGrass.get(type), (block) -> drops(block, TinkerWorld.slimeDirt.get(SlimeType.ICHOR)));
      this.addDrop(TinkerWorld.slimeLeaves.get(type), (block) -> randomDropSlimeBallOrSapling(type, block, TinkerWorld.slimeSapling.get(type), SAPLING_DROP_CHANCE));
      this.addDrop(TinkerWorld.slimeFern.get(type), BlockLootTableGenerator::dropsWithShears);
      this.addDrop(TinkerWorld.slimeTallGrass.get(type), BlockLootTableGenerator::dropsWithShears);
      this.addDrop(TinkerWorld.slimeSapling.get(type));
    }

    this.addDrop(TinkerWorld.enderSlimeVine.get(), BlockLootTableGenerator::dropsWithShears);

    this.addDrop(TinkerWorld.skySlimeVine.get(), BlockLootTableGenerator::dropsWithShears);
  }

  private void addGadgets() {
    this.addDrop(TinkerGadgets.stoneLadder.get());

    this.addDrop(TinkerGadgets.stoneTorch.get());

    this.addDrop(TinkerGadgets.wallStoneTorch.get(), TinkerGadgets.stoneTorch.get());

    this.addDrop(TinkerGadgets.punji.get());

    this.addDrop(TinkerGadgets.woodenRail.get());
    this.addDrop(TinkerGadgets.woodenDropperRail.get());
  }

  private void addSmeltery() {
    // controller
    this.addDrop(TinkerSmeltery.searedMelter.get());
    this.addDrop(TinkerSmeltery.searedHeater.get());
    this.addDrop(TinkerSmeltery.smelteryController.get());

    // smeltery component
    this.registerBuildingLootTables(TinkerSmeltery.searedStone);
    this.registerWallBuildingLootTables(TinkerSmeltery.searedCobble);
    this.registerBuildingLootTables(TinkerSmeltery.searedPaver);
    this.registerWallBuildingLootTables(TinkerSmeltery.searedBricks);
    this.addDrop(TinkerSmeltery.searedCrackedBricks.get());
    this.addDrop(TinkerSmeltery.searedFancyBricks.get());
    this.addDrop(TinkerSmeltery.searedTriangleBricks.get());
    this.addDrop(TinkerSmeltery.searedLadder.get());
    this.addDrop(TinkerSmeltery.searedGlass.get());
    this.addDrop(TinkerSmeltery.searedGlassPane.get());
    this.addDrop(TinkerSmeltery.searedDrain.get());
    this.addDrop(TinkerSmeltery.searedChute.get());
    this.addDrop(TinkerSmeltery.searedDuct.get());

    for (SearedTankBlock.TankType type : SearedTankBlock.TankType.values()) {
      this.addDrop(TinkerSmeltery.searedTank.get(type), (block) -> droppingWithFunctions(block, (builder) -> {
        return builder.apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
          .apply(CopyNbtLootFunction.builder(CopyNbtLootFunction.Source.BLOCK_ENTITY).withOperation(Tags.TANK, Tags.TANK));
      }));
    }

    // fluid
    this.addDrop(TinkerSmeltery.searedFaucet.get());
    this.addDrop(TinkerSmeltery.searedChannel.get());

    // casting
    this.addDrop(TinkerSmeltery.castingBasin.get());
    this.addDrop(TinkerSmeltery.castingTable.get());
  }

  /*
   * Utils
   */

  private static LootTable.Builder dropSapling(Block blockIn, Block saplingIn, float... fortuneIn) {
    return dropsWithSilkTouchOrShears(blockIn, addSurvivesExplosionCondition(blockIn, ItemEntry.builder(saplingIn)).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, fortuneIn)));
  }

  private static LootTable.Builder randomDropSlimeBallOrSapling(SlimeGrassBlock.FoliageType foliageType, Block blockIn, Block sapling, float... fortuneIn) {
    SlimeType slime = SlimeType.EARTH;
    switch (foliageType) {
      case ICHOR: case BLOOD:
        slime = SlimeType.ICHOR;
        break;
      case SKY:
        slime = SlimeType.SKY;
        break;
      case ENDER:
        slime = SlimeType.ENDER;
        break;
    }
    return dropSapling(blockIn, sapling, fortuneIn)
      .pool(LootPool.builder()
                           .rolls(ConstantLootTableRange.create(1))
                           .conditionally(WITHOUT_SILK_TOUCH_NOR_SHEARS)
                           .with(addSurvivesExplosionCondition(blockIn, ItemEntry.builder(TinkerCommons.slimeball.get(slime)))
                                       .conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 1/50f, 1/45f, 1/40f, 1/30f, 1/20f))));

  }

  private static LootTable.Builder droppingWithFunctions(Block block, Function<ItemEntry.Builder<?>,ItemEntry.Builder<?>> mapping) {
    return LootTable.builder().pool(addSurvivesExplosionCondition(block, LootPool.builder().rolls(ConstantLootTableRange.create(1)).with(mapping.apply(ItemEntry.builder(block)))));
  }

  /**
   * Registers all loot tables for a building block object
   * @param object  Object instance
   */
  private void registerBuildingLootTables(BuildingBlockObject object) {
    this.addDrop(object.get());
    this.addDrop(object.getSlab(), BlockLootTableGenerator::slabDrops);
    this.addDrop(object.getStairs());
  }

  /**
   * Registers all loot tables for a wall building block object
   * @param object  Object instance
   */
  private void registerWallBuildingLootTables(WallBuildingBlockObject object) {
    registerBuildingLootTables(object);
    this.addDrop(object.getWall());
  }
}
