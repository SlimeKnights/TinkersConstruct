package tconstruct.smeltery.component;

import java.util.ArrayList;
import mantle.world.CoordTuple;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import tconstruct.library.component.*;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.logic.AdaptiveSmelteryLogic;

public class SmelteryComponent extends LogicComponent
{
    final AdaptiveSmelteryLogic master;
    final SmelteryScan structure;
    final MultiFluidTank multitank;
    int maxTemp;

    public int fuelTicks;
    public int fuelGague;
    public int fuelAmount;
    boolean inUse;

    public int[] activeTemps = new int[0];
    public int[] meltingTemps = new int[0];

    CoordTuple activeLavaTank;

    public SmelteryComponent(AdaptiveSmelteryLogic inventory, SmelteryScan structure, MultiFluidTank multitank, int maxTemp)
    {
        master = inventory;
        this.structure = structure;
        this.multitank = multitank;
        this.maxTemp = maxTemp;
    }

    public void update ()
    {
        /*
         * if (useTime > 0 && inUse) useTime -= 3;
         */
        if (activeTemps.length == 0)
            inUse = false;

        updateFuelGague();
    }

    public void adjustSize (int size, boolean forceAdjust)
    {
        if (size != activeTemps.length || forceAdjust)
        {
            int[] tempActive = activeTemps;
            activeTemps = new int[size];
            int activeLength = tempActive.length > activeTemps.length ? activeTemps.length : tempActive.length;
            System.arraycopy(tempActive, 0, activeTemps, 0, activeLength);

            int[] tempMelting = meltingTemps;
            meltingTemps = new int[size];
            int meltingLength = tempMelting.length > meltingTemps.length ? meltingTemps.length : tempMelting.length;
            System.arraycopy(tempMelting, 0, meltingTemps, 0, meltingLength);

            if (activeTemps.length > 0 && activeTemps.length > tempActive.length)
            {
                for (int i = tempActive.length; i < activeTemps.length; i++)
                {
                    activeTemps[i] = 20;
                    meltingTemps[i] = 20;
                }
            }
        }
    }

    public void updateTemperatures ()
    {
        inUse = true;
        for (int i = 0; i < meltingTemps.length; i++)
        {
            meltingTemps[i] = Smeltery.instance.getLiquifyTemperature(master.getStackInSlot(i));
        }
    }

    public void heatItems ()
    {
        if (fuelTicks > 0)
        {
            boolean hasUse = false;
            for (int i = 0; i < meltingTemps.length; i++)
            {
                fuelTicks--;
                if (fuelTicks <= 0)
                    break;

                ItemStack slot = master.getStackInSlot(i);
                if (meltingTemps[i] > 20 && slot != null)
                {
                    hasUse = true;
                    if (activeTemps[i] < maxTemp && activeTemps[i] < meltingTemps[i])
                    {
                        activeTemps[i] += 1;
                    }
                    else if (activeTemps[i] >= meltingTemps[i] && !world.isRemote)
                    {
                        FluidStack result = getResultFor(slot);
                        if (result != null)
                        {
                            if (multitank.addFluidToTank(result, false))
                            {
                                master.setInventorySlotContents(i, null);
                                activeTemps[i] = 20;
                                ArrayList alloys = Smeltery.mixMetals(multitank.fluidlist);
                                for (int al = 0; al < alloys.size(); al++)
                                {
                                    FluidStack liquid = (FluidStack) alloys.get(al);
                                    multitank.addFluidToTank(liquid, true);
                                }
                                master.markDirty();
                                master.setUpdateFluids();
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

    void updateFuelGague ()
    {
        if (activeLavaTank == null || fuelTicks > 0 || structure.lavaTanks.size() < 1)
            return;

        if (!world.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        TileEntity tankContainer = world.getTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
        if (tankContainer == null)
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        if (tankContainer instanceof IFluidHandler && inUse)
        {
            FluidStack liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, drainFuelAmount(), false);
            if (liquid != null && liquid.getFluid().getBlock() == Blocks.lava)
            {
                liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, drainFuelAmount(), true);
                fuelTicks += liquid.amount * 15;

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
                    CoordTuple possibleTank = structure.lavaTanks.get(iter);
                    TileEntity newTankContainer = world.getTileEntity(possibleTank.x, possibleTank.y, possibleTank.z);
                    if (newTankContainer instanceof IFluidHandler)
                    {
                        FluidStack newliquid = ((IFluidHandler) newTankContainer).drain(ForgeDirection.UNKNOWN, drainFuelAmount(), false);
                        if (newliquid != null && newliquid.getFluid().getBlock() == Blocks.lava && newliquid.amount > 0)
                        {
                            foundTank = true;
                            setActiveLavaTank(possibleTank);
                            iter = structure.lavaTanks.size();

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
                    if (iter >= structure.lavaTanks.size())
                        foundTank = true;
                }
            }
        }
    }

    public void setActiveLavaTank (CoordTuple coord)
    {
        activeLavaTank = coord;
    }

    private int drainFuelAmount ()
    {
        if (activeTemps.length == 0)
            return 0;

        int amount = activeTemps.length / 3;
        if (amount < 150)
            amount = 150;
        return amount;
    }

    public FluidStack getResultFor (ItemStack stack)
    {
        return Smeltery.instance.getSmelteryResult(stack);
    }

    public int getInternalTemperature ()
    {
        return maxTemp;
    }

    public int getTempForSlot (int slot)
    {
        return activeTemps[slot];
    }

    public int getMeltingPointForSlot (int slot)
    {
        return meltingTemps[slot];
    }

    public int getScaledFuelGague (int scale)
    {
        int ret = (fuelGague * scale) / 52;
        if (ret < 1)
            ret = 1;
        return ret;
    }

    /* NBT */
    @Override
    public void readNetworkNBT (NBTTagCompound tags)
    {
        activeTemps = tags.getIntArray("Temperature");
        meltingTemps = tags.getIntArray("Melting");
        fuelAmount = tags.getInteger("Fuel");
        int[] tank = tags.getIntArray("LavaTank");
        if (tank.length > 0)
            activeLavaTank = new CoordTuple(tank[0], tank[1], tank[2]);
    }

    @Override
    public void writeNetworkNBT (NBTTagCompound tags)
    {
        tags.setIntArray("Temperature", activeTemps);
        tags.setIntArray("Melting", meltingTemps);
        tags.setInteger("Fuel", fuelAmount);
        if (activeLavaTank != null)
            tags.setIntArray("LavaTank", new int[] { activeLavaTank.x, activeLavaTank.y, activeLavaTank.z });
    }
}