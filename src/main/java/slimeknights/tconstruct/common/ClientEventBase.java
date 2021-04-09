package slimeknights.tconstruct.common;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.ItemConvertible;
import slimeknights.mantle.registration.object.EnumObject;

import java.util.function.Supplier;

/**
 * Contains helpers to use for registering client events
 */
public abstract class ClientEventBase implements ClientModInitializer {
  /**
   * Registers a block colors alias for the given block
   * @param blockColors  BlockColors instance
   * @param itemColors   ItemColors instance
   * @param block        Block to register
   */
  protected static void registerBlockItemColorAlias(ColorProviderRegistry<Block, BlockColorProvider> blockColors, ColorProviderRegistry<ItemConvertible, ItemColorProvider> itemColors, Block block) {
    itemColors.register((stack, index) -> blockColors.getColor(block.getDefaultState(), null, null, index), block);
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
