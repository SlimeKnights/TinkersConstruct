package slimeknights.tconstruct.smeltery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

/** Sent to clients to activate the faucet animation clientside **/
public class FaucetActivationPacket extends FluidUpdatePacket {

  private final boolean isPouring;
  public FaucetActivationPacket(BlockPos pos, FluidStack fluid, boolean isPouring) {
    super(pos, fluid);
    this.isPouring = isPouring;
  }

  public FaucetActivationPacket(PacketBuffer buffer) {
    super(buffer);
    this.isPouring = buffer.readBoolean();
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    super.encode(packetBuffer);
    packetBuffer.writeBoolean(isPouring);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {
    private static void handle(FaucetActivationPacket packet) {
      assert Minecraft.getInstance().world != null;
      TileEntity te = Minecraft.getInstance().world.getTileEntity(packet.pos);
      if (te instanceof FaucetTileEntity) {
        ((FaucetTileEntity) te).onActivationPacket(packet.fluid, packet.isPouring);
      }
    }
  }
}
