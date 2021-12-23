package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tools.common.tileentity.TileCraftingStation;

public class CraftingStationItemHandler extends ConfigurableInvWrapperCapability {

  private final TileCraftingStation tile;

  public CraftingStationItemHandler(TileCraftingStation tile, boolean canInsert, boolean canExtract) {
    super(tile, canInsert, canExtract);

    this.tile = tile;
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    ItemStack itemStack = super.insertItem(slot, stack, simulate);
    if(!simulate && itemStack != stack) {
      updateRecipeInOpenGUIs();
    }
    return itemStack;
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    ItemStack itemStack = super.extractItem(slot, amount, simulate);
    if(!simulate && !itemStack.isEmpty()) {
      updateRecipeInOpenGUIs();
    }
    return itemStack;
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    super.setStackInSlot(slot, stack);
    updateRecipeInOpenGUIs();
  }

  private void updateRecipeInOpenGUIs() {
    if(!tile.getWorld().isRemote) {
      tile.getWorld().playerEntities.stream()
                               .filter(player -> player.openContainer instanceof ContainerCraftingStation)
                               .forEach(player -> ((ContainerCraftingStation) player.openContainer).onCraftMatrixChanged());
    }
  }
}
