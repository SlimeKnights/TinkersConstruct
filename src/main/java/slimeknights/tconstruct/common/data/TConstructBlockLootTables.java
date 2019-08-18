package slimeknights.tconstruct.common.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.conditions.TableBonus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerPulseIds;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static slimeknights.tconstruct.shared.TinkerCommons.*;
import static slimeknights.tconstruct.world.TinkerWorld.*;

public class TConstructBlockLootTables extends BlockLootTables {

  private final Map<ResourceLocation, LootTable.Builder> loot_tables = Maps.newHashMap();

  private Set<Block> knownBlocks = new HashSet<>();

  private static LootTable.Builder dropSapling(Block blockIn, Block saplingIn, float... fortuneIn) {
    return func_218535_c(blockIn, func_218560_a(blockIn, ItemLootEntry.builder(saplingIn)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, fortuneIn)));
  }

  private static LootTable.Builder randomDropPurpleSlimeBall(Block blockIn, Block saplingIn, float... fortuneIn) {
    return dropSapling(blockIn, saplingIn, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(field_218577_e).addEntry(func_218560_a(blockIn, ItemLootEntry.builder(purple_slime_ball)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
  }

  private static LootTable.Builder randomDropBlueOrGreenSlimeBall(Block blockIn, Block saplingIn, float... fortuneIn) {
    return dropSapling(blockIn, saplingIn, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(field_218577_e).addEntry(AlternativesLootEntry.func_216149_a(func_218560_a(blockIn, ItemLootEntry.builder(blue_slime_ball)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)), func_218560_a(blockIn, ItemLootEntry.builder(Items.SLIME_BALL)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)))));
  }

  private void addCommon() {
    this.func_218492_c(grout);
    this.func_218492_c(slimy_mud_green);
    this.func_218492_c(slimy_mud_blue);
    this.func_218492_c(graveyard_soil);
    this.func_218492_c(consecrated_soil);
    this.func_218492_c(slimy_mud_magma);

    this.func_218492_c(blue_slime);
    this.func_218492_c(purple_slime);
    this.func_218492_c(blood_slime);
    this.func_218492_c(magma_slime);
    this.func_218492_c(pink_slime);

    this.func_218492_c(congealed_green_slime);
    this.func_218492_c(congealed_blue_slime);
    this.func_218492_c(congealed_purple_slime);
    this.func_218492_c(congealed_blood_slime);
    this.func_218492_c(congealed_magma_slime);
    this.func_218492_c(congealed_pink_slime);

    this.func_218492_c(cobalt_ore);
    this.func_218492_c(ardite_ore);

    this.func_218492_c(lavawood);
    this.func_218492_c(firewood);

    this.func_218492_c(mud_bricks);

    this.func_218492_c(clear_glass);

    this.func_218492_c(white_clear_stained_glass);
    this.func_218492_c(orange_clear_stained_glass);
    this.func_218492_c(magenta_clear_stained_glass);
    this.func_218492_c(light_blue_clear_stained_glass);
    this.func_218492_c(yellow_clear_stained_glass);
    this.func_218492_c(lime_clear_stained_glass);
    this.func_218492_c(pink_clear_stained_glass);
    this.func_218492_c(gray_clear_stained_glass);
    this.func_218492_c(light_gray_clear_stained_glass);
    this.func_218492_c(cyan_clear_stained_glass);
    this.func_218492_c(purple_clear_stained_glass);
    this.func_218492_c(blue_clear_stained_glass);
    this.func_218492_c(brown_clear_stained_glass);
    this.func_218492_c(green_clear_stained_glass);
    this.func_218492_c(red_clear_stained_glass);
    this.func_218492_c(black_clear_stained_glass);

    this.registerLootTable(mud_bricks_slab, BlockLootTables::func_218513_d);
    this.registerLootTable(lavawood_slab, BlockLootTables::func_218513_d);
    this.registerLootTable(firewood_slab, BlockLootTables::func_218513_d);

    this.func_218492_c(mud_bricks_stairs);
    this.func_218492_c(firewood_stairs);
    this.func_218492_c(lavawood_stairs);

    this.func_218492_c(cobalt_block);
    this.func_218492_c(ardite_block);
    this.func_218492_c(manyullyn_block);
    this.func_218492_c(knightslime_block);
    this.func_218492_c(pigiron_block);
    this.func_218492_c(alubrass_block);
    this.func_218492_c(silky_jewel_block);

    //this.registerLootTable(Blocks.GRASS_BLOCK, (p_218529_0_) -> {
    //  return func_218515_b(p_218529_0_, Blocks.DIRT);
    //});

    /*this.registerLootTable(Blocks.SNOW, (p_218508_0_) -> {
      return LootTable.builder().addLootPool(LootPool.builder().acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS))
              .addEntry(AlternativesLootEntry.func_216149_a(
                      AlternativesLootEntry.func_216149_a(ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 1))
                              , ((StandaloneLootEntry.Builder) ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 2))).acceptFunction(SetCount.func_215932_a(ConstantRange.of(2))), ((StandaloneLootEntry.Builder) ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 3))).acceptFunction(SetCount.func_215932_a(ConstantRange.of(3))), ((StandaloneLootEntry.Builder) ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 4))).acceptFunction(SetCount.func_215932_a(ConstantRange.of(4))),
                      ((StandaloneLootEntry.Builder) ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 5))).acceptFunction(SetCount.func_215932_a(ConstantRange.of(5))), ((StandaloneLootEntry.Builder) ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 6))).acceptFunction(SetCount.func_215932_a(ConstantRange.of(6))), ((StandaloneLootEntry.Builder) ItemLootEntry.builder(Items.SNOWBALL).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 7))).acceptFunction(SetCount.func_215932_a(ConstantRange.of(7))), ItemLootEntry.builder(Items.SNOWBALL).acceptFunction(SetCount.func_215932_a(ConstantRange.of(8)))).acceptCondition(field_218574_b),
                      AlternativesLootEntry
                      .func_216149_a(ItemLootEntry.builder(p_218508_0_).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 1)), ItemLootEntry.builder(p_218508_0_).acceptFunction(SetCount.func_215932_a(ConstantRange.of(2))).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 2)), ItemLootEntry.builder(p_218508_0_).acceptFunction(SetCount.func_215932_a(ConstantRange.of(3))).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 3)), ItemLootEntry.builder(p_218508_0_).acceptFunction(SetCount.func_215932_a(ConstantRange.of(4))).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 4)), ItemLootEntry.builder(p_218508_0_).acceptFunction(SetCount.func_215932_a(ConstantRange.of(5))).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 5)),
                              ItemLootEntry.builder(p_218508_0_).acceptFunction(SetCount.func_215932_a(ConstantRange.of(6))).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 6)), ItemLootEntry.builder(p_218508_0_).acceptFunction(SetCount.func_215932_a(ConstantRange.of(7))).acceptCondition(BlockStateProperty.builder(p_218508_0_).with(SnowBlock.LAYERS, 7)), ItemLootEntry.builder(Blocks.SNOW_BLOCK)))));
    });*/
  }

  private void addWorld() {
    this.func_218492_c(green_slime_dirt);
    this.func_218492_c(blue_slime_dirt);
    this.func_218492_c(purple_slime_dirt);
    this.func_218492_c(magma_slime_dirt);

    this.registerLootTable(blue_vanilla_slime_grass, (block) -> {
      return func_218515_b(block, Blocks.DIRT);
    });
    this.registerLootTable(purple_vanilla_slime_grass, (block) -> {
      return func_218515_b(block, Blocks.DIRT);
    });
    this.registerLootTable(orange_vanilla_slime_grass, (block) -> {
      return func_218515_b(block, Blocks.DIRT);
    });

    this.registerLootTable(blue_green_slime_grass, (block) -> {
      return func_218515_b(block, green_slime_dirt);
    });
    this.registerLootTable(purple_green_slime_grass, (block) -> {
      return func_218515_b(block, green_slime_dirt);
    });
    this.registerLootTable(orange_green_slime_grass, (block) -> {
      return func_218515_b(block, green_slime_dirt);
    });

    this.registerLootTable(blue_blue_slime_grass, (block) -> {
      return func_218515_b(block, blue_slime_dirt);
    });
    this.registerLootTable(purple_blue_slime_grass, (block) -> {
      return func_218515_b(block, blue_slime_dirt);
    });
    this.registerLootTable(orange_blue_slime_grass, (block) -> {
      return func_218515_b(block, blue_slime_dirt);
    });

    this.registerLootTable(blue_purple_slime_grass, (block) -> {
      return func_218515_b(block, purple_slime_dirt);
    });
    this.registerLootTable(purple_purple_slime_grass, (block) -> {
      return func_218515_b(block, purple_slime_dirt);
    });
    this.registerLootTable(orange_purple_slime_grass, (block) -> {
      return func_218515_b(block, purple_slime_dirt);
    });

    this.registerLootTable(blue_magma_slime_grass, (block) -> {
      return func_218515_b(block, magma_slime_dirt);
    });
    this.registerLootTable(purple_magma_slime_grass, (block) -> {
      return func_218515_b(block, magma_slime_dirt);
    });
    this.registerLootTable(orange_magma_slime_grass, (block) -> {
      return func_218515_b(block, magma_slime_dirt);
    });

    this.registerLootTable(blue_slime_leaves, (block) -> {
      return randomDropBlueOrGreenSlimeBall(block, blue_slime_sapling, field_218579_g);
    });

    this.registerLootTable(purple_slime_leaves, (block) -> {
      return randomDropPurpleSlimeBall(block, blue_slime_sapling, field_218579_g);
    });

    this.registerLootTable(orange_slime_leaves, (block) -> {
      return dropSapling(block, blue_slime_sapling, field_218579_g);
    });

    this.registerLootTable(blue_slime_fern, BlockLootTables::func_218486_d);
    this.registerLootTable(purple_slime_fern, BlockLootTables::func_218486_d);
    this.registerLootTable(orange_slime_fern, BlockLootTables::func_218486_d);

    this.registerLootTable(blue_slime_tall_grass, BlockLootTables::func_218486_d);
    this.registerLootTable(purple_slime_tall_grass, BlockLootTables::func_218486_d);
    this.registerLootTable(orange_slime_tall_grass, BlockLootTables::func_218486_d);

    this.func_218492_c(blue_slime_sapling);
    this.func_218492_c(orange_slime_sapling);
    this.func_218492_c(purple_slime_sapling);

    this.registerLootTable(purple_slime_vine, BlockLootTables::func_218486_d);
    this.registerLootTable(purple_slime_vine_middle, BlockLootTables::func_218486_d);
    this.registerLootTable(purple_slime_vine_end, BlockLootTables::func_218486_d);

    this.registerLootTable(blue_slime_vine, BlockLootTables::func_218486_d);
    this.registerLootTable(blue_slime_vine_middle, BlockLootTables::func_218486_d);
    this.registerLootTable(blue_slime_vine_end, BlockLootTables::func_218486_d);
  }

  @Override
  public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
    this.addCommon();

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID)) {
      this.addWorld();
    }

    Set<ResourceLocation> visited = Sets.newHashSet();

    for (Block block : this.knownBlocks) {
      ResourceLocation lootTable = block.getLootTable();
      if (lootTable != LootTables.EMPTY && visited.add(lootTable)) {
        LootTable.Builder builder = this.field_218581_i.remove(lootTable);
        if (builder == null) {
          throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", lootTable, block.getRegistryName()));
        }

        consumer.accept(lootTable, builder);
      }
    }

    if (!this.field_218581_i.isEmpty()) {
      throw new IllegalStateException("Created block loot tables for non-blocks: " + this.field_218581_i.keySet());
    }
  }

  @Override
  public void func_218564_a(Block blockIn, Block droppedBlockIn) {
    this.knownBlocks.add(blockIn);
    super.func_218564_a(blockIn, droppedBlockIn);
  }

  @Override
  public void func_218492_c(Block block) {
    this.knownBlocks.add(block);
    super.func_218492_c(block);
  }

  @Override
  public void registerLootTable(Block blockIn, Function<Block, LootTable.Builder> builderFunction) {
    this.knownBlocks.add(blockIn);
    super.registerLootTable(blockIn, builderFunction);
  }

  @Override
  public void registerLootTable(Block blockIn, LootTable.Builder builder) {
    this.knownBlocks.add(blockIn);
    super.registerLootTable(blockIn, builder);
  }
}
