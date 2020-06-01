package slimeknights.tconstruct.tables.inventory.table.crafting;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.tconstruct.shared.inventory.PersistentCraftingInventory;

public class FastCraftingResultSlot extends CraftingResultSlot {

  private final CraftingStationContainer craftingStationContainer;
  private final PersistentCraftingInventory persistentCraftingInventory;

  public FastCraftingResultSlot(CraftingStationContainer craftingStationContainer, PlayerEntity player, PersistentCraftingInventory craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, inventoryIn, slotIndex, xPosition, yPosition);
    this.craftingStationContainer = craftingStationContainer;
    this.persistentCraftingInventory = craftingInventory;
  }

  @Override
  public ItemStack decrStackSize(int amount) {
    if (this.getHasStack()) {
      this.amountCrafted += Math.min(amount, this.getStack().getCount());
    }

    return super.decrStackSize(amount);
  }

  @Override
  protected void onCrafting(ItemStack stack) {
    if (this.amountCrafted > 0) {
      stack.onCrafting(this.player.world, this.player, this.amountCrafted);
      net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerCraftingEvent(this.player, stack, this.craftMatrix);
    }

    if (this.inventory instanceof IRecipeHolder) {
      ((IRecipeHolder) this.inventory).onCrafting(this.player);
    }

    this.amountCrafted = 0;
  }

  public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
    this.onCrafting(stack);
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
    NonNullList<ItemStack> nonnulllist = craftingStationContainer.getRemainingItems();
    net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

    persistentCraftingInventory.setDoNotCallUpdates(true);

    for (int i = 0; i < nonnulllist.size(); ++i) {
      ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
      ItemStack itemstack1 = nonnulllist.get(i);

      if (!itemstack.isEmpty()) {
        this.craftMatrix.decrStackSize(i, 1);
        itemstack = this.craftMatrix.getStackInSlot(i);
      }

      if (!itemstack1.isEmpty()) {
        if (itemstack.isEmpty()) {
          this.craftMatrix.setInventorySlotContents(i, itemstack1);
        } else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
          itemstack1.grow(itemstack.getCount());
          this.craftMatrix.setInventorySlotContents(i, itemstack1);
        } else if (!this.player.inventory.addItemStackToInventory(itemstack1)) {
          this.player.dropItem(itemstack1, false);
        }
      }
    }

    persistentCraftingInventory.setDoNotCallUpdates(false);
    craftingStationContainer.onCraftMatrixChanged(persistentCraftingInventory);

    return stack;
  }
}
