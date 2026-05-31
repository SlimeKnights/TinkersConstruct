package slimeknights.tconstruct.smeltery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import slimeknights.tconstruct.compat.neoforged.neoforge.capabilities.ForgeCapabilities;
import slimeknights.mantle.compat.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

/** This class exists simply to allow us to have a block entity renderer for obsidian gauges. Though it is useful as a cache for the capability to render. */
public class GaugeBlockEntity extends BlockEntity {
  private LazyOptional<IFluidHandler> neighbor;
  public GaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public GaugeBlockEntity(BlockPos pos, BlockState state) {
    this(TinkerSmeltery.gauge.get(), pos, state);
  }

  /** Gets the neighbor fluid handler. Used mainly for rendering client side */
  public IFluidHandler getTank() {
    if (level == null) {
      return EmptyFluidHandler.INSTANCE;
    }
    // if we have not fetched the neighbor, fetch it
    if (neighbor == null) {
      Direction side = getBlockState().getValue(BlockStateProperties.FACING);
      BlockPos pos = getBlockPos().relative(side.getOpposite());
      BlockEntity te = level.getBlockEntity(pos);
      if (te != null) {
        neighbor = LazyOptional.ofNullable(level.getCapability(Capabilities.FluidHandler.BLOCK, pos, level.getBlockState(pos), te, side));
      } else {
        neighbor = LazyOptional.empty();
      }
    }
    // return tank or empty tank
    return neighbor.orElse(EmptyFluidHandler.INSTANCE);
  }
}
