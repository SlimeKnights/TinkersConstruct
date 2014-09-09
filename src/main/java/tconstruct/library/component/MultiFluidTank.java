package tconstruct.library.component;

import java.util.*;
import net.minecraft.nbt.*;
import net.minecraftforge.fluids.*;
import tconstruct.library.crafting.Smeltery;

public class MultiFluidTank extends LogicComponent implements IFluidTank
{
    public ArrayList<FluidStack> fluidlist = new ArrayList<FluidStack>();
    protected int maxLiquid;
    protected int currentLiquid;

    public MultiFluidTank()
    {
    }

    public MultiFluidTank(int max)
    {
        maxLiquid = max;
    }

    public void setCapacity (int i)
    {
        maxLiquid = i;
    }

    @Override
    public int getCapacity ()
    {
        return maxLiquid;
    }

    @Override
    public int getFluidAmount ()
    {
        return currentLiquid;
    }

    @Override
    public FluidStack drain (int maxDrain, boolean doDrain)
    {
        if (fluidlist.size() == 0)
            return null;

        FluidStack liquid = fluidlist.get(0);
        if (liquid != null)
        {
            if (liquid.amount - maxDrain <= 0)
            {
                FluidStack liq = liquid.copy();
                if (doDrain)
                {
                    fluidlist.remove(liquid);
                    currentLiquid = 0;
                }
                return liq;
            }
            else
            {
                if (doDrain)
                {
                    liquid.amount -= maxDrain;
                    currentLiquid -= maxDrain;
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
        if (resource != null && currentLiquid < maxLiquid)
        {
            if (resource.amount + currentLiquid > maxLiquid)
                resource.amount = maxLiquid - currentLiquid;
            int amount = resource.amount;

            if (doFill)
            {
                if (addFluidToTank(resource, false))
                {
                    ArrayList alloys = Smeltery.mixMetals(fluidlist);
                    for (int al = 0; al < alloys.size(); al++)
                    {
                        FluidStack liquid = (FluidStack) alloys.get(al);
                        addFluidToTank(liquid, true);
                    }
                }
            }
            return amount;
        }
        else
            return 0;
    }

    public boolean addFluidToTank (FluidStack liquid, boolean first)
    {
        if (fluidlist.size() == 0)
        {
            fluidlist.add(liquid.copy());
            currentLiquid += liquid.amount;
            return true;
        }
        else
        {
            if (liquid.amount + currentLiquid > maxLiquid)
                return false;

            currentLiquid += liquid.amount;
            boolean added = false;
            for (int i = 0; i < fluidlist.size(); i++)
            {
                FluidStack l = fluidlist.get(i);
                if (l.isFluidEqual(liquid))
                {
                    l.amount += liquid.amount;
                    added = true;
                }
                if (l.amount <= 0)
                {
                    fluidlist.remove(l);
                    i--;
                }
            }
            if (!added)
            {
                if (first)
                    fluidlist.add(0, liquid.copy());
                else
                    fluidlist.add(liquid.copy());
            }
            return true;
        }
    }

    @Override
    public FluidStack getFluid ()
    {
        if (fluidlist.size() == 0)
            return null;
        return fluidlist.get(0);
    }

    public List<FluidStack> getAllFluids ()
    {
        return fluidlist;
    }

    @Override
    public FluidTankInfo getInfo ()
    {
        return new FluidTankInfo(this);
    }

    public FluidTankInfo[] getMultiTankInfo ()
    {
        FluidTankInfo[] info = new FluidTankInfo[fluidlist.size() + 1];
        for (int i = 0; i < fluidlist.size(); i++)
        {
            FluidStack fluid = fluidlist.get(i);
            info[i] = new FluidTankInfo(fluid.copy(), fluid.amount);
        }
        info[fluidlist.size()] = new FluidTankInfo(null, maxLiquid - currentLiquid);
        return info;
    }

    /* Sync liquids */

    @Override
    public void readNetworkNBT (NBTTagCompound tags)
    {
        NBTTagList liquidTag = tags.getTagList("Liquids", 10);
        fluidlist.clear();

        for (int iter = 0; iter < liquidTag.tagCount(); iter++)
        {
            NBTTagCompound nbt = (NBTTagCompound) liquidTag.getCompoundTagAt(iter);
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null)
                fluidlist.add(fluid);
        }
    }

    @Override
    public void writeNetworkNBT (NBTTagCompound tags)
    {
        NBTTagList taglist = new NBTTagList();
        for (FluidStack liquid : fluidlist)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            taglist.appendTag(nbt);
        }

        tags.setTag("Liquids", taglist);
    }
}