package slimeknights.tconstruct.smeltery.tileentity.tank;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import net.minecraft.nbt.CompoundTag;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.tconstruct.fluids.FluidTank;
import slimeknights.tconstruct.smeltery.tileentity.ChannelTileEntity;

/** Tank for channel contents */
public class ChannelTank extends FluidTank {
	private static final String TAG_LOCKED = "locked";

	/**
	 * Amount of fluid that may not be extracted this tick
	 * Essentially, since we cannot guarantee tick order, this prevents us from having a net 0 fluid for the renderer
	 * if draining and filling at the same time
	 */
	private FluidAmount locked;

	/** Tank owner */
	private final ChannelTileEntity parent;

	public ChannelTank(int capacity, ChannelTileEntity parent) {
      super(0, null);
//		super(capacity, fluid -> !fluid.getRawFluid().getAttributes().isGaseous(fluid));
		this.parent = parent;
	}

	/**
	 * Called on channel update to clear the lock, allowing this fluid to be drained
	 */
	public void freeFluid() {
		this.locked = FluidAmount.ZERO;
	}

	/**
	 * Returns the maximum fluid that can be extracted from this tank
	 * @return  Max fluid that can be pulled
	 */
	public FluidAmount getMaxUsable() {
		return getTank(0).get().getAmount_F().sub(locked).max(FluidAmount.ZERO);
	}

	@Override
	public FluidVolume fill(FluidVolume resource, Simulation action) {
		boolean wasEmpty = isEmpty();
		FluidVolume amount = super.fill(resource, action);
		if(action.isAction()) {
			locked = locked.add(amount.getAmount_F());
			// if we added something, sync to client
			if (wasEmpty && !isEmpty()) {
				parent.sendFluidUpdate();
			}
		}
		return amount;
	}

	@Override
	public FluidVolume drain(int maxDrain, Simulation action) {
		boolean wasEmpty = isEmpty();
		FluidVolume stack = super.drain(maxDrain, action);
		// if we removed something, sync to client
		if (action.isAction() && !wasEmpty && isEmpty()) {
			parent.sendFluidUpdate();
		}
		return stack;
	}

	@Override
	public FluidTank readFromNBT(CompoundTag nbt) {
		this.locked = FluidAmount.fromNbt(nbt.getCompound(TAG_LOCKED));
		super.readFromNBT(nbt);
		return this;
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.put(TAG_LOCKED, locked.toNbt());
		return nbt;
	}
}
