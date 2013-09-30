package tconstruct.library.component;

import java.util.ArrayList;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.library.crafting.Smeltery;

public class SmelteryComponent extends LogicComponent
{
    World world;
    IInventory master;
    MultiFluidTank multitank;
    
    int internalTemp;
    public int useTime;
    public int fuelGague;
    public int fuelAmount;
    boolean inUse;
    
    public int[] activeTemps;
    public int[] meltingTemps;

    public SmelteryComponent(IInventory inventory, World world, MultiFluidTank multitank, int maxTemp)
    {
        master = inventory;
        this.world = world;
        this.multitank = multitank;
        internalTemp = maxTemp;
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
    
    void heatItems ()
    {
        if (useTime > 0)
        {
            boolean hasUse = false;
            for (int i = 0; i < meltingTemps.length; i++)
            {
                ItemStack slot = master.getStackInSlot(i);
                if (meltingTemps[i] > 20 && slot != null)
                {
                    hasUse = true;
                    if (activeTemps[i] < internalTemp && activeTemps[i] < meltingTemps[i])
                    {
                        activeTemps[i] += 1;
                    }
                    else if (activeTemps[i] >= meltingTemps[i])
                    {
                        if (!world.isRemote)
                        {
                            FluidStack result = getResultFor(slot);
                            if (result != null)
                            {
                                if (multitank.addFluidToTank(result, false))
                                {
                                    master.setInventorySlotContents(i, null);
                                    activeTemps[i] = 20;
                                    ArrayList alloys = Smeltery.mixMetals(multitank.moltenMetal);
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

                }

                else
                    activeTemps[i] = 20;
            }
            inUse = hasUse;
        }
    }
    
    public FluidStack getResultFor (ItemStack stack)
    {
        return Smeltery.instance.getSmelteryResult(stack);
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
}
