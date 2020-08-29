package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;
import slimeknights.mantle.recipe.inventory.InventorySlotWrapper;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;

import javax.annotation.Nonnull;
import java.util.Arrays;

public abstract class HeatingStructureTileEntity<T extends MultiblockDetection> extends MultiblockTile<T> {
  /* tags */
  public static final String TAG_FUEL = "fuel";
  public static final String TAG_TEMPERATURE = "temperature";
  public static final String TAG_NEEDS_FUEL = "needsFuel";
  public static final String TAG_ITEM_TEMPERATURES = "itemTemperatures";
  public static final String TAG_ITEM_TEMP_REQUIRED = "itemTempRequired";

  /** Number of ticks of fuel left */
  @Getter @Setter
  protected int fuel;
  /** Temperature of the current fuel */
  @Getter @Setter
  protected int temperature;
  /** If the last tick executed an operation that required fuel. */
  protected boolean needsFuel;

  /** Recipe slot wrappers for recipe fetches */
  protected ISingleItemInventory[] slotWrappers;
  /** Current temperature of items in each corresponding slot */
  @Getter
  protected int[] itemTemperatures;
  /** Needed temperature of the items in each corresponding slot */
  @Getter
  protected int[] itemTempRequired;

  public HeatingStructureTileEntity(TileEntityType<?> type, ITextComponent name, int inventorySize, int maxStackSize) {
    super(type, name, inventorySize, maxStackSize);

    this.slotWrappers = new ISingleItemInventory[this.getSizeInventory()];
    for (int i = 0; i < getSizeInventory(); i++) {
      this.slotWrappers[i] = new InventorySlotWrapper(this, i);
    }
    itemTemperatures = new int[this.getSizeInventory()];
    itemTempRequired = new int[this.getSizeInventory()];
  }

  @Override
  public void resize(int size) {
    super.resize(size);
    this.slotWrappers = Arrays.copyOf(slotWrappers, size);
    for (int i = 0; i < size; i++) {
      this.slotWrappers[i] = new InventorySlotWrapper(this, i);
    }
    this.itemTemperatures = Arrays.copyOf(itemTemperatures, size);
    this.itemTempRequired = Arrays.copyOf(itemTempRequired, size);
  }

  /*
   * Heating
   */

  /**
   * Heats items in the inventory
   * @return True if we need fuel
   */
  protected boolean heatItems() {
    boolean heatedItem = false;
    for(int i = 0; i < slotWrappers.length; i++) {
      // if we have a recipe
      int required = itemTempRequired[i];
      if (required > 0) {
        // if empty, clear required temp
        ISingleItemInventory inv = slotWrappers[i];
        if (inv.isEmpty()) {
          itemTempRequired[i] = 0;
        } else if (!hasFuel()) {
          // needs fuel
          return true;
        } else if (temperature >= required) {
          // if we are done, cook item
          if (itemTemperatures[i] >= required) {
            if (onItemFinishedHeating(inv, i)) {
              itemTemperatures[i] = 0;
              itemTempRequired[i] = 0;
            }
          } else {
            itemTemperatures[i] += temperature / 100;
            heatedItem = true;
          }
        }
      }
    }
    // if we heated anything, decrease fuel
    if(heatedItem) {
      fuel--;
    }
    // no fuel needed
    return false;
  }

  /**
   * Updates the heat required for the slot
   * @param slot  Slot index
   */
  protected abstract void updateHeatRequired(int slot);

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    // reset current heat if set to null or a different item
    ItemStack current = getStackInSlot(slot);
    if (itemstack.isEmpty() || current.isEmpty() || !ItemStack.areItemStacksEqual(itemstack, current)) {
      itemTemperatures[slot] = 0;
    }
    // update contents
    super.setInventorySlotContents(slot, itemstack);
    // update recipe
    updateHeatRequired(slot);
  }

  /**
   * Called when an item finishes heating
   * @param inv   Item inventory
   * @param slot  Slot index
   * @return  True if the item successfully heated, false otherwise
   */
  protected abstract boolean onItemFinishedHeating(ISingleItemInventory inv, int slot);

  /*
   * Fueling
   */

  /** Checks if we have fuel in the structure */
  protected boolean hasFuel() {
    return fuel > 0;
  }

  /**
   * Consume fuel if possible and increase the fuel-value accordingly
   */
  protected abstract void consumeFuel();

  /*
   * Client side
   */

  /**
   * Gets the percentage a slot is towards completion
   * @param slot  Slot index
   * @return  Slot percentage
   */
  public float getHeatingProgress(int slot) {
    if(slot < 0 || slot > getSizeInventory() - 1) {
      return Float.NaN;
    }
    // no heat error state
    int required = itemTempRequired[slot];
    if (temperature < required) {
      return -1;
    }
    return itemTemperatures[slot] / (float) required;
  }

  /*
   * NBT
   */

  @Override
  public void read(BlockState state, CompoundNBT tag) {
    fuel = tag.getInt(TAG_FUEL);
    temperature = tag.getInt(TAG_TEMPERATURE);
    needsFuel = tag.getBoolean(TAG_NEEDS_FUEL);
    itemTemperatures = validate(tag.getIntArray(TAG_ITEM_TEMPERATURES), getSizeInventory());
    itemTempRequired = validate(tag.getIntArray(TAG_ITEM_TEMP_REQUIRED), getSizeInventory());
    super.read(state, tag);
  }

  /**
   * Validates that an int array has the proper size
   * @param array  Array to validate
   * @param size   Size to check
   * @return  Array if its the proper size, or a copy of contents with the correct size
   */
  private static int[] validate(int[] array, int size) {
    if (array.length != size) {
      return Arrays.copyOf(array, size);
    }
    return array;
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.putInt(TAG_FUEL, fuel);
    tag.putInt(TAG_TEMPERATURE, temperature);
    tag.putBoolean(TAG_NEEDS_FUEL, needsFuel);
    tag.putIntArray(TAG_ITEM_TEMPERATURES, itemTemperatures);
    tag.putIntArray(TAG_ITEM_TEMP_REQUIRED, itemTempRequired);
    return super.write(tag);
  }

  @Nonnull
  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of write
    return this.write(new CompoundNBT());
  }
}
