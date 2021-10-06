package slimeknights.tconstruct.tools.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;

public class DoubleJumpPacket implements IThreadsafePacket {
  public static final DoubleJumpPacket INSTANCE = new DoubleJumpPacket();

  private DoubleJumpPacket() {}

  @Override
  public void encode(PacketBuffer packetBuffer) {}

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity player = context.getSender();
    if (player != null) {
      DoubleJumpModifier.extraJump(player);
    }
  }
}
