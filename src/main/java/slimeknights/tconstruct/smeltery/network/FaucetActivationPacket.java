package slimeknights.tconstruct.smeltery.network;

import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.smeltery.tileentity.FaucetTileEntity;

/** Sent to clients to activate the faucet animation clientside **/
public class FaucetActivationPacket extends FluidUpdatePacket {

  private final boolean isPouring;
  public FaucetActivationPacket(BlockPos pos, FluidVolume fluid, boolean isPouring) {
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
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
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
