package slimeknights.tconstruct.common.data;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.AlternativesLootEntry;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.TableBonus;
import net.minecraft.world.storage.loot.functions.CopyName;
import net.minecraft.world.storage.loot.functions.CopyNbt;
import net.minecraft.world.storage.loot.functions.CopyNbt.Action;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeDirtBlock;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TConstructBlockLootTables extends BlockLootTables {

  private final Map<ResourceLocation, LootTable.Builder> loot_tables = Maps.newHashMap();

  @Nonnull
  @Override
  protected Iterable<Block> getKnownBlocks() {
    return ForgeRegistries.BLOCKS.getValues().stream()
                                 .filter((block) -> TConstruct.modID.equals(block.getRegistryName().getNamespace()))
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
    this.registerDropSelfLootTable(TinkerModifiers.slimyMudGreen.get());
    this.registerDropSelfLootTable(TinkerModifiers.slimyMudBlue.get());
    this.registerDropSelfLootTable(TinkerModifiers.graveyardSoil.get());
    this.registerDropSelfLootTable(TinkerModifiers.consecratedSoil.get());
    this.registerDropSelfLootTable(TinkerModifiers.slimyMudMagma.get());

    this.registerBuildingLootTables(TinkerCommons.firewood);
    this.registerBuildingLootTables(TinkerCommons.lavawood);

    this.registerDropSelfLootTable(TinkerMaterials.cobaltBlock.get());
    this.registerDropSelfLootTable(TinkerMaterials.arditeBlock.get());
    this.registerDropSelfLootTable(TinkerMaterials.manyullynBlock.get());
    this.registerDropSelfLootTable(TinkerMaterials.knightSlimeBlock.get());
    this.registerDropSelfLootTable(TinkerMaterials.pigironBlock.get());
    this.registerDropSelfLootTable(TinkerMaterials.copperBlock.get());
    this.registerDropSelfLootTable(TinkerMaterials.roseGoldBlock.get());
    this.registerDropSelfLootTable(TinkerModifiers.silkyJewelBlock.get());
  }

  private void addDecorative() {
    this.registerDropSelfLootTable(TinkerCommons.clearGlass.get());
    this.registerDropSelfLootTable(TinkerCommons.clearGlassPane.get());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      this.registerDropSelfLootTable(TinkerCommons.clearStainedGlass.get(color));
      this.registerDropSelfLootTable(TinkerCommons.clearStainedGlassPane.get(color));
    }

    this.registerBuildingLootTables(TinkerCommons.mudBricks);
    this.registerBuildingLootTables(TinkerCommons.driedClay);
    this.registerBuildingLootTables(TinkerCommons.driedClayBricks);
  }

  private void addTools() {
    this.registerDropSelfLootTable(TinkerTables.craftingStation.get());
    for (Block block : new Block[] {TinkerTables.patternChest.get(), TinkerTables.partChest.get()}) {
      this.registerLootTable(block, droppingWithFunctions(block, (builder) -> {
        return builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                 .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Items", "TinkerData.Items"));
      }));
    }
    for (Block block : new Block[] {TinkerTables.partBuilder.get()}) {
      this.registerLootTable(block, droppingWithFunctions(block, (builder) -> {
        return builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
               .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).addOperation("LegTexture", "TinkerData.LegTexture", Action.REPLACE));
      }));
    }
    this.registerLootTable(TinkerTables.craftingStation.get(), (block) -> {
      return droppingWithFunctions(block, (builder) -> {
        return builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY));
      });
    });
  }

  private void addWorld() {
    this.registerDropSelfLootTable(TinkerWorld.cobaltOre.get());
    this.registerDropSelfLootTable(TinkerWorld.arditeOre.get());

    this.registerDropSelfLootTable(TinkerWorld.copperOre.get());

    // Only make loot table for our modded slime blocks
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.TINKER) {
      this.registerDropSelfLootTable(TinkerWorld.slime.get(slime));
    }
    // Make loot table for all congealed_slime variants
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
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
    this.registerLootTable(TinkerWorld.purpleSlimeVineMiddle.get(), BlockLootTables::onlyWithShears);
    this.registerLootTable(TinkerWorld.purpleSlimeVineEnd.get(), BlockLootTables::onlyWithShears);

    this.registerLootTable(TinkerWorld.blueSlimeVine.get(), BlockLootTables::onlyWithShears);
    this.registerLootTable(TinkerWorld.blueSlimeVineMiddle.get(), BlockLootTables::onlyWithShears);
    this.registerLootTable(TinkerWorld.blueSlimeVineEnd.get(), BlockLootTables::onlyWithShears);
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
    this.registerBuildingLootTables(TinkerSmeltery.searedStone);
    this.registerBuildingLootTables(TinkerSmeltery.searedCobble);
    this.registerBuildingLootTables(TinkerSmeltery.searedPaver);
    this.registerBuildingLootTables(TinkerSmeltery.searedStone);
    this.registerBuildingLootTables(TinkerSmeltery.searedBricks);
    this.registerBuildingLootTables(TinkerSmeltery.searedCrackedBricks);
    this.registerBuildingLootTables(TinkerSmeltery.searedFancyBricks);
    this.registerBuildingLootTables(TinkerSmeltery.searedSquareBricks);
    this.registerBuildingLootTables(TinkerSmeltery.searedSmallBricks);
    this.registerBuildingLootTables(TinkerSmeltery.searedTriangleBricks);
    this.registerBuildingLootTables(TinkerSmeltery.searedCreeper);
    this.registerBuildingLootTables(TinkerSmeltery.searedRoad);
    this.registerBuildingLootTables(TinkerSmeltery.searedTile);
    this.registerDropSelfLootTable(TinkerSmeltery.searedGlass.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedGlassPane.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedMelter.get());

    for (SearedTankBlock.TankType type : SearedTankBlock.TankType.values()) {
      this.registerLootTable(TinkerSmeltery.searedTank.get(type), (block) -> droppingWithFunctions(block, (builder) -> {
        return builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
          .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation(Tags.TANK, Tags.TANK));
      }));
    }

    this.registerDropSelfLootTable(TinkerSmeltery.searedFaucet.get());
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
        return dropSapling(blockIn, sapling, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withSurvivesExplosion(blockIn, ItemLootEntry.builder(TinkerCommons.slimeball.get(SlimeBlock.SlimeType.PURPLE))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
      case BLUE:
        return dropSapling(blockIn, sapling, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(AlternativesLootEntry.builder(withSurvivesExplosion(blockIn, ItemLootEntry.builder(TinkerCommons.slimeball.get(SlimeBlock.SlimeType.BLUE))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)), withSurvivesExplosion(blockIn, ItemLootEntry.builder(Items.SLIME_BALL)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)))));
      case ORANGE:
      default:
        return dropSapling(blockIn, sapling, fortuneIn);
    }
  }

  private static LootTable.Builder droppingWithFunctions(Block block, Function<ItemLootEntry.Builder,ItemLootEntry.Builder> mapping) {
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
}
