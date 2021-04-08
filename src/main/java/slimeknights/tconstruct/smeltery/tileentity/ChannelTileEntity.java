package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.util.WeakConsumerWrapper;
import slimeknights.tconstruct.library.fluid.FillOnlyFluidHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.ChannelBlock;
import slimeknights.tconstruct.smeltery.block.ChannelBlock.ChannelConnection;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket.IFluidPacketReceiver;
import slimeknights.tconstruct.smeltery.tileentity.tank.ChannelSideTank;
import slimeknights.tconstruct.smeltery.tileentity.tank.ChannelTank;

import org.jetbrains.annotations.Nullable;
import java.util.EnumMap;
import java.util.Map;

/**
 * Logic for channel fluid transfer
 */
public class ChannelTileEntity extends TileEntity implements ITickableTileEntity, IFluidPacketReceiver {
	public static final int LIQUID_TRANSFER = 16;

	/** Channel internal tank */
	private final ChannelTank tank = new ChannelTank(36, this);
	/** Handler to return from channel top */
	private final LazyOptional<IFluidHandler> topHandler = LazyOptional.of(() -> new FillOnlyFluidHandler(tank));
	/** Tanks for inserting on each side */
	private final Map<Direction,LazyOptional<IFluidHandler>> sideTanks = Util.make(new EnumMap<>(Direction.class), map -> {
		for (Direction direction : Plane.HORIZONTAL) {
			map.put(direction, LazyOptional.of(() -> new ChannelSideTank(this, tank, direction)));
		}
	});

	/** Cache of tanks on all neighboring sides */
	private final Map<Direction,LazyOptional<IFluidHandler>> neighborTanks = new EnumMap<>(Direction.class);
	/** Consumers to attach to each of the neighbors */
	private final Map<Direction,NonNullConsumer<LazyOptional<IFluidHandler>>> neighborConsumers = new EnumMap<>(Direction.class);

	/** Stores if the channel is currently flowing, set to 2 to allow a small buffer */
	private final byte[] isFlowing = new byte[5];

	public ChannelTileEntity() {
		this(TinkerSmeltery.channel.get());
	}

	protected ChannelTileEntity(TileEntityType<?> type) {
		super(type);
	}

	/**
	 * Gets the central fluid tank of this channel
	 * @return  Central tank
	 */
	public FluidStack getFluid() {
		return this.tank.getFluid();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	}


