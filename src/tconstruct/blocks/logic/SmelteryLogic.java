package tconstruct.blocks.logic;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import tconstruct.common.TContent;
import tconstruct.inventory.SmelteryContainer;
import tconstruct.library.blocks.InventoryLogic;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.util.PHConstruct;
import tconstruct.util.SmelteryDamageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* Simple class for storing items in the block
 */

public class SmelteryLogic extends InventoryLogic implements IActiveLogic, IFacingLogic, IFluidTank, IMasterLogic
{
    public boolean validStructure;
    public boolean tempValidStructure;
    byte direction;
    int internalTemp;
    public int useTime;
    public int fuelGague;
    public int fuelAmount;
    boolean inUse;

    ArrayList<CoordTuple> lavaTanks;
    CoordTuple activeLavaTank;
    public CoordTuple centerPos;

    public int[] activeTemps;
    public int[] meltingTemps;
    int tick;

    public ArrayList<FluidStack> moltenMetal = new ArrayList<FluidStack>();
    int maxLiquid;
    int currentLiquid;
    public int layers;
    int slag;

    int numBricks;

    Random rand = new Random();
    boolean needsUpdate;

    public SmelteryLogic()
    {
        super(0);
        lavaTanks = new ArrayList<CoordTuple>();
        activeTemps = new int[0];
        meltingTemps = new int[0];
    }

