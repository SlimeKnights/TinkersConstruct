package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.recipe.inventory.InventorySlotWrapper;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.shared.tileentity.TableTileEntity;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.MelterBlock;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.tileentity.inventory.MelterFuelWrapper;
import slimeknights.tconstruct.tables.client.model.ModelProperties;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Optional;

public class MelterTileEntity extends TableTileEntity implements ITankTileEntity, ITickableTileEntity {
  /** Max capacity for the tank */
  private static final int TANK_CAPACITY = MaterialValues.VALUE_Block;
  /* tags */
  private static final String TAG_FUEL = "fuel";
  private static final String TAG_TEMPERATURE = "temperature";
  private static final String TAG_ITEM_TEMPERATURES = "itemTemperatures";
  private static final String TAG_ITEM_TEMP_REQUIRED = "itemTempRequired";

  /* Tank */
  /** Internal fluid tank output */
  @Getter
  protected final FluidTankAnimated tank = new FluidTankAnimated(TANK_CAPACITY, this);
  /** Capability holder for the tank */
  private final LazyOptional<IFluidHandler> tankHolder = LazyOptional.of(() -> tank);
  /** Tank data for the model */
  private final ModelDataMap modelData;
  /** Last comparator strength to reduce block updates */
  private int lastStrength = -1;

  /* Heating */
  /** Internal tick counter */
  private int tick;
  /** Recipe slot wrappers for recipe fetches */
  private final ISingleItemInventory[] slotWrappers;
  /** Last recipe seen for each of the slots */
  private final IMeltingRecipe[] lastRecipe;

  /** Current temperature of items in each corresponding slot */
  @Getter
  private int[] itemTemperatures;
  /** Needed temperature of the items in each corresponding slot */
  @Getter
  private int[] itemTempRequired;

  /* Fuel */
  /** Fluid inventory to find new fuels */
  @Nullable
  private MelterFuelWrapper fuelInventory;
  /** Cache of the last fuel we tried to fetch */
  @Nullable
  private MeltingFuel lastFuel;
  /** Temperature of current fuel */
  @Getter @Setter
  private int temperature;
  /** Number of ticks of fuel left */
  @Getter @Setter
  private int fuel;

  /** Main constructor */
  public MelterTileEntity() {
    this(TinkerSmeltery.melter.get());
  }

  /** Extendable constructor */
  protected MelterTileEntity(TileEntityType<? extends MelterTileEntity> type) {
    super(type, Util.makeTranslationKey("gui", "melter"), 3, 1);

    // melting
    this.slotWrappers = new ISingleItemInventory[3];
    for (int i = 0; i < 3; i++) {
      this.slotWrappers[i] = new InventorySlotWrapper(this, i);
    }
    this.lastRecipe = new IMeltingRecipe[3];
    this.itemTemperatures = new int[3];
    this.itemTempRequired = new int[3];

    // tank data
    modelData = new ModelDataMap.Builder()
      .withInitial(ModelProperties.FLUID_TANK, tank)
      .build();
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity playerEntity) {
    return new MelterContainer(id, inv, this);
  }

  /*
   * Tank methods
   */


  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return tankHolder.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public int getLastStrength() {
    return lastStrength;
  }

  @Override
  public void setLastStrength(int strength) {
    lastStrength = strength;
  }

  @Override
  public IModelData getModelData() {
    return modelData;
  }

  /*
   * Melting
   */

  @Override
  public void tick() {
    if(!isServerWorld()) {
      return;
    }

    // are we fully formed?
    if(isActive()) {
      if(tick % 4 == 0) {
        // returns true if we need fuel
        if (heatItems()) {
          consumeFuel();
        }
      }
    }

    tick = (tick + 1) % 20;
  }

