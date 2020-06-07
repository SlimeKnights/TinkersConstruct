package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

import java.util.function.Supplier;

/** Sent to clients to activate the faucet animation clientside **/
public class FaucetActivationPacket extends FluidUpdatePacket {

  public FaucetActivationPacket() {}

  public FaucetActivationPacket(BlockPos pos, FluidStack fluid) {
    super(pos, fluid);
  }

  public FaucetActivationPacket(PacketBuffer buffer) {
    super(buffer);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> {
      if (supplier.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
        TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
        if (te instanceof FaucetTileEntity) {
          ((FaucetTileEntity) te).onActivationPacket(fluid);
        }
      }
    });

    supplier.get().setPacketHandled(true);
  }
}
