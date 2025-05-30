package slimeknights.tconstruct.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.network.packet.IThreadsafePacket;

public record UpdateInventoryPagePacket(int slot, String page) implements IThreadsafePacket {
  public UpdateInventoryPagePacket(FriendlyByteBuf buffer) {
    this(buffer.readVarInt(), buffer.readUtf(100));
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeVarInt(slot);
    buffer.writeUtf(this.page);
  }

  @Override
  public void handleThreadsafe(Context context) {
    Player player = context.getSender();
    if (player != null && slot >= 0 && page != null) {
      ItemStack stack = player.getInventory().getItem(slot);
      if (!stack.isEmpty()) {
        BookHelper.writeSavedPageToBook(stack, page);
      }
    }
  }
}
