package slimeknights.tconstruct.tools.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

/** Packet to tell a client to swing an entity arm, as the vanilla one resets cooldown */
public class SwingArmPacket implements IThreadsafePacket {
  private final int entityId;
  private final Hand hand;

  public SwingArmPacket(Entity entity, Hand hand) {
    this.entityId = entity.getEntityId();
    this.hand = hand;
  }

  public SwingArmPacket(PacketBuffer buffer) {
    this.entityId = buffer.readVarInt();
    this.hand = buffer.readEnumValue(Hand.class);
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeVarInt(entityId);
    buffer.writeEnumValue(hand);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  private static class HandleClient {
    private static void handle(SwingArmPacket packet) {
      World world = Minecraft.getInstance().world;
      if (world != null) {
        Entity entity = world.getEntityByID(packet.entityId);
        if (entity instanceof LivingEntity) {
          ToolAttackUtil.swingHand((LivingEntity) entity, packet.hand, false);
        }
      }
    }
  }
}
