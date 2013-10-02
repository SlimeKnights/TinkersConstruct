package tconstruct.blocks.component;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import tconstruct.library.component.LogicComponent;
import tconstruct.library.component.MultiFluidTank;
import tconstruct.library.crafting.Smeltery;
import tconstruct.library.util.CoordTuple;

public class SmelteryComponent extends LogicComponent
{
    final IInventory master;
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

    public SmelteryComponent(IInventory inventory, SmelteryScan structure, MultiFluidTank multitank, int maxTemp)
    {
        master = inventory;
        this.structure = structure;
        this.multitank = multitank;
        this.maxTemp = maxTemp;
    }

    public void update ()
    {
        /*if (useTime > 0 && inUse)
            useTime -= 3;*/

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
                                master.onInventoryChanged();
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
        if (activeLavaTank == null || fuelTicks > 0)
            return;

        if (!world.blockExists(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z))
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        TileEntity tankContainer = world.getBlockTileEntity(activeLavaTank.x, activeLavaTank.y, activeLavaTank.z);
        if (tankContainer == null)
        {
            fuelAmount = 0;
            fuelGague = 0;
            return;
        }

        if (tankContainer instanceof IFluidHandler)
        {
            FluidStack liquid = ((IFluidHandler) tankContainer).drain(ForgeDirection.DOWN, drainFuelAmount(), false);
            if (liquid != null && liquid.getFluid().getBlockID() == Block.lavaStill.blockID)
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
                    TileEntity newTankContainer = world.getBlockTileEntity(possibleTank.x, possibleTank.y, possibleTank.z);
                    if (newTankContainer instanceof IFluidHandler)
                    {
                        FluidStack newliquid = ((IFluidHandler) newTankContainer).drain(ForgeDirection.UNKNOWN, drainFuelAmount(), false);
                        if (newliquid != null && newliquid.getFluid().getBlockID() == Block.lavaStill.blockID && newliquid.amount > 0)
                        {
                            foundTank = true;
                            activeLavaTank = possibleTank;
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

    private int drainFuelAmount ()
    {
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
}
