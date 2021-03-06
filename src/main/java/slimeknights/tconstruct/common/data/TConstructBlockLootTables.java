package slimeknights.tconstruct.common.data;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.AlternativesLootEntry;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.TableBonus;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.CopyNbt.Action;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.StickySlimeBlock;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TConstructBlockLootTables extends BlockLootTables {

  private final Map<ResourceLocation, LootTable.Builder> loot_tables = Maps.newHashMap();

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
    this.registerDropSelfLootTable(TinkerSmeltery.grout.get());
    this.registerDropSelfLootTable(TinkerModifiers.graveyardSoil.get());
    this.registerDropSelfLootTable(TinkerModifiers.consecratedSoil.get());

    this.registerBuildingLootTables(TinkerCommons.firewood);
    this.registerBuildingLootTables(TinkerCommons.lavawood);

    this.registerDropSelfLootTable(TinkerModifiers.silkyJewelBlock.get());

    // ores
    this.registerDropSelfLootTable(TinkerMaterials.copper.get());
    this.registerDropSelfLootTable(TinkerMaterials.cobalt.get());
    // tier 3
    this.registerDropSelfLootTable(TinkerMaterials.slimesteel.get());
    this.registerDropSelfLootTable(TinkerMaterials.tinkersBronze.get());
    this.registerDropSelfLootTable(TinkerMaterials.roseGold.get());
    this.registerDropSelfLootTable(TinkerMaterials.pigiron.get());
    // tier 4
    this.registerDropSelfLootTable(TinkerMaterials.manyullyn.get());
    this.registerDropSelfLootTable(TinkerMaterials.hepatizon.get());
    this.registerDropSelfLootTable(TinkerMaterials.queensSlime.get());
    this.registerDropSelfLootTable(TinkerMaterials.soulsteel.get());
    // tier 5
    this.registerDropSelfLootTable(TinkerMaterials.knightslime.get());
  }

  private void addDecorative() {
    this.registerDropSelfLootTable(TinkerCommons.clearGlass.get());
    this.registerDropSelfLootTable(TinkerCommons.clearGlassPane.get());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      this.registerDropSelfLootTable(TinkerCommons.clearStainedGlass.get(color));
      this.registerDropSelfLootTable(TinkerCommons.clearStainedGlassPane.get(color));
    }
    this.registerDropSelfLootTable(TinkerCommons.soulGlass.get());
    this.registerDropSelfLootTable(TinkerCommons.soulGlassPane.get());

    this.registerBuildingLootTables(TinkerCommons.mudBricks);
    this.registerBuildingLootTables(TinkerCommons.driedClay);
    this.registerBuildingLootTables(TinkerCommons.driedClayBricks);
  }

  private void addTools() {
    this.registerDropSelfLootTable(TinkerTables.craftingStation.get());

    // chests
    Function<Block, LootTable.Builder> addChest = block -> droppingWithFunctions(block, (builder) ->
      builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                    .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Items", "TinkerData.Items")));
    this.registerLootTable(TinkerTables.modifierChest.get(), addChest);
    this.registerLootTable(TinkerTables.partChest.get(), addChest);
    this.registerLootTable(TinkerTables.castChest.get(), addChest);

    // tables with legs
    Function<Block, LootTable.Builder> addTable = block -> droppingWithFunctions(block, (builder) ->
      builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                    .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).addOperation("LegTexture", "TinkerData.LegTexture", Action.REPLACE)));
    this.registerLootTable(TinkerTables.partBuilder.get(), addTable);
    this.registerLootTable(TinkerTables.tinkerStation.get(), addTable);

    // normal tables
    this.registerLootTable(TinkerTables.craftingStation.get(), block ->
      droppingWithFunctions(block, (builder) -> builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))));
  }

  private void addWorld() {
    this.registerDropSelfLootTable(TinkerWorld.cobaltOre.get());
    this.registerDropSelfLootTable(TinkerWorld.copperOre.get());

    // Only make loot table for our modded slime blocks
    for (StickySlimeBlock.SlimeType slime : StickySlimeBlock.SlimeType.TINKER) {
      this.registerDropSelfLootTable(TinkerWorld.slime.get(slime));
    }
    // Make loot table for all congealed_slime variants
    for (StickySlimeBlock.SlimeType slime : StickySlimeBlock.SlimeType.values()) {
      this.registerDropSelfLootTable(TinkerWorld.congealedSlime.get(slime));
    }

    for (SlimeDirtBlock.SlimeDirtType dirtType : SlimeDirtBlock.SlimeDirtType.values()) {
      this.registerDropSelfLootTable(TinkerWorld.slimeDirt.get(dirtType));
    }

    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      this.registerLootTable(TinkerWorld.vanillaSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, Blocks.DIRT));
      this.registerLootTable(TinkerWorld.greenSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, TinkerWorld.slimeDirt.get(SlimeDirtBlock.SlimeDirtType.GREEN)));
      this.registerLootTable(TinkerWorld.blueSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, TinkerWorld.slimeDirt.get(SlimeDirtBlock.SlimeDirtType.BLUE)));
      this.registerLootTable(TinkerWorld.purpleSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, TinkerWorld.slimeDirt.get(SlimeDirtBlock.SlimeDirtType.PURPLE)));
      this.registerLootTable(TinkerWorld.magmaSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, TinkerWorld.slimeDirt.get(SlimeDirtBlock.SlimeDirtType.MAGMA)));
      this.registerLootTable(TinkerWorld.slimeLeaves.get(type), (block) -> randomDropSlimeBallOrSapling(type, block, TinkerWorld.slimeSapling.get(type), DEFAULT_SAPLING_DROP_RATES));
      this.registerLootTable(TinkerWorld.slimeFern.get(type), BlockLootTables::onlyWithShears);
      this.registerLootTable(TinkerWorld.slimeTallGrass.get(type), BlockLootTables::onlyWithShears);
      this.registerDropSelfLootTable(TinkerWorld.slimeSapling.get(type));
    }

    this.registerLootTable(TinkerWorld.purpleSlimeVine.get(), BlockLootTables::onlyWithShears);

    this.registerLootTable(TinkerWorld.blueSlimeVine.get(), BlockLootTables::onlyWithShears);
  }

  private void addGadgets() {
    this.registerDropSelfLootTable(TinkerGadgets.stoneLadder.get());

    this.registerDropSelfLootTable(TinkerGadgets.stoneTorch.get());

    this.registerDropping(TinkerGadgets.wallStoneTorch.get(), TinkerGadgets.stoneTorch.get());

    this.registerDropSelfLootTable(TinkerGadgets.punji.get());

    this.registerDropSelfLootTable(TinkerGadgets.woodenRail.get());
    this.registerDropSelfLootTable(TinkerGadgets.woodenDropperRail.get());
  }

  private void addSmeltery() {
    // controller
    this.registerDropSelfLootTable(TinkerSmeltery.searedMelter.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedHeater.get());
    this.registerDropSelfLootTable(TinkerSmeltery.smelteryController.get());

    // smeltery component
    this.registerBuildingLootTables(TinkerSmeltery.searedStone);
    this.registerWallBuildingLootTables(TinkerSmeltery.searedCobble);
    this.registerBuildingLootTables(TinkerSmeltery.searedPaver);
    this.registerWallBuildingLootTables(TinkerSmeltery.searedBricks);
    this.registerDropSelfLootTable(TinkerSmeltery.searedCrackedBricks.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedFancyBricks.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedTriangleBricks.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedLadder.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedGlass.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedGlassPane.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedDrain.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedChute.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedDuct.get());

    for (SearedTankBlock.TankType type : SearedTankBlock.TankType.values()) {
      this.registerLootTable(TinkerSmeltery.searedTank.get(type), (block) -> droppingWithFunctions(block, (builder) -> {
        return builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
          .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation(Tags.TANK, Tags.TANK));
      }));
    }

    // fluid
    this.registerDropSelfLootTable(TinkerSmeltery.searedFaucet.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedChannel.get());

    // casting
    this.registerDropSelfLootTable(TinkerSmeltery.castingBasin.get());
    this.registerDropSelfLootTable(TinkerSmeltery.castingTable.get());
  }

  /*
   * Utils
   */

  private static LootTable.Builder dropSapling(Block blockIn, Block saplingIn, float... fortuneIn) {
    return droppingWithSilkTouchOrShears(blockIn, withSurvivesExplosion(blockIn, ItemLootEntry.builder(saplingIn)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, fortuneIn)));
  }

  private static LootTable.Builder randomDropSlimeBallOrSapling(SlimeGrassBlock.FoliageType foliageType, Block blockIn, Block sapling, float... fortuneIn) {
    switch (foliageType) {
      case PURPLE:
        return dropSapling(blockIn, sapling, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withSurvivesExplosion(blockIn, ItemLootEntry.builder(TinkerCommons.slimeball.get(StickySlimeBlock.SlimeType.PURPLE))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
      case BLUE:
        return dropSapling(blockIn, sapling, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(AlternativesLootEntry.builder(withSurvivesExplosion(blockIn, ItemLootEntry.builder(TinkerCommons.slimeball.get(StickySlimeBlock.SlimeType.BLUE))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)), withSurvivesExplosion(blockIn, ItemLootEntry.builder(Items.SLIME_BALL)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)))));
      case ORANGE:
      default:
        return dropSapling(blockIn, sapling, fortuneIn);
    }
  }

  private static LootTable.Builder droppingWithFunctions(Block block, Function<ItemLootEntry.Builder<?>,ItemLootEntry.Builder<?>> mapping) {
    return LootTable.builder().addLootPool(withSurvivesExplosion(block, LootPool.builder().rolls(ConstantRange.of(1)).addEntry(mapping.apply(ItemLootEntry.builder(block)))));
  }

  /**
   * Registers all loot tables for a building block object
   * @param object  Object instance
   */
  private void registerBuildingLootTables(BuildingBlockObject object) {
    this.registerDropSelfLootTable(object.get());
    this.registerLootTable(object.getSlab(), BlockLootTables::droppingSlab);
    this.registerDropSelfLootTable(object.getStairs());
  }

  /**
   * Registers all loot tables for a wall building block object
   * @param object  Object instance
   */
  private void registerWallBuildingLootTables(WallBuildingBlockObject object) {
    registerBuildingLootTables(object);
    this.registerDropSelfLootTable(object.getWall());
  }
}
