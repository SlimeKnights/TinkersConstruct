package slimeknights.tconstruct.smeltery.tileentity.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class represents a single item slot that can melt into a liquid
 */
@RequiredArgsConstructor
public class MeltingModule implements ISingleItemInventory, IIntArray {
  public static final int NO_SPACE = -1;

  private static final String TAG_CURRENT_TEMP = "temp";
  private static final String TAG_REQUIRED_TEMP = "required";
  private static final int CURRENT_TEMP = 0;
  private static final int REQUIRED_TEMP = 1;

  /** Tile entity containing this melting module */
  private final MantleTileEntity parent;
  /** Function that accepts fluid output from this module */
  private final Predicate<FluidStack> outputFunction;

  /** Current temperature of the item in the slot */
  @Getter
  private int currentTemp = 0;
  /** Required temperature for the item in the slot */
  @Getter
  private int requiredTemp = 0;

  /** Last recipe this slot contained */
  private IMeltingRecipe lastRecipe;

  /** Current item in this slot */
  @Getter
  private ItemStack stack = ItemStack.EMPTY;

  /**
   * Sets the contents of this module
   * @param newStack  New stack
   */
  public void setStack(ItemStack newStack) {
    // clear progress if setting to empty or the items do not match
    if (this.stack.isEmpty() || newStack.isEmpty() || !ItemHandlerHelper.canItemStacksStack(this.stack, newStack)) {
      currentTemp = 0;
    }

    // update stack and heat required
    this.stack = newStack;
    int newHeat = 0;
    if(!stack.isEmpty()) {
      IMeltingRecipe recipe = findRecipe();
      if (recipe != null) {
        newHeat = recipe.getTemperature(this);
      }
    }
    requiredTemp = newHeat;
    parent.markDirtyFast();
  }


  /**
   * Checks if this slot has an item it can heat
   * @return  True if this slot has an item it can heat
   */
  public boolean canHeatItem() {
    // must have a recipe and an item
    if (requiredTemp > 0) {
      if (stack.isEmpty()) {
        currentTemp = 0;
        requiredTemp = 0;
        return false;
      }
      // don't mark items as can heat if done heating
      return currentTemp != NO_SPACE;
    }
    return false;
  }

  /**
   * Heats the item in this slot
   * @param temperature     Heating structure temperature
   */
  public void heatItem(int temperature) {
    // if the slot is able to be heated, heat it
    if ((canHeatItem() || currentTemp == NO_SPACE) && temperature >= requiredTemp) {
      // if we are done, cook item
      if (currentTemp == NO_SPACE || currentTemp >= requiredTemp) {
        if (onItemFinishedHeating()) {
          currentTemp = 0;
          requiredTemp = 0;
        }
      } else {
        currentTemp += temperature / 200;
      }
    }
  }

  /**
   * Cools down the item, reversing the recipe progress
   */
  public void coolItem() {
    // if done heating but no space, try placing into the smeltery,
    // cooling done that already finished smelting causes the smeltery to constantly drain fuel
    if (currentTemp == NO_SPACE) {
      if (onItemFinishedHeating()) {
        currentTemp = 0;
        requiredTemp = 0;
      }
      // if the item is heated, cool down rapidly
    } else if (canHeatItem() && currentTemp > 0) {
      currentTemp -= 5;
    }
  }

  /**
   * Finds a melting recipe
   * @return  Melting recipe found, or null if no match
   */
  @Nullable
  private IMeltingRecipe findRecipe() {
    World world = parent.getWorld();
    if (world == null) {
      return null;
    }

    // first, try last recipe for the slot
    IMeltingRecipe last = lastRecipe;
    if (last != null && last.matches(this, world)) {
      return last;
    }
    // if that fails, try to find a new recipe
    Optional<IMeltingRecipe> newRecipe = world.getRecipeManager().getRecipe(RecipeTypes.MELTING, this, world);
    if (newRecipe.isPresent()) {
      lastRecipe = newRecipe.get();
      return lastRecipe;
    }
    return null;
  }

  /**
   * Called when the slot finishes heating its item
   * @return  True if the slot should clear its state
   */
  private boolean onItemFinishedHeating() {
    IMeltingRecipe recipe = findRecipe();
    if (recipe == null) {
      return true;
    }

    // get output fluid
    FluidStack output = recipe.getOutput(this);
    if (output.isEmpty()) {
      return true;
    }

    // try filling the output tank, if successful empty the slot
    if (outputFunction.test(output)) {
      setStack(ItemStack.EMPTY);
      return true;
    }

    currentTemp = NO_SPACE;
    return false;
  }

  /**
   * Writes this module to NBT
   * @return  Module in NBT
   */
  public CompoundNBT writeToNBT() {
    CompoundNBT nbt = new CompoundNBT();
    if (!stack.isEmpty()) {
      stack.write(nbt);
      nbt.putInt(TAG_CURRENT_TEMP, currentTemp);
      nbt.putInt(TAG_REQUIRED_TEMP, requiredTemp);
    }
    return nbt;
  }

  /**
   * Reads this module from NBT
   * @param nbt  NBT
   */
  public void readFromNBT(CompoundNBT nbt) {
    stack = ItemStack.read(nbt);
    currentTemp = nbt.getInt(TAG_CURRENT_TEMP);
    requiredTemp = nbt.getInt(TAG_REQUIRED_TEMP);
  }

  /* Container sync */

  @Override
  public int size() {
    return 2;
  }

  @Override
  public int get(int index) {
    switch (index) {
      case CURRENT_TEMP:
        return currentTemp;
      case REQUIRED_TEMP:
        return requiredTemp;
    }
    return 0;
  }

  @Override
  public void set(int index, int value) {
    switch (index) {
      case CURRENT_TEMP:
        currentTemp = value;
        break;
      case REQUIRED_TEMP:
        requiredTemp = value;
        break;
    }
  }
}
