package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.client.GuiSmeltery;
import slimeknights.tconstruct.smeltery.events.TinkerSmelteryEvent;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockSmeltery;

public class TileSmeltery extends TileHeatingStructure implements IMasterLogic, IUpdatePlayerListBox, IInventoryGui {

  protected static final int MAX_SIZE = 7;
  protected static final int CAPACITY_PER_BLOCK = Material.VALUE_Ingot * 8;

  // Info about the smeltery structure/multiblock
  public boolean active;
  public MultiblockDetection.MultiblockStructure info;
  public List<BlockPos> tanks;
  public BlockPos currentTank;

  // Info about the state of the smeltery. Liquids etc.
  protected SmelteryTank liquids;

  protected MultiblockSmeltery multiblock;
  protected int tick;

  public TileSmeltery() {
    super("gui.smeltery.name", 0, 1);
    multiblock = new MultiblockSmeltery(this);
    tanks = Lists.newLinkedList();
  }

  @Override
  public void update() {
    if(this.worldObj.isRemote) {
      return;
    }
    tick = (tick + 1) % 20;

    // are we fully formed?
    if(!isActive()) {
      // check for smeltery once per second
      if(tick == 0) {
        checkSmelteryStructure();
      }
    }
    else {
      // smeltery structure is there.. do stuff with the current fuel
      // this also updates the needsFuel flag, which causes us to consume fuel at the end.
      // This way fuel is only consumed if it's actually needed

      if(tick == 0) {
        interactWithEntitiesInside();
      }
      heatItems();
      alloyAlloys();

      if(needsFuel) {
        consumeFuel();
      }
    }
  }

  /* Smeltery processing logic. Consuming fuel, heating stuff, creating alloys etc. */

  // melt stuff
  @Override
  protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
    MeltingRecipe recipe = TinkerRegistry.getMelting(stack);

    if(recipe == null) return false;

    TinkerSmelteryEvent.OnMelting event = TinkerSmelteryEvent.OnMelting.fireEvent(this, stack, recipe.output);

    int filled = liquids.fill(event.result, false);

    if(filled == event.result.amount) {
      liquids.fill(event.result, true);

      // only clear out items n stuff if it was successful
      setInventorySlotContents(slot, null);
      return true;
    }

