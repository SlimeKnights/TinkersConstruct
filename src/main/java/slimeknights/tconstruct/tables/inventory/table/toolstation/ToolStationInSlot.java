
package slimeknights.tconstruct.tables.inventory.table.toolstation;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.tools.IToolPart;

public class ToolStationInSlot extends Slot {

  public Container parent;
  public boolean dormant;

  public ItemStack icon;

  public PartMaterialRequirement restriction;

  public ToolStationInSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, Container parentContainer) {
    super(inventoryIn, index, xPosition, yPosition);
    this.parent = parentContainer;
  }

  @Override
  public void onSlotChanged() {
    this.parent.onCraftMatrixChanged(this.inventory);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    // dormant slots don't take any items, they can only be taken out of
    if (this.dormant) {
      return false;
    }

    if (this.restriction != null) {
      if (stack != ItemStack.EMPTY && stack.getItem() instanceof IToolPart) {
        return this.restriction.isValidItem(stack.getItem());
      }
      return false;
    }

    return super.isItemValid(stack);
  }

  public boolean isDormant() {
    return this.dormant;
  }

  public void activate() {
    this.dormant = false;
  }

  public void deactivate() {
    this.dormant = true;
  }

  public void setRestriction(PartMaterialRequirement restriction) {
    this.restriction = restriction;
  }

  @OnlyIn(Dist.CLIENT)
  public void updateIcon() {
    this.icon = null;
    if (this.restriction != null) {
      icon = new ItemStack(restriction.getPossiblePart());
    }
  }
}
