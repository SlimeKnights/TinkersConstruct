package slimeknights.tconstruct.smeltery.tileentity;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.block.BlockTinkerTankController;
import slimeknights.tconstruct.smeltery.client.GuiTinkerTank;
import slimeknights.tconstruct.smeltery.inventory.ContainerTinkerTank;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockTinkerTank;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidUpdatePacket;

public class TileTinkerTank extends TileEntity implements IMasterLogic, IInventoryGui, ISmelteryTankHandler, IWorldNameable {

  public static final String TAG_ACTIVE = "active";
  public static final String TAG_MINPOS = "minPos";
  public static final String TAG_MAXPOS = "maxPos";

  protected static final int MAX_SIZE = 9; // consistancy by this point. All others do 9x9
  protected static final int CAPACITY_PER_BLOCK = Fluid.BUCKET_VOLUME * 4;

  // Info about the structure/multiblock
  public MultiblockDetection.MultiblockStructure info;

  public BlockPos minPos; // smallest coordinate INSIDE the tank
  public BlockPos maxPos; // biggest coordinate INSIDE the tank

  protected MultiblockTinkerTank multiblock;
  protected boolean active;

  // Info about the state of the tank
  protected SmelteryTank liquids;
  protected String inventoryTitle;
  protected boolean hasCustomName;

  public TileTinkerTank() {
    multiblock = new MultiblockTinkerTank(this);
    liquids = new SmelteryTank(this);
    this.inventoryTitle = "gui.tinkertank.name";
  }

  /** Called by the servants */
  @Override
  public void notifyChange(IServantLogic servant, BlockPos pos) {
    checkTankStructure();
  }

  // Checks if the tank is fully built and updates status accordingly
  public void checkTankStructure() {
    boolean wasActive = isActive();

    IBlockState state = this.worldObj.getBlockState(getPos());
    if(!(state.getBlock() instanceof BlockTinkerTankController)) {
      active = false;
    }
    else {
      EnumFacing in = state.getValue(BlockSmelteryController.FACING).getOpposite();

      MultiblockDetection.MultiblockStructure structure = multiblock.detectMultiblock(this.worldObj, this.getPos().offset(in), MAX_SIZE);
      if(structure == null) {
        active = false;
        updateTankInfo(null);
      }
      else {
        // we found a valid tank. booyah!
        active = true;
        MultiblockDetection.assignMultiBlock(this.worldObj, this.getPos(), structure.blocks);
        updateTankInfo(structure);
        // we still have to update since something caused us to rebuild our stats
        // might be the tank size changed
        if(wasActive) {
          worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
      }
    }

    // mark the block for updating so the controller block updates its graphics
    if(wasActive != isActive()) {
      worldObj.notifyBlockUpdate(getPos(), state, state, 3);
      this.markDirty();
    }
  }

  protected void updateTankInfo(MultiblockDetection.MultiblockStructure structure) {
    info = structure;

    if(structure == null) {
      structure = new MultiblockDetection.MultiblockStructure(0, 0, 0, ImmutableList.<BlockPos>of(this.pos));
    }

    if(info != null) {
      minPos = info.minPos.add(1, 1, 1); // add walls and floor
      maxPos = info.maxPos.add(-1, -1, -1); // subtract walls and ceiling
    }
    else {
      minPos = maxPos = this.pos;
    }

    // we add 2 to the coordinates so we include the walls/floor/ceiling in the size caculation
    // otherwise a 3x3x3 tank is way too little capacity
    int liquidSize = (structure.xd + 2) * (structure.yd + 2) * (structure.zd + 2);
    this.liquids.setCapacity(liquidSize * CAPACITY_PER_BLOCK);
  }


  /* Fluid handling */
  @Override
  public SmelteryTank getTank() {
    return liquids;
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerTinkerTank(this);
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiTinkerTank((ContainerTinkerTank) createContainer(inventoryplayer, world, pos), this);
  }

  @Nonnull
  @Override
  public String getName() {
    return this.inventoryTitle;
  }

  @Override
  public boolean hasCustomName() {
    return this.hasCustomName;
  }

  public void setCustomName(String customName) {
    this.hasCustomName = true;
    this.inventoryTitle = customName;
  }

  @Nonnull
  @Override
  public ITextComponent getDisplayName() {
    if(hasCustomName()) {
      return new TextComponentString(getName());
    }

    return new TextComponentTranslation(getName());
  }

  @Nonnull
  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    if(minPos == null || maxPos == null) {
      return super.getRenderBoundingBox();
    }
    // we need to include the controller's position as we render a face there
    return new AxisAlignedBB(
        Math.min(minPos.getX(), pos.getX()),
        Math.min(minPos.getY(), pos.getY()),
        Math.min(minPos.getZ(), pos.getZ()),
        Math.max(maxPos.getX(), pos.getX()) + 1,
        Math.max(maxPos.getY(), pos.getY()) + 1,
        Math.max(maxPos.getY(), pos.getZ()) + 1
      );
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void updateFluidsFromPacket(List<FluidStack> fluids) {
    this.liquids.setFluids(fluids);
  }

  @Override
  public void onTankChanged(List<FluidStack> fluids, FluidStack changed) {
    // notify clients of liquid changes.
    // the null check is to prevent potential crashes during loading
    if(worldObj != null && !worldObj.isRemote) {
      TinkerNetwork.sendToAll(new SmelteryFluidUpdatePacket(pos, fluids));
    }
  }

  public boolean isActive() {
    return active;
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);
    liquids.writeToNBT(compound);

    compound.setBoolean(TAG_ACTIVE, active);
    compound.setTag(TAG_MINPOS, TagUtil.writePos(minPos));
    compound.setTag(TAG_MAXPOS, TagUtil.writePos(maxPos));

    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    liquids.readFromNBT(compound);

    active = compound.getBoolean(TAG_ACTIVE);
    minPos = TagUtil.readPos(compound.getCompoundTag(TAG_MINPOS));
    maxPos = TagUtil.readPos(compound.getCompoundTag(TAG_MAXPOS));
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    boolean wasActive = active;

    readFromNBT(pkt.getNbtCompound());

    // update chunk (rendering) if the active state changed
    if(isActive() != wasActive) {
      IBlockState state = worldObj.getBlockState(getPos());
      worldObj.notifyBlockUpdate(getPos(), state, state, 3);
    }
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

}
