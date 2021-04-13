package slimeknights.tconstruct.fluids.fluids;

import lombok.AllArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

import java.util.function.Supplier;

@AllArgsConstructor
public class UnplaceableFluid extends Fluid {

  private final Supplier<? extends Item> bucket;
//  private final FluidAttributes.Builder builder;

  @Override
  public Item getBucketItem() {
    return bucket.get();
  }

  @Override
  protected boolean canBeReplacedWith(FluidState p_215665_1_, BlockView p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
    return false;
  }

  @Override
  public int getTickRate(WorldView p_205569_1_) {
    return 5;
  }

  @Override
  protected float getBlastResistance() {
    return 100;
  }

  @Override
  protected BlockState toBlockState(FluidState state) {
    return Blocks.AIR.getDefaultState();
  }

//  @Override
//  protected FluidAttributes createAttributes()
//  {
//    return builder.build(this);
//  }

  /* Required methods */

  @Override
  protected Vec3d getVelocity(BlockView p_215663_1_, BlockPos p_215663_2_, FluidState p_215663_3_) {
    return new Vec3d(0, 0, 0);
  }

  @Override
  public boolean isStill(FluidState state) {
    return false;
  }

  @Override
  public float getHeight(FluidState p_223407_1_) {
    return 1;
  }

  @Override
  public float getHeight(FluidState p_215662_1_, BlockView p_215662_2_, BlockPos p_215662_3_) {
    return 1;
  }

  @Override
  public int getLevel(FluidState p_207192_1_) {
    return 0;
  }

  @Override
  public VoxelShape getShape(FluidState p_215664_1_, BlockView p_215664_2_, BlockPos p_215664_3_) {
    return VoxelShapes.fullCube();
  }
}
