package slimeknights.tconstruct.smeltery.tileentity;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.fluid.ChannelSideTank;
import slimeknights.tconstruct.library.fluid.ChannelTank;
import slimeknights.tconstruct.smeltery.network.ChannelConnectionPacket;
import slimeknights.tconstruct.smeltery.network.ChannelFlowPacket;
import slimeknights.tconstruct.smeltery.network.FluidUpdatePacket.IFluidPacketReceiver;

public class TileChannel extends MantleTileEntity implements ITickable, IFluidPacketReceiver {

  /** Stores if the channel can be connected on the side */
  private ChannelConnection[] connections;
  /** Connection on the bottom as its boolean */
  private boolean connectedDown;


  /** Stores if the channel is currently flowing, byte will determine for how long */
  private byte[] isFlowing;
  /** Stores if the channel is currently flowing down */
  private boolean isFlowingDown;

  /** Stores if the block was powered last update */
  private boolean wasPowered;

  private int numOutputs;

  private ChannelTank tank;
  private ChannelSideTank[] sideTanks;

  public TileChannel() {
    this.connections = new ChannelConnection[4];
    this.connectedDown = false;

    this.isFlowing = new byte[4];

    this.tank = new ChannelTank(36, this);
    this.sideTanks = new ChannelSideTank[4];
    this.numOutputs = 0;
  }

  /* Flow */

  /**
   * Ticking logic
   */
  @Override
  public void update() {
    if(getWorld().isRemote) {
      return;
    }

    FluidStack fluid = tank.getFluid();
    if(fluid != null && fluid.amount > 0) {

      // if we have down, use only that
      boolean hasFlown = false;
      if(isConnectedDown()) {
        hasFlown = trySide(EnumFacing.DOWN, TileFaucet.LIQUID_TRANSFER);
        // otherwise, ensure we have a connection before pouring
      }
      if(!hasFlown && numOutputs > 0) {
        // we want to split the fluid if needed rather than favoring a side
        int flowRate = Math.max(1, Math.min(tank.usableFluid() / numOutputs, TileFaucet.LIQUID_TRANSFER));
        // then just try each side
        for(EnumFacing side : EnumFacing.HORIZONTALS) {
          trySide(side, flowRate);
        }
      }
    }

    // clear flowing if we should no longer flow on a side
    for(int i = 0; i < 4; i++) {
      if(isFlowing[i] > 0) {
        isFlowing[i]--;
        if(isFlowing[i] == 0) {
          TinkerNetwork.sendToClients((WorldServer) world, pos, new ChannelFlowPacket(pos, EnumFacing.getHorizontal(i), false));
        }
      }
    }

    tank.freeFluid();
  }

  protected boolean trySide(@Nonnull EnumFacing side, int flowRate) {
    if(tank.getFluid() == null || this.getConnection(side) != ChannelConnection.OUT) {
      return false;
    }

    // what are we flowing into
    TileEntity te = world.getTileEntity(pos.offset(side));
    // for channels, we have slightly quicker logic
    if(te instanceof TileChannel) {
      TileChannel channel = (TileChannel)te;
      // only flow if the other channel is receiving
      EnumFacing opposite = side.getOpposite();
      if(channel.getConnection(opposite) == ChannelConnection.IN) {
        return fill(side, channel.getTank(opposite), flowRate);
      }
    }
    else {
      IFluidHandler toFill = getFluidHandler(te, side.getOpposite());
      if(toFill != null) {
        return fill(side, toFill, flowRate);
      }
    }

    return false;
  }

  protected boolean fill(EnumFacing side, @Nonnull IFluidHandler handler, int amount) {
    FluidStack fluid = tank.getUsableFluid();
    // make sure we do not allow more than the fluid allows
    fluid.amount = Math.min(fluid.amount, amount);
    int filled = fluid.amount == 0 ? 0 : handler.fill(fluid, false);
    if(filled > 0) {
      setFlow(side, true);
      filled = handler.fill(fluid, true);
      tank.drainInternal(filled, true);
      return true;
    }

    setFlow(side, false);
    return false;
  }

  protected TileChannel getChannel(BlockPos pos) {
    TileEntity te = getWorld().getTileEntity(pos);
    if(te != null && te instanceof TileChannel) {
      return (TileChannel) te;
    }
    return null;
  }

