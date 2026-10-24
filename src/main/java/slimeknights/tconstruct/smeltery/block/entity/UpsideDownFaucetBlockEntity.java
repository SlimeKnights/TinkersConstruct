package slimeknights.tconstruct.smeltery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import static slimeknights.tconstruct.smeltery.block.FaucetBlock.FACING;

public class UpsideDownFaucetBlockEntity extends FaucetBlockEntity {

  public UpsideDownFaucetBlockEntity(BlockPos pos, BlockState state) {
    super(TinkerSmeltery.upsideDownFaucetEntity.get(), pos, state);
  }

  @Override
  public void neighborChanged(BlockPos neighbor) {
    // if the neighbor was above us, remove output
    if (worldPosition.equals(neighbor.below())) {
      outputHandler = null;
      // neighbor behind us
    } else if (worldPosition.equals(neighbor.relative(getBlockState().getValue(FACING)))) {
      inputHandler = null;
    }
  }

  @Override
  protected LazyOptional<IFluidHandler> getOutputHandler() {
    if (outputHandler == null) {
      outputHandler = findFluidHandler(Direction.UP);
      if (outputHandler.isPresent()) {
        outputHandler.addListener(outputListener);
      }
    }
    return outputHandler;
  }

  @Override
  public AABB getRenderBoundingBox() {
    return new AABB(worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
  }

  @Override
  protected LazyOptional<IFluidHandler> getInputHandler() {
    if (inputHandler == null) {
      var dir = getBlockState().getValue(FACING);
      if (dir.getAxis() != Direction.Axis.Y) dir = dir.getOpposite();
      inputHandler = findFluidHandler(dir);
      if (inputHandler.isPresent()) {
        inputHandler.addListener(inputListener);
      }
    }
    return inputHandler;
  }
}
