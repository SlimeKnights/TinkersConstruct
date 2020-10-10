package slimeknights.tconstruct.common;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import slimeknights.mantle.registration.deferred.BlockDeferredRegister;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;

import java.util.function.Function;
import java.util.function.Supplier;

/** TODO: move new methods to Mantle */
public class BlockDeferredRegisterExtension extends BlockDeferredRegister {
  public BlockDeferredRegisterExtension(String modID) {
    super(modID);
  }

  /**
   * Registers a building block with slabs and stairs, using a custom block
   * @param name   Block name
   * @param block  Block supplier
   * @param item   Item block, used for all variants
   * @return  Building block object
   */
  public BuildingBlockObject registerBuilding(String name, Supplier<? extends Block> block, Function<? super Block, ? extends BlockItem> item) {
    ItemObject<Block> blockObj = this.register(name, block, item);
    return new BuildingBlockObject(
      blockObj,
      this.register(name + "_slab", () -> new SlabBlock(AbstractBlock.Properties.from(blockObj.get())), item),
      this.register(name + "_stairs", () -> new StairsBlock(() -> blockObj.get().getDefaultState(), AbstractBlock.Properties.from(blockObj.get())), item));
  }

  /**
   * Registers a building block with slabs, stairs and wall, using a custom block
   * @param name   Block name
   * @param block  Block supplier
   * @param item   Item block, used for all variants
   * @return  Building block object
   */
  public WallBuildingBlockObject registerWallBuilding(String name, Supplier<? extends Block> block, Function<? super Block, ? extends BlockItem> item) {
    BuildingBlockObject obj = this.registerBuilding(name, block, item);
    return new WallBuildingBlockObject(obj, this.register(name + "_wall", () -> new WallBlock(AbstractBlock.Properties.from(obj.get())), item));
  }
}