  /**
   * Heats items in the inventory
   * @return True if we need fuel
   */
  private boolean heatItems() {
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
   * Finds a melting recipe
   * @param inv  Inventory instance
   * @param slot    Slot index for cache
   * @return  Melting recipe found, or null if no match
   */
  @Nullable
  private IMeltingRecipe findRecipe(ISingleItemInventory inv, int slot) {
    if (world == null) {
      return null;
    }
    // first, try last recipe for the slot
    IMeltingRecipe last = lastRecipe[slot];
    if (last != null && last.matches(inv, world)) {
      return last;
    }
    // if that fails, try to find a new recipe
    Optional<IMeltingRecipe> newRecipe = world.getRecipeManager().getRecipe(RecipeTypes.MELTING, inv, world);
    if (newRecipe.isPresent()) {
      lastRecipe[slot] = newRecipe.get();
      return lastRecipe[slot];
    }
    return null;
  }

  /**
   * Updates the heat required for the slot
   * @param slot  Slot index
   */
  private void updateHeatRequired(int slot) {
    ISingleItemInventory inv = slotWrappers[slot];
    int newHeat = 0;
    if(!inv.isEmpty()) {
      IMeltingRecipe recipe = findRecipe(inv, slot);
      if (recipe != null) {
        newHeat = recipe.getTemperature(inv);
      }
    }
    itemTempRequired[slot] = newHeat;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack stack) {
    // reset current heat if set to null or a different item
    ItemStack current = getStackInSlot(slot);
    if(stack.isEmpty() || current.isEmpty() || !ItemStack.areItemStacksEqual(stack, current)) {
      itemTemperatures[slot] = 0;
    }
    // update contents
    super.setInventorySlotContents(slot, stack);
    // update recipe
    updateHeatRequired(slot);
  }

  /**
   * Called when an item finishes heating
   * @param inv   Item inventory
   * @param slot  Slot index
   * @return  True if the item successfully heated, false otherwise
   */
  private boolean onItemFinishedHeating(ISingleItemInventory inv, int slot) {
    IMeltingRecipe recipe = findRecipe(inv, slot);
    if (recipe == null) {
      return false;
    }

    // get output fluid
    FluidStack output = recipe.getOutput(inv);
    if (output.isEmpty()) {
      return false;
    }

    // try to fill tank, if failed set error
    int filled = tank.fill(output.copy(), FluidAction.SIMULATE);
    if (filled != output.getAmount()) {
      // TODO: proper error state enum?
      itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
      return false;
    }

    // actually fill the tank
    tank.fill(output, FluidAction.EXECUTE);
    setInventorySlotContents(slot, ItemStack.EMPTY);
    return true;
  }

  /*
   * Fueling
   */
  /** Checks if we have fuel in the melter */
  private boolean hasFuel() {
    return fuel > 0;
  }

  /**
   * Gets the fuel inventory for the melter
   * @return  Fuel inventory, fetching it if needed
   */
  @Nullable
  public MelterFuelWrapper getFuelInventory() {
    if (world != null && (fuelInventory == null || !fuelInventory.isValid())) {
      TileEntity te = world.getTileEntity(this.getPos().down());
      if (te instanceof ITankTileEntity) {
        fuelInventory = new MelterFuelWrapper(((ITankTileEntity)te).getTank());
      } else {
        fuelInventory = null;
      }
    }
    return fuelInventory;
  }

  /** Consumes fuel to power the melter */
  private void consumeFuel() {
    // no need to consume fuel if we have fuel
    if (hasFuel()) {
      return;
    }

    // cannot consume if inactive
    if (!isActive()) {
      return;
    }

    // if no fluid inventory, fetch from tile below
    getFuelInventory();

    // find a new fuel
    if (fuelInventory != null) {
      MeltingFuel fuel = findMeltingFuel();
      if (fuel != null) {
        // store fuel stats
        temperature = fuel.getTemperature();
        this.fuel = fuelInventory.consumeFuel(fuel);
        // TODO: syncing, what does the client need? can the UI do it?
      }
    }
  }

  /**
   * Finds melting fuel for the current melter
   * @return Melting fuel recipe
   */
  @Nullable
  public MeltingFuel findMeltingFuel() {
    if (fuelInventory == null || world == null) {
      return null;
    }
    // try last fuel for a match
    if (lastFuel != null && lastFuel.matches(fuelInventory, world)) {
      return lastFuel;
    }
    // if no match, find a new fuel
    Optional<MeltingFuel> newFuel = world.getRecipeManager().getRecipe(RecipeTypes.FUEL, fuelInventory, world);
    if (newFuel.isPresent()) {
      // update last
      lastFuel = newFuel.get();
      return lastFuel;
    }
    // no fuel found
    return null;
  }

  /*
   * Client side
   */

  /**
   * Gets the percentage a slot is towards completion
   * @param slot  Slot index
   * @return  Slot percentage
   */
  public float getHeatingProgress(int slot) {
    if (slot < 0 || slot >= this.getSizeInventory()) {
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
  public void read(CompoundNBT tag) {
    tank.readFromNBT(tag.getCompound(Tags.TANK));
    this.fuel = tag.getInt(TAG_FUEL);
    this.temperature = tag.getInt(TAG_TEMPERATURE);
    this.itemTemperatures = validate(tag.getIntArray(TAG_ITEM_TEMPERATURES), 3);
    this.itemTempRequired = validate(tag.getIntArray(TAG_ITEM_TEMP_REQUIRED), 3);
    super.read(tag);
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

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.put(Tags.TANK, tank.writeToNBT(new CompoundNBT()));
    tag.putInt(TAG_FUEL, fuel);
    tag.putInt(TAG_TEMPERATURE, temperature);
    tag.putIntArray(TAG_ITEM_TEMPERATURES, itemTemperatures);
    tag.putIntArray(TAG_ITEM_TEMP_REQUIRED, itemTempRequired);
    return super.write(tag);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return this.write(new CompoundNBT());
  }

  /*
   * Helpers
   */
  /** Checks if we are on a server world */
  private boolean isServerWorld() {
    return this.getWorld() != null && !this.getWorld().isRemote;
  }

  /** Checks if the tile entity is active */
  private boolean isActive() {
    BlockState state = this.getBlockState();
    return state.has(MelterBlock.ACTIVE) && state.get(MelterBlock.ACTIVE);
  }
}