  protected IFluidHandler getFluidHandler(TileEntity te, EnumFacing direction) {
    if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction)) {
      return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction);
    }
    return null;
  }

  /* Fluid interactions */
  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      // only allow inserting if the side is set to in
      // basically, block out and none, along with sides that cannot be in
      return side == null || getConnection(side) == ChannelConnection.IN;
    }
    return super.hasCapability(capability, side);
  }

  @Nonnull
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
    if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      // ensure we allow on that side
      if(side == null || getConnection(side) == ChannelConnection.IN) {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(getTank(side));
      }
    }
    return super.getCapability(capability, side);
  }


  /**
   * Called on block placement to fill data from blocks on all sides
   * @param side   Side clicked
   * @param sneak  If true, player was sneaking
   */
  public void onPlaceBlock(EnumFacing hit, boolean sneak) {
    EnumFacing side = hit.getOpposite();

    // if placed below a TE, update to connect to it
    TileEntity te = world.getTileEntity(pos.offset(side));
    if(te == null) {
      return;
    }

    if(side == EnumFacing.UP) {
      if(te instanceof TileChannel) {
        ((TileChannel)te).connectedDown = true;
      }
    }

    // for the rest, try to connect to it
    // if its a channel, connect to each other
    else if(te instanceof TileChannel) {
      // if its a channel, update ours and their connections to each other
      // if we hit the bottom of a channel, make it flow into us
      if(side == EnumFacing.DOWN) {
        this.connectedDown = true;
      } else {
        // default to out on click, but do in if sneaking
        ChannelConnection connection = sneak ? ChannelConnection.IN : ChannelConnection.OUT;
        this.setConnection(side, connection.getOpposite());
        ((TileChannel)te).setConnection(hit, connection);
      }
      // if its another fluid container, just connect to it
    } else if(te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) {
      // we already know we can connect, so just set out
      this.setConnection(side, ChannelConnection.OUT);
    }

    this.wasPowered = world.isBlockPowered(pos);
  }

  /**
   * Handles an update from another block to update the shape
   * @param fromPos      BlockPos that changed
   */
  public void handleBlockUpdate(BlockPos fromPos, boolean didPlace, boolean isPowered) {
    if(world.isRemote) {
      return;
    }

    EnumFacing side = Util.facingFromNeighbor(this.pos, fromPos);
    // we don't care about up as we don't connect on up
    if(side != null && side != EnumFacing.UP) {
      boolean isValid = false;
      boolean shouldOutput = false;
      TileEntity te = world.getTileEntity(fromPos);
      if(te instanceof TileChannel) {
        isValid = true;
      } else if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) {
        isValid = true;
        // if we placed it and are not a channel, set the output state
        shouldOutput = didPlace;
      }


      // if there is a connection and its no longer valid, clear it
      ChannelConnection connection = this.getConnection(side);
      if(connection != ChannelConnection.NONE && !isValid) {
        this.setConnection(side, ChannelConnection.NONE);
        TinkerNetwork.sendToClients((WorldServer) world, pos, new ChannelConnectionPacket(pos, side, false));
        // if there is no connection and one can be formed, we might automatically add one on condition
        // the new block must have been placed (not just updated) and must not be a channel
      } else if(shouldOutput && connection == ChannelConnection.NONE && isValid) {
        this.setConnection(side, ChannelConnection.OUT);
        TinkerNetwork.sendToClients((WorldServer) world, pos, new ChannelConnectionPacket(pos, side, true));
      }
    }

    // redstone power
    if(isPowered != wasPowered && side != EnumFacing.DOWN) {
      TileEntity te = world.getTileEntity(pos.down());
      boolean isValid2 = te != null && (te instanceof TileChannel || te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()));
      this.connectedDown = isValid2 && isPowered;

      TinkerNetwork.sendToClients((WorldServer) world, pos, new ChannelConnectionPacket(pos, EnumFacing.DOWN, this.connectedDown));
      wasPowered = isPowered;
    }
  }

  /**
   * Interacts with the tile entity, setting the side to show or hide
   * @param side  Side clicked
   * @return true if the channel changed
   */
  public boolean interact(EntityPlayer player, EnumFacing side) {
    // if placed below a channel, connect it to us
    TileEntity te = world.getTileEntity(pos.offset(side));

    // if the TE is a channel, note that for later
    boolean isChannel = false;
    if(te instanceof TileChannel) {
      isChannel = true;
      // otherwise ensure we can actually connect on that side
    } else if(te == null || !te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite())) {
      // if it is already none, no reason to set it back to none
      if(this.getConnection(side) == ChannelConnection.NONE) {
        // but for sides lets try again with the bottom connection
        if(side != EnumFacing.DOWN) {
          return this.interact(player, EnumFacing.DOWN);
        }
      } else {
        this.setConnection(side, ChannelConnection.NONE);
        this.updateBlock(pos);
      }
      return false;
    }

    // if down, just reverse the connection
    String message;
    if(side == EnumFacing.DOWN) {
      this.connectedDown = !this.connectedDown;
      this.updateBlock(pos);
      message = this.connectedDown ? "channel.connected_down.allow" : "channel.connected_down.disallow";
    } else {
      // otherwise, we rotate though connections
      ChannelConnection newConnect = this.getConnection(side).getNext(player.isSneaking());
      this.setConnection(side, newConnect);

      // if we have a neighbor, update them as well
      BlockPos offset = this.pos.offset(side);
      if(isChannel) {
        ((TileChannel)te).setConnection(side.getOpposite(), newConnect.getOpposite());
      }
      // block updates
      this.updateBlock(pos);
      this.updateBlock(offset);
      switch(newConnect) {
        case OUT:
          message = "channel.connected.out";
          break;
        case IN:
          message = "channel.connected.in";
          break;
        default:
          message = "channel.connected.none";
      }
    }
    player.sendStatusMessage(new TextComponentTranslation(Util.prefix(message)), true);
    return true;
  }

  /* Helper methods */

  public ChannelTank getTank() {
    return this.tank;
  }

  protected IFluidHandler getTank(@Nullable EnumFacing side) {
    if(side == null || side == EnumFacing.UP) {
      return tank;
    }

    int index = side.getHorizontalIndex();
    if(index >= 0) {
      if(sideTanks[index] == null) {
        sideTanks[index] = new ChannelSideTank(this, tank, side);
      }

      return sideTanks[index];
    }

    return null;
  }

  /**
   * Gets the connection for a side
   * @param side  Side to query
   * @return  Connection on the specified side
   */
  @Nonnull
  public ChannelConnection getConnection(@Nonnull EnumFacing side) {
    // just always return in for up, thats fine
    if(side == EnumFacing.UP) {
      return ChannelConnection.IN;
    }
    // down should ask the boolean, might be out
    if(side == EnumFacing.DOWN) {
      return this.connectedDown ? ChannelConnection.OUT : ChannelConnection.NONE;
    }

    // the other four use an array index
    int index = side.getHorizontalIndex();
    if(index < 0) {
      return null;
    }

    // not nullable
    ChannelConnection connection = connections[index];
    return connection == null ? ChannelConnection.NONE : connection;
  }

  public boolean isConnectedDown() {
    return connectedDown;
  }

  public void setConnection(@Nonnull EnumFacing side, @Nonnull ChannelConnection connection) {
    if(side == EnumFacing.DOWN) {
      this.connectedDown = connection == ChannelConnection.OUT;
      return;
    }

    int index = side.getHorizontalIndex();
    if(index >= 0) {
      ChannelConnection oldConnection = this.connections[index];
      // if we changed from or to none, adjust connections
      if(oldConnection != ChannelConnection.OUT && connection == ChannelConnection.OUT) {
        numOutputs++;
      } else if (oldConnection == ChannelConnection.OUT && connection != ChannelConnection.OUT) {
        numOutputs--;
      }

      this.connections[index] = connection;
    }
  }

  public void setFlow(@Nonnull EnumFacing side, boolean isFlowing) {
    if(side == EnumFacing.UP) {
      return;
    }

    boolean wasFlowing = setFlowRaw(side, isFlowing);
    if(wasFlowing != isFlowing) {
      TinkerNetwork.sendToClients((WorldServer) world, pos, new ChannelFlowPacket(pos, side, isFlowing));
    }
  }

  private boolean setFlowRaw(@Nonnull EnumFacing side, boolean isFlowing) {
    boolean wasFlowing;
    if(side == EnumFacing.DOWN) {
      wasFlowing = this.isFlowingDown;
      this.isFlowingDown = isFlowing;
    } else {
      int index = side.getHorizontalIndex();
      wasFlowing = this.isFlowing[index] > 0;
      this.isFlowing[index] = (byte) (isFlowing ? 2 : 0);
    }

    return wasFlowing;
  }

  public boolean isFlowing(@Nonnull EnumFacing side) {
    if(side == EnumFacing.DOWN) {
      return this.isFlowingDown;
    }

    int index = side.getHorizontalIndex();
    if(index >= 0) {
      return this.isFlowing[index] > 0;
    }

    return false;
  }

  public boolean isFlowingDown() {
    return isFlowingDown;
  }

  private void updateBlock(BlockPos pos) {
    IBlockState state = world.getBlockState(pos);
    world.notifyBlockUpdate(pos, state, state, 2);
  }


  /* Rendering */
  @Override
  public boolean hasFastRenderer() {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getRenderBoundingBox() {
    return new AxisAlignedBB(pos.getX(), pos.getY() - 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
  }


  /* NBT */
  private static final String TAG_CONNECTIONS = "connections";
  private static final String TAG_CONNECTED_DOWN = "connected_down";
  private static final String TAG_IS_FLOWING = "is_flowing";
  private static final String TAG_IS_FLOWING_DOWN = "is_flowing_down";
  private static final String TAG_WAS_POWERED = "was_powered";
  private static final String TAG_TANK = "tank";

  // load and save
  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
    nbt = super.writeToNBT(nbt);

    byte[] bytes = new byte[4];
    ChannelConnection connection;
    for(int i = 0; i < 4; i++) {
      connection = connections[i];
      bytes[i] = connection == null ? 0 : connection.getIndex();
    }
    nbt.setByteArray(TAG_CONNECTIONS, bytes);
    nbt.setBoolean(TAG_CONNECTED_DOWN, connectedDown);
    nbt.setByteArray(TAG_IS_FLOWING, isFlowing);
    nbt.setBoolean(TAG_IS_FLOWING_DOWN, isFlowingDown);
    nbt.setBoolean(TAG_WAS_POWERED, wasPowered);
    nbt.setTag(TAG_TANK, tank.writeToNBT(new NBTTagCompound()));

    return nbt;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbt) {
    super.readFromNBT(nbt);

    // connections
    if(nbt.hasKey(TAG_CONNECTIONS)) {
      this.connections = new ChannelConnection[4];
      this.numOutputs = 0;
      byte[] bytes = nbt.getByteArray(TAG_CONNECTIONS);
      for(int i = 0; i < 4 && i < bytes.length; i++) {
        this.connections[i] = ChannelConnection.fromIndex(bytes[i]);

        // just calc this instead of storing it
        if(this.connections[i] != ChannelConnection.NONE) {
          this.numOutputs++;
        }
      }
    }
    this.connectedDown = nbt.getBoolean(TAG_CONNECTED_DOWN);

    // isFlowing
    if(nbt.hasKey(TAG_IS_FLOWING)) {
      this.isFlowing = nbt.getByteArray(TAG_IS_FLOWING);
    }
    this.isFlowingDown = nbt.getBoolean(TAG_IS_FLOWING_DOWN);
    this.wasPowered = nbt.getBoolean(TAG_WAS_POWERED);

    // tank
    NBTTagCompound tankTag = nbt.getCompoundTag(TAG_TANK);
    if(tankTag != null) {
      tank.readFromNBT(tankTag);
    }
  }

  // networking
  @Override
  public void updateFluidTo(FluidStack fluid) {
    tank.setFluid(fluid);
  }

  @SideOnly(Side.CLIENT)
  public void updateConnection(EnumFacing side, boolean connect) {
    this.setConnection(side, connect ? ChannelConnection.OUT : ChannelConnection.NONE);
    this.updateBlock(pos);
  }

  @SideOnly(Side.CLIENT)
  public void updateFlow(EnumFacing side, boolean flow) {
    this.setFlowRaw(side, flow);
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    readFromNBT(pkt.getNbtCompound());
  }

  @Nonnull
  @Override
  public NBTTagCompound getUpdateTag() {
    // new tag instead of super since default implementation calls the super of writeToNBT
    return writeToNBT(new NBTTagCompound());
  }

  @Override
  public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
    readFromNBT(tag);
  }

  public static enum ChannelConnection implements IStringSerializable {
    NONE,
    IN,
    OUT;

    byte index;
    ChannelConnection() {
      index = (byte)ordinal();
    }

    public byte getIndex() {
      return index;
    }

    public ChannelConnection getOpposite() {
      switch(this) {
        case IN:  return OUT;
        case OUT: return IN;
      }
      return NONE;
    }

    public ChannelConnection getNext(boolean reverse) {
      if(reverse) {
        switch(this) {
          case NONE: return IN;
          case IN:   return OUT;
          case OUT:  return NONE;
        }
      } else {
        switch(this) {
          case NONE: return OUT;
          case OUT:  return IN;
          case IN:   return NONE;
        }
      }
      // not possible
      throw new UnsupportedOperationException();
    }

    public static ChannelConnection fromIndex(int index) {
      if(index < 0 || index >= values().length) {
        return NONE;
      }

      return values()[index];
    }

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }

    public boolean canFlow() {
      return this != NONE;
    }

    public static boolean canFlow(ChannelConnection connection) {
      return connection != null && connection != NONE;
    }
  }
}
