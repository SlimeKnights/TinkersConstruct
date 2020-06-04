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
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.GadgetBlocks;
import slimeknights.tconstruct.blocks.SmelteryBlocks;
import slimeknights.tconstruct.blocks.TableBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.items.FoodItems;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock;
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
    this.registerDropSelfLootTable(CommonBlocks.grout.get());
    this.registerDropSelfLootTable(CommonBlocks.slimy_mud_green.get());
    this.registerDropSelfLootTable(CommonBlocks.slimy_mud_blue.get());
    this.registerDropSelfLootTable(CommonBlocks.graveyard_soil.get());
    this.registerDropSelfLootTable(CommonBlocks.consecrated_soil.get());
    this.registerDropSelfLootTable(CommonBlocks.slimy_mud_magma.get());

    this.registerBuildingLootTables(CommonBlocks.firewood);
    this.registerBuildingLootTables(CommonBlocks.lavawood);

    this.registerDropSelfLootTable(CommonBlocks.cobalt_block.get());
    this.registerDropSelfLootTable(CommonBlocks.ardite_block.get());
    this.registerDropSelfLootTable(CommonBlocks.manyullyn_block.get());
    this.registerDropSelfLootTable(CommonBlocks.knightslime_block.get());
    this.registerDropSelfLootTable(CommonBlocks.pigiron_block.get());
    this.registerDropSelfLootTable(CommonBlocks.alubrass_block.get());
    this.registerDropSelfLootTable(CommonBlocks.silky_jewel_block.get());
  }

  private void addDecorative() {
    this.registerDropSelfLootTable(DecorativeBlocks.clear_glass.get());

    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      this.registerDropSelfLootTable(DecorativeBlocks.clear_stained_glass.get(color));
    }

    this.registerBuildingLootTables(DecorativeBlocks.mud_bricks);
    this.registerBuildingLootTables(DecorativeBlocks.dried_clay);
    this.registerBuildingLootTables(DecorativeBlocks.dried_clay_bricks);
  }

  private void addTools() {
    this.registerDropSelfLootTable(TableBlocks.crafting_station.get());
    for (Block block : new Block[] {TableBlocks.pattern_chest.get(), TableBlocks.part_chest.get()}) {
      this.registerLootTable(block, droppingWithFunctions(block, (builder) -> {
        return builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
                 .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).replaceOperation("Items", "TinkerData.Items"));
      }));
    }
    for (Block block : new Block[] {TableBlocks.part_builder.get()}) {
      this.registerLootTable(block, droppingWithFunctions(block, (builder) -> {
        return builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY))
               .acceptFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY).addOperation("LegTexture", "TinkerData.LegTexture", Action.REPLACE));
      }));
    }
    this.registerLootTable(TableBlocks.crafting_station.get(), (block) -> {
      return droppingWithFunctions(block, (builder) -> {
        return builder.acceptFunction(CopyName.builder(CopyName.Source.BLOCK_ENTITY));
      });
    });
  }

  private void addWorld() {
    this.registerDropSelfLootTable(WorldBlocks.cobalt_ore.get());
    this.registerDropSelfLootTable(WorldBlocks.ardite_ore.get());

    // Only make loot table for our modded slime blocks
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.TINKER) {
      this.registerDropSelfLootTable(WorldBlocks.slime.get(slime));
    }
    // Make loot table for all congealed_slime variants
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      this.registerDropSelfLootTable(WorldBlocks.congealed_slime.get(slime));
    }

    for (SlimeDirtBlock.SlimeDirtType dirtType : SlimeDirtBlock.SlimeDirtType.values()) {
      this.registerDropSelfLootTable(WorldBlocks.slime_dirt.get(dirtType));
    }

    for (SlimeGrassBlock.FoliageType type : SlimeGrassBlock.FoliageType.values()) {
      this.registerLootTable(WorldBlocks.vanilla_slime_grass.get(type), (block) -> droppingWithSilkTouch(block, Blocks.DIRT));
      this.registerLootTable(WorldBlocks.green_slime_grass.get(type), (block) -> droppingWithSilkTouch(block, WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.GREEN)));
      this.registerLootTable(WorldBlocks.blue_slime_grass.get(type), (block) -> droppingWithSilkTouch(block, WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.BLUE)));
      this.registerLootTable(WorldBlocks.purple_slime_grass.get(type), (block) -> droppingWithSilkTouch(block, WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.PURPLE)));
      this.registerLootTable(WorldBlocks.magma_slime_grass.get(type), (block) -> droppingWithSilkTouch(block, WorldBlocks.slime_dirt.get(SlimeDirtBlock.SlimeDirtType.MAGMA)));
      this.registerLootTable(WorldBlocks.slime_leaves.get(type), (block) -> randomDropSlimeBallOrSapling(type, block, WorldBlocks.slime_sapling.get(type), DEFAULT_SAPLING_DROP_RATES));
      this.registerLootTable(WorldBlocks.slime_fern.get(type), BlockLootTables::onlyWithShears);
      this.registerLootTable(WorldBlocks.slime_tall_grass.get(type), BlockLootTables::onlyWithShears);
      this.registerDropSelfLootTable(WorldBlocks.slime_sapling.get(type));
    }

    this.registerLootTable(WorldBlocks.purple_slime_vine.get(), BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.purple_slime_vine_middle.get(), BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.purple_slime_vine_end.get(), BlockLootTables::onlyWithShears);

    this.registerLootTable(WorldBlocks.blue_slime_vine.get(), BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.blue_slime_vine_middle.get(), BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.blue_slime_vine_end.get(), BlockLootTables::onlyWithShears);
  }

  private void addGadgets() {
    this.registerDropSelfLootTable(GadgetBlocks.stone_ladder.get());

    this.registerDropSelfLootTable(GadgetBlocks.stone_torch.get());

    this.registerDropping(GadgetBlocks.wall_stone_torch.get(), GadgetBlocks.stone_torch.get());

    this.registerDropSelfLootTable(GadgetBlocks.punji.get());

    this.registerDropSelfLootTable(GadgetBlocks.wooden_rail.get());
    this.registerDropSelfLootTable(GadgetBlocks.wooden_dropper_rail.get());
  }

  private void addSmeltery() {
    this.registerBuildingLootTables(SmelteryBlocks.seared_stone);
    this.registerBuildingLootTables(SmelteryBlocks.seared_cobble);
    this.registerBuildingLootTables(SmelteryBlocks.seared_paver);
    this.registerBuildingLootTables(SmelteryBlocks.seared_stone);
    this.registerBuildingLootTables(SmelteryBlocks.seared_bricks);
    this.registerBuildingLootTables(SmelteryBlocks.seared_cracked_bricks);
    this.registerBuildingLootTables(SmelteryBlocks.seared_fancy_bricks);
    this.registerBuildingLootTables(SmelteryBlocks.seared_square_bricks);
    this.registerBuildingLootTables(SmelteryBlocks.seared_small_bricks);
    this.registerBuildingLootTables(SmelteryBlocks.seared_triangle_bricks);
    this.registerBuildingLootTables(SmelteryBlocks.seared_creeper);
    this.registerBuildingLootTables(SmelteryBlocks.seared_road);
    this.registerBuildingLootTables(SmelteryBlocks.seared_tile);
    this.registerDropSelfLootTable(SmelteryBlocks.seared_glass.get());
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
        return dropSapling(blockIn, sapling, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withSurvivesExplosion(blockIn, ItemLootEntry.builder(FoodItems.slime_ball.get(SlimeBlock.SlimeType.PURPLE))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
      case BLUE:
        return dropSapling(blockIn, sapling, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(AlternativesLootEntry.builder(withSurvivesExplosion(blockIn, ItemLootEntry.builder(FoodItems.slime_ball.get(SlimeBlock.SlimeType.BLUE))).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)), withSurvivesExplosion(blockIn, ItemLootEntry.builder(Items.SLIME_BALL)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)))));
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
