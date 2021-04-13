package slimeknights.tconstruct.tables.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;

public class UpdateStationScreenPacket implements IThreadsafePacket {

  private final PacketType type;
  private final Text message;

  /**
   *
   * @param type the type of sync the packet is, can be NO_TYPE, ERROR OR WARNING
   * @param message the message to display if a warning or error occurred
   */
  public UpdateStationScreenPacket(PacketType type, Text message) {
    this.type = type;
    this.message = message;
  }

  public UpdateStationScreenPacket(PacketByteBuf buffer) {
    this.type = buffer.readEnumConstant(PacketType.class);

    if (buffer.readBoolean()) {
      this.message = buffer.readText();
    }
    else {
      this.message = LiteralText.EMPTY;
    }
  }

  @Override
  public void encode(PacketByteBuf packetBuffer) {
    packetBuffer.writeEnumConstant(this.type);

    if (this.message == LiteralText.EMPTY) {
      packetBuffer.writeBoolean(false);
    }
    else {
      packetBuffer.writeBoolean(true);
      packetBuffer.writeText(this.message);
    }
  }

  @Override
  public void handleThreadsafe(PlayerEntity context, PacketSender sender) {
    HandleClient.handle(this);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {

    private static void handle(UpdateStationScreenPacket packet) {
      Screen screen = MinecraftClient.getInstance().currentScreen;
      if (screen != null) {
        if (screen instanceof BaseStationScreen) {
          switch (packet.type) {
            case ERROR:
              ((BaseStationScreen) screen).error(packet.message);
              break;
            case WARNING:
              ((BaseStationScreen) screen).warning(packet.message);
              break;
            case SUCCESS:
            default:
              ((BaseStationScreen) screen).updateDisplay();
              break;
          }
        }
      }
    }
  }

  public enum PacketType {
    ERROR,
    WARNING,
    SUCCESS;
  }
}
