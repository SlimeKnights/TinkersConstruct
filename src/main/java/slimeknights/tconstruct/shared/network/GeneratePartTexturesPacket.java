package slimeknights.tconstruct.shared.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.shared.client.ClientGeneratePartTexturesCommand;

/** Packet to tell the client to generate tool textures */
@RequiredArgsConstructor
public class GeneratePartTexturesPacket implements IThreadsafePacket {
  private final Operation operation;
  private final String modId;
  private final String materialPath;

  public GeneratePartTexturesPacket(FriendlyByteBuf buffer) {
    operation = buffer.readEnum(Operation.class);
    modId = buffer.readUtf(Short.MAX_VALUE);
    materialPath = buffer.readUtf(Short.MAX_VALUE);
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeEnum(operation);
    buffer.writeUtf(modId);
    buffer.writeUtf(materialPath);
  }

  @Override
  public void handleThreadsafe(Context context) {
    context.enqueueWork(() -> ClientGeneratePartTexturesCommand.generateTextures(operation, modId, materialPath));
  }

  public enum Operation { ALL, MISSING }
}
