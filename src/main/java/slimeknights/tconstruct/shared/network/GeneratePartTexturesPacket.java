package slimeknights.tconstruct.shared.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.shared.client.ClientGeneratePartTexturesCommand;

/** Packet to tell the client to generate tool textures */
@RequiredArgsConstructor
public class GeneratePartTexturesPacket implements IThreadsafePacket {
  private final Operation operation;
  private final String modId;
  private final String materialPath;

  public GeneratePartTexturesPacket(PacketBuffer buffer) {
    operation = buffer.readEnumValue(Operation.class);
    modId = buffer.readString(Short.MAX_VALUE);
    materialPath = buffer.readString(Short.MAX_VALUE);
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeEnumValue(operation);
    buffer.writeString(modId);
    buffer.writeString(materialPath);
  }

  @Override
  public void handleThreadsafe(Context context) {
    context.enqueueWork(() -> ClientGeneratePartTexturesCommand.generateTextures(operation, modId, materialPath));
  }

  public enum Operation { ALL, MISSING }
}
