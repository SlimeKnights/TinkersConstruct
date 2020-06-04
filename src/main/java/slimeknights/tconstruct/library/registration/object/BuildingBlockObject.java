package slimeknights.tconstruct.library.registration.object;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.Item;

public class BuildingBlockObject extends BlockItemObject<Block> {
  private final BlockItemObject<SlabBlock> slab;
  private final BlockItemObject<StairsBlock> stairs;

  public BuildingBlockObject(
    BlockItemObject<Block> block,
    BlockItemObject<SlabBlock> slab,
    BlockItemObject<StairsBlock> stairs) {
    super(block.block, block.item);
    this.slab = slab;
    this.stairs = stairs;
  }

  public static BuildingBlockObject fromBlocks(Block block, Block slab, Block stairs) {
    return new BuildingBlockObject(
      BlockItemObject.fromBlock(block),
      BlockItemObject.fromBlock((SlabBlock)slab),
      BlockItemObject.fromBlock((StairsBlock)stairs)
    );
  }

  /** Gets the slab for this block */
  public SlabBlock getSlab() {
    return slab.get();
  }

  /** Gets the stairs for this block */
  public StairsBlock getStairs() {
    return stairs.get();
  }

  /** Gets the slab item for this block */
  public Item getSlabItem() {
    return slab.asItem();
  }

  /** Gets the stairs item for this block */
  public Item getStairsItem() {
    return stairs.asItem();
  }
}
