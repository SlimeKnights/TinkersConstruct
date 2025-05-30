package slimeknights.tconstruct.smeltery.block.entity.tank;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

import javax.annotation.Nonnull;

/** Tank for channel contents */
public class ChannelTank extends FluidTank {
	private static final String TAG_LOCKED = "locked";

	/**
	 * Amount of fluid that may not be extracted this tick
	 * Essentially, since we cannot guarantee tick order, this prevents us from having a net 0 fluid for the renderer
	 * if draining and filling at the same time
	 */
	private int locked;

	/** Tank owner */
	private final ChannelBlockEntity parent;

	public ChannelTank(int capacity, ChannelBlockEntity parent) {
		super(capacity);
		this.parent = parent;
	}

	/**
	 * Called on channel update to clear the lock, allowing this fluid to be drained
	 */
	public void freeFluid() {
		this.locked = 0;
	}

	/**
	 * Returns the maximum fluid that can be extracted from this tank
	 * @return  Max fluid that can be pulled
	 */
	public int getMaxUsable() {
		return Math.max(fluid.getAmount() - locked, 0);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		boolean wasEmpty = isEmpty();
		int amount = super.fill(resource, action);
		if(action.execute()) {
			locked += amount;
			// if we added something, sync to client
			if (wasEmpty && !isEmpty()) {
				parent.sendFluidUpdate();
			}
		}
		return amount;
	}

	@Nonnull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		boolean wasEmpty = isEmpty();
		FluidStack stack = super.drain(maxDrain, action);
		// if we removed something, sync to client
		if (action.execute() && !wasEmpty && isEmpty()) {
			parent.sendFluidUpdate();
		}
		return stack;
	}

	@Override
	public FluidTank readFromNBT(CompoundTag nbt) {
		this.locked = nbt.getInt(TAG_LOCKED);
		super.readFromNBT(nbt);
		return this;
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.putInt(TAG_LOCKED, locked);
		return nbt;
	}
}
