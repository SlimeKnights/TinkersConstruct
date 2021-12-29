package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.component.TankTileEntity;

/** Extension of {@link TankTileEntity} that uses no TESR, forcing the model fluid render, its more efficient for decoration */
public class LanternTileEntity extends TankTileEntity {
  public LanternTileEntity(BlockPos pos, BlockState state) {
    this(pos, state, TinkerSmeltery.searedLantern.get());
  }

  /** Main constructor */
  public LanternTileEntity(BlockPos pos, BlockState state, ITankBlock block) {
    super(TinkerSmeltery.lantern.get(), pos, state, block);
  }

  @Override
  public boolean isFluidInModel() {
    return true;
  }
}
