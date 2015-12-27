package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Logger;

import java.util.List;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.smeltery.SmelteryDamageSource;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.client.GuiSmeltery;
import slimeknights.tconstruct.smeltery.events.TinkerSmelteryEvent;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockSmeltery;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryFuelUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryInventoryUpdatePacket;

public class TileSmeltery extends TileHeatingStructure implements IMasterLogic, ITickable, IInventoryGui,
                                                                  ISmelteryTankHandler {

  static final Logger log = Util.getLogger("Smeltery");

  protected static final int MAX_SIZE = 9; // 9 to allow 8x8 smelteries which hold 1 stack and 9x9 for nugget/ingot processing.
  protected static final int CAPACITY_PER_BLOCK = Material.VALUE_Ingot * 8;
  protected static final int ALLOYING_PER_TICK = 10; // how much liquid can be created per tick to make alloys

  // Info about the smeltery structure/multiblock
  public boolean active;
  public MultiblockDetection.MultiblockStructure info;
  public List<BlockPos> tanks;
  public BlockPos currentTank;
  public FluidStack currentFuel; // the fuel that was last consumed

  public BlockPos minPos; // smallest coordinate INSIDE the smeltery
  public BlockPos maxPos; // biggest coordinate INSIDE the smeltery

  // Info about the state of the smeltery. Liquids etc.
  protected SmelteryTank liquids;

  protected MultiblockSmeltery multiblock;
  protected int tick;

  public TileSmeltery() {
    super("gui.smeltery.name", 0, 1);
    multiblock = new MultiblockSmeltery(this);
    liquids = new SmelteryTank(this);
    tanks = Lists.newLinkedList();
  }

  @Override
  public void update() {
    if(this.worldObj.isRemote) {
      return;
    }

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

    tick = (tick + 1) % 20;
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
    else {
      // can't fill into the smeltery, set error satte
      itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
    }

    return false;
  }

  // This is how you get blisters
  protected void interactWithEntitiesInside() {
    // find all entities inside the smeltery

    AxisAlignedBB bb = info.getBoundingBox();

    List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, bb);
    for(Entity entity : entities) {
      // item?
      if(entity instanceof EntityItem) {
        if(TinkerRegistry.getMelting(((EntityItem) entity).getEntityItem()) != null) {
          // todo: pick up and ISidedInventory
        }
      }
      else {
        // custom melting?
        FluidStack fluid = TinkerRegistry.getMeltingForEntity(entity);
        // no custom melting but a living entity that's alive?
        if(fluid == null && entity instanceof EntityLivingBase) {
          if(entity.isEntityAlive() && !entity.isDead) {
            fluid = new FluidStack(FluidRegistry.WATER, 1); // todo: blood
          }
        }

        if(fluid != null) {
          // hurt it
          if(entity.attackEntityFrom(SmelteryDamageSource.instance, 1f)) {
            // spill the blood
            liquids.fill(fluid, true);
          }
        }
      }
    }
  }

  // check for alloys and create them
  protected void alloyAlloys() {
    for(AlloyRecipe recipe : TinkerRegistry.getAlloys()) {
        // find out how often we can apply the recipe
        int matched = recipe.matches(liquids.getFluids());
        if(matched > ALLOYING_PER_TICK) {
          matched = ALLOYING_PER_TICK;
        }
        while(matched > 0) {
          // remove all liquids from the tank
          for(FluidStack liquid : recipe.getFluids()) {
            FluidStack toDrain = liquid.copy();
            FluidStack drained = liquids.drain(toDrain, true);
            // error logging
            if(!drained.isFluidEqual(toDrain) || drained.amount != toDrain.amount) {
              log.error("Smeltery alloy creation drained incorrect amount: was %s:%d, should be %s:%d", drained
                  .getUnlocalizedName(), drained.amount, toDrain.getUnlocalizedName(), toDrain.amount);
            }
          }

          // and insert the alloy
          FluidStack toFill = recipe.getResult().copy();
          int filled = liquids.fill(toFill, true);
          if(filled != recipe.getResult().amount) {
            log.error("Smeltery alloy creation filled incorrect amount: was %d, should be %d (%s)", filled,
                      recipe.getResult().amount * matched, recipe.getResult().getUnlocalizedName());
          }
          matched -= filled;
      }
    }
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
          currentFuel = drained.copy();
          addFuel(bonusFuel, drained.getFluid().getTemperature(drained));

          // notify client of fuel/temperature changes
          if(worldObj != null && !worldObj.isRemote) {
            TinkerNetwork.sendToAll(new SmelteryFuelUpdatePacket(pos, currentTank, temperature, currentFuel));
          }
        }
      }
    }
  }

  private void searchForFuel() {
    // is the current tank still up to date?
    if(currentTank != null && hasFuel(currentTank, currentFuel)) {
      return;
    }

    // nope, current tank is empty, check others for same fuel
    for(BlockPos pos : tanks) {
      if(hasFuel(pos, currentFuel)) {
        currentTank = pos;
        return;
      }
    }

    // nothing found, try again with new fuel
    for(BlockPos pos : tanks) {
      if(hasFuel(pos, null)) {
        currentTank = pos;
        return;
      }
    }

    currentTank = null;
  }

  // checks if the given location has a fluid tank that contains fuel
  private boolean hasFuel(BlockPos pos, FluidStack preference) {
    IFluidTank tank = getTankAt(pos);
    if(tank != null) {
      if(tank.getFluidAmount() > 0 && TinkerRegistry.isSmelteryFuel(tank.getFluid())) {
        // if we have a preference, only use that
        if(preference != null && tank.getFluid().isFluidEqual(preference)) {
          return true;
        }
        else if(preference == null) {
          return true;
        }
      }
    }

    return false;
  }

  private IFluidTank getTankAt(BlockPos pos) {
    TileEntity te = worldObj.getTileEntity(pos);
    if(te instanceof TileTank) {
      return ((TileTank) te).getInternalTank();
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
      EnumFacing in = state.getValue(BlockSmelteryController.FACING).getOpposite();

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
        // we still have to update since something caused us to rebuild our stats
        // might be the smeltery size changed
        if(wasActive)
          worldObj.markBlockForUpdate(pos);
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

    if(info != null) {
      minPos = info.minPos.add(1,1,1); // add walls and floor
      maxPos = info.maxPos.add(-1, 0, -1); // subtract walls, no ceiling
    }
    else {
      minPos = maxPos = this.pos;
    }

    // find all tanks for input
    tanks.clear();
    for(BlockPos pos : structure.blocks) {
      if(worldObj.getBlockState(pos).getBlock() == TinkerSmeltery.searedTank) {
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

    //System.out.println(String.format("[%s] Smeltery detected. Size: %d x %d x %d, %d slots", worldObj != null && worldObj.isRemote ? "Client" : "Server", structure.xd, structure.zd, structure.yd, inventorySize));
  }

  private void dropItem(ItemStack stack) {
    EnumFacing direction = worldObj.getBlockState(pos).getValue(BlockSmelteryController.FACING);
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
    return new GuiSmeltery((ContainerSmeltery)createContainer(inventoryplayer, world, pos), this);
  }

  public float getMeltingProgress(int index) {
    if(index < 0 || index > getSizeInventory() - 1) {
      return -1f;
    }

    if(itemTempRequired[index] > temperature) {
      return -1f;
    }

    return (float)itemTemperatures[index]/(float)itemTempRequired[index];
  }

  @SideOnly(Side.CLIENT)
  public FuelInfo getFuelDisplay() {
    FuelInfo info = new FuelInfo();

    // we still have leftover fuel
    if(hasFuel()) {
      info.fluid = currentFuel.copy();
      info.fluid.amount = 0;
      info.heat = this.temperature;
      info.maxCap = currentFuel.amount;
    }
    else if(currentTank != null) {
      // we need to consume fuel, check the current tank
      if(hasFuel(currentTank, currentFuel)) {
        IFluidTank tank = getTankAt(currentTank);
        info.fluid = tank.getFluid().copy();
        info.heat = temperature;
        info.maxCap = tank.getCapacity();
      }
    }

    // check all other tanks (except the current one that we already checked) for more fuel
    for(BlockPos pos : tanks) {
      if(pos == currentTank) continue;

      IFluidTank tank = getTankAt(pos);
      // tank exists and has something in it
      if(tank != null && tank.getFluidAmount() > 0) {
        // we don't have fuel yet, use this
        if(info.fluid == null) {
          info.fluid = tank.getFluid().copy();
          info.heat = info.fluid.getFluid().getTemperature(info.fluid);
          info.maxCap = tank.getCapacity();
        }
        // otherwise add the same together
        else if(tank.getFluid().isFluidEqual(info.fluid)) {
          info.fluid.amount += tank.getFluidAmount();
          info.maxCap += tank.getCapacity();
        }
      }
    }

    return info;
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    if(minPos == null || maxPos == null) {
      return super.getRenderBoundingBox();
    }
    return AxisAlignedBB.fromBounds(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX()+1, maxPos.getY()+1, maxPos.getZ()+1);
  }

  /* Network & Saving */

  @Override
  public void setInventorySlotContents(int slot, ItemStack itemstack) {
    // send to client if needed
    if(this.worldObj != null && this.worldObj instanceof WorldServer && !this.worldObj.isRemote && !ItemStack.areItemStacksEqual(itemstack, getStackInSlot(slot))) {
      TinkerNetwork.sendToClients((WorldServer) this.worldObj, this.pos, new SmelteryInventoryUpdatePacket(itemstack, slot, pos));
    }
    super.setInventorySlotContents(slot, itemstack);
  }

  @SideOnly(Side.CLIENT)
  public void updateTemperatureFromPacket(int index, int heat) {
    if(index < 0 || index > getSizeInventory()-1) {
      return;
    }

    itemTemperatures[index] = heat;
  }

  @SideOnly(Side.CLIENT)
  public void updateFluidsFromPacket(List<FluidStack> fluids) {
    this.liquids.setFluids(fluids);
    // todo: update smeltery liquid rendering in world
  }

  @Override
  public void onTankChanged(List<FluidStack> fluids, FluidStack changed) {
    // notify clients of liquid changes.
    // the null check is to prevent potential crashes during loading
    if(worldObj != null && !worldObj.isRemote) {
      TinkerNetwork.sendToAll(new SmelteryFluidUpdatePacket(pos, fluids));
    }
  }

  @Override
  public void validate() {
    super.validate();
    // on validation we set active to false so the smeltery checks anew if it's formed
    active = false;
  }

  @Override
  public void writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    liquids.writeToNBT(compound);

    compound.setBoolean("active", active);
    compound.setTag("currentTank", TagUtil.writePos(currentTank));
    NBTTagList tankList = new NBTTagList();
    for(BlockPos pos : tanks) {
      tankList.appendTag(TagUtil.writePos(pos));
    }
    compound.setTag("tanks", tankList);

    NBTTagCompound fuelTag = new NBTTagCompound();
    if(currentFuel != null) {
      currentFuel.writeToNBT(fuelTag);
    }
    compound.setTag("currentFuel", fuelTag);

    compound.setTag("minPos", TagUtil.writePos(minPos));
    compound.setTag("maxPos", TagUtil.writePos(maxPos));
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    liquids.readFromNBT(compound);

    active = compound.getBoolean("active");
    NBTTagList tankList = compound.getTagList("tanks", 10);
    tanks.clear();
    for(int i = 0; i < tankList.tagCount(); i++) {
      tanks.add(TagUtil.readPos(tankList.getCompoundTagAt(i)));
    }

    NBTTagCompound fuelTag = compound.getCompoundTag("currentFuel");
    currentFuel = FluidStack.loadFluidStackFromNBT(fuelTag);

    minPos = TagUtil.readPos(compound.getCompoundTag("minPos"));
    maxPos = TagUtil.readPos(compound.getCompoundTag("maxPos"));
  }

  @Override
  public Packet getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeToNBT(tag);
    return new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    boolean wasActive = active;

    readFromNBT(pkt.getNbtCompound());

    // update chunk (rendering) if the active state changed
    if(isActive() != wasActive) {
      worldObj.markBlockForUpdate(pos);
    }
  }

  /* Getter */

  public boolean isActive() {
    return active;
  }

  public static class FuelInfo {
    public int heat;
    public int maxCap;
    public FluidStack fluid;
  }
}
