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
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.GadgetBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.items.CommonItems;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TConstructBlockLootTables extends BlockLootTables {

  private final Map<ResourceLocation, LootTable.Builder> loot_tables = Maps.newHashMap();

  private Set<Block> knownBlocks = new HashSet<>();

  private static LootTable.Builder dropSapling(Block blockIn, Block saplingIn, float... fortuneIn) {
    return droppingWithSilkTouchOrShears(blockIn, withSurvivesExplosion(blockIn, ItemLootEntry.builder(saplingIn)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, fortuneIn)));
  }

  private static LootTable.Builder randomDropPurpleSlimeBall(Block blockIn, Block saplingIn, float... fortuneIn) {
    return dropSapling(blockIn, saplingIn, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(withSurvivesExplosion(blockIn, ItemLootEntry.builder(CommonItems.purple_slime_ball)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
  }

  private static LootTable.Builder randomDropBlueOrGreenSlimeBall(Block blockIn, Block saplingIn, float... fortuneIn) {
    return dropSapling(blockIn, saplingIn, fortuneIn).addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(NOT_SILK_TOUCH_OR_SHEARS).addEntry(AlternativesLootEntry.builder(withSurvivesExplosion(blockIn, ItemLootEntry.builder(CommonItems.blue_slime_ball)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)), withSurvivesExplosion(blockIn, ItemLootEntry.builder(Items.SLIME_BALL)).acceptCondition(TableBonus.builder(Enchantments.FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F)))));
  }

  private void addCommon() {
    this.registerDropSelfLootTable(CommonBlocks.grout);
    this.registerDropSelfLootTable(CommonBlocks.slimy_mud_green);
    this.registerDropSelfLootTable(CommonBlocks.slimy_mud_blue);
    this.registerDropSelfLootTable(CommonBlocks.graveyard_soil);
    this.registerDropSelfLootTable(CommonBlocks.consecrated_soil);
    this.registerDropSelfLootTable(CommonBlocks.slimy_mud_magma);

    this.registerDropSelfLootTable(CommonBlocks.lavawood);
    this.registerLootTable(CommonBlocks.lavawood_slab, BlockLootTables::droppingSlab);
    this.registerDropSelfLootTable(CommonBlocks.firewood_stairs);

    this.registerDropSelfLootTable(CommonBlocks.firewood);
    this.registerLootTable(CommonBlocks.firewood_slab, BlockLootTables::droppingSlab);
    this.registerDropSelfLootTable(CommonBlocks.lavawood_stairs);

    this.registerDropSelfLootTable(CommonBlocks.cobalt_block);
    this.registerDropSelfLootTable(CommonBlocks.ardite_block);
    this.registerDropSelfLootTable(CommonBlocks.manyullyn_block);
    this.registerDropSelfLootTable(CommonBlocks.knightslime_block);
    this.registerDropSelfLootTable(CommonBlocks.pigiron_block);
    this.registerDropSelfLootTable(CommonBlocks.alubrass_block);
    this.registerDropSelfLootTable(CommonBlocks.silky_jewel_block);
  }

  private void addDecorative() {
    this.registerDropSelfLootTable(DecorativeBlocks.clear_glass);

    this.registerDropSelfLootTable(DecorativeBlocks.white_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.orange_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.magenta_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.light_blue_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.yellow_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.lime_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.pink_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.gray_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.light_gray_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.cyan_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.purple_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.blue_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.brown_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.green_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.red_clear_stained_glass);
    this.registerDropSelfLootTable(DecorativeBlocks.black_clear_stained_glass);

    this.registerDropSelfLootTable(DecorativeBlocks.mud_bricks);
    this.registerLootTable(DecorativeBlocks.mud_bricks_slab, BlockLootTables::droppingSlab);
    this.registerDropSelfLootTable(DecorativeBlocks.mud_bricks_stairs);

    this.registerDropSelfLootTable(DecorativeBlocks.dried_clay);
    this.registerLootTable(DecorativeBlocks.dried_clay_slab, BlockLootTables::droppingSlab);
    this.registerDropSelfLootTable(DecorativeBlocks.dried_clay_stairs);

    this.registerDropSelfLootTable(DecorativeBlocks.dried_clay_bricks);
    this.registerLootTable(DecorativeBlocks.dried_clay_bricks_slab, BlockLootTables::droppingSlab);
    this.registerDropSelfLootTable(DecorativeBlocks.dried_clay_bricks_stairs);
  }

  private void addWorld() {
    this.registerDropSelfLootTable(WorldBlocks.cobalt_ore);
    this.registerDropSelfLootTable(WorldBlocks.ardite_ore);

    this.registerDropSelfLootTable(WorldBlocks.blue_slime);
    this.registerDropSelfLootTable(WorldBlocks.purple_slime);
    this.registerDropSelfLootTable(WorldBlocks.blood_slime);
    this.registerDropSelfLootTable(WorldBlocks.magma_slime);
    this.registerDropSelfLootTable(WorldBlocks.pink_slime);

    this.registerDropSelfLootTable(WorldBlocks.congealed_green_slime);
    this.registerDropSelfLootTable(WorldBlocks.congealed_blue_slime);
    this.registerDropSelfLootTable(WorldBlocks.congealed_purple_slime);
    this.registerDropSelfLootTable(WorldBlocks.congealed_blood_slime);
    this.registerDropSelfLootTable(WorldBlocks.congealed_magma_slime);
    this.registerDropSelfLootTable(WorldBlocks.congealed_pink_slime);

    this.registerDropSelfLootTable(WorldBlocks.green_slime_dirt);
    this.registerDropSelfLootTable(WorldBlocks.blue_slime_dirt);
    this.registerDropSelfLootTable(WorldBlocks.purple_slime_dirt);
    this.registerDropSelfLootTable(WorldBlocks.magma_slime_dirt);

    this.registerLootTable(WorldBlocks.blue_vanilla_slime_grass, (block) -> droppingWithSilkTouch(block, Blocks.DIRT));
    this.registerLootTable(WorldBlocks.purple_vanilla_slime_grass, (block) -> droppingWithSilkTouch(block, Blocks.DIRT));
    this.registerLootTable(WorldBlocks.orange_vanilla_slime_grass, (block) -> droppingWithSilkTouch(block, Blocks.DIRT));

    this.registerLootTable(WorldBlocks.blue_green_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.green_slime_dirt));
    this.registerLootTable(WorldBlocks.purple_green_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.green_slime_dirt));
    this.registerLootTable(WorldBlocks.orange_green_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.green_slime_dirt));

    this.registerLootTable(WorldBlocks.blue_blue_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.blue_slime_dirt));
    this.registerLootTable(WorldBlocks.purple_blue_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.blue_slime_dirt));
    this.registerLootTable(WorldBlocks.orange_blue_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.blue_slime_dirt));

    this.registerLootTable(WorldBlocks.blue_purple_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.purple_slime_dirt));
    this.registerLootTable(WorldBlocks.purple_purple_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.purple_slime_dirt));
    this.registerLootTable(WorldBlocks.orange_purple_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.purple_slime_dirt));

    this.registerLootTable(WorldBlocks.blue_magma_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.magma_slime_dirt));
    this.registerLootTable(WorldBlocks.purple_magma_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.magma_slime_dirt));
    this.registerLootTable(WorldBlocks.orange_magma_slime_grass, (block) -> droppingWithSilkTouch(block, WorldBlocks.magma_slime_dirt));

    this.registerLootTable(WorldBlocks.blue_slime_leaves, (block) -> randomDropBlueOrGreenSlimeBall(block, WorldBlocks.blue_slime_sapling, DEFAULT_SAPLING_DROP_RATES));

    this.registerLootTable(WorldBlocks.purple_slime_leaves, (block) -> randomDropPurpleSlimeBall(block, WorldBlocks.blue_slime_sapling, DEFAULT_SAPLING_DROP_RATES));

    this.registerLootTable(WorldBlocks.orange_slime_leaves, (block) -> dropSapling(block, WorldBlocks.blue_slime_sapling, DEFAULT_SAPLING_DROP_RATES));

    this.registerLootTable(WorldBlocks.blue_slime_fern, BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.purple_slime_fern, BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.orange_slime_fern, BlockLootTables::onlyWithShears);

    this.registerLootTable(WorldBlocks.blue_slime_tall_grass, BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.purple_slime_tall_grass, BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.orange_slime_tall_grass, BlockLootTables::onlyWithShears);

    this.registerDropSelfLootTable(WorldBlocks.blue_slime_sapling);
    this.registerDropSelfLootTable(WorldBlocks.orange_slime_sapling);
    this.registerDropSelfLootTable(WorldBlocks.purple_slime_sapling);

    this.registerLootTable(WorldBlocks.purple_slime_vine, BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.purple_slime_vine_middle, BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.purple_slime_vine_end, BlockLootTables::onlyWithShears);

    this.registerLootTable(WorldBlocks.blue_slime_vine, BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.blue_slime_vine_middle, BlockLootTables::onlyWithShears);
    this.registerLootTable(WorldBlocks.blue_slime_vine_end, BlockLootTables::onlyWithShears);
  }

  private void addGadgets() {
    this.registerDropSelfLootTable(GadgetBlocks.stone_ladder);

    this.registerDropSelfLootTable(GadgetBlocks.stone_torch);

    this.registerDropping(GadgetBlocks.wall_stone_torch, GadgetBlocks.stone_torch);

    this.registerDropSelfLootTable(GadgetBlocks.punji);

    this.registerDropSelfLootTable(GadgetBlocks.wooden_rail);
    this.registerDropSelfLootTable(GadgetBlocks.wooden_dropper_rail);
  }

  @Override
  public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
    this.addCommon();
    this.addDecorative();
    this.addWorld();
    this.addGadgets();

    Set<ResourceLocation> visited = Sets.newHashSet();

    for (Block block : this.knownBlocks) {
      ResourceLocation lootTable = block.getLootTable();
      if (lootTable != LootTables.EMPTY && visited.add(lootTable)) {
        LootTable.Builder builder = this.lootTables.remove(lootTable);
        if (builder == null) {
          throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", lootTable, block.getRegistryName()));
        }

        consumer.accept(lootTable, builder);
      }
    }

    if (!this.lootTables.isEmpty()) {
      throw new IllegalStateException("Created block loot tables for non-blocks: " + this.lootTables.keySet());
    }
  }

  @Override
  public void registerSilkTouch(Block blockIn, Block droppedBlockIn) {
    this.knownBlocks.add(blockIn);
    super.registerSilkTouch(blockIn, droppedBlockIn);
  }

  @Override
  public void registerDropSelfLootTable(Block block) {
    this.knownBlocks.add(block);
    super.registerDropSelfLootTable(block);
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