	/* Fluid handlers */

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		// top side gets the insert direct
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      if (side == null || side == Direction.UP) {
        return topHandler.cast();
      }
      // side tanks keep track of which side inserts
      if (side != Direction.DOWN && getBlockState().get(ChannelBlock.DIRECTION_MAP.get(side)) == ChannelConnection.IN) {
        return sideTanks.get(side).cast();
      }
    }

		return super.getCapability(capability, side);
	}

	/**
	 * Gets the fluid handler directly from a neighbor, skipping the cache
	 * @param side  Side of the neighbor to fetch
	 * @return  Fluid handler, or empty
	 */
	private LazyOptional<IFluidHandler> getNeighborHandlerUncached(Direction side) {
		assert world != null;
		// must have a TE with a fluid handler
		TileEntity te = world.getTileEntity(pos.offset(side));
		if (te != null) {
			LazyOptional<IFluidHandler> handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
			if (handler.isPresent()) {
				handler.addListener(neighborConsumers.computeIfAbsent(side, s -> new WeakConsumerWrapper<>(this, (self, lazy) -> self.neighborTanks.remove(s))));
				return handler;
			}
		}
		return LazyOptional.empty();
	}

	/**
	 * Gets the fluid handler from a neighbor
	 * @param side  Side of the neighbor to fetch
	 * @return  Fluid handler, or empty
	 */
	protected LazyOptional<IFluidHandler> getNeighborHandler(Direction side) {
		return neighborTanks.computeIfAbsent(side, this::getNeighborHandlerUncached);
	}

	/**
	 * Removes a cached handler from the given neighbor
	 * @param side  Side to remove
	 */
	public void removeCachedNeighbor(Direction side) {
		neighborTanks.remove(side);
	}


	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		topHandler.invalidate();
		for (LazyOptional<IFluidHandler> handler : sideTanks.values()) {
			handler.invalidate();
		}
	}


	/* Flowing property */

	/**
	 * Gets the index for the given side for flowing. Same as regular index but without up
	 * @param side  Side to index
	 * @return Flow index
	 */
	private int getFlowIndex(Direction side) {
		if (side.getAxis().isVertical()) {
			return 0;
		}
		return side.getIndex() - 1;
	}

	/**
	 * Marks the given side as flowing for the sake of rendering
	 * @param side     Side to set
	 * @param flowing  True to mark it as flowing
	 */
	public void setFlow(Direction side, boolean flowing) {
		if (side == Direction.UP) {
			return;
		}
		// update flowing state
		int index = getFlowIndex(side);
		boolean wasFlowing = isFlowing[index] > 0;
		isFlowing[index] = (byte)(flowing ? 2 : 0);

		// send packet to client if it changed
		if(wasFlowing != flowing && world != null && !world.isRemote) {
			syncFlowToClient(side, flowing);
		}
	}

	/**
	 * Checks if the given side is flowing
	 * @param side  Side to check
	 * @return  True if flowing
	 */
	public boolean isFlowing(Direction side) {
		if (side == Direction.UP) {
			return false;
		}

		return isFlowing[getFlowIndex(side)] > 0;
	}


	/* Utilities */

	/**
	 * Gets the connection for a side
	 * @param side  Side to query
	 * @return  Connection on the specified side
	 */
	protected boolean isOutput(Direction side) {
		// just always return in for up, thats fine
		if(side == Direction.UP) {
			return false;
		}
		// down is boolean, sides is multistate
		if(side == Direction.DOWN) {
			return this.getBlockState().get(ChannelBlock.DOWN);
		}
		return this.getBlockState().get(ChannelBlock.DIRECTION_MAP.get(side)) == ChannelConnection.OUT;
	}

	/**
	 * Counts the number of side outputs on the given side
	 * @param state  State to check
	 * @return  Number of outputs
	 */
	private static int countOutputs(BlockState state) {
		int count = 0;
		for (Direction direction : Plane.HORIZONTAL) {
			if (state.get(ChannelBlock.DIRECTION_MAP.get(direction)) == ChannelConnection.OUT) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Syncs the given flowing state to the client side
	 * @param side     Side to sync
	 * @param flowing  Flowing state to sync
	 */
	private void syncFlowToClient(Direction side, boolean flowing) {
		TinkerNetwork.getInstance().sendToClientsAround(new ChannelFlowPacket(pos, side, flowing), world, pos);
	}


	/* Flow */

	/**
	 * Ticking logic
	 */
	@Override
	public void tick() {
		if(world == null || world.isRemote) {
			return;
		}

		// must have fluid first
		FluidStack fluid = tank.getFluid();
		if(!fluid.isEmpty()) {

			// if we have down and can flow, skip sides
			boolean hasFlown = false;
			BlockState state = getBlockState();
			if(state.get(ChannelBlock.DOWN)) {
				hasFlown = trySide(Direction.DOWN, FaucetTileEntity.MB_PER_TICK);
			}
			// try sides if we have any sides
			int outputs = countOutputs(state);
			if(!hasFlown && outputs > 0) {
				// split the fluid evenly between sides
				int flowRate = MathHelper.clamp(tank.getMaxUsable() / outputs, 1, FaucetTileEntity.MB_PER_TICK);
				// then transfer on each side
				for(Direction side : Plane.HORIZONTAL) {
					trySide(side, flowRate);
				}
			}
		}

		// clear flowing if we should no longer flow on a side
		for (int i = 0; i < 5; i++) {
			if (isFlowing[i] > 0) {
				isFlowing[i]--;
				if (isFlowing[i] == 0) {
					Direction direction;
					if (i == 0) {
						direction = Direction.DOWN;
					} else {
						direction = Direction.byIndex(i + 1);
					}
					syncFlowToClient(direction, false);
				}
			}
		}

		tank.freeFluid();
	}

	/**
	 * Tries transferring fluid on a single side of the channel
	 * @param side      Side to transfer from
	 * @param flowRate  Maximum amount to output
	 * @return  True if the side transferred fluid
	 */
	protected boolean trySide(Direction side, int flowRate) {
		if(tank.isEmpty() || !this.isOutput(side)) {
			return false;
		}

		// get the handler on the side, try filling
		return getNeighborHandler(side).filter(handler -> fill(side, handler, flowRate))
																	 .isPresent();
	}

	/**
	 * Fill the fluid handler on the given side
	 * @param side     Side to fill
	 * @param handler  Handler to fill
	 * @param amount   Amount to fill
	 * @return  True if the side successfully filled something
	 */
	protected boolean fill(Direction side, IFluidHandler handler, int amount) {
		// make sure we do not allow more than the fluid allows, should not happen but just in case
		int usable = Math.min(tank.getMaxUsable(), amount);
		if (usable > 0) {
			// see how much works
			FluidStack fluid = tank.drain(usable, FluidAction.SIMULATE);
			int filled = handler.fill(fluid, FluidAction.SIMULATE);
			if (filled > 0) {
				// drain the amount that worked
				fluid = tank.drain(filled, FluidAction.EXECUTE);
				handler.fill(fluid, FluidAction.EXECUTE);

				// mark that the side is flowing
				setFlow(side, true);
				return true;
			}
		}

		// failed to flow, mark side as not flowing
		setFlow(side, false);
		return false;
	}


	/* NBT and sync */
	private static final String TAG_IS_FLOWING = "is_flowing";
	private static final String TAG_TANK = "tank";

	/**
	 * Sends a fluid update to the client with the current fluid
	 */
	public void sendFluidUpdate() {
		if (world != null && !world.isRemote) {
			TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(pos, getFluid()), world, pos);
		}
	}

	@Override
  public void updateFluidTo(FluidStack fluid) {
		tank.setFluid(fluid);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		// new tag instead of super since default implementation calls the super of writeToNBT
		return write(new CompoundNBT());
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);

		nbt.putByteArray(TAG_IS_FLOWING, isFlowing);
		nbt.put(TAG_TANK, tank.writeToNBT(new CompoundNBT()));

		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);

		// isFlowing
		if (nbt.contains(TAG_IS_FLOWING)) {
			byte[] nbtFlowing = nbt.getByteArray(TAG_IS_FLOWING);
			int max = Math.min(5, nbtFlowing.length);
			for (int i = 0; i < max; i++) {
				byte b = nbtFlowing[i];
				if (b > 2) {
					isFlowing[i] = 2;
				} else if (b < 0) {
					isFlowing[i] = 0;
				} else {
					isFlowing[i] = b;
				}
			}
		}

		// tank
		CompoundNBT tankTag = nbt.getCompound(TAG_TANK);
		tank.readFromNBT(tankTag);
	}
}
