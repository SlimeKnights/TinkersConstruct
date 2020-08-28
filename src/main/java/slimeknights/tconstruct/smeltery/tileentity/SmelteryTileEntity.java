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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;
import slimeknights.mantle.recipe.inventory.InventorySlotWrapper;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloy.inventory.IAlloyInventory;
import slimeknights.tconstruct.library.recipe.alloy.inventory.SmelteryAlloyingWrapper;
import slimeknights.tconstruct.library.recipe.alloy.recipe.IAlloyRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.inventory.SmelteryContainer;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockSmeltery;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidUpdatePacket;
import slimeknights.tconstruct.smeltery.tileentity.inventory.MelterFuelWrapper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SmelteryTileEntity extends HeatingStructureFuelTankTileEntity<MultiblockSmeltery> implements ITickableTileEntity, ISmelteryTankHandler {
  /** Capacity per internal block */
  private static final int CAPACITY_PER_BLOCK = MaterialValues.VALUE_Ingot * 8;

  /* Tank */
  /** Internal fluid tank */
  @Getter
  protected SmelteryTank liquids;
  /** Capability holder for the tank */
  private LazyOptional<ISmelteryTankHandler> liquidHolder;// = LazyOptional.of(() -> liquids)
  /** Last comparator strength to reduce block updates */
  @Getter // TODO: needed?
  private int lastStrength = -1;

  /* Heating */
  /** Internal tick counter */
  private int tick;
  /** Last recipe seen for each of the slots */
  private IMeltingRecipe[] lastMeltingRecipe;

  /* Alloying */
  /** Recipe inventory wrapper for recipe fetches */
  private SmelteryAlloyingWrapper alloyingWrapper;
  /** Last recipe seen */
  private IAlloyRecipe lastAlloyRecipe;

  /* Multiblock */
  /** Last checked position for validity inside the smeltery */
  private BlockPos insideCheck;
  private int fullCheckCounter;

  /** Main constructor */
  public SmelteryTileEntity() {
    this(TinkerSmeltery.smeltery.get());
  }
  /** Extendable constructor */
  public SmelteryTileEntity(TileEntityType<? extends SmelteryTileEntity> type) {
    super(type, new TranslationTextComponent(Util.makeTranslationKey("gui", "smeltery_controller")), 0, 1);

    // melting
    this.lastMeltingRecipe = new IMeltingRecipe[this.getSizeInventory()];
    setMultiblock(new MultiblockSmeltery(this));
    this.liquids = new SmelteryTank(this);
    this.lastAlloyRecipe = null;
    this.alloyingWrapper = new SmelteryAlloyingWrapper(liquids);
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity playerEntity) {
    return new SmelteryContainer(id, inv, this);
  }

  /*
   * Tank methods
   */

  @Override
  public void onTankChanged(List<FluidStack> fluids, FluidStack changed) {
    if (isServerWorld()) {
      TinkerNetwork.getInstance().sendToClientsAround(new SmelteryFluidUpdatePacket(getPos(), fluids), (ServerWorld) world, getPos());
    }
  }

  @Nullable
  @Override
  public SmelteryTank getTank() {
    return isActive() ? liquids : null;
  }

  @Override
  public void updateFluidsFromPacket(List<FluidStack> liquids) {
    this.liquids.setFluids(liquids);
  }

  /*
   * Smelting
   */

  @Override
  public void tick() {
    if (!isServerWorld()) {
      return;
    }

    // are we fully formed?
    if (!isActive()) {
      if (tick == 0) {
        checkMultiblockStructure();
      }
    }
    else {
      if (tick % 4 == 0) {
        if (heatItems()) {
          consumeFuel();
        }
//        alloyAlloys();
      }

      // we gradually check if the inside of the smeltery is blocked (for performance reasons)
      if (tick == 0) {
        // called very second, we check every 15s or so
        if (++fullCheckCounter >= 15) {
          fullCheckCounter = 0;
          checkMultiblockStructure();
        } else {
          // outside or unset?
          updateInsideCheck();
          if (!Objects.requireNonNull(getWorld()).isAirBlock(insideCheck)) {
            // we broke. inside blocked. :(
            setInvalid();
            insideCheck = null;
            BlockState state = getWorld().getBlockState(this.pos);
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
          }
          else {
            // advance to next block
            progressInsideCheck();
          }
        }
      }
    }

    tick = (tick + 1) % 20;
  }

  /*
   * Melting
   */

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

    // first try last recipe for the slot
    IMeltingRecipe last = lastMeltingRecipe[slot];
    if (last != null && last.matches(inv, world)) {
      return last;
    }
    // if that fails, try to find a new recipe
    Optional<IMeltingRecipe> newRecipe = world.getRecipeManager().getRecipe(RecipeTypes.MELTING, inv, world);
    if (newRecipe.isPresent()) {
      lastMeltingRecipe[slot] = newRecipe.get();
      return lastMeltingRecipe[slot];
    }
    return null;
  }

  /**
   * Updates the heat required for the slot
   * @param slot  Slot index
   */
  @Override
  protected void updateHeatRequired(int slot) {
    ISingleItemInventory inv = slotWrappers[slot];
    int newHeat = 0;
    if (!inv.isEmpty()) {
      IMeltingRecipe recipe = findRecipe(inv, slot);
      if (recipe != null) {
        newHeat = recipe.getTemperature(inv);
      }
    }
    itemTempRequired[slot] = newHeat;
  }

  /**
   * Called when an item finishes heating
   * @param inv   Item inventory
   * @param slot  Slot index
   * @return  True if the item successfully heated, false otherwise
   */
  protected boolean onItemFinishedHeating(ISingleItemInventory inv, int slot) {
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
    int filled = liquids.fill(output.copy(), IFluidHandler.FluidAction.SIMULATE);
    if (filled != output.getAmount()) {
      itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
      return false;
    }

    // actually fill the tank
    liquids.fill(output, IFluidHandler.FluidAction.EXECUTE);
    setInventorySlotContents(slot, ItemStack.EMPTY);
    return true;
  }

  /*
   * Alloying
   */

  /**
   * Alloys alloys in the inventory
   */
  private void alloyAlloys() {
    boolean alloyedAlloy = false;
    if (liquids.getFluidAmount() > liquids.getCapacity()) {
      return;
    }
    if (lastAlloyRecipe == null) {
      lastAlloyRecipe = findRecipe(alloyingWrapper);
    }
    if (lastAlloyRecipe == null) {
      return;
    }
    for (FluidIngredient fluid : lastAlloyRecipe.getFluidIngredients()) {
      for (FluidStack stack : liquids.getFluids()) {
        if (fluid.test(stack)) {
          liquids.drain(stack, IFluidHandler.FluidAction.EXECUTE);
        }
      }
      liquids.fill(lastAlloyRecipe.getOutput(alloyingWrapper), IFluidHandler.FluidAction.EXECUTE);
      alloyedAlloy = true;
    }
    /*
     * For each ingredient
     *   find matching fluid
     *   if !inv.drain(matchingFluid)
     *     write some error
     *     return false
     * fill with output
     * return true
     */
  }

  @Nullable
  private IAlloyRecipe findRecipe(IAlloyInventory inv) {
    if (world == null) {
      return null;
    }

    // first we try the last recipe
    if (lastAlloyRecipe != null && lastAlloyRecipe.matches(inv, world)) {
      return lastAlloyRecipe;
    }
    // if that fails, try to find a new recipe
    Optional<IAlloyRecipe> newRecipe = world.getRecipeManager().getRecipe(RecipeTypes.ALLOY, inv, world);
    newRecipe.ifPresent(iAlloyRecipe -> lastAlloyRecipe = iAlloyRecipe);
    return lastAlloyRecipe;
  }

  /*
   * Multiblock
   */

  @Override
  protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
    super.updateStructureInfo(structure);
    this.liquids.setCapacity(getSizeInventory() * CAPACITY_PER_BLOCK);
  }

  @Override
  public void resize(int size) {
    super.resize(size);
    this.lastMeltingRecipe = Arrays.copyOf(lastMeltingRecipe, size);
  }

  @Override
  protected int getUpdatedInventorySize(int width, int height, int depth) {
    return width * height * depth;
  }private void updateInsideCheck() {
    if(insideCheck == null
      || insideCheck.getX() < minPos.getX()
      || insideCheck.getY() < minPos.getY()
      || insideCheck.getZ() < minPos.getZ()
      || insideCheck.getX() > maxPos.getX()
      || insideCheck.getY() > maxPos.getY()
      || insideCheck.getZ() > maxPos.getZ()) {
      insideCheck = minPos;
    }
  }

  private void progressInsideCheck() {
    insideCheck = insideCheck.add(1, 0, 0);
    if(insideCheck.getX() > maxPos.getX()) {
      insideCheck = new BlockPos(minPos.getX(), insideCheck.getY(), insideCheck.getZ() + 1);
      if(insideCheck.getZ() > maxPos.getZ()) {
        insideCheck = new BlockPos(minPos.getX(), insideCheck.getY() + 1, minPos.getZ());
      }
    }
  }

  /*
   * NBT
   */

  @Override
  public void read(BlockState state, CompoundNBT tag) {
    this.liquids.readFromNBT(tag.getCompound(Tags.TANK));
    super.read(state, tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.put(Tags.TANK, liquids.writeToNBT(new CompoundNBT()));
    return super.write(tag);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return this.write(new CompoundNBT());
  }
}
