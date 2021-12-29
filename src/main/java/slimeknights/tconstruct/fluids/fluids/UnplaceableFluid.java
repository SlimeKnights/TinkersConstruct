package slimeknights.tconstruct.fluids.fluids;

import lombok.AllArgsConstructor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.Item;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.function.Supplier;

@AllArgsConstructor
public class UnplaceableFluid extends Fluid {

  private final Supplier<? extends Item> bucket;
  private final FluidAttributes.Builder builder;

  @Override
  public Item getBucket() {
    return bucket.get();
  }

  @Override
  protected boolean canBeReplacedWith(FluidState p_215665_1_, BlockGetter p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
    return false;
  }

  @Override
  public int getTickDelay(LevelReader p_205569_1_) {
    return 5;
  }

  @Override
  protected float getExplosionResistance() {
    return 100;
  }

  @Override
  protected BlockState createLegacyBlock(FluidState state) {
    return Blocks.AIR.defaultBlockState();
  }

  @Override
  protected FluidAttributes createAttributes()
  {
    return builder.build(this);
  }

  /* Required methods */

  @Override
  protected Vec3 getFlow(BlockGetter p_215663_1_, BlockPos p_215663_2_, FluidState p_215663_3_) {
    return new Vec3(0, 0, 0);
  }

  @Override
  public boolean isSource(FluidState state) {
    return false;
  }

  @Override
  public float getOwnHeight(FluidState p_223407_1_) {
    return 1;
  }

  @Override
  public float getHeight(FluidState p_215662_1_, BlockGetter p_215662_2_, BlockPos p_215662_3_) {
    return 1;
  }

  @Override
  public int getAmount(FluidState p_207192_1_) {
    return 0;
  }

  @Override
  public VoxelShape getShape(FluidState p_215664_1_, BlockGetter p_215664_2_, BlockPos p_215664_3_) {
    return Shapes.block();
  }
}