    return false;
  }


  // This is how you get blisters
  protected void interactWithEntitiesInside() {

  }

  // check for alloys and create them
  protected void alloyAlloys() {

  }

  @Override
  protected void consumeFuel() {
    // no need to consume fuel
    if(hasFuel()) {
      return;
    }

    // get current tank
    searchForFuel();

    // got a tank?
    if(currentTank != null) {
      // consume fuel!
      IFluidTank tank = getTankAt(currentTank);
      FluidStack liquid = tank.getFluid();
      if(liquid != null) {
        FluidStack in = liquid.copy();
        int bonusFuel = TinkerRegistry.consumeSmelteryFuel(in);
        int amount = liquid.amount - in.amount;
        FluidStack drained = tank.drain(amount, false);

        // we can drain. actually drain and add the fuel
        if(drained.amount == amount) {
          tank.drain(amount, true);
          addFuel(bonusFuel, drained.getFluid().getTemperature(drained));
        }
      }
    }
  }

  private void searchForFuel() {
    // is the current tank still up to date?
    if(hasFuel(currentTank)) {
      return;
    }

    // nope, current tank is empty, check others
    for(BlockPos pos : tanks) {
      if(hasFuel(pos)) {
        currentTank = pos;
        return;
      }
    }

    currentTank = null;
  }

  // checks if the given location has a fluid tank that contains fuel
  private boolean hasFuel(BlockPos pos) {
    IFluidTank tank = getTankAt(pos);
    if(tank != null) {
      if(tank.getFluidAmount() > 0 && TinkerRegistry.isSmelteryFuel(tank.getFluid())) {
        return true;
      }
    }

    return false;
  }

  private IFluidTank getTankAt(BlockPos pos) {
    IBlockState state = worldObj.getBlockState(pos);
    if(state.getBlock() instanceof IFluidTank) {
      return (IFluidTank) state.getBlock();
    }

    return null;
  }

  /* Smeltery Multiblock Detection/Formation */

  /** Called by the servants */
  @Override
  public void notifyChange(IServantLogic servant, BlockPos pos) {
    checkSmelteryStructure();
  }

  // Checks if the smeltery is fully built and updates status accordingly
  public void checkSmelteryStructure() {
    boolean wasActive = isActive();

    IBlockState state = this.worldObj.getBlockState(getPos());
    if(!(state.getBlock() instanceof BlockSmelteryController)) {
      active = false;
    }
    else {
      EnumFacing in = ((EnumFacing) state.getValue(BlockSmelteryController.FACING)).getOpposite();

      MultiblockDetection.MultiblockStructure structure = multiblock.detectMultiblock(this.worldObj, this.getPos().offset(in), MAX_SIZE);
      if(structure == null) {
        active = false;
        updateSmelteryInfo(new MultiblockDetection.MultiblockStructure(0, 0, 0, Lists.<BlockPos>newLinkedList()));
      }
      else {
        // we found a valid smeltery. yay.
        active = true;
        MultiblockDetection.assignMultiBlock(this.worldObj, this.getPos(), structure.blocks);
        updateSmelteryInfo(structure);
      }
    }

    // mark the block for updating so the smeltery controller block updates its graphics
    if(wasActive != isActive()) {
      worldObj.markBlockForUpdate(pos);
      this.markDirty();
    }
  }

  protected void updateSmelteryInfo(MultiblockDetection.MultiblockStructure structure) {
    info = structure;

    // find all tanks for input
    tanks.clear();
    for(BlockPos pos : structure.blocks) {
      // todo: check if is tank
      if(false) {
        tanks.add(pos);
      }
    }

    int inventorySize = structure.xd * structure.yd * structure.zd;
    // if the new smeltery is smaller we pop out all items that don't fit in anymore
    if(this.getSizeInventory() > inventorySize) {
      for(int i = inventorySize; i < getSizeInventory(); i++) {
        if(getStackInSlot(i) != null) {
          dropItem(getStackInSlot(i));
        }
      }
    }

    this.liquids.setCapacity(inventorySize * CAPACITY_PER_BLOCK);

    // adjust inventory sizes
    this.resize(inventorySize);
  }

  private void dropItem(ItemStack stack) {
    EnumFacing direction = (EnumFacing) worldObj.getBlockState(pos).getValue(BlockSmelteryController.FACING);
    BlockPos pos = this.getPos().offset(direction);

    EntityItem entityitem = new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), stack);
    worldObj.spawnEntityInWorld(entityitem);
  }

  /* Fluid handling */

  public SmelteryTank getTank() {
    return liquids;
  }

  /* GUI */
  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerSmeltery(inventoryplayer, this);
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiSmeltery((ContainerSmeltery)createContainer(inventoryplayer, world, pos));
  }

  /* Network & Saving */

  @Override
  public void writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);

    writeDataToNBT(compound);
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);

    readDataFromNBT(compound);

    // this is only called on the initial load of the smeltery
    // we verify its state
    checkSmelteryStructure();
  }

  protected void writeDataToNBT(NBTTagCompound tag) {
    tag.setBoolean("active", active);
  }

  protected void readDataFromNBT(NBTTagCompound tag) {
    active = tag.getBoolean("active");
  }

  @Override
  public Packet getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeDataToNBT(tag);
    return new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);

    boolean wasActive = active;

    readDataFromNBT(pkt.getNbtCompound());

    // update chunk (rendering) if the active state changed
    if(isActive() != wasActive) {
      worldObj.markBlockForUpdate(pos);
    }
  }

  /* Getter */

  public boolean isActive() {
    return active;
  }
}
