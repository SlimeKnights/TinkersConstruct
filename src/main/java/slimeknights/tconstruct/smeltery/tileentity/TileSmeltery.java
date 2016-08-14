package slimeknights.tconstruct.smeltery.tileentity;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Logger;

import java.util.List;

import javax.annotation.Nonnull;

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
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockSmelteryController;
import slimeknights.tconstruct.smeltery.client.GuiSmeltery;
import slimeknights.tconstruct.smeltery.events.TinkerSmelteryEvent;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockDetection;
import slimeknights.tconstruct.smeltery.multiblock.MultiblockSmeltery;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidUpdatePacket;
import slimeknights.tconstruct.smeltery.network.SmelteryInventoryUpdatePacket;

public class TileSmeltery extends TileHeatingStructureFuelTank implements IMasterLogic, ITickable, IInventoryGui,
                                                                          ISmelteryTankHandler {

  public static final DamageSource smelteryDamage = new DamageSource("smeltery").setFireDamage();

  static final Logger log = Util.getLogger("Smeltery");

  // NBT tags
  public static final String TAG_MINPOS = "minPos";
  public static final String TAG_MAXPOS = "maxPos";
  public static final String TAG_INSIDEPOS = "insidePos";

  protected static final int MAX_SIZE = 9; // 9 to allow 8x8 smelteries which hold 1 stack and 9x9 for nugget/ingot processing.
  protected static final int CAPACITY_PER_BLOCK = Material.VALUE_Ingot * 8;
  protected static final int ALLOYING_PER_TICK = 10; // how much liquid can be created per tick to make alloys

  // Info about the smeltery structure/multiblock
  public MultiblockDetection.MultiblockStructure info;

  public BlockPos minPos; // smallest coordinate INSIDE the smeltery
  public BlockPos maxPos; // biggest coordinate INSIDE the smeltery

  // Info about the state of the smeltery. Liquids etc.
  protected SmelteryTank liquids;

  protected MultiblockSmeltery multiblock;
  protected int tick;

  private BlockPos insideCheck; // last checked position for validity inside the smeltery
  private int fullCheckCounter = 0;

  public TileSmeltery() {
    super("gui.smeltery.name", 0, 1);
    multiblock = new MultiblockSmeltery(this);
    liquids = new SmelteryTank(this);
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
      if(tick % 4 == 0) {
        heatItems();
        alloyAlloys();
      }

      if(needsFuel) {
        consumeFuel();
      }

      // we gradually check if the inside of the smeltery is blocked (for performance reasons)
      if(tick == 0) {
        // called every second, we check every 15s or so
        if(++fullCheckCounter >= 15) {
          fullCheckCounter = 0;
          checkSmelteryStructure();
        }
        else {
          // outside or unset?
          if(insideCheck == null
             || insideCheck.getX() < minPos.getX()
             || insideCheck.getY() < minPos.getY()
             || insideCheck.getZ() < minPos.getZ()
             || insideCheck.getX() > maxPos.getX()
             || insideCheck.getY() > maxPos.getY()
             || insideCheck.getZ() > maxPos.getZ()) {
            insideCheck = minPos;
          }

          if(!worldObj.isAirBlock(insideCheck)) {
            // we broke. inside blocked. :(
            this.active = false;
            updateSmelteryInfo(null);
            insideCheck = null;
            IBlockState state = worldObj.getBlockState(this.pos);
            worldObj.notifyBlockUpdate(getPos(), state, state, 3);
          }
          else {
            // advance to next block
            insideCheck = insideCheck.add(1, 0, 0);
            if(insideCheck.getX() > maxPos.getX()) {
              insideCheck = new BlockPos(minPos.getX(), insideCheck.getY(), insideCheck.getZ() + 1);
              if(insideCheck.getZ() > maxPos.getZ()) {
                insideCheck = new BlockPos(minPos.getX(), insideCheck.getY() + 1, minPos.getZ());
              }
            }
          }
        }
      }
    }

    tick = (tick + 1) % 20;
  }

  /* Smeltery processing logic. Consuming fuel, heating stuff, creating alloys etc. */

  @Override
  protected void updateHeatRequired(int index) {
    ItemStack stack = getStackInSlot(index);
    if(stack != null) {
      MeltingRecipe melting = TinkerRegistry.getMelting(stack);
      if(melting != null) {
        setHeatRequiredForSlot(index, Math.max(5, melting.getUsableTemperature()));

        // instantly consume fuel if required
        if(!hasFuel()) {
          consumeFuel();
        }

        return;
      }
    }

    setHeatRequiredForSlot(index, 0);
  }

  // melt stuff
  @Override
  protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
    MeltingRecipe recipe = TinkerRegistry.getMelting(stack);

    if(recipe == null) {
      return false;
    }

    TinkerSmelteryEvent.OnMelting event = TinkerSmelteryEvent.OnMelting.fireEvent(this, stack, recipe.output.copy());

    int filled = liquids.fill(event.result, false);

    if(filled == event.result.amount) {
      liquids.fill(event.result, true);

      // only clear out items n stuff if it was successful
      setInventorySlotContents(slot, null);
      return true;
    }
    else {
      // can't fill into the smeltery, set error state
      itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
    }

    return false;
  }

  // This is how you get blisters
  protected void interactWithEntitiesInside() {
    // find all entities inside the smeltery

    AxisAlignedBB bb = info.getBoundingBox().contract(1).offset(0, 0.5, 0).expand(0, 0.5, 0);

    List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, bb);
    for(Entity entity : entities) {
      // item?
      if(entity instanceof EntityItem) {
        if(TinkerRegistry.getMelting(((EntityItem) entity).getEntityItem()) != null) {
          ItemStack stack = ((EntityItem) entity).getEntityItem();
          // pick it up if we can melt it
          for(int i = 0; i < this.getSizeInventory(); i++) {
            if(!isStackInSlot(i)) {
              // remove 1 from the stack and add it to the smeltery
              ItemStack invStack = stack.copy();
              stack.stackSize--;
              invStack.stackSize = 1;
              this.setInventorySlotContents(i, invStack);
            }
            if(stack.stackSize <= 0) {
              // picked up whole stack
              entity.setDead();
              break;
            }
          }
        }
      }
      // we only melt living entities if we have something in the smeltery
      else if(liquids.getFluidAmount() > 0) {
        // custom melting?
        FluidStack fluid = TinkerRegistry.getMeltingForEntity(entity);
        // no custom melting but a living entity that's alive?
        if(fluid == null && entity instanceof EntityLivingBase) {
          if(entity.isEntityAlive() && !entity.isDead) {
            fluid = new FluidStack(TinkerFluids.blood, 10);
          }
        }

        if(fluid != null) {
          // hurt it
          if(entity.attackEntityFrom(smelteryDamage, 2f)) {
            // spill the blood
            liquids.fill(fluid.copy(), true);
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
        updateSmelteryInfo(null);
      }
      else {
        // we found a valid smeltery. yay.
        active = true;
        MultiblockDetection.assignMultiBlock(this.worldObj, this.getPos(), structure.blocks);
        updateSmelteryInfo(structure);
        // we still have to update since something caused us to rebuild our stats
        // might be the smeltery size changed
        if(wasActive) {
          worldObj.notifyBlockUpdate(getPos(), state, state, 3);
        }
      }
    }

    // mark the block for updating so the smeltery controller block updates its graphics
    if(wasActive != isActive()) {
      worldObj.notifyBlockUpdate(getPos(), state, state, 3);
      this.markDirty();
    }
  }

  protected void updateSmelteryInfo(MultiblockDetection.MultiblockStructure structure) {
    info = structure;

    if(structure == null) {
      structure = new MultiblockDetection.MultiblockStructure(0, 0, 0, ImmutableList.<BlockPos>of(this.pos));
    }

    if(info != null) {
      minPos = info.minPos.add(1, 1, 1); // add walls and floor
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
  @Override
  public SmelteryTank getTank() {
    return liquids;
  }

  /* GUI */
  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerSmeltery(inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiSmeltery((ContainerSmeltery) createContainer(inventoryplayer, world, pos), this);
  }

  @Nonnull
  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    if(minPos == null || maxPos == null) {
      return super.getRenderBoundingBox();
    }
    return new AxisAlignedBB(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX() + 1, maxPos.getY() + 1, maxPos.getZ() + 1);
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

  @Override
  public void validate() {
    super.validate();
    // on validation we set active to false so the smeltery checks anew if it's formed
    active = false;
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound = super.writeToNBT(compound);
    liquids.writeToNBT(compound);

    compound.setTag(TAG_MINPOS, TagUtil.writePos(minPos));
    compound.setTag(TAG_MAXPOS, TagUtil.writePos(maxPos));
    compound.setTag(TAG_INSIDEPOS, TagUtil.writePos(insideCheck));

    return compound;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    liquids.readFromNBT(compound);

    minPos = TagUtil.readPos(compound.getCompoundTag(TAG_MINPOS));
    maxPos = TagUtil.readPos(compound.getCompoundTag(TAG_MAXPOS));
    insideCheck = TagUtil.readPos(compound.getCompoundTag(TAG_INSIDEPOS));
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
