package tconstruct.tools.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.tinkering.MaterialItem;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.tools.IToolPart;

public class SlotToolStationIn extends Slot {

  public boolean dormant;
  public PartMaterialType restriction;
  public ItemStack icon;
  public Container parent;

  public SlotToolStationIn(IInventory inventoryIn, int index, int xPosition, int yPosition, Container parent) {
    super(inventoryIn, index, xPosition, yPosition);
    this.parent = parent;
  }

  @Override
  public void onSlotChanged() {
    // notify container to update craft result
    parent.onCraftMatrixChanged(inventory);
  }

  @Override
  public boolean isItemValid(ItemStack stack) {
    // dormant slots don't take any items, they can only be taken out of
    if(dormant) return false;

    // otherwise we check if we have item info and restrict it to that
    if(restriction != null)
      return restriction.isValid(stack);

    // note that we only take the part into acount when it's set. This is because it's only ever set clientside
    return super.isItemValid(stack);
  }

  public boolean isDormant() {
    return dormant;
  }

  public void activate() {
    dormant = false;
  }

  public void deactivate() {
    dormant = true;
  }

  public void setRestriction(PartMaterialType restriction) {
    this.restriction = restriction;

    if(restriction != null) {
      for(IToolPart part : restriction.getPossibleParts()) {
        if(part instanceof MaterialItem) {
          icon = ((MaterialItem) part).getItemstackWithMaterial(CustomTextureCreator.guiMaterial);
          break;
        }
      }
    }
    else {
      icon = null;
    }
  }
}
