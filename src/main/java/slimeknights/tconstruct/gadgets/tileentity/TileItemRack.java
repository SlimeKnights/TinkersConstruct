package slimeknights.tconstruct.gadgets.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import slimeknights.tconstruct.gadgets.block.BlockRack;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class TileItemRack extends TileTable {

  // for the sake of the drying rack
  protected TileItemRack(String name, int slots) {
    super(name, slots, 1);
  }

  public TileItemRack() {
    this("gui.itemrack.name", 1);
  }

  @Override
  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

    for(int i = 0; i < this.getSizeInventory(); i++) {
      if(isStackInSlot(i)) {
        PropertyTableItem.TableItem item = getTableItem(getStackInSlot(i), this.getWorld(), null);
        assert item != null;
        item.y -= 11 / 16f;

        // changes based on item or block
        if(getStackInSlot(i).getItem() instanceof ItemBlock) {
          // shrink block models
          item.s = 0.5f;
        }
        else {
          // rotate items so they face downwards and move them down half a pixel
          item.s = 0.875f;
          item.r = (float) Math.PI * 2;
          item.y += 1 / 16f;
        }

        // adjust the item location based on the state
        switch(state.getValue(BlockRack.ORIENTATION)) {
          case DOWN_X:
          case DOWN_Z:
            item.y -= 12 / 16f;
            break;
          case NORTH:
          case SOUTH:
          case WEST:
          case EAST:
            item.z += 6 / 16f;
            break;
          default:
            break;
        }

        toDisplay.items.add(item);
      }
    }

    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }

  // note that if there is no slot 1, the item check will always return false
  public void interact(EntityPlayer player) {
    // completely empty -> insert current item into input
    if(!isStackInSlot(0) && !isStackInSlot(1)) {
      ItemStack stack = player.inventory.decrStackSize(player.inventory.currentItem, stackSizeLimit);
      setInventorySlotContents(0, stack);

      // take item out
    }
    else {
      // take out of stack 1 if something is in there, 0 otherwise
      int slot = isStackInSlot(1) ? 1 : 0;

      ItemHandlerHelper.giveItemToPlayer(player, getStackInSlot(slot));
      setInventorySlotContents(slot, ItemStack.EMPTY);
    }
  }
}