    void adjustLayers (int lay, boolean forceAdjust)
    {
        if (lay != layers || forceAdjust)
        {
            needsUpdate = true;
            layers = lay;
            maxLiquid = 20000 * lay;
            int[] tempActive = activeTemps;
            activeTemps = new int[9 * lay];
            int activeLength = tempActive.length > activeTemps.length ? activeTemps.length : tempActive.length;
            System.arraycopy(tempActive, 0, activeTemps, 0, activeLength);

            int[] tempMelting = meltingTemps;
            meltingTemps = new int[9 * lay];
            int meltingLength = tempMelting.length > meltingTemps.length ? meltingTemps.length : tempMelting.length;
            System.arraycopy(tempMelting, 0, meltingTemps, 0, meltingLength);

            ItemStack[] tempInv = inventory;
            inventory = new ItemStack[9 * lay];
            int invLength = tempInv.length > inventory.length ? inventory.length : tempInv.length;
            System.arraycopy(tempInv, 0, inventory, 0, invLength);

            if (activeTemps.length > 0 && activeTemps.length > tempActive.length)
            {
                for (int i = tempActive.length; i < activeTemps.length; i++)
                {
                    activeTemps[i] = 20;
                    meltingTemps[i] = 20;
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
                            EntityItem entityitem = new EntityItem(worldObj, (double) ((float) xCoord + jumpX + offsetX), (double) ((float) yCoord + jumpY),
                                    (double) ((float) zCoord + jumpZ + offsetZ), new ItemStack(stack.itemID, itemSize, stack.getItemDamage()));

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
        needsUpdate = true;
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
        return internalTemp;
    }

    public int getTempForSlot (int slot)
    {
        return activeTemps[slot];
    }

    public int getMeltingPointForSlot (int slot)
    {
        return meltingTemps[slot];
    }

    /* Updating */
    public void updateEntity ()
    {
        /*if (worldObj.isRemote)
            return;*/

        tick++;
        if (tick % 4 == 0)
            heatItems();

        if (tick % 20 == 0)
        {
            if (!validStructure)
                checkValidPlacement();

            if (useTime > 0 && inUse)
                useTime -= 3;

            if (validStructure && useTime <= 0)
            {
                updateFuelGague();
            }

            if (needsUpdate)
            {
                needsUpdate = false;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }

        if (tick == 60)
        {
            tick = 0;
            if (validStructure)
                detectEntities();
        }
    }

    void detectEntities ()
    {
        AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(centerPos.x, centerPos.y, centerPos.z, centerPos.x + 1.0D, centerPos.y + 1.0D, centerPos.z + 1.0D).expand(1.0D, 0.0D, 1.0D);

        List list = worldObj.getEntitiesWithinAABB(Entity.class, box);
        for (Object o : list)
        {
            if (o instanceof EntityVillager)
            {
                EntityVillager villager = (EntityVillager) o;
                if (villager.attackEntityFrom(new SmelteryDamageSource(), 1))
                {
                    if (currentLiquid + 8 < maxLiquid)
                    {
                        int amount = villager.isChild() ? 1 : 8;
                        this.addMoltenMetal(new FluidStack(TContent.moltenEmeraldFluid, amount), false);
                    }
                }
            }
            else if (o instanceof EntityEnderman)
            {
                EntityEnderman villager = (EntityEnderman) o;
                if (villager.attackEntityFrom(new SmelteryDamageSource(), 1))
                {
                    if (currentLiquid + 25 < maxLiquid)
                    {
                        this.addMoltenMetal(new FluidStack(TContent.moltenEnderFluid, 25), false);
                    }
                }
            }
            else if (o instanceof EntityLiving)
            {
                EntityLiving living = (EntityLiving) o;
                if (living.attackEntityFrom(new SmelteryDamageSource(), 1))
                {
                    /*if (currentLiquid + 8 < maxLiquid)
                    {
                        int amount = living.isChild() ? 2 : 8;
                        this.addMoltenMetal(new FluidStack(TContent.liquidMetalStill.blockID, amount, 16), false);
                    }*/
                }
            }
            else if (PHConstruct.throwableSmeltery && o instanceof EntityItem)
            {
                handleItemEntity((EntityItem) o);
            }
        }
    }

    private void handleItemEntity(EntityItem item)
    {
        // Clients like to play merry hell with this and cause breakage (we update their inv on syncs)
        if (worldObj.isRemote) return;

        item.age = 0;
        ItemStack istack = item.getEntityItem();
        if (istack.stackSize <= 0) return;

        int maxSlot = this.getSizeInventory();
        boolean itemDestroyed = false;
        boolean itemAdded = false;

        for (int i = 0; i < maxSlot; i++)
        {
            ItemStack stack = this.getStackInSlot(i);
            if (stack == null && istack.stackSize > 0)
            {
                this.setInventorySlotContents(i, new ItemStack(istack.getItem(), 1));
                istack.stackSize -= 1;
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
        if (itemAdded) {
            this.needsUpdate = true;
            PacketDispatcher.sendPacketToAllInDimension(getDescriptionPacket(), worldObj.provider.dimensionId);
        }
    }

    void heatItems ()
    {
        if (useTime > 0)
        {
            boolean hasUse = false;
            for (int i = 0; i < 9 * layers; i++)
            {
                if (meltingTemps[i] > 20 && this.isStackInSlot(i))
                {
                    hasUse = true;
                    if (activeTemps[i] < internalTemp && activeTemps[i] < meltingTemps[i])
                    {
                        activeTemps[i] += 1;
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
                                    activeTemps[i] = 20;
                                    ArrayList alloys = Smeltery.mixMetals(moltenMetal);
                                    for (int al = 0; al < alloys.size(); al++)
                                    {
                                        FluidStack liquid = (FluidStack) alloys.get(al);
                                        addMoltenMetal(liquid, true);
                                    }
                                    onInventoryChanged();
                                }
                            }
                        }
                    }

                }

                else
                    activeTemps[i] = 20;
            }
            inUse = hasUse;
        }
    }

    boolean addMoltenMetal (FluidStack liquid, boolean first)
    {
        needsUpdate = true;
        if (moltenMetal.size() == 0)
        {
            moltenMetal.add(liquid.copy());
            currentLiquid += liquid.amount;
            return true;
        }
        else
        {
            if (liquid.amount + currentLiquid > maxLiquid)
                return false;

            currentLiquid += liquid.amount;
            //System.out.println("Current liquid: "+currentLiquid);
            boolean added = false;
            for (int i = 0; i < moltenMetal.size(); i++)
            {
                FluidStack l = moltenMetal.get(i);
                //if (l.itemID == liquid.itemID && l.itemMeta == liquid.itemMeta)
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

    void updateTemperatures ()
    {
        inUse = true;
        for (int i = 0; i < 9 * layers; i++)
        {
            meltingTemps[i] = Smeltery.instance.getLiquifyTemperature(inventory[i]);
        }
    }

    public void updateFuelDisplay ()
    {
        if (activeLavaTank == null || useTime > 0)
            return;

        if (!worldObj.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        TileEntity tankContainer = worldObj.getBlockTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
        if (tankContainer == null)
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }
        if (tankContainer instanceof IFluidHandler)
        {
            needsUpdate = true;
            FluidStack liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, 150, false);
            if (liquid != null && liquid.getFluid().getBlockID() == Block.lavaStill.blockID)
            {
                FluidTankInfo[] info = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN);
                if (info.length > 0)
                {
                    int capacity = info[0].capacity;
                    fuelAmount = liquid.amount;
                    fuelGague = liquid.amount * 52 / capacity;
                }
            }
            else
            {
                fuelAmount = 0;
                fuelGague = 0;
            }
        }
    }

    void updateFuelGague ()
    {
        if (activeLavaTank == null || useTime > 0)
            return;

        if (!worldObj.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        TileEntity tankContainer = worldObj.getBlockTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
        if (tankContainer == null)
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }
        if (tankContainer instanceof IFluidHandler)
        {
            needsUpdate = true;
            FluidStack liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, 150, false);
            if (liquid != null && liquid.getFluid().getBlockID() == Block.lavaStill.blockID)
            {
                liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, 150, true);
                useTime += liquid.amount;

                FluidTankInfo[] info = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN);
                liquid = info[0].fluid;
                int capacity = info[0].capacity;
                if (liquid != null)
                {
                    fuelAmount = liquid.amount;
                    fuelGague = liquid.amount * 52 / capacity;
                }
                else
                {
                    fuelAmount = 0;
                    fuelGague = 0;
                }
            }
            else
            {
                boolean foundTank = false;
                int iter = 0;
                while (!foundTank)
                {
                    CoordTuple possibleTank = lavaTanks.get(iter);
                    TileEntity newTankContainer = worldObj.getBlockTileEntity(possibleTank.x, possibleTank.y, possibleTank.z);
                    if (newTankContainer instanceof IFluidHandler)
                    {
                        //System.out.println("Tank: "+possibleTank.toString());
                        FluidStack newliquid = ((IFluidHandler) newTankContainer).drain(ForgeDirection.UNKNOWN, 150, false);
                        if (newliquid != null && newliquid.getFluid().getBlockID() == Block.lavaStill.blockID && newliquid.amount > 0)
                        {
                            //System.out.println("Tank: "+possibleTank.toString());
                            foundTank = true;
                            activeLavaTank = possibleTank;
                            iter = lavaTanks.size();

                            /*IFluidTank newTank = ((IFluidHandler) newTankContainer).getTank(ForgeDirection.UNKNOWN, liquid);
                            liquid = newTank.getFluid();
                            int capacity = newTank.getCapacity();*/
                            FluidTankInfo[] info = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN);
                            liquid = info[0].fluid;
                            int capacity = info[0].capacity;
                            if (liquid != null)
                            {
                                fuelAmount = liquid.amount;
                                fuelGague = liquid.amount * 52 / capacity;
                            }
                            else
                            {
                                fuelAmount = 0;
                                fuelGague = 0;
                            }
                        }
                    }
                    iter++;
                    if (iter >= lavaTanks.size())
                        foundTank = true;
                }
                //System.out.println("Searching for tank, size: "+lavaTanks.size());
            }
        }
    }

