package tconstruct.smeltery.logic;

import cpw.mods.fml.relauncher.*;
import java.util.*;
import mantle.blocks.abstracts.*;
import mantle.blocks.iface.*;
import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import tconstruct.TConstruct;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.*;
import tconstruct.smeltery.inventory.SmelteryContainer;
import tconstruct.util.config.PHConstruct;

/* Simple class for storing items in the block
 */

public class SmelteryLogic extends InventoryLogic implements IActiveLogic, IFacingLogic, IFluidTank, IMasterLogic
{
    private static final int MAX_SMELTERY_SIZE = 7;
    public static final int MB_PER_BLOCK_CAPACITY = TConstruct.ingotLiquidValue*10;

    public boolean validStructure;
    public boolean tempValidStructure;
    protected byte direction;

    public CoordTuple minPos = new CoordTuple(0, 0, 0);
    public CoordTuple maxPos = new CoordTuple(0, 0, 0);
    public int layers;
    public int maxBlockCapacity;

    protected int internalTemp;
    public int useTime;
    public int fuelGague;
    public int fuelAmount;
    protected boolean inUse;

    protected ArrayList<CoordTuple> lavaTanks;
    protected CoordTuple activeLavaTank;

    public int[] activeTemps; // values are multiplied by 10
    public int[] meltingTemps; // values are multiplied by 10
    private int tick;

    public ArrayList<FluidStack> moltenMetal = new ArrayList<FluidStack>();
    public int maxLiquid;
    public int currentLiquid;

    Random rand = new Random();
    boolean needsUpdate;

    public SmelteryLogic()
    {
        super(0);
        lavaTanks = new ArrayList<CoordTuple>();
        activeTemps = new int[0];
        meltingTemps = new int[0];
    }

    public int getBlocksPerLayer ()
    {
        int xd = maxPos.x - minPos.x + 1;
        int zd = maxPos.z - minPos.z + 1;
        return xd * zd;
    }

    public int getCapacityPerLayer ()
    {
        return getBlocksPerLayer() * MB_PER_BLOCK_CAPACITY;
    }

    public int getBlockCapacity ()
    {
        return maxBlockCapacity;
    }

    void adjustLayers (int lay, boolean forceAdjust)
    {
        if (lay != layers || forceAdjust)
        {
            needsUpdate = true;
            layers = lay;
            maxBlockCapacity = getBlocksPerLayer() * layers;
            maxLiquid = maxBlockCapacity * MB_PER_BLOCK_CAPACITY;

            int[] tempActive = activeTemps;
            activeTemps = new int[maxBlockCapacity];
            int activeLength = tempActive.length > activeTemps.length ? activeTemps.length : tempActive.length;
            System.arraycopy(tempActive, 0, activeTemps, 0, activeLength);

            int[] tempMelting = meltingTemps;
            meltingTemps = new int[maxBlockCapacity];
            int meltingLength = tempMelting.length > meltingTemps.length ? meltingTemps.length : tempMelting.length;
            System.arraycopy(tempMelting, 0, meltingTemps, 0, meltingLength);

            ItemStack[] tempInv = inventory;
            inventory = new ItemStack[maxBlockCapacity];
            int invLength = tempInv.length > inventory.length ? inventory.length : tempInv.length;
            System.arraycopy(tempInv, 0, inventory, 0, invLength);

            if (activeTemps.length > 0 && activeTemps.length > tempActive.length)
            {
                for (int i = tempActive.length; i < activeTemps.length; i++)
                {
                    activeTemps[i] = 200;
                    meltingTemps[i] = 200;
                }
            }

            if (tempInv.length > inventory.length)
            {
                for (int i = inventory.length; i < tempInv.length; i++)
                {
                    ItemStack stack = tempInv[i];
                    if (stack != null)
                    {
                        float jumpX = rand.nextFloat() * 0.8F + 0.1F;
                        float jumpY = rand.nextFloat() * 0.8F + 0.1F;
                        float jumpZ = rand.nextFloat() * 0.8F + 0.1F;

                        int offsetX = 0;
                        int offsetZ = 0;
                        switch (getRenderDirection())
                        {
                        case 2: // +z
                            offsetZ = -1;
                            break;
                        case 3: // -z
                            offsetZ = 1;
                            break;
                        case 4: // +x
                            offsetX = -1;
                            break;
                        case 5: // -x
                            offsetX = 1;
                            break;
                        }

                        while (stack.stackSize > 0)
                        {
                            int itemSize = rand.nextInt(21) + 10;

                            if (itemSize > stack.stackSize)
                            {
                                itemSize = stack.stackSize;
                            }

                            stack.stackSize -= itemSize;
                            EntityItem entityitem = new EntityItem(worldObj, (double) ((float) xCoord + jumpX + offsetX), (double) ((float) yCoord + jumpY), (double) ((float) zCoord + jumpZ + offsetZ), new ItemStack(stack.getItem(), itemSize, stack.getItemDamage()));

                            if (stack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                            }

                            float offset = 0.05F;
                            entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                            entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                            entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                            worldObj.spawnEntityInWorld(entityitem);
                        }
                    }
                }
            }
        }

        // update current liquid. This is done in case some config or something changed the capacity or other things.
        updateCurrentLiquid();
    }

