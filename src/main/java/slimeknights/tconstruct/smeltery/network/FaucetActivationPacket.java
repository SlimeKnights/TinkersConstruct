package slimeknights.tconstruct.smeltery.network;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
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

  public FaucetActivationPacket(PacketByteBuf buffer) {
    super(buffer);
    this.isPouring = buffer.readBoolean();
  }

  @Override
  public void encode(PacketByteBuf packetBuffer) {
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
      assert MinecraftClient.getInstance().world != null;
      BlockEntity te = MinecraftClient.getInstance().world.getBlockEntity(packet.pos);
      if (te instanceof FaucetTileEntity) {
        ((FaucetTileEntity) te).onActivationPacket(packet.fluid, packet.isPouring);
      }
    }
  }
}
