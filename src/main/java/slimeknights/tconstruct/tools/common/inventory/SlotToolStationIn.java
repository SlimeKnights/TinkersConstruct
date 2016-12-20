package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;

import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IToolPart;

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
    if(dormant) {
      return false;
    }

    // otherwise we check if we have item info and restrict it to that
    if(restriction != null) {
      if(stack != null && stack.getItem() instanceof IToolPart) {
        return restriction.isValidItem((IToolPart) stack.getItem());
      }
      return false;
    }

    // note that we only take the part into account when it's set. This is because it's only ever set clientside
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
  }

  @SideOnly(Side.CLIENT)
  public void updateIcon() {
    icon = null;
    if(restriction != null) {
      Iterator<IToolPart> iterator = restriction.getPossibleParts().iterator();
      while(iterator.hasNext() && icon == null) {
        icon = iterator.next().getOutlineRenderStack();
      }
    }
  }
}
