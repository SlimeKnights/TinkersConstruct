package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

import slimeknights.mantle.tileentity.TileInventory;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

/** Represents a structure that has an inventory where it heats its items. Like a smeltery. */
public abstract class TileHeatingStructure extends TileInventory {

  private static final int TIME_FACTOR = 8; // basically an "accuracy" so the heat can be more fine grained. required temp is multpilied by this

  protected int fuel; // Ticks left until the current fuel is depleted and fuel is taken from the tanks. Depletes every tick
  protected int temperature; // internal temperature of the smeltery == speed of the smeltery
  protected boolean needsFuel; // If the last tick executed an operation that required fuel.

  protected int[] itemTemperatures; // current temperature of each item in the corresponding slot
  protected int[] itemTempRequired; // Temperature where the items want to goooooo

  public TileHeatingStructure(String name, int inventorySize, int maxStackSize) {
    super(name, inventorySize, maxStackSize);

    itemTemperatures = new int[0];
    itemTempRequired = new int[0];
  }

  @Override
  public void resize(int size) {
    super.resize(size);
    this.itemTemperatures = Arrays.copyOf(itemTemperatures, size);
    this.itemTempRequired = Arrays.copyOf(itemTempRequired, size);
  }


  public boolean canHeat(int index) {
    return temperature >= getHeatRequiredForSlot(index);
  }

  public float getProgress(int index) {
    if(index >= itemTemperatures.length) {
      return 0f;
    }
    return (float)itemTemperatures[index]/(float)itemTempRequired[index];
  }

  protected void setHeatRequiredForSlot(int index, int heat) {
    if(index < itemTempRequired.length) {
      itemTempRequired[index] = heat * TIME_FACTOR;
    }
  }

  protected int getHeatRequiredForSlot(int index) {
    if(index >= itemTempRequired.length) {
      return 0;
    }
    return itemTempRequired[index]/TIME_FACTOR;
  }

  /**
   * Calculate the heat required for the given slot
   */
  protected abstract void updateHeatRequired(int index);

  protected void heatItems() {
    for(int i = 0; i < getSizeInventory(); i++) {
      ItemStack stack = getStackInSlot(i);
      if(stack != null) {
        // heat item if possible
        if(itemTempRequired[i] > 0) {
          // fuel is present, turn up the heat
          if(fuel > 0) {
            // are we done heating?
            if(itemTemperatures[i] >= itemTempRequired[i]) {
              if(onItemFinishedHeating(stack, i)) {
                itemTemperatures[i] = 0;
                itemTempRequired[i] = 0;
              }
            }
            // otherwise turn up the heat
            else {
              itemTemperatures[i] += heatSlot(i);
            }
          }
          else {
            // can't heat. no fuel. abort and try to get fuel for next tick
            this.needsFuel = true;
            return;
          }
        }
      }
      else {
        itemTemperatures[i] = 0;
      }
    }
  }

  protected int heatSlot(int i) {
    return temperature/100; // if your smeltery has <100 heat then it deserves to not create any heat .
  }

  public int getTemperature(int i) {
    return itemTemperatures[i];
  }

  public int getTemperature() {
    return temperature;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    // reset heat if set to null or a different item
    if(itemstack == null || (getStackInSlot(slot) != null && !itemstack.getIsItemStackEqual(getStackInSlot(slot)))) {
      itemTemperatures[slot] = 0;
    }
    super.setInventorySlotContents(slot, itemstack);

    // when an item gets added, check for its heat required
    updateHeatRequired(slot);
  }

  /**
   * Called when an item finished heating up.
   * Return true if the processing was successful, then the heating data will be cleared.
   */
  protected abstract boolean onItemFinishedHeating(ItemStack stack, int slot);

  /**
   * Consume fuel if possible and increase the fuel-value accordingly
   */
  protected abstract void consumeFuel();

  protected void addFuel(int fuel, int newTemperature) {
    this.fuel += fuel;
    this.needsFuel = false;
    this.temperature = newTemperature;
  }

  public boolean hasFuel() {
    return fuel > 0;
  }

  /* Loading and Saving */

  @SideOnly(Side.CLIENT)
  public void updateTemperatureFromPacket(int temperature) {
    this.temperature = temperature;
  }

  @Override
  public void writeToNBT(NBTTagCompound tags) {
    super.writeToNBT(tags);
    tags.setInteger("fuel", fuel);
    tags.setInteger("temperature", temperature);
    tags.setBoolean("needsFuel", needsFuel);
    tags.setIntArray("itemTemperatures", itemTemperatures);
    tags.setIntArray("itemTempRequired", itemTempRequired);
  }

  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);
    fuel = tags.getInteger("fuel");
    temperature = tags.getInteger("temperature");
    needsFuel = tags.getBoolean("needsFuel");
    itemTemperatures = tags.getIntArray("itemTemperatures");
    itemTempRequired = tags.getIntArray("itemTempRequired");
  }
}
