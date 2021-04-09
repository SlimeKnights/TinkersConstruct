package slimeknights.tconstruct.library.fluid;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;

public class FluidTankBase<T extends MantleTileEntity> extends SimpleFixedFluidInv {

  protected T parent;

  public FluidTankBase(FluidAmount capacity, T parent) {
    super(1, capacity);
    this.parent = parent;
    this.addListener((inv, tank, previous, current) -> {
      if (parent instanceof IFluidTankUpdater) {
        ((IFluidTankUpdater) parent).onTankContentsChanged();
      }

      parent.markDirty();
      World world = parent.getWorld();
      if (!world.isClient) {
        TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(parent.getPos(), current), (ServerWorld) world, parent.getPos());
      }
    }, () -> {
    });
  }
}
