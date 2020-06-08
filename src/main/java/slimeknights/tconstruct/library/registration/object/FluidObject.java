package slimeknights.tconstruct.library.registration.object;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.BucketItem;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public class FluidObject<F extends ForgeFlowingFluid> implements Supplier<F>, IItemProvider {
  private final Supplier<F> source;
  private final Supplier<F> flowing;
  private final Supplier<FlowingFluidBlock> block;
  private final Supplier<BucketItem> bucket;

  /**
   * Creates a new fluid object
   * @param source    Supplier to the source fluid
   * @param flowing   Supplier to the flowing fluid
   * @param block     Supplier to the block
   * @param bucket    Supplier to the bucket
   */
  public FluidObject(Supplier<F> source, Supplier<F> flowing, Supplier<FlowingFluidBlock> block, Supplier<BucketItem> bucket) {
    this.source = source;
    this.flowing = flowing;
    this.block = block;
    this.bucket = bucket;
  }

  /**
   * Gets the still form of this fluid
   * @return  Still form
   */
  public F getStill() {
    return source.get();
  }

  /**
   * Gets the flowing form of this fluid
   * @return  flowing form
   */
  public F getFlowing() {
    return flowing.get();
  }

  @Override
  public F get() {
    return getStill();
  }

  /**
   * Gets the block form of this fluid
   * @return  Block form
   */
  public FlowingFluidBlock getBlock() {
    return block.get();
  }

  /**
   * Gets the bucket form of this fluid
   * @return  Bucket form
   */
  @Override
  public BucketItem asItem() {
    return bucket.get();
  }
}
