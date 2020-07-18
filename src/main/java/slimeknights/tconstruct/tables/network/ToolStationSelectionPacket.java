package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.client.inventory.table.ToolStationScreen;
import slimeknights.tconstruct.tables.inventory.table.toolstation.ToolStationContainer;

import java.util.function.Supplier;

public class ToolStationSelectionPacket extends AbstractPacket {

  public ItemStack tool;
  public int activeSlots;

  public ToolStationSelectionPacket(ItemStack tool, int activeSlots) {
    this.tool = tool;
    this.activeSlots = activeSlots;
  }

  public ToolStationSelectionPacket(PacketBuffer buffer) {
    this.tool = buffer.readItemStack();
    this.activeSlots = buffer.readInt();
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeItemStack(this.tool);
    packetBuffer.writeInt(this.activeSlots);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> {
      if (supplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
        if (supplier.get().getSender() != null) {
          ServerPlayerEntity playerEntity = supplier.get().getSender();
          Container container = playerEntity.openContainer;

          if (container instanceof ToolStationContainer) {
            ((ToolStationContainer) container).setToolSelection(tool, activeSlots);
            ServerWorld serverWorld = playerEntity.getServerWorld();
            for (PlayerEntity player : serverWorld.getPlayers()) {
              if (player == playerEntity) {
                continue;
              }

              if (player.openContainer instanceof ToolStationContainer) {
                if (((BaseContainer) container).sameGui((BaseContainer) player.openContainer)) {
                  ((ToolStationContainer) player.openContainer).setToolSelection(tool, activeSlots);
                  // same gui, send him an update
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
            ((ToolStationContainer) container).setToolSelection(tool, activeSlots);
            if (Minecraft.getInstance().currentScreen instanceof ToolStationScreen) {
              ((ToolStationScreen) Minecraft.getInstance().currentScreen).onToolSelectionPacket(this);
            }
          }
        }
      }
    });
    supplier.get().setPacketHandled(true);
  }
}
