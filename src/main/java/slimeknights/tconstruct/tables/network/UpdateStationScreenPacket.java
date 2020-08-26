package slimeknights.tconstruct.tables.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tables.client.inventory.BaseStationScreen;

public class UpdateStationScreenPacket implements IThreadsafePacket {

  private final String type;
  private final ITextComponent message;

  public static final String NO_TYPE = "no_type";
  public static final String ERROR_TYPE = "error";
  public static final String WARNING_TYPE = "warning";

  /**
   *
   * @param type the type of sync the packet is, can be NO_TYPE, ERROR OR WARNING
   * @param message the message to display if a warning or error occurred
   */
  public UpdateStationScreenPacket(String type, ITextComponent message) {
    this.type = type;
    this.message = message;
  }

  public UpdateStationScreenPacket(PacketBuffer buffer) {
    this.type = buffer.readString();
    this.message = buffer.readTextComponent();
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeString(this.type);
    packetBuffer.writeTextComponent(this.message);
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
            case ERROR_TYPE:
              ((BaseStationScreen) screen).error(packet.message);
            case WARNING_TYPE:
              ((BaseStationScreen) screen).warning(packet.message);
            case NO_TYPE:
            default:
              ((BaseStationScreen) screen).updateDisplay();
          }
        }
      }
    }
  }
}
