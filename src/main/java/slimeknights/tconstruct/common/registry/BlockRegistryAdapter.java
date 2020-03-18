package slimeknights.tconstruct.common.registry;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.tconstruct.smeltery.block.SearedSlabBlock;
import slimeknights.tconstruct.smeltery.block.SearedStairsBlock;

// MANTLE
// TODO: move to mantle

/**
 * Provides utility registration methods when registering blocks.
 */
public class BlockRegistryAdapter extends BaseRegistryAdapter<Block> {

  public BlockRegistryAdapter(IForgeRegistry<Block> registry) {
    super(registry);
  }

  /**
   * Registers the given block as well as a slab and a stair variant for it.
   * Uses the vanilla slab and stair blocks. Uses the passed blocks properties for both.
   * Slabs and stairs are registered with a "_slab" and "_stairs" prefix
   *
   * @param block        The main block to register and whose properties to use
   * @param registryName The registry name to use for the block and as base for the slab and stairs
   */
  public void registerSlabsAndStairs(Block block, String registryName) {
    this.register(block, registryName);
    this.register(new SlabBlock(Block.Properties.from(block)), registryName + "_slab");
    this.register(new StairsBlock(block::getDefaultState, Block.Properties.from(block)), registryName + "_stairs");
  }

  /**
   * Registers the given block as well as a slab and a stair variant for it.
   * Uses the vanilla slab and stair blocks. Uses the passed blocks properties for both.
   * Slabs and stairs are registered with a "_slab" and "_stairs" prefix
   *
   * @param block        The main block to register and whose properties to use
   * @param registryName The registry name to use for the block and as base for the slab and stairs
   */
  public void registerSmelterySlabsAndStairs(Block block, String registryName) {
    this.register(block, registryName);
    this.register(new SearedSlabBlock(Block.Properties.from(block)), registryName + "_slab");
    this.register(new SearedStairsBlock(block::getDefaultState, Block.Properties.from(block)), registryName + "_stairs");
  }
}
