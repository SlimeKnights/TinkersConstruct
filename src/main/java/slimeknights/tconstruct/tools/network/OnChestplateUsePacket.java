package slimeknights.tconstruct.tools.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tools.logic.InteractionHandler;

/** Packet sent client to server when the chestplate uses an empty hand interaction */
@RequiredArgsConstructor
public enum OnChestplateUsePacket implements IThreadsafePacket {
  MAINHAND(InteractionHand.MAIN_HAND),
  OFFHAND(InteractionHand.OFF_HAND);

  private final InteractionHand hand;

  /** Gets the packet for the given hand */
  public static OnChestplateUsePacket from(InteractionHand hand) {
    return hand == InteractionHand.OFF_HAND ? OFFHAND : MAINHAND;
  }

  /** Gets the packet from the packet buffer */
  public static OnChestplateUsePacket read(FriendlyByteBuf buffer) {
    return from(buffer.readEnum(InteractionHand.class));
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeEnum(hand);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer player = context.getSender();
    if (player != null && !player.isSpectator()) {
      ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
      if (chestplate.is(TinkerTags.Items.CHESTPLATES) && player.getItemInHand(hand).isEmpty()) {
        InteractionResult result = InteractionHandler.onChestplateUse(player, chestplate, hand);
        // TODO: needed?
//        if (!player.isHandActive()) {
//          player.sendContainerToPlayer(player.container);
//        }
        if (result.shouldSwing()) {
          player.swing(hand, true);
        }
      }
    }
  }
}