    /* Misc */
    @Override
    public String getDefaultName ()
    {
        return "crafters.Smeltery";
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return new SmelteryContainer(inventoryplayer, this);
    }

    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public void setDirection (int side)
    {

    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
        switch (facing)
        {
        case 0:
            direction = 2;
            break;

        case 1:
            direction = 5;
            break;

        case 2:
            direction = 3;
            break;

        case 3:
            direction = 4;
            break;
        }
    }

    @Override
    public boolean getActive ()
    {
        return validStructure;
    }

    @Override
    public void setActive (boolean flag)
    {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getScaledFuelGague (int scale)
    {
        int ret = (fuelGague * scale) / 52;
        if (ret < 1)
            ret = 1;
        return ret;
    }

    public int getInternalTemperature ()
    {
        if (!validStructure)
            return 20;

        return internalTemp;
    }

    public int getTempForSlot (int slot)
    {
        return activeTemps[slot] / 10;
    }

    public int getMeltingPointForSlot (int slot)
    {
        return meltingTemps[slot] / 10;
    }

    /* Updating */
    @Override
    public void updateEntity ()
    {
        tick++;
        if (tick == 60)
        {
            tick = 0;
            detectEntities();
        }

        /*
         * if (worldObj.isRemote) return;
         */


        if (tick % 4 == 0) {
            if (useTime > 0)
                useTime -= 4;

            if(validStructure) {
                checkHasItems();

                // consume fuel if needed
                if(useTime <= 0 && inUse)
                    updateFuelGague();

                heatItems();
            }
        }

        if (tick % 20 == 0)
        {
            if (!validStructure)
                checkValidPlacement();

            if (needsUpdate)
            {
                needsUpdate = false;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    void detectEntities ()
    {
        if (minPos == null || maxPos == null)
            return;

        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(minPos.x, minPos.y, minPos.z, maxPos.x + 1, minPos.y + layers, maxPos.z + 1);

        List list = worldObj.getEntitiesWithinAABB(Entity.class, box);
        for (Object o : list)
        {
            if (moltenMetal.size() >= 1)
            {
                if (o instanceof EntityVillager && PHConstruct.meltableVillagers)
                {
                    EntityVillager villager = (EntityVillager) o;
                    if (villager.attackEntityFrom(new SmelteryDamageSource(), 5))
                    {
                        if (currentLiquid + 40 < maxLiquid)
                        {
                            int amount = villager.isChild() ? 5 : 40;
                            this.fill(new FluidStack(TinkerSmeltery.moltenEmeraldFluid, amount), true);
                        }
                    }
                }
                else if (o instanceof EntityEnderman)
                {
                    EntityEnderman villager = (EntityEnderman) o;
                    if (villager.attackEntityFrom(new SmelteryDamageSource(), 5))
                    {
                        if (currentLiquid + 125 < maxLiquid)
                        {
                            this.fill(new FluidStack(TinkerSmeltery.moltenEnderFluid, 125), true);
                        }
                    }
                }
                else if (o instanceof EntityIronGolem)
                {
                    EntityIronGolem golem = (EntityIronGolem) o;
                    if (golem.attackEntityFrom(new SmelteryDamageSource(), 5))
                    {
                        if (currentLiquid + 40 < maxLiquid)
                        {
                            this.fill(new FluidStack(TinkerSmeltery.moltenIronFluid, 40), true);
                        }
                    }
                }
                else if (o instanceof EntityHorse && PHConstruct.meltableHorses)
                {
                    EntityHorse horse = (EntityHorse) o;
                    if (horse.attackEntityFrom(new SmelteryDamageSource(), 5))
                    {
                        if (currentLiquid + 108 < maxLiquid)
                        {
                            this.fill(new FluidStack(TinkerSmeltery.glueFluid, 108), true);
                        }
                    }
                }
                else if (o instanceof EntityLivingBase)
                {
                    EntityLivingBase living = (EntityLivingBase) o;
                    if (living.attackEntityFrom(new SmelteryDamageSource(), 5))
                    {
                        if (currentLiquid + 40 < maxLiquid)
                        {
                            int amount = (living.isChild() || living instanceof EntityPlayer) ? 5 : 40;
                            this.fill(new FluidStack(TinkerSmeltery.bloodFluid, amount), true);
                        }
                    }
                }
            }
            else if (PHConstruct.throwableSmeltery && o instanceof EntityItem)
            {
                handleItemEntity((EntityItem) o);
            }
        }
    }

    private void handleItemEntity (EntityItem item)
    {
        // Clients like to play merry hell with this and cause breakage (we
        // update their inv on syncs)
        if (worldObj.isRemote)
            return;

        item.age = 0;
        ItemStack istack = item.getEntityItem();
        if (istack == null || istack.stackSize <= 0) // Probably most definitely
                                                     // not necessary
            return;

        int maxSlot = this.getSizeInventory();
        boolean itemDestroyed = false;
        boolean itemAdded = false;

        for (int i = 0; i < maxSlot; i++)
        {
            ItemStack stack = this.getStackInSlot(i);
            if (stack == null && istack.stackSize > 0)
            {
                ItemStack copy = istack.splitStack(1);
                this.setInventorySlotContents(i, copy);
                itemAdded = true;
                if (istack.stackSize <= 0)
                {
                    item.setDead();
                    itemDestroyed = true;
                    break;
                }
            }
        }

        if (!itemDestroyed)
            item.setEntityItemStack(istack);
        if (itemAdded)
        {
            this.needsUpdate = true;
            //TODO 1.7.5 send description packet in better way to not cause render update
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    private void checkHasItems()
    {
        inUse = false;
        for (int i = 0; i < maxBlockCapacity; i++)
            if(this.isStackInSlot(i) && meltingTemps[i] > 200)
            {
                inUse = true;
                break;
            }
    }

    private void heatItems ()
    {
        if (useTime > 0)
        {
            boolean hasUse = false;
            int temperature = this.getInternalTemperature();
            int speed = temperature / 100;
            int refTemp = temperature * 10;
            for (int i = 0; i < maxBlockCapacity; i++)
            {
                if (meltingTemps[i] > 200 && this.isStackInSlot(i))
                {
                    hasUse = true;
                    if (activeTemps[i] < refTemp && activeTemps[i] < meltingTemps[i])
                    {
                        activeTemps[i] += speed; // lava has temp of 1000. we increase by 10 per application.
                    }
                    else if (activeTemps[i] >= meltingTemps[i])
                    {
                        if (!worldObj.isRemote)
                        {
                            FluidStack result = getResultFor(inventory[i]);
                            if (result != null)
                            {
                                if (addMoltenMetal(result, false))
                                {
                                    inventory[i] = null;
                                    activeTemps[i] = 200;
                                    ArrayList alloys = Smeltery.mixMetals(moltenMetal);
                                    for (int al = 0; al < alloys.size(); al++)
                                    {
                                        FluidStack liquid = (FluidStack) alloys.get(al);
                                        addMoltenMetal(liquid, true);
                                    }
                                    markDirty();
                                }
                            }
                        }
                    }

                }

                else
                    activeTemps[i] = 200;
            }
            inUse = hasUse;
        }
    }

    boolean addMoltenMetal (FluidStack liquid, boolean first)
    {
        needsUpdate = true;
        if (moltenMetal.size() == 0)
        {
            // does it fit in?
            if(liquid.amount > this.getCapacity())
                return false;

            moltenMetal.add(liquid.copy());
            updateCurrentLiquid();
            return true;
        }
        else
        {
            // update liquid amount..
            updateCurrentLiquid();

            if (liquid.amount + currentLiquid > maxLiquid)
                return false;

            currentLiquid += liquid.amount;
            // TConstruct.logger.info("Current liquid: "+currentLiquid);
            boolean added = false;
            for (int i = 0; i < moltenMetal.size(); i++)
            {
                FluidStack l = moltenMetal.get(i);
                // if (l.itemID == liquid.itemID && l.itemMeta ==
                // liquid.itemMeta)
                if (l.isFluidEqual(liquid))
                {
                    l.amount += liquid.amount;
                    added = true;
                }
                if (l.amount <= 0)
                {
                    moltenMetal.remove(l);
                    i--;
                }
            }
            if (!added)
            {
                if (first)
                    moltenMetal.add(0, liquid.copy());
                else
                    moltenMetal.add(liquid.copy());
            }
            return true;
        }
    }

    private void updateCurrentLiquid() {
        currentLiquid = 0;
        for(FluidStack liquid : moltenMetal)
            currentLiquid += liquid.amount;
    }

    private void updateTemperatures ()
    {
        for (int i = 0; i < maxBlockCapacity && i < meltingTemps.length; i++)
        {
            meltingTemps[i] = Smeltery.getLiquifyTemperature(inventory[i]) * 10; // temperatures are *10 for more progress control
        }
    }

    public void updateFuelDisplay ()
    {
        // ensure our active tank is valid
        verifyFuelTank();
        if (activeLavaTank == null) {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        // checks are all done before in verifyFuelTank. Don't do this without checks!
        IFluidHandler tankContainer = (IFluidHandler) worldObj.getTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
        FluidTankInfo[] info = tankContainer.getTankInfo(ForgeDirection.DOWN);

        int capacity = info[0].capacity;
        fuelAmount = info[0].fluid.amount;
        fuelGague = (int)((float)fuelAmount * 52f / (float)capacity);
    }

    // actually is updateFuel.
    public void updateFuelGague ()
    {
        // no need to update
        if(useTime > 0 || !inUse)
            return;

        // ensure active lava tank
        verifyFuelTank();
        if (activeLavaTank == null)
            return;

        // checks are all done before in verifyFuelTank. Don't do this without checks!
        IFluidHandler tankContainer = (IFluidHandler) worldObj.getTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);

        // get liquid from the tank
        FluidStack liquid = tankContainer.drain(ForgeDirection.DOWN, 15, false);
        if (liquid != null && Smeltery.isSmelteryFuel(liquid.getFluid())) // doublecheck that everything is ok
        {
            do {
                // drain actual liquid, non simulated
                liquid = tankContainer.drain(ForgeDirection.DOWN, 15, true);
                // we try to do it as long as we don't have enough. Only needed for rapid-use fuels.
                if(liquid == null || liquid.amount == 0)
                    break;
                useTime += (int) ((float) Smeltery.getFuelDuration(liquid.getFluid()) * Math.round(15f / (float) liquid.amount));
                internalTemp = Smeltery.getFuelPower(liquid.getFluid());
            } while(useTime < 0);

            // update fuel display
            updateFuelDisplay(); // this also ensures that the next fuel tank is displayed if this drain made the current one empty
        }
    }

    protected void verifyFuelTank()
    {
        // is our current tank still good?
        if(activeLavaTank != null && worldObj.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            TileEntity tankContainer = worldObj.getTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
            if (tankContainer instanceof IFluidHandler)
            {
                FluidStack liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, 15, false);
                // current tank still has liquid and it's a fuel. everythin ok.
                if (liquid != null && Smeltery.isSmelteryFuel(liquid.getFluid()))
                    return;
            }
        }


        // our tank got derped or is empty. time to look for a new one!
        activeLavaTank = null;
        for(CoordTuple tank : lavaTanks)
        {
            // does the tank still exist?
            if (!worldObj.blockExists(tank.x, tank.y, tank.z))
                continue;

            // yes, it does, but is it a tank?
            TileEntity tankContainer = worldObj.getTileEntity(tank.x, tank.y, tank.z);
            if (! (tankContainer instanceof IFluidHandler))
                continue;

            // yes it is, but does it contain a liquid?
            FluidTankInfo[] info = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN);
            if(info.length <= 0 || info[0].fluid == null || info[0].fluid.amount <= 0)
                continue;

            // is it also a smeltery fuel?
            if(!Smeltery.isSmelteryFuel(info[0].fluid.getFluid()))
                continue;

            // we found a tank! :)
            activeLavaTank = tank;
            return;
        }

        // possibly assign default empty tank here (tanks.get(0)) so we don't have a null activeLavaTank if all are empty
    }

    @SideOnly(Side.CLIENT)
    public FluidStack getFuel()
    {
        if (activeLavaTank == null)
            // sane default
            return new FluidStack(FluidRegistry.LAVA, 0);

        TileEntity tankContainer = worldObj.getTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
        if (tankContainer instanceof IFluidHandler)
            return ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN)[0].fluid;

        return new FluidStack(FluidRegistry.LAVA, 0);
    }

    public FluidStack getResultFor (ItemStack stack)
    {
        return Smeltery.getSmelteryResult(stack);
    }

    /* Inventory */
    /*
     * public int getMaxStackStackSize (ItemStack stack) { FluidStack liquid =
     * getResultFor(stack); if (liquid == null) return 0; return liquid.amount;
     * }
     */

    @Override
    public int getInventoryStackLimit ()
    {
        return 1;
    }

    @Override
    public void markDirty ()
    {
        updateTemperatures();
        updateEntity();

        super.markDirty();
        needsUpdate = true;
        // worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        // worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    /*
     * @Override public void setInventorySlotContents (int slot, ItemStack
     * itemstack) { inventory[slot] = itemstack != null ?
     * itemstack.splitStack(1) : null; //May include unintended side effects.
     * Possible fix for max stack size of 1? }
     */

    /* Multiblock */
    @Override
    public void notifyChange (IServantLogic servant, int x, int y, int z)
    {
        checkValidPlacement();
    }

    public void checkValidPlacement ()
    {
        switch (getRenderDirection())
        {
        case 2: // +z
            alignInitialPlacement(xCoord, yCoord, zCoord + 1);
            break;
        case 3: // -z
            alignInitialPlacement(xCoord, yCoord, zCoord - 1);
            break;
        case 4: // +x
            alignInitialPlacement(xCoord + 1, yCoord, zCoord);
            break;
        case 5: // -x
            alignInitialPlacement(xCoord - 1, yCoord, zCoord);
            break;
        }
    }

    // aligns the position given (inside the smeltery) to be the center of the smeltery
    public void alignInitialPlacement (int x, int y, int z)
    {
        // x/y/z = the block behind the controller "inside the smeltery"

        // adjust the x-position of the block until the difference between the outer walls is at most 1
        // basically this means we center the block inside the smeltery on the x axis.
        int xd1 = 1, xd2 = 1; // x-difference
        for (int i = 1; i < MAX_SMELTERY_SIZE; i++) // don't check farther than needed
        {
            if (worldObj.getBlock(x - xd1, y, z) == null || worldObj.isAirBlock(x - xd1, y, z))
                xd1++;
            else if (worldObj.getBlock(x + xd2, y, z) == null || worldObj.isAirBlock(x + xd2, y, z))
                xd2++;

            // if one side hit a wall and the other didn't we might have to center our x-position again
            if (xd1 - xd2 > 1)
            {
                // move x and offsets to the -x
                xd1--;
                x--;
                xd2++;
            }
            // or the right
            if (xd2 - xd1 > 1)
            {
                xd2--;
                x++;
                xd1++;
            }
        }
        // same for z-axis
        int zd1 = 1, zd2 = 1;
        for (int i = 1; i < MAX_SMELTERY_SIZE; i++) // don't check farther than needed
        {
            if (worldObj.getBlock(x, y, z - zd1) == null || worldObj.isAirBlock(x, y, z - zd1))
                zd1++;
            else if (worldObj.getBlock(x, y, z + zd2) == null || worldObj.isAirBlock(x, y, z + zd2))
                zd2++;

            // if one side hit a wall and the other didn't we might have to center our x-position again
            if (zd1 - zd2 > 1)
            {
                // move x and offsets to the -x
                zd1--;
                z--;
                zd2++;
            }
            // or the right
            if (zd2 - zd1 > 1)
            {
                zd2--;
                z++;
                zd1++;
            }
        }

        // do the check
        int[] sides = new int[] { xd1, xd2, zd1, zd2 };
        checkValidStructure(x, y, z, sides);
    }

    /**
     *
     * @param x x-center of the smeltery +-1
     * @param y y-position of the controller block
     * @param z z-center of the smeltery +-1
     * @param sides distance between the center point and the wall. [-x,+x,-z,+z]
     */
    public void checkValidStructure (int x, int y, int z, int[] sides)
    {
        int checkLayers = 0;
        //worldObj.setBlock(x,y,z, Blocks.redstone_block);
        //worldObj.setBlock(x+sides[1]-sides[0],y+1,z+sides[3]-sides[2], Blocks.lapis_block);

        tempValidStructure = false;
        // this piece of code here does the complete validity check.
        if (checkSameLevel(x, y, z, sides))
        {
            checkLayers++;
            checkLayers += recurseStructureUp(x, y + 1, z, sides, 0);
            checkLayers += recurseStructureDown(x, y - 1, z, sides, 0);
        }

        // maxLiquid = capacity * 20000;

        if (tempValidStructure != validStructure || checkLayers != this.layers)
        {
            if (tempValidStructure)
            {
                // try to derive temperature from fueltank
                activeLavaTank = null;
                for (CoordTuple tank : lavaTanks)
                {
                    TileEntity tankContainer = worldObj.getTileEntity(tank.x, tank.y, tank.z);
                    if (!(tankContainer instanceof IFluidHandler))
                        continue;

                    FluidStack liquid = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN)[0].fluid;
                    if (liquid == null)
                        continue;
                    if (!Smeltery.isSmelteryFuel(liquid.getFluid()))
                        continue;

                    internalTemp = Smeltery.getFuelPower(liquid.getFluid());
                    activeLavaTank = tank;
                    break;
                }

                // no tank with fuel. we reserve the first found one
                if (activeLavaTank == null)
                    activeLavaTank = lavaTanks.get(0);

                // update other stuff
                adjustLayers(checkLayers, true);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = true;
            }
            else
            {
                internalTemp = 20;
                if(validStructure)
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = false;
            }
        }
    }

    public boolean checkBricksOnLevel (int x, int y, int z, int[] sides)
    {
        int numBricks = 0;
        Block block;
        int xMin = x - sides[0];
        int xMax = x + sides[1];
        int zMin = z - sides[2];
        int zMax = z + sides[3];

        // Check inside
        for (int xPos = xMin + 1; xPos <= xMax - 1; xPos++)
        {
            for (int zPos = zMin + 1; zPos <= zMax - 1; zPos++)
            {
                block = worldObj.getBlock(xPos, y, zPos);
                if (block != null && !worldObj.isAirBlock(xPos, y, zPos))
                    return false;
            }
        }

        // Check outer layer
        for (int xPos = xMin + 1; xPos <= xMax - 1; xPos++)
        {
            numBricks += checkBricks(xPos, y, zMin);
            numBricks += checkBricks(xPos, y, zMax);
        }

        for (int zPos = zMin + 1; zPos <= zMax - 1; zPos++)
        {
            numBricks += checkBricks(xMin, y, zPos);
            numBricks += checkBricks(xMax, y, zPos);
        }

        int neededBricks = (xMax - xMin) * 2 + (zMax - zMin) * 2 - 4; // -4 because corners are not needed

        return numBricks == neededBricks;
    }

    public boolean checkSameLevel (int x, int y, int z, int[] sides)
    {
        lavaTanks.clear();

        boolean check = checkBricksOnLevel(x, y, z, sides);

        if (check && lavaTanks.size() > 0)
            return true;
        else
            return false;
    }

    public int recurseStructureUp (int x, int y, int z, int[] sides, int count)
    {
        boolean check = checkBricksOnLevel(x, y, z, sides);

        if (!check)
            return count;

        count++;
        return recurseStructureUp(x, y + 1, z, sides, count);
    }

    public int recurseStructureDown (int x, int y, int z, int[] sides, int count)
    {
        boolean check = checkBricksOnLevel(x, y, z, sides);

        if (!check)
        {
            // regular check failed, maybe it's the bottom?
            Block block = worldObj.getBlock(x, y, z);
            if (block != null && !worldObj.isAirBlock(x, y, z))
                if (validBlockID(block))
                    return validateBottom(x, y, z, sides, count);

            return count;
        }

        count++;
        return recurseStructureDown(x, y - 1, z, sides, count);
    }

    public int validateBottom (int x, int y, int z, int[] sides, int count)
    {
        int bottomBricks = 0;
        int xMin = x - sides[0] + 1;
        int xMax = x + sides[1] - 1;
        int zMin = z - sides[2] + 1;
        int zMax = z + sides[3] - 1;

        // Check inside
        for (int xPos = xMin; xPos <= xMax; xPos++)
        {
            for (int zPos = zMin; zPos <= zMax; zPos++)
            {
                if (validBlockID(worldObj.getBlock(xPos, y, zPos)) && (worldObj.getBlockMetadata(xPos, y, zPos) >= 2)) {
                    TileEntity te = worldObj.getTileEntity(xPos, y, zPos);

                    if (te instanceof MultiServantLogic) {
                        MultiServantLogic servant = (MultiServantLogic) te;
                        if (servant.hasValidMaster()) {
                            if (servant.verifyMaster(this, worldObj, this.xCoord, this.yCoord, this.zCoord))
                                bottomBricks++;
                        } else {
                            servant.overrideMaster(this.xCoord, this.yCoord, this.zCoord);
                            bottomBricks++;
                        }
                    }
                }
            }
        }

        int neededBricks = (xMax + 1 - xMin) * (zMax + 1 - zMin); // +1 because we want inclusive the upper bound

        if (bottomBricks == neededBricks)
        {
            tempValidStructure = true;
            minPos = new CoordTuple(xMin, y + 1, zMin);
            maxPos = new CoordTuple(xMax, y + 1, zMax);
        }
        return count;
    }

    /*
     * Returns whether the brick is a lava tank or not. Increments bricks, sets
     * them as part of the structure, and adds tanks to the list.
     */
    int checkBricks (int x, int y, int z)
    {
        int tempBricks = 0;
        Block blockID = worldObj.getBlock(x, y, z);
        if (validBlockID(blockID) || validTankID(blockID))
        {
            TileEntity te = worldObj.getTileEntity(x, y, z);
            if (te == this)
            {
                tempBricks++;
            }
            else if (te instanceof MultiServantLogic)
            {
                MultiServantLogic servant = (MultiServantLogic) te;

                if (servant.hasValidMaster())
                {
                    if (servant.verifyMaster(this, worldObj, this.xCoord, this.yCoord, this.zCoord))
                        tempBricks++;
                }
                else
                {
                    servant.overrideMaster(this.xCoord, this.yCoord, this.zCoord);
                    tempBricks++;
                }

                if (te instanceof LavaTankLogic)
                {
                    lavaTanks.add(new CoordTuple(x, y, z));
                }
            }
        }
        return tempBricks;
    }

    boolean validBlockID (Block blockID)
    {
        return blockID == TinkerSmeltery.smeltery || blockID == TinkerSmeltery.smelteryNether;
    }

    boolean validTankID (Block blockID)
    {
        return blockID == TinkerSmeltery.lavaTank || blockID == TinkerSmeltery.lavaTankNether;
    }

    @Override
    public int getCapacity ()
    {
        return maxLiquid;
    }

    public int getTotalLiquid ()
    {
        return currentLiquid;
    }

    @Override
    public FluidStack drain (int maxDrain, boolean doDrain)
    {
        // don't drain if we're not complete
        if(!validStructure)
            return null;

        if (moltenMetal.size() == 0)
            return null;

        FluidStack liquid = moltenMetal.get(0);
        if (liquid != null)
        {
            if (liquid.amount - maxDrain <= 0)
            {
                FluidStack liq = liquid.copy();
                if (doDrain)
                {
                    // liquid = null;
                    moltenMetal.remove(liquid);
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    needsUpdate = true;
                    updateCurrentLiquid();
                }
                return liq;
            }
            else
            {
                if (doDrain && maxDrain > 0)
                {
                    liquid.amount -= maxDrain;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    currentLiquid -= maxDrain;
                    needsUpdate = true;
                }
                return new FluidStack(liquid.getFluid(), maxDrain, liquid.tag);
            }
        }
        else
        {
            return new FluidStack(0, 0);
        }
    }

    @Override
    public int fill (FluidStack resource, boolean doFill)
    {
        // don't fill if we're not complete
        if(!validStructure)
            return 0;

        if (resource != null && currentLiquid < maxLiquid)// resource.amount +
                                                          // currentLiquid <
                                                          // maxLiquid)
        {
            if (resource.amount + currentLiquid > maxLiquid)
                resource.amount = maxLiquid - currentLiquid;
            int amount = resource.amount;

            if (amount > 0 && doFill)
            {
                if (addMoltenMetal(resource, false))
                {
                    ArrayList alloys = Smeltery.mixMetals(moltenMetal);
                    for (int al = 0; al < alloys.size(); al++)
                    {
                        FluidStack liquid = (FluidStack) alloys.get(al);
                        addMoltenMetal(liquid, true);
                    }
                }
                needsUpdate = true;
                worldObj.func_147479_m(xCoord, yCoord, zCoord);
            }
            return amount;
        }
        else
            return 0;
    }

    @Override
    public FluidStack getFluid ()
    {
        if (moltenMetal.size() == 0)
            return null;
        return moltenMetal.get(0);
    }

    @Override
    public int getFluidAmount ()
    {
        return currentLiquid;
    }

    @Override
    public FluidTankInfo getInfo ()
    {
        return new FluidTankInfo(this);
    }

    public FluidTankInfo[] getMultiTankInfo ()
    {
        FluidTankInfo[] info = new FluidTankInfo[moltenMetal.size() + 1];
        for (int i = 0; i < moltenMetal.size(); i++)
        {
            FluidStack fluid = moltenMetal.get(i);
            info[i] = new FluidTankInfo(fluid.copy(), fluid.amount);
        }
        info[moltenMetal.size()] = new FluidTankInfo(null, maxLiquid - currentLiquid);
        return info;
    }

    /* NBT */

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        layers = tags.getInteger("Layers");
        int[] pos = tags.getIntArray("MinPos");
        if (pos.length > 2)
            minPos = new CoordTuple(pos[0], pos[1], pos[2]);
        else
            minPos = new CoordTuple(xCoord, yCoord, zCoord);

        pos = tags.getIntArray("MaxPos");
        if (pos.length > 2)
            maxPos = new CoordTuple(pos[0], pos[1], pos[2]);
        else
            maxPos = new CoordTuple(xCoord, yCoord, zCoord);

        maxBlockCapacity = getBlocksPerLayer() * layers;
        inventory = new ItemStack[maxBlockCapacity];
        super.readFromNBT(tags);

        internalTemp = tags.getInteger("InternalTemp");
        inUse = tags.getBoolean("InUse");

        direction = tags.getByte("Direction");
        useTime = tags.getInteger("UseTime");
        currentLiquid = tags.getInteger("CurrentLiquid");
        maxLiquid = tags.getInteger("MaxLiquid");
        meltingTemps = tags.getIntArray("MeltingTemps");
        activeTemps = tags.getIntArray("ActiveTemps");

        NBTTagList liquidTag = tags.getTagList("Liquids", 10);
        moltenMetal.clear();

        for (int iter = 0; iter < liquidTag.tagCount(); iter++)
        {
            NBTTagCompound nbt = (NBTTagCompound) liquidTag.getCompoundTagAt(iter);
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null)
                moltenMetal.add(fluid);
        }

        //if(maxBlockCapacity != meltingTemps.length)
          //  adjustLayers(layers, true);

        if(!tags.getBoolean("ValidStructure"))
            validStructure = false; // only negative update because we want to do a clientside structure check too
        else if(!validStructure && worldObj != null) // if the worldobj is null it happens on loading of a world. check shouldn't be done there
            checkValidPlacement();

        // adjustLayers(layers, true);
        // checkValidPlacement();
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);

        tags.setBoolean("ValidStructure", validStructure);
        tags.setInteger("InternalTemp", internalTemp);
        tags.setBoolean("InUse", inUse);

        int[] pos;
        if (minPos == null)
            pos = new int[] { xCoord, yCoord, zCoord };
        else
            pos = new int[] { minPos.x, minPos.y, minPos.z };
        tags.setIntArray("MinPos", pos);

        if (maxPos == null)
            pos = new int[] { xCoord, yCoord, zCoord };
        else
            pos = new int[] { maxPos.x, maxPos.y, maxPos.z };
        tags.setIntArray("MaxPos", pos);

        tags.setByte("Direction", direction);
        tags.setInteger("UseTime", useTime);
        tags.setInteger("CurrentLiquid", currentLiquid);
        tags.setInteger("MaxLiquid", maxLiquid);
        tags.setInteger("Layers", layers);
        tags.setIntArray("MeltingTemps", meltingTemps);
        tags.setIntArray("ActiveTemps", activeTemps);

        NBTTagList taglist = new NBTTagList();
        for (FluidStack liquid : moltenMetal)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            taglist.appendTag(nbt);
        }

        tags.setTag("Liquids", taglist);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.func_148857_g());
        markDirty();
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
        this.needsUpdate = true;
    }

    @Override
    public String getInventoryName ()
    {
        return getDefaultName();
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return true;
    }

    @Override
    public void closeInventory ()
    {
    }

    @Override
    public void openInventory ()
    {
    }
}
