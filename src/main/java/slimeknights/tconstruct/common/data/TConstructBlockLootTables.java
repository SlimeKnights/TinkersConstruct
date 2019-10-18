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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.blocks.WorldBlocks;

import static slimeknights.tconstruct.blocks.GadgetBlocks.punji;
import static slimeknights.tconstruct.blocks.GadgetBlocks.stone_ladder;
import static slimeknights.tconstruct.blocks.GadgetBlocks.stone_torch;
import static slimeknights.tconstruct.blocks.GadgetBlocks.wall_stone_torch;
import static slimeknights.tconstruct.blocks.GadgetBlocks.wooden_dropper_rail;
import static slimeknights.tconstruct.blocks.GadgetBlocks.wooden_rail;

public class TConstructBlockLootTables extends BlockLootTables {

  private final Map<ResourceLocation, LootTable.Builder> loot_tables = Maps.newHashMap();

  private Set<Block> knownBlocks = new HashSet<>();

  private static LootTable.Builder dropSapling(Block blockIn, Block saplingIn, float... fortuneIn) {
    return func_218535_c(blockIn, func_218560_a(blockIn, ItemLootEntry.builder(saplingIn)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, fortuneIn)));
  }

  private static LootTable.Builder randomDropPurpleSlimeBall(Block blockIn, Block saplingIn, float... fortuneIn) {
    return dropSapling(blockIn, saplingIn, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(field_218577_e).addEntry(func_218560_a(blockIn, ItemLootEntry.builder(CommonItems.purple_slime_ball)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
  }

  private static LootTable.Builder randomDropBlueOrGreenSlimeBall(Block blockIn, Block saplingIn, float... fortuneIn) {
    return dropSapling(blockIn, saplingIn, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(field_218577_e).addEntry(AlternativesLootEntry.func_216149_a(func_218560_a(blockIn, ItemLootEntry.builder(CommonItems.blue_slime_ball)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)), func_218560_a(blockIn, ItemLootEntry.builder(Items.SLIME_BALL)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)))));
  }

  private void addCommon() {
    this.func_218492_c(CommonBlocks.grout);
    this.func_218492_c(CommonBlocks.slimy_mud_green);
    this.func_218492_c(CommonBlocks.slimy_mud_blue);
    this.func_218492_c(CommonBlocks.graveyard_soil);
    this.func_218492_c(CommonBlocks.consecrated_soil);
    this.func_218492_c(CommonBlocks.slimy_mud_magma);

    this.func_218492_c(WorldBlocks.blue_slime);
    this.func_218492_c(WorldBlocks.purple_slime);
    this.func_218492_c(WorldBlocks.blood_slime);
    this.func_218492_c(WorldBlocks.magma_slime);
    this.func_218492_c(WorldBlocks.pink_slime);

    this.func_218492_c(WorldBlocks.congealed_green_slime);
    this.func_218492_c(WorldBlocks.congealed_blue_slime);
    this.func_218492_c(WorldBlocks.congealed_purple_slime);
    this.func_218492_c(WorldBlocks.congealed_blood_slime);
    this.func_218492_c(WorldBlocks.congealed_magma_slime);
    this.func_218492_c(WorldBlocks.congealed_pink_slime);

    this.func_218492_c(CommonBlocks.cobalt_ore);
    this.func_218492_c(CommonBlocks.ardite_ore);

    this.func_218492_c(CommonBlocks.lavawood);
    this.func_218492_c(CommonBlocks.firewood);

    this.func_218492_c(DecorativeBlocks.mud_bricks);

    this.func_218492_c(DecorativeBlocks.clear_glass);

    this.func_218492_c(DecorativeBlocks.white_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.orange_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.magenta_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.light_blue_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.yellow_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.lime_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.pink_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.gray_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.light_gray_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.cyan_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.purple_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.blue_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.brown_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.green_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.red_clear_stained_glass);
    this.func_218492_c(DecorativeBlocks.black_clear_stained_glass);

    this.registerLootTable(DecorativeBlocks.mud_bricks_slab, BlockLootTables::func_218513_d);
    this.registerLootTable(CommonBlocks.lavawood_slab, BlockLootTables::func_218513_d);
    this.registerLootTable(CommonBlocks.firewood_slab, BlockLootTables::func_218513_d);

    this.func_218492_c(DecorativeBlocks.mud_bricks_stairs);
    this.func_218492_c(CommonBlocks.firewood_stairs);
    this.func_218492_c(CommonBlocks.lavawood_stairs);

    this.func_218492_c(CommonBlocks.cobalt_block);
    this.func_218492_c(CommonBlocks.ardite_block);
    this.func_218492_c(CommonBlocks.manyullyn_block);
    this.func_218492_c(CommonBlocks.knightslime_block);
    this.func_218492_c(CommonBlocks.pigiron_block);
    this.func_218492_c(CommonBlocks.alubrass_block);
    this.func_218492_c(CommonBlocks.silky_jewel_block);
  }

  private void addWorld() {
    this.func_218492_c(WorldBlocks.green_slime_dirt);
    this.func_218492_c(WorldBlocks.blue_slime_dirt);
    this.func_218492_c(WorldBlocks.purple_slime_dirt);
    this.func_218492_c(WorldBlocks.magma_slime_dirt);

    this.registerLootTable(WorldBlocks.blue_vanilla_slime_grass, (block) -> {
      return func_218515_b(block, Blocks.DIRT);
    });
    this.registerLootTable(WorldBlocks.purple_vanilla_slime_grass, (block) -> {
      return func_218515_b(block, Blocks.DIRT);
    });
    this.registerLootTable(WorldBlocks.orange_vanilla_slime_grass, (block) -> {
      return func_218515_b(block, Blocks.DIRT);
    });

    this.registerLootTable(WorldBlocks.blue_green_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.green_slime_dirt);
    });
    this.registerLootTable(WorldBlocks.purple_green_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.green_slime_dirt);
    });
    this.registerLootTable(WorldBlocks.orange_green_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.green_slime_dirt);
    });

    this.registerLootTable(WorldBlocks.blue_blue_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.blue_slime_dirt);
    });
    this.registerLootTable(WorldBlocks.purple_blue_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.blue_slime_dirt);
    });
    this.registerLootTable(WorldBlocks.orange_blue_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.blue_slime_dirt);
    });

    this.registerLootTable(WorldBlocks.blue_purple_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.purple_slime_dirt);
    });
    this.registerLootTable(WorldBlocks.purple_purple_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.purple_slime_dirt);
    });
    this.registerLootTable(WorldBlocks.orange_purple_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.purple_slime_dirt);
    });

    this.registerLootTable(WorldBlocks.blue_magma_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.magma_slime_dirt);
    });
    this.registerLootTable(WorldBlocks.purple_magma_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.magma_slime_dirt);
    });
    this.registerLootTable(WorldBlocks.orange_magma_slime_grass, (block) -> {
      return func_218515_b(block, WorldBlocks.magma_slime_dirt);
    });

    this.registerLootTable(WorldBlocks.blue_slime_leaves, (block) -> {
      return randomDropBlueOrGreenSlimeBall(block, WorldBlocks.blue_slime_sapling, field_218579_g);
    });

    this.registerLootTable(WorldBlocks.purple_slime_leaves, (block) -> {
      return randomDropPurpleSlimeBall(block, WorldBlocks.blue_slime_sapling, field_218579_g);
    });

    this.registerLootTable(WorldBlocks.orange_slime_leaves, (block) -> {
      return dropSapling(block, WorldBlocks.blue_slime_sapling, field_218579_g);
    });

    this.registerLootTable(WorldBlocks.blue_slime_fern, BlockLootTables::func_218486_d);
    this.registerLootTable(WorldBlocks.purple_slime_fern, BlockLootTables::func_218486_d);
    this.registerLootTable(WorldBlocks.orange_slime_fern, BlockLootTables::func_218486_d);

    this.registerLootTable(WorldBlocks.blue_slime_tall_grass, BlockLootTables::func_218486_d);
    this.registerLootTable(WorldBlocks.purple_slime_tall_grass, BlockLootTables::func_218486_d);
    this.registerLootTable(WorldBlocks.orange_slime_tall_grass, BlockLootTables::func_218486_d);

    this.func_218492_c(WorldBlocks.blue_slime_sapling);
    this.func_218492_c(WorldBlocks.orange_slime_sapling);
    this.func_218492_c(WorldBlocks.purple_slime_sapling);

    this.registerLootTable(WorldBlocks.purple_slime_vine, BlockLootTables::func_218486_d);
    this.registerLootTable(WorldBlocks.purple_slime_vine_middle, BlockLootTables::func_218486_d);
    this.registerLootTable(WorldBlocks.purple_slime_vine_end, BlockLootTables::func_218486_d);

    this.registerLootTable(WorldBlocks.blue_slime_vine, BlockLootTables::func_218486_d);
    this.registerLootTable(WorldBlocks.blue_slime_vine_middle, BlockLootTables::func_218486_d);
    this.registerLootTable(WorldBlocks.blue_slime_vine_end, BlockLootTables::func_218486_d);
  }

  private void addGadgets() {
    this.func_218492_c(stone_ladder);

    this.func_218492_c(stone_torch);

    this.func_218493_a(wall_stone_torch, stone_torch);

    this.func_218492_c(punji);

    this.func_218492_c(wooden_rail);
    this.func_218492_c(wooden_dropper_rail);
  }

  @Override
  public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
    this.addCommon();

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID)) {
      this.addWorld();
    }

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_GADGETS_PULSE_ID)) {
      this.addGadgets();
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
