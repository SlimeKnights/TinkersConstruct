package slimeknights.tconstruct.smeltery.recipe;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.nbt.CompoundTag;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;

import javax.annotation.Nullable;

/**
 * Inventory containing a single item and a fluid
 */
public interface ICastingInventory extends ISingleItemInventory {
  /**
   * Gets the contained fluid in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();

  /**
   * Gets the NBT for the contained fluid
   * @return  Fluid's NBT
   */
  @Nullable
  default CompoundTag getFluidTag() {
    return null;
  }
}
