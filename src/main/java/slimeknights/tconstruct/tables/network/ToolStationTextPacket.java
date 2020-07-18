package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.inventory.table.toolstation.ToolStationContainer;

import java.util.function.Supplier;

public class ToolStationTextPacket extends AbstractPacket {
  public String text;

  public ToolStationTextPacket(String text) {
    this.text = text;
  }

  public ToolStationTextPacket(PacketBuffer buffer) {
    this.text = buffer.readString(32767);
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeString(this.text);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> {
      if (supplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
        if (supplier.get().getSender() != null) {
          ServerPlayerEntity playerEntity = supplier.get().getSender();
          Container container = playerEntity.openContainer;

          if (container instanceof ToolStationContainer) {
            ((ToolStationContainer) container).setToolName(text);

            ServerWorld serverWorld = playerEntity.getServerWorld();
            for (PlayerEntity player : serverWorld.getPlayers()) {
              if (player.openContainer instanceof ToolStationContainer) {
                if (((BaseContainer) container).sameGui((BaseContainer) player.openContainer)) {
                  TinkerNetwork.getInstance().sendTo(this, (ServerPlayerEntity) player);
                }
              }
            }
          }
        }
      } else {
        if (Minecraft.getInstance().player != null) {
          Container container = Minecraft.getInstance().player.openContainer;
          if (container instanceof ToolStationContainer) {
            ((ToolStationContainer) container).setToolName(text);
          }
        }
      }
    });

    supplier.get().setPacketHandled(true);
  }
}
