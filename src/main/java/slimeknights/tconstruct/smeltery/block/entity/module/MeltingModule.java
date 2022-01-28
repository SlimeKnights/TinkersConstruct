package slimeknights.tconstruct.smeltery.block.entity.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tconstruct.common.network.InventorySlotSyncPacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This class represents a single item slot that can melt into a liquid
 */
@RequiredArgsConstructor
public class MeltingModule implements IMeltingContainer, ContainerData {
  public static final int NO_SPACE = -1;

  private static final String TAG_CURRENT_TIME = "time";
  private static final String TAG_REQUIRED_TIME = "required";
  private static final String TAG_REQUIRED_TEMP = "temp";
  private static final int CURRENT_TIME = 0;
  private static final int REQUIRED_TIME = 1;
  private static final int REQUIRED_TEMP = 2;

  /** Tile entity containing this melting module */
  private final MantleBlockEntity parent;
  /** Function that accepts fluid output from this module */
  private final Predicate<IMeltingRecipe> outputFunction;
  /** Function that boosts the ores based on the rate type */
  private final IOreRate oreRate;
  /** Slot index for updates */
  private final int slotIndex;

  /** Current time of the item in the slot */
  @Getter
  private int currentTime = 0;
  /** Required time for the item in the slot */
  @Getter
  private int requiredTime = 0;
  /** Required temperature for the item in the slot */
  @Getter
  private int requiredTemp = 0;

  /** Last recipe this slot contained */
  private IMeltingRecipe lastRecipe;

  /** Current item in this slot */
  @Getter
  private ItemStack stack = ItemStack.EMPTY;

  @Override
  public IOreRate getOreRate() {
    return oreRate;
  }

  /**
   * Resets recipe time values
   */
  private void resetRecipe() {
    currentTime = 0;
    requiredTime = 0;
    requiredTemp = 0;
  }

  /**
   * Sets the contents of this module
   * @param newStack  New stack
   */
  public void setStack(ItemStack newStack) {
    // send a slot update to the client when items change, so we can update the TESR
    Level world = parent.getLevel();
    if (slotIndex != -1 && world != null && !world.isClientSide && !ItemStack.matches(stack, newStack)) {
      TinkerNetwork.getInstance().sendToClientsAround(new InventorySlotSyncPacket(newStack, slotIndex, parent.getBlockPos()), world, parent.getBlockPos());
    }

    // clear progress if setting to empty or the items do not match
    if (newStack.isEmpty()) {
      resetRecipe();
    } else if (this.stack.isEmpty() || !ItemHandlerHelper.canItemStacksStack(this.stack, newStack)) {
      currentTime = 0;
    }

    // update stack and heat required
    this.stack = newStack;
    int newTime = 0;
    int newTemp = 0;
    if(!stack.isEmpty()) {
      IMeltingRecipe recipe = findRecipe();
      if (recipe != null) {
        newTime = recipe.getTime(this) * 10;
        newTemp = recipe.getTemperature(this);
      }
    }
    requiredTime = newTime;
    requiredTemp = newTemp;
    parent.setChangedFast();
  }


  /**
   * Checks if this slot has an item it can heat
   * @param  temperature  Temperature to try
   * @return  True if this slot has an item it can heat
   */
  public boolean canHeatItem(int temperature) {
    // must have a recipe and an item
    if (requiredTime > 0) {
      if (stack.isEmpty()) {
        resetRecipe();
        return false;
      }
      // don't mark items as can heat if done heating
      return currentTime != NO_SPACE && temperature >= requiredTemp;
    }
    return false;
  }

  /**
   * Heats the item in this slot
   * @param temperature     Heating structure temperature
   */
  public void heatItem(int temperature) {
    // if the slot is able to be heated, heat it
    if (currentTime == NO_SPACE || canHeatItem(temperature)) {
      // if we are done, cook item
      if (currentTime == NO_SPACE || currentTime >= requiredTime) {
        if (onItemFinishedHeating()) {
          resetRecipe();
        }
      } else {
        currentTime += temperature / 100;
      }
    }
  }

  /**
   * Cools down the item, reversing the recipe progress
   */
  public void coolItem() {
    // if done heating but no space, try placing into the smeltery,
    // cooling done that already finished smelting causes the smeltery to constantly drain fuel
    if (currentTime == NO_SPACE) {
      if (onItemFinishedHeating()) {
        resetRecipe();
      }
      // if the item is heated, cool down rapidly
    } else if (currentTime > 0 && requiredTime > 0) {
      currentTime -= 5;
    }
  }

  /**
   * Finds a melting recipe
   * @return  Melting recipe found, or null if no match
   */
  @Nullable
  private IMeltingRecipe findRecipe() {
    Level world = parent.getLevel();
    if (world == null) {
      return null;
    }

    // first, try last recipe for the slot
    IMeltingRecipe last = lastRecipe;
    if (last != null && last.matches(this, world)) {
      return last;
    }
    // if that fails, try to find a new recipe
    Optional<IMeltingRecipe> newRecipe = world.getRecipeManager().getRecipeFor(RecipeTypes.MELTING, this, world);
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

    // try filling the output tank, if successful empty the slot
    if (outputFunction.test(recipe)) {
      setStack(ItemStack.EMPTY);
      return true;
    }

    currentTime = NO_SPACE;
    return false;
  }

  /**
   * Writes this module to NBT
   * @return  Module in NBT
   */
  public CompoundTag writeToTag() {
    CompoundTag nbt = new CompoundTag();
    if (!stack.isEmpty()) {
      stack.save(nbt);
      nbt.putInt(TAG_CURRENT_TIME, currentTime);
      nbt.putInt(TAG_REQUIRED_TIME, requiredTime);
      nbt.putInt(TAG_REQUIRED_TEMP, requiredTemp);
    }
    return nbt;
  }

  /**
   * Reads this module from NBT
   * @param nbt  NBT
   */
  public void readFromTag(CompoundTag nbt) {
    stack = ItemStack.of(nbt);
    if (!stack.isEmpty()) {
      currentTime = nbt.getInt(TAG_CURRENT_TIME);
      requiredTime = nbt.getInt(TAG_REQUIRED_TIME);
      requiredTemp = nbt.getInt(TAG_REQUIRED_TEMP);
    }
  }

  /* Container sync */

  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public int get(int index) {
    return switch (index) {
      case CURRENT_TIME -> currentTime;
      case REQUIRED_TIME -> requiredTime;
      case REQUIRED_TEMP -> requiredTemp;
      default -> 0;
    };
  }

  @Override
  public void set(int index, int value) {
    switch (index) {
      case CURRENT_TIME -> currentTime = value;
      case REQUIRED_TIME -> requiredTime = value;
      case REQUIRED_TEMP -> requiredTemp = value;
    }
  }
}
