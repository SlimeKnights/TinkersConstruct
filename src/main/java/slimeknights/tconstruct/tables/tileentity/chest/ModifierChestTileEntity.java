package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.chest.ModifierChestContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModifierChestTileEntity extends TinkerChestTileEntity {

  public ModifierChestTileEntity() {
    super(TinkerTables.modifierChestTile.get(), "gui.tconstruct.modifier_chest", TinkerChestTileEntity.MAX_INVENTORY, 1);
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new ModifierChestContainer(menuId, playerInventory, this);
  }

  @Override
  public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    // TODO: implement properly, probably a tag
    return true;
  }
}
