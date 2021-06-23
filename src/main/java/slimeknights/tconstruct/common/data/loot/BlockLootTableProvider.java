package slimeknights.tconstruct.common.data.loot;

import com.google.common.collect.Maps;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.MatchTool;
import net.minecraft.loot.conditions.TableBonus;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.loot.RetexturedLootFunction;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.WoodBlockObject;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockLootTableProvider extends BlockLootTables {

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
    this.addFoundry();
  }

  private void addCommon() {
    this.registerBuildingLootTables(TinkerCommons.blazewood);
    this.registerBuildingLootTables(TinkerCommons.lavawood);

    this.registerDropSelfLootTable(TinkerModifiers.silkyJewelBlock.get());

    // ores
    this.registerDropSelfLootTable(TinkerMaterials.copper.get());
    this.registerDropSelfLootTable(TinkerMaterials.cobalt.get());
    // tier 3
    this.registerDropSelfLootTable(TinkerMaterials.slimesteel.get());
    this.registerDropSelfLootTable(TinkerMaterials.tinkersBronze.get());
    this.registerDropSelfLootTable(TinkerMaterials.roseGold.get());
    this.registerDropSelfLootTable(TinkerMaterials.pigIron.get());
    // tier 4
    this.registerDropSelfLootTable(TinkerMaterials.manyullyn.get());
    this.registerDropSelfLootTable(TinkerMaterials.hepatizon.get());
    this.registerDropSelfLootTable(TinkerMaterials.queensSlime.get());
    this.registerDropSelfLootTable(TinkerMaterials.soulsteel.get());
    // tier 5
    this.registerDropSelfLootTable(TinkerMaterials.knightslime.get());
  }

  private void addDecorative() {
    this.registerDropSelfLootTable(TinkerCommons.obsidianPane.get());
    this.registerDropSelfLootTable(TinkerCommons.clearGlass.get());
    this.registerDropSelfLootTable(TinkerCommons.clearGlassPane.get());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      this.registerDropSelfLootTable(TinkerCommons.clearStainedGlass.get(color));
      this.registerDropSelfLootTable(TinkerCommons.clearStainedGlassPane.get(color));
    }
    this.registerDropSelfLootTable(TinkerCommons.soulGlass.get());
    this.registerDropSelfLootTable(TinkerCommons.soulGlassPane.get());

    this.registerBuildingLootTables(TinkerCommons.mudBricks);
  }

  private void addTools() {
    // chests
    Function<Block, LootTable.Builder> addChest = block -> droppingWithFunctions(block, (builder) ->
      builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                    .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Items", "TinkerData.Items")));
    this.registerLootTable(TinkerTables.modifierChest.get(), addChest);
    this.registerLootTable(TinkerTables.partChest.get(), addChest);
    this.registerLootTable(TinkerTables.castChest.get(), addChest);

    // tables with legs
    Function<Block, LootTable.Builder> addTable = block -> droppingWithFunctions(block, (builder) ->
      builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY)).acceptFunction(RetexturedLootFunction::new));
    this.registerLootTable(TinkerTables.craftingStation.get(), addTable);
    this.registerLootTable(TinkerTables.partBuilder.get(), addTable);
    this.registerLootTable(TinkerTables.tinkerStation.get(), addTable);
    this.registerLootTable(TinkerTables.tinkersAnvil.get(), addTable);
    this.registerLootTable(TinkerTables.scorchedAnvil.get(), addTable);
  }

  private void addWorld() {
    this.registerDropSelfLootTable(TinkerWorld.cobaltOre.get());
    this.registerDropSelfLootTable(TinkerWorld.copperOre.get());

    // Only make loot table for our modded slime blocks
    for (SlimeType slime : SlimeType.TINKER) {
      this.registerDropSelfLootTable(TinkerWorld.slime.get(slime));
    }
    // congealed slime drops like clay blocks
    for (SlimeType slime : SlimeType.values()) {
      this.registerLootTable(TinkerWorld.congealedSlime.get(slime), block -> droppingWithSilkTouchOrRandomly(block, TinkerCommons.slimeball.get(slime), ConstantRange.of(4)));
    }
    for (SlimeType type : SlimeType.TRUE_SLIME) {
      this.registerDropSelfLootTable(TinkerWorld.slimeDirt.get(type));
    }

    for (SlimeType type : SlimeType.values()) {
      this.registerLootTable(TinkerWorld.vanillaSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, Blocks.DIRT));
      this.registerLootTable(TinkerWorld.earthSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, TinkerWorld.slimeDirt.get(SlimeType.EARTH)));
      this.registerLootTable(TinkerWorld.skySlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, TinkerWorld.slimeDirt.get(SlimeType.SKY)));
      this.registerLootTable(TinkerWorld.enderSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, TinkerWorld.slimeDirt.get(SlimeType.ENDER)));
      this.registerLootTable(TinkerWorld.ichorSlimeGrass.get(type), (block) -> droppingWithSilkTouch(block, TinkerWorld.slimeDirt.get(SlimeType.ICHOR)));
      this.registerLootTable(TinkerWorld.slimeLeaves.get(type), (block) -> randomDropSlimeBallOrSapling(type, block, TinkerWorld.slimeSapling.get(type), DEFAULT_SAPLING_DROP_RATES));
      this.registerLootTable(TinkerWorld.slimeFern.get(type), BlockLootTableProvider::onlyShearsTag);
      this.registerLootTable(TinkerWorld.slimeTallGrass.get(type), BlockLootTableProvider::onlyShearsTag);
      this.registerDropSelfLootTable(TinkerWorld.slimeSapling.get(type));
    }

    this.registerLootTable(TinkerWorld.skySlimeVine.get(), BlockLootTableProvider::onlyShearsTag);
    this.registerLootTable(TinkerWorld.enderSlimeVine.get(), BlockLootTableProvider::onlyShearsTag);

    this.registerWoodLootTables(TinkerWorld.greenheart);
    this.registerWoodLootTables(TinkerWorld.skyroot);
    this.registerWoodLootTables(TinkerWorld.bloodshroom);
  }

  private void addGadgets() {
    this.registerDropSelfLootTable(TinkerGadgets.punji.get());
    TinkerGadgets.cake.forEach(block -> this.registerLootTable(block, blockNoDrop()));
  }

  private void addSmeltery() {
    this.registerDropSelfLootTable(TinkerSmeltery.grout.get());
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

    Function<Block, LootTable.Builder> dropTank = block -> droppingWithFunctions(block, builder ->
      builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
             .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation(NBTTags.TANK, NBTTags.TANK)));
    TinkerSmeltery.searedTank.forEach(block -> this.registerLootTable(block, dropTank));
    this.registerLootTable(TinkerSmeltery.searedLantern.get(), dropTank);

    // fluid
    this.registerDropSelfLootTable(TinkerSmeltery.searedFaucet.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedChannel.get());

    // casting
    this.registerDropSelfLootTable(TinkerSmeltery.searedBasin.get());
    this.registerDropSelfLootTable(TinkerSmeltery.searedTable.get());
  }

  private void addFoundry() {
    this.registerDropSelfLootTable(TinkerSmeltery.netherGrout.get());
    // controller
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedAlloyer.get());
    this.registerDropSelfLootTable(TinkerSmeltery.foundryController.get());

    // smeltery component
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedStone.get());
    this.registerDropSelfLootTable(TinkerSmeltery.polishedScorchedStone.get());
    this.registerFenceBuildingLootTables(TinkerSmeltery.scorchedBricks);
    this.registerDropSelfLootTable(TinkerSmeltery.chiseledScorchedBricks.get());
    this.registerBuildingLootTables(TinkerSmeltery.scorchedRoad);
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedLadder.get());
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedGlass.get());
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedGlassPane.get());
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedDrain.get());
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedChute.get());
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedDuct.get());

    Function<Block, LootTable.Builder> dropTank = block -> droppingWithFunctions(block, builder ->
      builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
             .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation(NBTTags.TANK, NBTTags.TANK)));
    TinkerSmeltery.scorchedTank.forEach(block -> this.registerLootTable(block, dropTank));
    this.registerLootTable(TinkerSmeltery.scorchedLantern.get(), dropTank);

    // fluid
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedFaucet.get());
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedChannel.get());

    // casting
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedBasin.get());
    this.registerDropSelfLootTable(TinkerSmeltery.scorchedTable.get());
  }


  /*
   * Utils
   */

  private static final ILootCondition.IBuilder SILK_TOUCH = MatchTool.builder(ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.IntBound.atLeast(1))));
  private static final ILootCondition.IBuilder SHEARS = MatchTool.builder(ItemPredicate.Builder.create().tag(Tags.Items.SHEARS));
  private static final ILootCondition.IBuilder SILK_TOUCH_OR_SHEARS = SHEARS.alternative(SILK_TOUCH);

  protected static LootTable.Builder onlyShearsTag(IItemProvider item) {
    return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(SHEARS).addEntry(ItemLootEntry.builder(item)));
  }

  private static LootTable.Builder droppingSilkOrShearsTag(Block block, LootEntry.Builder<?> alternativeLootEntry) {
    return dropping(block, SILK_TOUCH_OR_SHEARS, alternativeLootEntry);
  }

  private static LootTable.Builder dropSapling(Block blockIn, Block saplingIn, float... fortuneIn) {
    return droppingSilkOrShearsTag(blockIn, withSurvivesExplosion(blockIn, ItemLootEntry.builder(saplingIn)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, fortuneIn)));
  }

  private static LootTable.Builder randomDropSlimeBallOrSapling(SlimeType foliageType, Block blockIn, Block sapling, float... fortuneIn) {
    SlimeType slime = foliageType;
    if (foliageType == SlimeType.BLOOD) {
      slime = SlimeType.ICHOR;
    }
    return dropSapling(blockIn, sapling, fortuneIn)
      .addLootPool(LootPool.builder()
                           .rolls(ConstantRange.of(1))
                           .acceptCondition(NOT_SILK_TOUCH_OR_SHEARS)
                           .addEntry(withSurvivesExplosion(blockIn, ItemLootEntry.builder(TinkerCommons.slimeball.get(slime)))
                                       .acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 1/50f, 1/45f, 1/40f, 1/30f, 1/20f))));

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

  /**
   * Registers all loot tables for a fence building block object
   * @param object  Object instance
   */
  private void registerFenceBuildingLootTables(FenceBuildingBlockObject object) {
    registerBuildingLootTables(object);
    this.registerDropSelfLootTable(object.getFence());
  }

  /** Adds all loot tables relevant to the given wood object */
  private void registerWoodLootTables(WoodBlockObject object) {
    registerBuildingLootTables(object);
    // basic
    this.registerDropSelfLootTable(object.getLog());
    this.registerDropSelfLootTable(object.getStrippedLog());
    this.registerDropSelfLootTable(object.getWood());
    this.registerDropSelfLootTable(object.getStrippedWood());
    // door
    this.registerDropSelfLootTable(object.getFence());
    this.registerDropSelfLootTable(object.getFenceGate());
    this.registerLootTable(object.getDoor(), BlockLootTables::registerDoor);
    this.registerDropSelfLootTable(object.getTrapdoor());
    // redstone
    this.registerDropSelfLootTable(object.getPressurePlate());
    this.registerDropSelfLootTable(object.getButton());
    // sign
    //this.registerDropSelfLootTable(object.getSign());
  }
}
