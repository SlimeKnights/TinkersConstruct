package slimeknights.tconstruct.library.registration.object;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.List;

public class BuildingBlockObject extends BlockItemObject<Block> {
  protected final BlockItemObject<SlabBlock> slab;
  protected final BlockItemObject<StairsBlock> stairs;

  /**
   * Creates a new object from BlockItemObjects
   * @param block   Base block
   * @param slab    Slab block
   * @param stairs  Stairs block
   */
  public BuildingBlockObject(
    BlockItemObject<Block> block,
    BlockItemObject<SlabBlock> slab,
    BlockItemObject<StairsBlock> stairs) {
    super(block.block, block.item);
    this.slab = slab;
    this.stairs = stairs;
  }

  /**
   * Creates a new object from another building block object
   * @param object   Object to copy
   */
  protected BuildingBlockObject(BuildingBlockObject object) {
    super(object.block, object.item);
    this.slab = object.slab;
    this.stairs = object.stairs;
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

  /**
   * Gets an array of the blocks in this object
   * @return  Array of the blocks in this object
   */
  public List<Block> values() {
    return Arrays.asList(get(), getSlab(), getStairs());
  }
}
