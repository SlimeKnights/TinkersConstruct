package slimeknights.tconstruct.library.fluid;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

public class FluidTankBase<T extends MantleTileEntity> extends FluidTank {

  protected T parent;

  public FluidTankBase(int capacity, T parent) {
    super(capacity);
    this.parent = parent;
  }

  @Override
  protected void onContentsChanged() {
    if (parent instanceof IFluidTankUpdater) {
      ((IFluidTankUpdater) parent).onTankContentsChanged();
    }

    parent.setChanged();
    World world = parent.getLevel();
    if(!world.isClientSide) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(parent.getBlockPos(), this.getFluid()), (ServerWorld) world, parent.getBlockPos());
    }
  }
}
