package slimeknights.tconstruct.library.registration.object;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.item.Item;

public class WallBuildingBlockObject extends BuildingBlockObject {
  private final BlockItemObject<WallBlock> wall;

  /**
   * Creates a new building block object with all parameters
   * @param block    Block object
   * @param slab     Slab object
   * @param stairs   Stairs object
   * @param wall     Wall object
   */
  public WallBuildingBlockObject(
      BlockItemObject<Block> block,
      BlockItemObject<SlabBlock> slab,
      BlockItemObject<StairsBlock> stairs,
      BlockItemObject<WallBlock> wall) {
    super(block, slab, stairs);
    this.wall = wall;
  }

  /**
   * Creates a new object from a building block object plus a wall
   * @param object  Previous building block object
   * @param wall    Wall object
   */
  public WallBuildingBlockObject(BuildingBlockObject object, BlockItemObject<WallBlock> wall) {
    super(object);
    this.wall = wall;
  }

  public static WallBuildingBlockObject fromBlocks(Block block, Block slab, Block stairs, Block wall) {
    return new WallBuildingBlockObject(
      BlockItemObject.fromBlock(block),
      BlockItemObject.fromBlock((SlabBlock)slab),
      BlockItemObject.fromBlock((StairsBlock)stairs),
      BlockItemObject.fromBlock((WallBlock)wall)
    );
  }

  /** Gets the wall for this block */
  public WallBlock getWall() {
    return wall.get();
  }

  /** Gets the wall item for this block */
  public Item getWallItem() {
    return wall.asItem();
  }
}
