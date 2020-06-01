package slimeknights.tconstruct.tables.inventory.table.crafting;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import javax.annotation.Nonnull;

public class CraftingStationItemHandler extends ConfigurableInvWrapperCapability {

  private final CraftingStationTileEntity craftingStationTileEntity;

  public CraftingStationItemHandler(CraftingStationTileEntity inv, boolean canInsert, boolean canExtract) {
    super(inv, canInsert, canExtract);

    this.craftingStationTileEntity = inv;
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    ItemStack itemStack = super.insertItem(slot, stack, simulate);

    if (!simulate && itemStack != stack) {
      this.updateRecipeInOpenGUIs();
    }

    return itemStack;
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    ItemStack itemStack = super.extractItem(slot, amount, simulate);

    if (!simulate && !itemStack.isEmpty()) {
      this.updateRecipeInOpenGUIs();
    }

    return itemStack;
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    super.setStackInSlot(slot, stack);

    this.updateRecipeInOpenGUIs();
  }

  private void updateRecipeInOpenGUIs() {
    if (this.craftingStationTileEntity.getWorld() != null && !this.craftingStationTileEntity.getWorld().isRemote) {
      this.craftingStationTileEntity.getWorld().getPlayers().stream()
        .filter(player -> player.openContainer instanceof CraftingStationContainer)
        .forEach(player -> ((CraftingStationContainer) player.openContainer).onCraftMatrixChanged());
    }
  }
}
