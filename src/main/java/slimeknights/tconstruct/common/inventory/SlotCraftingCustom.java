package slimeknights.tconstruct.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotCraftingCustom extends SlotCrafting {

  private final InventoryCrafting craftMatrix;
  private final IContainerCraftingCustom callback;

  /**
   * @param callback           Container that gets the crafting call on crafting
   * @param player             Player that does the crafting
   * @param craftingInventory  Inventory where the ingredients are taken from
   * @param craftResult        Inventory where the result is put
   */
  public SlotCraftingCustom(IContainerCraftingCustom callback, EntityPlayer player, InventoryCrafting craftingInventory, IInventory craftResult, int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, craftResult, slotIndex, xPosition, yPosition);

    this.craftMatrix = craftingInventory;
    this.callback = callback;
  }

  @Override
  public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
    net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
    this.onCrafting(stack);

    callback.onCrafting(playerIn, stack, craftMatrix);
  }
}
