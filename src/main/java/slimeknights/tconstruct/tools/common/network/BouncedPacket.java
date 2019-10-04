package slimeknights.tconstruct.tools.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.AbstractPacket;

import java.util.function.Supplier;

public class BouncedPacket extends AbstractPacket {

  public BouncedPacket() {

  }

  public BouncedPacket(PacketBuffer buffer) {

  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> {
      if (supplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
        if (supplier.get().getSender() != null) {
          supplier.get().getSender().fallDistance = 0.0f;
        }
      }
    });
    supplier.get().setPacketHandled(true);
  }

}
