package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.block.MultiblockControllerBlock;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.tileentity.inventory.MelterFuelWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public abstract class HeatingStructureFuelTankTileEntity<T extends MultiblockDetection> extends HeatingStructureTileEntity<T> {

  /* tags */
  public static final String TAG_TANKS = "tanks";
  public static final String TAG_CURRENT_FUEL = "currentFuel";
  public static final String TAG_CURRENT_TANK = "currentTank";

  /* Fuel */
  @Nullable
  private MelterFuelWrapper fuelInventory;
  /** Cache of the last fuel we tried to fetch */
  @Nullable
  private MeltingFuel lastFuel;
  /** Number of ticks of fuel left */
  @Getter @Setter
  private int fuel;
  /** Fuel tank information */
  public List<BlockPos> tanks;
  public BlockPos currentTank;

  public HeatingStructureFuelTankTileEntity(TileEntityType<?> type, ITextComponent name, int inventorySize, int maxStackSize) {
    super(type, name, inventorySize, maxStackSize);

    tanks = Lists.newLinkedList();
  }

  /*
   * Fueling
   */

  /**
   * Gets the fuel inventory for the structure
   * @return  Fuel inventory, fetching it if needed
   */
  @Nullable
  public MelterFuelWrapper getFuelInventory() {
    if (world != null && (fuelInventory == null || !fuelInventory.isValid())) {
      for (BlockPos pos : tanks) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ITankTileEntity) {
          fuelInventory = new MelterFuelWrapper(((ITankTileEntity)te).getTank());
          if (world.getRecipeManager().getRecipe(RecipeTypes.FUEL, fuelInventory, world).isPresent()) {
            currentTank = pos;
            break;
          }
        } else {
          fuelInventory = null;
          currentTank = null;
        }
      }
    }
    return fuelInventory;
  }

  /** Consumes fuel to power the structure */
  @Override
  protected void consumeFuel() {
    // no need to consume fuel if we have fuel
    if (hasFuel()) {
      return;
    }

    // cannot consume if inactive
    if (!isActive()) {
      return;
    }

    // if no fluid inventory, fetch it
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
   * Finds fuel for the structure
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
   * Multiblock
   */

  @Override
  protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
    // find all tanks for input
    tanks.clear();
    for (BlockPos pos : structure.blocks) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof ITankTileEntity) {
        tanks.add(pos);
      }
    }

    int inventorySize = getUpdatedInventorySize(structure.xd, structure.yd, structure.zd);

    // if the new multiblock is smaller we pop out all items that don't fit in anymore
    if(!world.isRemote && this.getSizeInventory() > inventorySize) {
      for(int i = inventorySize; i < getSizeInventory(); i++) {
        if(!getStackInSlot(i).isEmpty()) {
          dropItem(getStackInSlot(i));
        }
      }
    }

    // adjust inventory sizes
    this.resize(inventorySize);
  }

  /** When the multiblock forms the inventory size is readjusted. Return the inventory size from the (total) structure size */
  protected abstract int getUpdatedInventorySize(int width, int height, int depth);

  protected void dropItem(ItemStack stack) {
    Direction direction = getWorld().getBlockState(pos).get(MultiblockControllerBlock.FACING);
    BlockPos pos = this.getPos().offset(direction);

    ItemEntity entityitem = new ItemEntity(getWorld(), pos.getX(), pos.getY(), pos.getZ(), stack);
    getWorld().addEntity(entityitem);
  }

  /*
   * NBT
   */

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.put(TAG_CURRENT_TANK, TagUtil.writePos(currentTank));
    ListNBT tankList = new ListNBT();
    for(BlockPos pos : tanks) {
      tankList.add(TagUtil.writePos(pos));
    }
    tag.put(TAG_TANKS, tankList);

//    CompoundNBT fuelTag = new CompoundNBT();
//    if(lastFuel != null) {
//      lastFuel.writeToNBT(fuelTag);
//    }
//    tag.put(TAG_CURRENT_FUEL, fuelTag);

    return super.write(tag);
  }

  @Override
  public void read(BlockState state, CompoundNBT tag) {

    ListNBT tankList = tag.getList(TAG_TANKS, 10);
    tanks.clear();
    for(int i = 0; i < tankList.size(); i++) {
      tanks.add(TagUtil.readPos(tankList.getCompound(i)));
    }

//    CompoundNBT fuelTag = tag.getCompound(TAG_CURRENT_FUEL);
//    lastFuel = FluidStack.loadFluidStackFromNBT(fuelTag);
    super.read(state, tag);
  }
}
