package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fluids.Fluid;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;

public class TileCastingBasin extends TileCasting {

  @Override
  protected ICastingRecipe findRecipe(ItemStack cast, Fluid fluid) {
    return TinkerRegistry.getBasinCasting(cast, fluid);
  }

  @Override
  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

    for(int i = 0; i < this.getSizeInventory(); i++) {
      if(isStackInSlot(i)) {
        PropertyTableItem.TableItem item = getTableItem(getStackInSlot(i), this.getWorld(), null);
        assert item != null;
        item.s = 12 / 16f;
        item.y -= 9 / 16f;
        item.y -= 0.001f;
        toDisplay.items.add(item);
      }
    }

    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }
}
