package slimeknights.tconstruct.tools.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;

/**
 * Generic packet for various controls the client may send to the server
 */
public enum TinkerControlPacket implements IThreadsafePacket {
  DOUBLE_JUMP,
  START_HELMET_INTERACT, STOP_HELMET_INTERACT,
  START_LEGGINGS_INTERACT, STOP_LEGGINGS_INTERACT;

  public static TinkerControlPacket read(PacketBuffer buffer) {
    return buffer.readEnumValue(TinkerControlPacket.class);
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeEnumValue(this);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity player = context.getSender();
    if (player != null) {
      switch (this) {
        case DOUBLE_JUMP:
          DoubleJumpModifier.extraJump(player);
          break;
        case START_HELMET_INTERACT:
          InteractionHandler.startArmorInteract(player, EquipmentSlotType.HEAD);
          break;
        case STOP_HELMET_INTERACT:
          InteractionHandler.stopArmorInteract(player, EquipmentSlotType.HEAD);
          break;
        case START_LEGGINGS_INTERACT:
          InteractionHandler.startArmorInteract(player, EquipmentSlotType.LEGS);
          break;
        case STOP_LEGGINGS_INTERACT:
          InteractionHandler.stopArmorInteract(player, EquipmentSlotType.LEGS);
          break;
      }
    }
  }
}
