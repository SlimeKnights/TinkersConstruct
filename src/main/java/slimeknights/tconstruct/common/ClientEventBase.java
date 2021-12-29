package slimeknights.tconstruct.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import slimeknights.mantle.registration.object.EnumObject;

import java.util.function.Supplier;

/**
 * Contains helpers to use for registering client events
 */
public abstract class ClientEventBase {
  /**
   * Registers a block colors alias for the given block
   * @param blockColors  BlockColors instance
   * @param itemColors   ItemColors instance
   * @param block        Block to register
   */
  protected static void registerBlockItemColorAlias(BlockColors blockColors, ItemColors itemColors, Block block) {
    itemColors.register((stack, index) -> blockColors.getColor(block.defaultBlockState(), null, null, index), block);
  }

  /**
   * Registers a block colors alias for the given block suppliers
   * @param blockColors  BlockColors instance
   * @param itemColors   ItemColors instance
   * @param block        Block to register
   */
  protected static void registerBlockItemColorAlias(BlockColors blockColors, ItemColors itemColors, Supplier<? extends Block> block) {
    registerBlockItemColorAlias(blockColors, itemColors, block.get());
  }

  /**
   * Registers a block colors alias for all blocks in the given instance
   * @param blockColors  BlockColors instance
   * @param itemColors   ItemColors instance
   * @param blocks       EnumBlock instance
   */
  protected static <B extends Block> void registerBlockItemColorAlias(BlockColors blockColors, ItemColors itemColors, EnumObject<?,B> blocks) {
    for (B block : blocks.values()) {
      registerBlockItemColorAlias(blockColors, itemColors, block);
    }
  }
}
