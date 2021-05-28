package slimeknights.tconstruct.smeltery.tileentity.module.alloying;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.alloying.IMutableAlloyTank;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Alloy tank that takes inputs from neighboring blocks
 */
@RequiredArgsConstructor
public class MixerAlloyTank implements IMutableAlloyTank {
  // parameters
  /** Handler parent */
  private final MantleTileEntity parent;
  /** Tank for outputs */
  private final IFluidHandler outputTank;

  /** Current temperature. Provided as a getter and setter as there are a few contexts with different source for temperature */
  @Getter
  @Setter
  private int temperature = 0;

  // side tank cache
  /** Cache of tanks for each of the sides */
  private final Map<Direction,LazyOptional<IFluidHandler>> inputs = new EnumMap<>(Direction.class);
  /** Map of invalidation listeners for each side */
  private final Map<Direction,NonNullConsumer<LazyOptional<IFluidHandler>>> listeners = new EnumMap<>(Direction.class);
  /** Map of tank index to tank on the side */
  @Nullable
  private IFluidHandler[] indexedList = null;

  // state
  /** If true, tanks are marked for refresh later */
  private boolean needsRefresh = true;
  /** Number of currently held tanks */
  private int currentTanks = 0;

  @Override
  public int getTanks() {
    checkTanks();
    return currentTanks;
  }

  /** Gets the map of index to direction */
  private IFluidHandler[] indexTanks() {
    // convert map into indexed list of fluid handlers, will be cleared next time a side updates
    if (indexedList == null) {
      indexedList = new IFluidHandler[currentTanks];
      if (currentTanks > 0) {
        int nextTank = 0;
        for (Direction direction : Direction.values()) {
          if (direction != Direction.DOWN) {
            LazyOptional<IFluidHandler> handler = inputs.getOrDefault(direction, LazyOptional.empty());
            if (handler.isPresent()) {
              indexedList[nextTank] = handler.orElse(EmptyFluidHandler.INSTANCE);
              nextTank++;
            }
          }
        }
      }
    }
    return indexedList;
  }

  /** Gets the fluid handler for the given tank index */
  public IFluidHandler getFluidHandler(int tank) {
    checkTanks();
    // invalid index, nothing
    if (tank >= currentTanks || tank < 0) {
      return EmptyFluidHandler.INSTANCE;
    }
    return indexTanks()[tank];
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    checkTanks();
    // invalid index, nothing
    if (tank >= currentTanks || tank < 0) {
      return FluidStack.EMPTY;
    }
    // get the first fluid from the proper tank, we do not support multiple fluids on a side
    return indexTanks()[tank].getFluidInTank(0);
  }

  @Override
  public FluidStack drain(int tank, FluidStack fluidStack) {
    checkTanks();
    // invalid index, nothing
    if (tank >= currentTanks || tank < 0) {
      return FluidStack.EMPTY;
    }
    return indexTanks()[tank].drain(fluidStack, FluidAction.EXECUTE);
  }

  @Override
  public boolean canFit(FluidStack fluid, int removed) {
    checkTanks();
    return outputTank.fill(fluid, FluidAction.SIMULATE) == fluid.getAmount();
  }

  @Override
  public int fill(FluidStack fluidStack) {
    return outputTank.fill(fluidStack, FluidAction.EXECUTE);
  }

  /**
   * Refreshes the cached tanks if needed
   * After calling this method, all five tank sides will have been fetched
   */
  private void checkTanks() {
    // need world to do anything
    World world = parent.getWorld();
    if (world == null) {
      return;
    }
    if (needsRefresh) {
      for (Direction direction : Direction.values()) {
        // update each direction we are missing
        if (direction != Direction.DOWN && !inputs.containsKey(direction)) {
          BlockPos target = parent.getPos().offset(direction);
          // limit by blocks as that gives the modpack more control, say they want to allow only scorched tanks
          if (world.getBlockState(target).isIn(TinkerTags.Blocks.ALLOYER_TANKS)) {
            TileEntity te = world.getTileEntity(target);
            if (te != null) {
              // if we found a tank, increment the number of tanks
              LazyOptional<IFluidHandler> capability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite());
              if (capability.isPresent()) {
                // attach a listener so we know when the side invalidates
                capability.addListener(listeners.computeIfAbsent(direction, dir -> new WeakConsumerWrapper<>(this, (self, handler) -> {
                  if (handler == self.inputs.get(dir)) {
                    refresh(dir, false);
                  }
                })));
                inputs.put(direction, capability);
                currentTanks++;
              } else {
                inputs.put(direction, LazyOptional.empty());
              }
            } else {
              inputs.put(direction, LazyOptional.empty());
            }
          }
        }
      }
      needsRefresh = false;

      // close the UI for any players in this UI
      if (!world.isRemote) {
        for (PlayerEntity player : world.getPlayers()) {
          if (player.openContainer instanceof BaseContainer && ((BaseContainer<?>)player.openContainer).getTile() == parent) {
            player.closeScreen();
          }
        }
      }
    }
  }

  /**
   * Called on block update or when a capability invalidates to mark that a direction needs updates
   * @param direction  Side updating
   * @param checkInput If true, validates that the side contains an input before reducing tank count. False when invalidated through the capability
   * */
  public void refresh(Direction direction, boolean checkInput) {
    if (direction == Direction.DOWN) {
      return;
    }
    if (!checkInput || (inputs.containsKey(direction) && inputs.get(direction).isPresent())) {
      currentTanks--;
    }
    inputs.remove(direction);
    needsRefresh = true;
    indexedList = null;
  }
}
