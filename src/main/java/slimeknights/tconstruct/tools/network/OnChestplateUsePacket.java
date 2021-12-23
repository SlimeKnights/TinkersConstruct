package slimeknights.tconstruct.tools.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tools.logic.InteractionHandler;

/** Packet sent client to server when the chestplate uses an empty hand interaction */
@RequiredArgsConstructor
public enum OnChestplateUsePacket implements IThreadsafePacket {
  MAINHAND(Hand.MAIN_HAND),
  OFFHAND(Hand.OFF_HAND);

  private final Hand hand;

  /** Gets the packet for the given hand */
  public static OnChestplateUsePacket from(Hand hand) {
    return hand == Hand.OFF_HAND ? OFFHAND : MAINHAND;
  }

  /** Gets the packet from the packet buffer */
  public static OnChestplateUsePacket read(PacketBuffer buffer) {
    return from(buffer.readEnumValue(Hand.class));
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeEnumValue(hand);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayerEntity player = context.getSender();
    if (player != null && !player.isSpectator()) {
      ItemStack chestplate = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
      if (TinkerTags.Items.CHESTPLATES.contains(chestplate.getItem()) && player.getHeldItem(hand).isEmpty()) {
        ActionResultType result = InteractionHandler.onChestplateUse(player, chestplate, hand);
        // TODO: needed?
//        if (!player.isHandActive()) {
//          player.sendContainerToPlayer(player.container);
//        }
        if (result.isSuccess()) {
          player.swing(hand, true);
        }
      }
    }
  }
}
