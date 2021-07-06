package slimeknights.tconstruct.smeltery.tileentity.controller;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.controller.MelterBlock;
import slimeknights.tconstruct.smeltery.inventory.AlloyerContainer;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;
import slimeknights.tconstruct.smeltery.tileentity.module.alloying.MixerAlloyTank;
import slimeknights.tconstruct.smeltery.tileentity.module.alloying.SingleAlloyingModule;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * Dedicated alloying block
 */
public class AlloyerTileEntity extends NamableTileEntity implements ITankTileEntity, ITickableTileEntity {
  /** Max capacity for the tank */
  private static final int TANK_CAPACITY = FluidValues.METAL_BLOCK * 3;

  /** Tank for this mixer */
  @Getter
  protected final FluidTankAnimated tank = new FluidTankAnimated(TANK_CAPACITY, this);
  /* Capability for return */
  private final LazyOptional<IFluidHandler> tankHolder = LazyOptional.of(() -> tank);

  // modules
  /** Logic for a mixer alloying */
  @Getter
  private final MixerAlloyTank alloyTank = new MixerAlloyTank(this, tank);
  /** Base alloy logic */
  private final SingleAlloyingModule alloyingModule = new SingleAlloyingModule(this, alloyTank);
  /** Fuel handling logic */
  @Getter
  private final FuelModule fuelModule = new FuelModule(this, () -> Collections.singletonList(this.pos.down()));

  /** Last comparator strength to reduce block updates */
  @Getter @Setter
  private int lastStrength = -1;

  /** Internal tick counter */
  private int tick;

  public AlloyerTileEntity() {
    this(TinkerSmeltery.alloyer.get());
  }

  protected AlloyerTileEntity(TileEntityType<?> type) {
    super(type, TConstruct.makeTranslation("gui", "alloyer"));
  }

  /*
   * Capability
   */

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return tankHolder.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    this.tankHolder.invalidate();
  }


  /*
   * Alloying
   */

  /** Checks if the tile entity is active */
  private boolean isFormed() {
    BlockState state = this.getBlockState();
    return state.hasProperty(MelterBlock.IN_STRUCTURE) && state.get(MelterBlock.IN_STRUCTURE);
  }

  @Override
  public void tick() {
    if (world == null || world.isRemote || !isFormed()) {
      return;
    }

    switch (tick) {
      // tick 0: find fuel
      case 0:
        alloyTank.setTemperature(fuelModule.findFuel(false));
        if (!fuelModule.hasFuel() && alloyingModule.canAlloy()) {
          fuelModule.findFuel(true);
        }
        break;
        // tick 2: alloy alloys and consume fuel
      case 2: {
        BlockState state = getBlockState();
        boolean hasFuel = fuelModule.hasFuel();

        // update state for new fuel state
        if (state.get(ControllerBlock.ACTIVE) != hasFuel) {
          world.setBlockState(pos, state.with(ControllerBlock.ACTIVE, hasFuel));
          // update the heater below
          BlockPos down = pos.down();
          BlockState downState = world.getBlockState(down);
          if (TinkerTags.Blocks.FUEL_TANKS.contains(downState.getBlock()) && downState.hasProperty(ControllerBlock.ACTIVE) && downState.get(ControllerBlock.ACTIVE) != hasFuel) {
            world.setBlockState(down, downState.with(ControllerBlock.ACTIVE, hasFuel));
          }
        }

        // actual alloying
        if (hasFuel) {
          alloyTank.setTemperature(fuelModule.getTemperature());
          alloyingModule.doAlloy();
          fuelModule.decreaseFuel(1);
        }
        break;
      }
    }
    tick = (tick + 1) % 4;
  }

  /**
   * Called when a neighbor of this block is changed to update the tank cache
   * @param side  Side changed
   */
  public void neighborChanged(Direction side) {
    alloyTank.refresh(side, true);
  }

  /*
   * Display
   */

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity playerEntity) {
    return new AlloyerContainer(id, inv, this);
  }


  /*
   * NBT
   */

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void writeSynced(CompoundNBT tag) {
    super.writeSynced(tag);
    tag.put(NBTTags.TANK, tank.writeToNBT(new CompoundNBT()));
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag = super.write(tag);
    fuelModule.writeToNBT(tag);
    return tag;
  }

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    tank.readFromNBT(nbt.getCompound(NBTTags.TANK));
    fuelModule.readFromNBT(nbt);
  }
}
