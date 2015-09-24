package slimeknights.mantle.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IContainerCraftingCustom {
  void onCrafting(EntityPlayer player, ItemStack output, IInventory craftMatrix);
}
