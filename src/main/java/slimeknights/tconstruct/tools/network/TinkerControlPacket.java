package slimeknights.tconstruct.tools.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.logic.DoubleJumpHandler;
import slimeknights.tconstruct.tools.logic.InteractionHandler;

/**
 * Generic packet for various controls the client may send to the server
 */
@RequiredArgsConstructor
public enum TinkerControlPacket implements IThreadsafePacket {
  DOUBLE_JUMP,
  ANTIGRAVITY_JUMP,
  // helmet
  START_HELMET_INTERACT(TooltipKey.NORMAL),
  START_HELMET_INTERACT_SHIFT(TooltipKey.SHIFT),
  START_HELMET_INTERACT_CONTROL(TooltipKey.CONTROL),
  START_HELMET_INTERACT_ALT(TooltipKey.ALT),
  STOP_HELMET_INTERACT,
  // leggings
  START_LEGGINGS_INTERACT(TooltipKey.NORMAL),
  START_LEGGINGS_INTERACT_SHIFT(TooltipKey.SHIFT),
  START_LEGGINGS_INTERACT_CONTROL(TooltipKey.CONTROL),
  START_LEGGINGS_INTERACT_ALT(TooltipKey.ALT),
  STOP_LEGGINGS_INTERACT;

  private final TooltipKey modifier;

  TinkerControlPacket() {
    this(TooltipKey.UNKNOWN);
  }

  /** Gets the packet for helmet interaction */
  public static TinkerControlPacket getStartHelmetInteract(TooltipKey key) {
    return switch (key) {
      case SHIFT -> START_HELMET_INTERACT_SHIFT;
      case CONTROL -> START_HELMET_INTERACT_CONTROL;
      case ALT -> START_HELMET_INTERACT_ALT;
      default -> START_HELMET_INTERACT;
    };
  }

  /** Gets the packet for leggings interaction */
  public static TinkerControlPacket getStartLeggingsInteract(TooltipKey key) {
    return switch (key) {
      case SHIFT -> START_LEGGINGS_INTERACT_SHIFT;
      case CONTROL -> START_LEGGINGS_INTERACT_CONTROL;
      case ALT -> START_LEGGINGS_INTERACT_ALT;
      default -> START_LEGGINGS_INTERACT;
    };
  }

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
        case DOUBLE_JUMP -> DoubleJumpHandler.extraJump(player);
        case ANTIGRAVITY_JUMP -> TinkerEffects.antigravity.get().antigravityJump(player);
        case START_HELMET_INTERACT, START_HELMET_INTERACT_SHIFT, START_HELMET_INTERACT_CONTROL, START_HELMET_INTERACT_ALT
          -> InteractionHandler.startArmorInteract(player, EquipmentSlot.HEAD, this.modifier);
        case STOP_HELMET_INTERACT -> InteractionHandler.stopArmorInteract(player, EquipmentSlot.HEAD);
        case START_LEGGINGS_INTERACT, START_LEGGINGS_INTERACT_SHIFT, START_LEGGINGS_INTERACT_CONTROL, START_LEGGINGS_INTERACT_ALT
          -> InteractionHandler.startArmorInteract(player, EquipmentSlot.LEGS, this.modifier);
        case STOP_LEGGINGS_INTERACT -> InteractionHandler.stopArmorInteract(player, EquipmentSlot.LEGS);
      }
    }
  }
}
