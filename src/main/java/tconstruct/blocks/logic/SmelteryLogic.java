package tconstruct.blocks.logic;

import java.util.*;

import mantle.debug.DebugData;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import org.apache.commons.lang3.ArrayUtils;

import tconstruct.common.TRepo;
import tconstruct.inventory.SmelteryContainer;
import mantle.blocks.abstracts.InventoryLogic;
import tconstruct.library.crafting.Smeltery;
import mantle.blocks.iface.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;
import mantle.blocks.abstracts.MultiServantLogic;
import mantle.world.CoordTuple;
import mantle.blocks.iface.IMasterLogic;
import mantle.blocks.iface.IServantLogic;
import tconstruct.util.SmelteryDamageSource;
import tconstruct.util.config.PHConstruct;
import cpw.mods.fml.common.network.PacketDispatcher;

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
                            EntityItem entityitem = new EntityItem(field_145850_b, (double) ((float) field_145851_c + jumpX + offsetX), (double) ((float) field_145848_d + jumpY),
                                    (double) ((float) field_145849_e + jumpZ + offsetZ), new ItemStack(stack.getItem(), itemSize, stack.getItemDamage()));

                            if (stack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                            }

                            float offset = 0.05F;
                            entityitem.motionX = (double) ((float) rand.nextGaussian() * offset);
                            entityitem.motionY = (double) ((float) rand.nextGaussian() * offset + 0.2F);
                            entityitem.motionZ = (double) ((float) rand.nextGaussian() * offset);
                            field_145850_b.spawnEntityInWorld(entityitem);
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
        field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
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
        /*if (field_145850_b.isRemote)
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
                field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
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

        List list = field_145850_b.getEntitiesWithinAABB(Entity.class, box);
        for (Object o : list)
        {
            if (moltenMetal.size() >= 1)
            {
                if (o instanceof EntityVillager)
                {
                    EntityVillager villager = (EntityVillager) o;
                    if (villager.attackEntityFrom(new SmelteryDamageSource(), 5))
                    {
                        if (currentLiquid + 40 < maxLiquid)
                        {
                            int amount = villager.isChild() ? 5 : 40;
                            this.fill(new FluidStack(TRepo.moltenEmeraldFluid, amount), true);
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
                            this.fill(new FluidStack(TRepo.moltenEnderFluid, 125), true);
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
                            this.fill(new FluidStack(TRepo.moltenIronFluid, 40), true);
                        }
                    }
                }
                else if (o instanceof EntityHorse)
                {
                    EntityHorse horse = (EntityHorse) o;
                    if (PHConstruct.meltableHorses && horse.attackEntityFrom(new SmelteryDamageSource(), 5))
                    {
                        if (currentLiquid + 108 < maxLiquid)
                        {
                            this.fill(new FluidStack(TRepo.glueFluid, 108), true);
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
                            this.fill(new FluidStack(TRepo.bloodFluid, amount), true);
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
        // Clients like to play merry hell with this and cause breakage (we update their inv on syncs)
        if (field_145850_b.isRemote)
            return;

        item.age = 0;
        ItemStack istack = item.getEntityItem();
        if (istack == null || istack.stackSize <= 0) //Probably most definitely not necessary
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
            PacketDispatcher.sendPacketToAllInDimension(getDescriptionPacket(), field_145850_b.provider.dimensionId);
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
                        if (!field_145850_b.isRemote)
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
            //TConstruct.logger.info("Current liquid: "+currentLiquid);
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

        if (!field_145850_b.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        TileEntity tankContainer = field_145850_b.func_147438_o(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
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

    void updateFuelGague () //TODO: Call this method when the GUI is opened
    {
        if (activeLavaTank == null || useTime > 0)
            return;

        if (!field_145850_b.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        TileEntity tankContainer = field_145850_b.func_147438_o(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
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
                    TileEntity newTankContainer = field_145850_b.func_147438_o(possibleTank.x, possibleTank.y, possibleTank.z);
                    if (newTankContainer instanceof IFluidHandler)
                    {
                        //TConstruct.logger.info("Tank: "+possibleTank.toString());
                        FluidStack newliquid = ((IFluidHandler) newTankContainer).drain(ForgeDirection.UNKNOWN, 150, false);
                        if (newliquid != null && newliquid.getFluid().getBlockID() == Block.lavaStill.blockID && newliquid.amount > 0)
                        {
                            //TConstruct.logger.info("Tank: "+possibleTank.toString());
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
                //TConstruct.logger.info("Searching for tank, size: "+lavaTanks.size());
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
        //field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
        //field_145850_b.markBlockForRenderUpdate(field_145851_c, field_145848_d, field_145849_e);
    }

    /*@Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack != null ? itemstack.splitStack(1) : null; //May include unintended side effects. Possible fix for max stack size of 1?
    }*/

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
            alignInitialPlacement(field_145851_c, field_145848_d, field_145849_e + 2);
            break;
        case 3: // -z
            alignInitialPlacement(field_145851_c, field_145848_d, field_145849_e - 2);
            break;
        case 4: // +x
            alignInitialPlacement(field_145851_c + 2, field_145848_d, field_145849_e);
            break;
        case 5: // -x
            alignInitialPlacement(field_145851_c - 2, field_145848_d, field_145849_e);
            break;
        }
    }

    public void alignInitialPlacement (int x, int y, int z)
    {
        Block northBlock = field_145850_b.func_147439_a(x, y, z + 1);
        Block southBlock = field_145850_b.func_147439_a(x, y, z - 1);
        Block eastBlock = field_145850_b.func_147439_a(x + 1, y, z);
        Block westBlock = field_145850_b.func_147439_a(x - 1, y, z);

        if ((northBlock == null || northBlock == Blocks.air) && (southBlock == null || southBlock == Blocks.air)
                && (eastBlock == null || eastBlock == Blocks.air) && (westBlock == null || westBlock == Blocks.air))
        {
            checkValidStructure(x, y, z);
        }

        else if ((northBlock != null && !(northBlock == Blocks.air) && (southBlock == null || southBlock == Blocks.air)
                && (eastBlock == null || eastBlock == Blocks.air) && (westBlock == null || westBlock == Blocks.air)))
        {
            checkValidStructure(x, y, z - 1);
        }

        else if ((northBlock == null || northBlock == Blocks.air) && (southBlock != null && !(southBlock == Blocks.air))
                && (eastBlock == null || eastBlock == Blocks.air) && (westBlock == null || westBlock == Blocks.air))
        {
            checkValidStructure(x, y, z + 1);
        }

        else if ((northBlock == null || northBlock == Blocks.air) && (southBlock == null || southBlock == Blocks.air)
                && (eastBlock != null && !(eastBlock == Blocks.air)) && (westBlock == null || westBlock == Blocks.air))
        {
            checkValidStructure(x - 1, y, z);
        }

        else if ((northBlock == null || northBlock == Blocks.air) && (southBlock == null || southBlock == Blocks.air)
                && (eastBlock == null || eastBlock == Blocks.air) && (westBlock != null && !(westBlock == Blocks.air)))
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
                field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
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
                block = field_145850_b.func_147439_a(xPos, y, zPos);
                if (block != null && block != Blocks.air)
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
                Block block = field_145850_b.func_147439_a(xPos, y, zPos);
                if (block != null && !(block == Blocks.air))
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
                Block block = field_145850_b.func_147439_a(xPos, y, zPos);
                if (block != null && block != Blocks.air)
                {
                    if (validBlock(block))
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
                if (validBlock(field_145850_b.func_147439_a(xPos, y, zPos)) && (field_145850_b.getBlockMetadata(xPos, y, zPos) >= 2))
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
        Block block = field_145850_b.func_147439_a(x, y, z);
        if (validBlock(block) || validTank(block))
        {
            TileEntity te = field_145850_b.func_147438_o(x, y, z);
            if (te == this)
            {
                tempBricks++;
            }
            else if (te instanceof MultiServantLogic)
            {
                MultiServantLogic servant = (MultiServantLogic) te;
                if (servant.hasValidMaster())
                {
                    if (servant.verifyMaster(this, this.field_145851_c, this.field_145848_d, this.field_145849_e))
                        tempBricks++;
                }
                else if (servant.setMaster(this.field_145851_c, this.field_145848_d, this.field_145849_e))
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

    boolean validBlock (Block block)
    {
        return block == TRepo.smeltery || block == TRepo.smelteryNether;
    }

    boolean validTank (Block block)
    {
        return block == TRepo.lavaTank || block == TRepo.lavaTankNether;
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
        if (liquid != null)
        {
            if (liquid.amount - maxDrain <= 0)
            {
                FluidStack liq = liquid.copy();
                if (doDrain)
                {
                    //liquid = null;
                    moltenMetal.remove(liquid);
                    field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
                    currentLiquid = 0;
                    needsUpdate = true;
                }
                return liq;
            }
            else
            {
                if (doDrain && maxDrain > 0)
                {
                    liquid.amount -= maxDrain;
                    field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
                    currentLiquid -= maxDrain;
                    needsUpdate = true;
                }
                return new FluidStack(liquid.fluidID, maxDrain, liquid.tag);
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
        if (resource != null && currentLiquid < maxLiquid)//resource.amount + currentLiquid < maxLiquid)
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
                field_145850_b.markBlockForRenderUpdate(field_145851_c, field_145848_d, field_145849_e);
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
        inventory = new ItemStack[layers * 9];
        super.readFromNBT(tags);

        //validStructure = tags.getBoolean("ValidStructure");
        internalTemp = tags.getInteger("InternalTemp");
        inUse = tags.getBoolean("InUse");

        int[] center = tags.getIntArray("CenterPos");
        if (center.length > 2)
            centerPos = new CoordTuple(center[0], center[1], center[2]);
        else
            centerPos = new CoordTuple(field_145851_c, field_145848_d, field_145849_e);

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
            if (fluid != null)
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
            center = new int[] { field_145851_c, field_145848_d, field_145849_e };
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
        return new Packet132TileEntityData(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readFromNBT(packet.data);
        onInventoryChanged();
        field_145850_b.markBlockForRenderUpdate(field_145851_c, field_145848_d, field_145849_e);
        this.needsUpdate = true;
    }

    // IDebuggable
    @Override
    public DebugData getDebugInfo (EntityPlayer player)
    {
        List<String> str = new ArrayList<String>(Arrays.asList(super.getDebugInfo(player).strings));
        str.add("layers: " + layers + ", liquid: " + currentLiquid + "/" + maxLiquid + ", direction: " + direction);
        str.add("inUse: " + inUse + ", tick: " + tick);
        return new DebugData(player, getClass(), str.toArray(new String[str.size()]));
    }
}
