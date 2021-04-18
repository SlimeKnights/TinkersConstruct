package slimeknights.tconstruct.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

public class ItemHandlerHelper {

  public static ItemStack copyStackWithSize(ItemStack stack, int count) {
    final ItemStack copy = stack.copy();
    copy.setCount(count);
    return copy;
  }

  public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b) {
    if (a.isEmpty() || !sameItem(a, b) || a.hasTag() != b.hasTag())
      return false;

    return (!a.hasTag() || a.getTag().equals(b.getTag()));
  }

  private static boolean sameItem(ItemStack a, ItemStack b) {
    return !a.isEmpty() && !b.isEmpty() && a.getItem() == b.getItem();
  }

  public static void giveItemToPlayer(PlayerEntity player, ItemStack stack, int selectedSlot) {
    if (player.inventory.getStack(selectedSlot) != ItemStack.EMPTY) {
      player.giveItemStack(stack);
    } else {
      player.inventory.insertStack(selectedSlot, stack);
    }
  }

  public static ItemStack insertItem(TinkerChestTileEntity dest, ItemStack stack) {
    if (dest == null || stack.isEmpty())
      return stack;

    for (int i = 0; i < dest.size(); i++) {
      if(dest.getStack(i) == ItemStack.EMPTY) {
        dest.setStack(i, stack);
        return stack;
      }
    }
    return stack;
  }
}