    public FluidStack getResultFor (ItemStack stack)
    {
        return Smeltery.instance.getSmelteryResult(stack);
    }

    /* Inventory */
    /*public int getMaxStackStackSize (ItemStack stack)
    {
    	FluidStack liquid = getResultFor(stack);
    	if (liquid == null)
    		return 0;
    	return liquid.amount;
    }*/

    public int getInventoryStackLimit ()
    {
        return 1;
    }

    public void onInventoryChanged ()
    {
        updateTemperatures();
        updateEntity();
        super.onInventoryChanged();
        needsUpdate = true;
        //worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        //worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    /* Multiblock */
    @Override
    public void notifyChange (int x, int y, int z)
    {
        checkValidPlacement();
    }

    public void checkValidPlacement ()
    {
        switch (getRenderDirection())
        {
        case 2: // +z
            alignInitialPlacement(xCoord, yCoord, zCoord + 2);
            break;
        case 3: // -z
            alignInitialPlacement(xCoord, yCoord, zCoord - 2);
            break;
        case 4: // +x
            alignInitialPlacement(xCoord + 2, yCoord, zCoord);
            break;
        case 5: // -x
            alignInitialPlacement(xCoord - 2, yCoord, zCoord);
            break;
        }
    }

    public void alignInitialPlacement (int x, int y, int z)
    {
        int northID = worldObj.getBlockId(x, y, z + 1);
        int southID = worldObj.getBlockId(x, y, z - 1);
        int eastID = worldObj.getBlockId(x + 1, y, z);
        int westID = worldObj.getBlockId(x - 1, y, z);

        Block northBlock = Block.blocksList[northID];
        Block southBlock = Block.blocksList[southID];
        Block eastBlock = Block.blocksList[eastID];
        Block westBlock = Block.blocksList[westID];

        if ((northBlock == null || northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock == null || southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock == null || eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock == null || westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x, y, z);
        }

        else if ((northBlock != null && !northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock == null || southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock == null || eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock == null || westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x, y, z - 1);
        }

        else if ((northBlock == null || northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock != null && !southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock == null || eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock == null || westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x, y, z + 1);
        }

        else if ((northBlock == null || northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock == null || southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock != null && !eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock == null || westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x - 1, y, z);
        }

        else if ((northBlock == null || northBlock.isAirBlock(worldObj, x, y, z + 1)) && (southBlock == null || southBlock.isAirBlock(worldObj, x, y, z - 1))
                && (eastBlock == null || eastBlock.isAirBlock(worldObj, x + 1, y, z)) && (westBlock != null && !westBlock.isAirBlock(worldObj, x - 1, y, z)))
        {
            checkValidStructure(x + 1, y, z);
        }

        //Not valid, sorry
    }

    public void checkValidStructure (int x, int y, int z)
    {
        int checkLayers = 0;
        tempValidStructure = false;
        if (checkSameLevel(x, y, z))
        {
            checkLayers++;
            checkLayers += recurseStructureUp(x, y + 1, z, 0);
            checkLayers += recurseStructureDown(x, y - 1, z, 0);
        }

        //maxLiquid = capacity * 20000;

        if (tempValidStructure != validStructure || checkLayers != this.layers)
        {
            if (tempValidStructure)
            {
                internalTemp = 800;
                activeLavaTank = lavaTanks.get(0);
                adjustLayers(checkLayers, false);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = true;
            }
            else
            {
                internalTemp = 20;
                validStructure = false;
            }
        }
    }

    public boolean checkSameLevel (int x, int y, int z)
    {
        numBricks = 0;
        lavaTanks.clear();
        Block block;

        //Check inside
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++)
            {
                block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos))
                    return false;
            }
        }

        //Check outer layer
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 2);
            numBricks += checkBricks(xPos, y, z + 2);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            numBricks += checkBricks(x - 2, y, zPos);
            numBricks += checkBricks(x + 2, y, zPos);
        }

        if (numBricks == 12 && lavaTanks.size() > 0)
            return true;
        else
            return false;
    }

    public int recurseStructureUp (int x, int y, int z, int count)
    {
        numBricks = 0;
        //Check inside
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++)
            {
                Block block = Block.blocksList[worldObj.getBlockId(xPos, y, zPos)];
                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos))
                    return count;
            }
        }

        //Check outer layer
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 2);
            numBricks += checkBricks(xPos, y, z + 2);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            numBricks += checkBricks(x - 2, y, zPos);
            numBricks += checkBricks(x + 2, y, zPos);
        }

        if (numBricks != 12)
            return count;

        count++;
        return recurseStructureUp(x, y + 1, z, count);
    }

    public int recurseStructureDown (int x, int y, int z, int count)
    {
        numBricks = 0;
        //Check inside
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++)
            {
                int blockID = worldObj.getBlockId(xPos, y, zPos);
                Block block = Block.blocksList[blockID];
                if (block != null && !block.isAirBlock(worldObj, xPos, y, zPos))
                {
                    if (blockID == TContent.smeltery.blockID)
                        return validateBottom(x, y, z, count);
                    else
                        return count;
                }
            }
        }

        //Check outer layer
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            numBricks += checkBricks(xPos, y, z - 2);
            numBricks += checkBricks(xPos, y, z + 2);
        }

        for (int zPos = z - 1; zPos <= z + 1; zPos++)
        {
            numBricks += checkBricks(x - 2, y, zPos);
            numBricks += checkBricks(x + 2, y, zPos);
        }

        if (numBricks != 12)
            return count;

        count++;
        return recurseStructureDown(x, y - 1, z, count);
    }

    public int validateBottom (int x, int y, int z, int count)
    {
        int bottomBricks = 0;
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int zPos = z - 1; zPos <= z + 1; zPos++)
            {
                if (worldObj.getBlockId(xPos, y, zPos) == TContent.smeltery.blockID && (worldObj.getBlockMetadata(xPos, y, zPos) >= 2))
                    bottomBricks++;
            }
        }

        if (bottomBricks == 9)
        {
            tempValidStructure = true;
            centerPos = new CoordTuple(x, y + 1, z);
        }
        return count;
    }

    /* Returns whether the brick is a lava tank or not.
     * Increments bricks, sets them as part of the structure, and adds tanks to the list.
     */
    int checkBricks (int x, int y, int z)
    {
        int tempBricks = 0;
        int blockID = worldObj.getBlockId(x, y, z);
        if (blockID == TContent.smeltery.blockID || blockID == TContent.lavaTank.blockID)
        {
            TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te == this)
            {
                tempBricks++;
            }
            else if (te instanceof MultiServantLogic)
            {
                MultiServantLogic servant = (MultiServantLogic) te;
                if (servant.hasValidMaster())
                {
                    if (servant.verifyMaster(this.xCoord, this.yCoord, this.zCoord))
                        tempBricks++;
                }
                else if (servant.setMaster(this.xCoord, this.yCoord, this.zCoord))
                {
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
        if (moltenMetal.size() == 0)
            return null;

        FluidStack liquid = moltenMetal.get(0);
        if (liquid.amount - maxDrain <= 0)
        {
            FluidStack liq = liquid.copy();
            if (doDrain)
            {
                //liquid = null;
                moltenMetal.remove(liquid);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                currentLiquid = 0;
                needsUpdate = true;
            }
            return liq;
        }
        else
        {
            if (doDrain)
            {
                liquid.amount -= maxDrain;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                currentLiquid -= maxDrain;
                needsUpdate = true;
            }
            return new FluidStack(liquid.fluidID, maxDrain, liquid.tag);
        }
    }

    @Override
    public int fill (FluidStack resource, boolean doFill)
    {
        if (resource != null && resource.amount + currentLiquid < maxLiquid)
        {
            int amount = resource.amount;
            if (doFill)
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
                worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
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
    
    public FluidTankInfo[] getMultiTankInfo()
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
        inventory = new ItemStack[layers * 9];
        super.readFromNBT(tags);

        //validStructure = tags.getBoolean("ValidStructure");
        internalTemp = tags.getInteger("InternalTemp");
        inUse = tags.getBoolean("InUse");

        int[] center = tags.getIntArray("CenterPos");
        if (center.length > 2)
            centerPos = new CoordTuple(center[0], center[1], center[2]);
        else
            centerPos = new CoordTuple(xCoord, yCoord, zCoord);

        direction = tags.getByte("Direction");
        useTime = tags.getInteger("UseTime");
        currentLiquid = tags.getInteger("CurrentLiquid");
        maxLiquid = tags.getInteger("MaxLiquid");
        meltingTemps = tags.getIntArray("MeltingTemps");
        activeTemps = tags.getIntArray("ActiveTemps");

        NBTTagList liquidTag = tags.getTagList("Liquids");
        moltenMetal.clear();

        for (int iter = 0; iter < liquidTag.tagCount(); iter++)
        {
            NBTTagCompound nbt = (NBTTagCompound) liquidTag.tagAt(iter);
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            moltenMetal.add(fluid);
        }
        //adjustLayers(layers, true);
        //checkValidPlacement();
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);

        //tags.setBoolean("ValidStructure", validStructure);
        tags.setInteger("InternalTemp", internalTemp);
        tags.setBoolean("InUse", inUse);

        int[] center = new int[3];// { centerPos.x, centerPos.y, centerPos.z };
        if (centerPos == null)
            center = new int[] { xCoord, yCoord, zCoord };
        else
            center = new int[] { centerPos.x, centerPos.y, centerPos.z };
        tags.setIntArray("CenterPos", center);

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
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.data);
        onInventoryChanged();
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        this.needsUpdate = true;
    }
}
