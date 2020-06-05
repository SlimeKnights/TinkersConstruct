package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.tables.inventory.chest.PartChestContainer;
import slimeknights.tconstruct.tileentities.TablesTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PartChestTileEntity extends TinkerChestTileEntity {

  public PartChestTileEntity() {
    super(TablesTileEntities.part_chest.get(), "gui.tconstruct.part_chest");
  }

  @Override
  public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {

    return true;
    //TODO
    /*
    // check if there is no other slot containing that item
    for (int i = 0; i < this.getSizeInventory(); i++) {
      // don't compare count
      if (ItemStack.areItemsEqual(itemstack, this.getStackInSlot(i))
        && ItemStack.areItemStackTagsEqual(itemstack, this.getStackInSlot(i))) {
        return i == slot; // only allowed in the same slot
      }
    }

    return itemstack.getItem() instanceof IToolPart;*/
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new PartChestContainer(menuId, playerInventory, this);
  }
}
