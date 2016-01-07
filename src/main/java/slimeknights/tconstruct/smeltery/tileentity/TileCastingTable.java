package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;

public class TileCastingTable extends TileCasting {

  @Override
  protected CastingRecipe findRecipe(ItemStack cast, Fluid fluid) {
    return TinkerRegistry.getTableCasting(cast, fluid);
  }

  @Override
  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

    for(int i = 0; i < this.getSizeInventory(); i++) {
      if(isStackInSlot(i)) {
        PropertyTableItem.TableItem item = getTableItem(getStackInSlot(i));
        item.y -= 1/16f;
        item.s = 1;
        toDisplay.items.add(item);
      }
    }

    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }
}
