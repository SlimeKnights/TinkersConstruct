package slimeknights.tconstruct.library.fluid;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.library.network.TinkerNetwork;
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

    parent.markDirty();
    World world = parent.getWorld();
    if(!world.isClient) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(parent.getPos(), this.getFluid()), (ServerWorld) world, parent.getPos());
    }
  }
}
