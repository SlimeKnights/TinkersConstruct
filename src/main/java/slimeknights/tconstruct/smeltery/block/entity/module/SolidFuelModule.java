package slimeknights.tconstruct.smeltery.block.entity.module;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.mantle.inventory.EmptyItemHandler;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;

/** Fuel module variant that supports both item and fluid fuels. Only supports a single fluid position which should not change. */
public class SolidFuelModule extends FuelModule {
  /** Listener to attach to stored item capabilities */
  private final NonNullConsumer<LazyOptional<IItemHandler>> itemListener = new WeakConsumerWrapper<>(this, SolidFuelModule::resetHandler);

  /** Location of the fuel tank */
  private final BlockPos fuelPos;
  /** Last item handler where items were extracted */
  @Nullable
  private LazyOptional<IItemHandler> itemHandler;

  public SolidFuelModule(MantleBlockEntity parent, BlockPos fuelPos) {
    super(parent);
    this.fuelPos = fuelPos;
  }

  @Override
  protected void resetHandler(@Nullable LazyOptional<?> source) {
    // if the source is either of our handlers, clear both listeners to ensure cleanest refetc
    if (source == null || source == itemHandler || source == fluidHandler) {
      // remove listeners for efficiency, but we have to skip removing the listener that caused this
      if (Util.isForge()) {
        if (itemHandler != null && itemHandler != source) {
          itemHandler.removeListener(itemListener);
        }
        if (fluidHandler != null && fluidHandler != source) {
          fluidHandler.removeListener(fluidListener);
        }
      }
      itemHandler = null;
      fluidHandler = null;
    }
  }


  /* Fuel updating */

  /**
   * Tries to consume fuel from the given fluid handler
   * @param handler  Handler to consume fuel from
   * @return   Temperature of the consumed fuel, 0 if none found
   */
  private int trySolidFuel(IItemHandler handler, boolean consume) {
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack stack = handler.getStackInSlot(i);
      int time = ForgeHooks.getBurnTime(stack, TinkerRecipeTypes.FUEL.get()) / 4;
      if (time > 0) {
        MeltingFuel solid = MeltingFuelLookup.getSolid();
        if (consume) {
          ItemStack extracted = handler.extractItem(i, 1, false);
          if (ItemStack.isSameItem(extracted, stack)) {
            fuel += time;
            fuelQuality = time;
            temperature = solid.getTemperature();
            rate = solid.getRate();
            parent.setChangedFast();
            // return the container
            ItemStack container = extracted.getCraftingRemainingItem();
            if (!container.isEmpty()) {
              // if we cannot insert the container back, spit it on the ground
              ItemStack notInserted = ItemHandlerHelper.insertItem(handler, container, false);
              if (!notInserted.isEmpty()) {
                Level world = getLevel();
                double x = (world.random.nextFloat() * 0.5F) + 0.25D;
                double y = (world.random.nextFloat() * 0.5F) + 0.25D;
                double z = (world.random.nextFloat() * 0.5F) + 0.25D;
                ItemEntity itementity = new ItemEntity(world, fuelPos.getX() + x, fuelPos.getY() + y, fuelPos.getZ() + z, container);
                itementity.setDefaultPickUpDelay();
                world.addFreshEntity(itementity);
              }
            }
          } else {
            TConstruct.LOG.error("Invalid item removed from solid fuel handler");
          }
        }
        return solid.getTemperature();
      }
    }
    return 0;
  }

  /** Fetches any relevant fuel handlers from the target position */
  private void fetchHandlers() {
    // if we have handlers, nothing to do
    if (fluidHandler != null && itemHandler != null) {
      return;
    }
    BlockEntity te = getLevel().getBlockEntity(fuelPos);
    if (te != null) {
      // first, identify a capability that has what we need
      // on the chance both are present, we prioritize fluid; we don't expect that to change
      fluidHandler = te.getCapability(ForgeCapabilities.FLUID_HANDLER);
      if (fluidHandler.isPresent()) {
        fluidHandler.addListener(fluidListener);
      }
      itemHandler = te.getCapability(ForgeCapabilities.ITEM_HANDLER);
      if (itemHandler.isPresent()) {
        itemHandler.addListener(itemListener);
      }
    } else {
      fluidHandler = LazyOptional.empty();
      itemHandler = LazyOptional.empty();
    }
  }

  @Override
  public int findFuel(boolean consume) {
    fetchHandlers();
    assert fluidHandler != null;
    assert itemHandler != null;

    // prioritize liquid fuel - it usually goes hotter
    int temperature = 0;
    if (fluidHandler.isPresent()) {
      temperature = tryLiquidFuel(fluidHandler.orElse(EmptyFluidHandler.INSTANCE), consume);
    }
    // next, try solid fuel
    if (temperature == 0 && itemHandler.isPresent()) {
      temperature = trySolidFuel(itemHandler.orElse(EmptyItemHandler.INSTANCE), consume);
    }
    // no handler found, tell client of the lack of fuel
    if (temperature == 0 && consume) {
      this.temperature = 0;
      this.rate = 0;
    }
    return temperature;
  }


  /* UI Syncing */

  @Override
  public FuelInfo getFuelInfo() {
    fetchHandlers();
    assert itemHandler != null;

    FuelInfo info = super.getFuelInfo();
    if (info.isEmpty() && itemHandler.isPresent()) {
      return FuelInfo.ITEM;
    }
    return info;
  }


  /* Fluid handler */

  /** Gets the fluid handler for proxy */
  public IFluidHandler getTank() {
    if (fluidHandler != null) {
      return fluidHandler.orElse(EmptyFluidHandler.INSTANCE);
    }
    return EmptyFluidHandler.INSTANCE;
  }
}
