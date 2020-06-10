package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

import java.util.function.Supplier;

public class FluidUpdatePacket extends AbstractPacketThreadsafe {

  public BlockPos pos;
  public FluidStack fluid;

  public FluidUpdatePacket(BlockPos pos, FluidStack fluid) {
    this.pos = pos;
    this.fluid = fluid;
  }

  public FluidUpdatePacket(PacketBuffer buffer) {
    this.pos = readPos(buffer);
    this.fluid = FluidStack.loadFluidStackFromNBT(buffer.readCompoundTag());
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    writePos(pos, packetBuffer);
    CompoundNBT tag = new CompoundNBT();
    if (fluid != null) {
      fluid.writeToNBT(tag);
    }
    packetBuffer.writeCompoundTag(tag);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> {
      if (supplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
        TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
        if (te instanceof IFluidPacketReceiver) {
          ((IFluidPacketReceiver) te).updateFluidTo(fluid);
        }
      }
    });

    supplier.get().setPacketHandled(true);
  }

  public interface IFluidPacketReceiver {

    /**
     * Updates the current fluid to the specified value
     *
     * @param fluid New fluidstack
     */
    void updateFluidTo(FluidStack fluid);
  }
}
