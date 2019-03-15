package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.client.GuiTinkerTank;
import slimeknights.tconstruct.smeltery.inventory.ContainerTinkerTank;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockTinkerTank;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidUpdatePacket;

public class TileTinkerTank extends TileMultiblock<MultiblockTinkerTank> implements ITickable, IInventoryGui, ISmelteryTankHandler {

  protected static final int CAPACITY_PER_BLOCK = Fluid.BUCKET_VOLUME * 4;

  protected MultiblockTinkerTank multiblock;
  protected boolean active;

  // Info about the state of the tank
  protected SmelteryTank liquids;
  protected int tick;

  public TileTinkerTank() {
    super("gui.tinkertank.name", 0);
    setMultiblock(new MultiblockTinkerTank(this));
    liquids = new SmelteryTank(this);
  }

  @Override
  public void update() {
    if(this.getWorld().isRemote) {
      return;
    }

    // are we fully formed?
    if(!isActive()) {
      // check for tank once per second
      if(tick == 0) {
        checkMultiblockStructure();
      }

      tick = (tick + 1) % 20;
    }

    // if we are already active, we don't do anything
  }

  @Override
  protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
    // we add 2 to the coordinates so we include the walls/floor/ceiling in the size calculation
    // otherwise a 3x3x3 tank is way too little capacity
    int liquidSize = (structure.xd + 2) * (structure.yd + 2) * (structure.zd + 2);
    this.liquids.setCapacity(liquidSize * TileTinkerTank.CAPACITY_PER_BLOCK);
    this.markDirtyFast();
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
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiTinkerTank(createContainer(inventoryplayer, world, pos), this);
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
    // we stretch the bounding on the X and Z since the liquids show in the full structure rather than just the inside
    // we also need to include the controller's position for Y value as we render a face there (but X/Z is covered above)
    return new AxisAlignedBB(
        minPos.getX() - 1,
        Math.min(minPos.getY(), pos.getY()),
        minPos.getZ() - 1,
        maxPos.getX() + 2,
        Math.max(maxPos.getY(), pos.getY()) + 1,
        maxPos.getZ() + 2
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
    if(isServerWorld()) {
      TinkerNetwork.sendToAll(new SmelteryFluidUpdatePacket(pos, fluids));
    }
    this.markDirtyFast();
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);
    liquids.writeToNBT(compound);

    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    liquids.readFromNBT(compound);
  }
}
