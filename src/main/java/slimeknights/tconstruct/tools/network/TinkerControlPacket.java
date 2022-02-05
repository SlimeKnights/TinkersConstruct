package slimeknights.tconstruct.tools.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent.Context;
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

  public static TinkerControlPacket read(FriendlyByteBuf buffer) {
    return buffer.readEnum(TinkerControlPacket.class);
  }

  @Override
  public void encode(FriendlyByteBuf packetBuffer) {
    packetBuffer.writeEnum(this);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer player = context.getSender();
    if (player != null) {
      switch (this) {
        case DOUBLE_JUMP -> DoubleJumpModifier.extraJump(player);
        case START_HELMET_INTERACT -> InteractionHandler.startArmorInteract(player, EquipmentSlot.HEAD);
        case STOP_HELMET_INTERACT -> InteractionHandler.stopArmorInteract(player, EquipmentSlot.HEAD);
        case START_LEGGINGS_INTERACT -> InteractionHandler.startArmorInteract(player, EquipmentSlot.LEGS);
        case STOP_LEGGINGS_INTERACT -> InteractionHandler.stopArmorInteract(player, EquipmentSlot.LEGS);
      }
    }
  }
}
