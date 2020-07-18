package slimeknights.tconstruct.tables.inventory.table.toolstation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ToolStationOutSlot extends Slot {

  public ToolStationContainer parent;

  public ToolStationOutSlot(int index, int xPosition, int yPosition, ToolStationContainer parent) {
    super(new CraftResultInventory(), index, xPosition, yPosition);

    this.parent = parent;
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    return false;
  }

  @Nonnull
  @Override
  public ItemStack onTake(PlayerEntity playerIn, @Nonnull ItemStack stack) {
    net.minecraftforge.fml.hooks.BasicEventHooks.firePlayerCraftingEvent(playerIn, stack, this.parent.getTileEntity());
    this.parent.onResultTaken(playerIn, stack);
    stack.onCrafting(playerIn.getEntityWorld(), playerIn, 1);

    return super.onTake(playerIn, stack);
  }
}
