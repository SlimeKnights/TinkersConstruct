package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;

public class TileCastingTable extends TileCasting {

  @Override
  protected ICastingRecipe findRecipe(ItemStack cast, Fluid fluid) {
    return TinkerRegistry.getTableCasting(cast, fluid);
  }

  @Override
  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

    for(int i = 0; i < this.getSizeInventory(); i++) {
      if(isStackInSlot(i)) {
        PropertyTableItem.TableItem item = getTableItem(getStackInSlot(i), this.getWorld(), null);
        assert item != null;
        item.s = 0.875f;// * 0.875f;
        item.y -= 1 / 16f * item.s;

        //item.s = 1f;
        toDisplay.items.add(item);
        if(i == 0) {
          item.y -= 0.001f; // don't overlap
        }
      }
    }

    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }
}
