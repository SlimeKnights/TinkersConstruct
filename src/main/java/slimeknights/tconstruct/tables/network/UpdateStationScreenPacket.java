package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;

public class UpdateStationScreenPacket implements IThreadsafePacket {

  private final PacketType type;
  private final ITextComponent message;

  /**
   *
   * @param type the type of sync the packet is, can be NO_TYPE, ERROR OR WARNING
   * @param message the message to display if a warning or error occurred
   */
  public UpdateStationScreenPacket(PacketType type, ITextComponent message) {
    this.type = type;
    this.message = message;
  }

  public UpdateStationScreenPacket(PacketBuffer buffer) {
    this.type = buffer.readEnumValue(PacketType.class);

    if (buffer.readBoolean()) {
      this.message = buffer.readTextComponent();
    }
    else {
      this.message = StringTextComponent.EMPTY;
    }
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeEnumValue(this.type);

    if (this.message == StringTextComponent.EMPTY) {
      packetBuffer.writeBoolean(false);
    }
    else {
      packetBuffer.writeBoolean(true);
      packetBuffer.writeTextComponent(this.message);
    }
  }

  @Override
  public void handleThreadsafe(NetworkEvent.Context context) {
    HandleClient.handle(this);
  }

  /** Safely runs client side only code in a method only called on client */
  private static class HandleClient {

    private static void handle(UpdateStationScreenPacket packet) {
      Screen screen = Minecraft.getInstance().currentScreen;
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
