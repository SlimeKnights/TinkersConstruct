package tconstruct.blocks.logic;

import mantle.blocks.abstracts.MultiServantLogic;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class LavaTankLogic extends MultiServantLogic implements IFluidHandler
{
    public FluidTank tank;
    public int renderOffset;

    public LavaTankLogic()
    {
        tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 4);
    }

    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        int amount = tank.fill(resource, doFill);
        if (amount > 0 && doFill)
        {
            renderOffset = resource.amount;
            field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
        }

        return amount;
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        FluidStack amount = tank.drain(maxDrain, doDrain);
        if (amount != null && doDrain)
        {
            renderOffset = -maxDrain;
            field_145850_b.markBlockForUpdate(field_145851_c, field_145848_d, field_145849_e);
        }
        return amount;
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        //return tank.fill(fluid, false) > 0;
        return false;
    }

    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        FluidStack fluid = null;
        if (tank.getFluid() != null)
            fluid = tank.getFluid().copy();
        return new FluidTankInfo[] { new FluidTankInfo(fluid, tank.getCapacity()) };
    }

    /*@Override
    public IFluidTank[] getTanks (ForgeDirection direction)
    {
        return new IFluidTank[] { tank };
    }

    @Override
    public IFluidTank getTank (ForgeDirection direction, FluidStack type)
    {
        return tank;
    }*/

    public float getFluidAmountScaled ()
    {
        return (float) (tank.getFluid().amount - renderOffset) / (float) (tank.getCapacity() * 1.01F);
    }

    public boolean containsFluid ()
    {
        return tank.getFluid() != null;
    }

    public int getBrightness ()
    {
        if (containsFluid())
        {
            int id = tank.getFluid().fluidID;
            if (id < 4096)
            {
                return Block.lightValue[id];
            }
        }
        return 0;
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    @Override
    public void func_145839_a (NBTTagCompound tags)
    {
        super.func_145839_a(tags);
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        if (tags.getBoolean("hasFluid"))
            tank.setFluid(new FluidStack(tags.getInteger("itemID"), tags.getInteger("amount")));
        else
            tank.setFluid(null);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        FluidStack liquid = tank.getFluid();
        tags.setBoolean("hasFluid", liquid != null);
        if (liquid != null)
        {
            tags.setInteger("itemID", liquid.fluidID);
            tags.setInteger("amount", liquid.amount);
        }
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        field_145850_b.markBlockForRenderUpdate(field_145851_c, field_145848_d, field_145849_e);
    }

    /* Updating */
    public boolean canUpdate ()
    {
        return true;
    }

    @Override
    public void func_145845_h ()
    {
        if (renderOffset > 0)
        {
            renderOffset -= 6;
            field_145850_b.markBlockForRenderUpdate(field_145851_c, field_145848_d, field_145849_e);
        }
    }
}
