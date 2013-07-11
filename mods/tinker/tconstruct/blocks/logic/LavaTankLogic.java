package mods.tinker.tconstruct.blocks.logic;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class LavaTankLogic extends MultiServantLogic implements ITankContainer
{
    public LiquidTank tank;
    public int renderOffset;

    //public LiquidStack renderLiquid;
    //public int counter;
    //public int updateAmount;

    public LavaTankLogic()
    {
        tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 4);
    }

    @Override
    public int fill (ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        return fill(0, resource, doFill);
    }

    @Override
    public int fill (int tankIndex, LiquidStack resource, boolean doFill)
    {
        /*if (resource != null && resource.amount > 20 && counter == 0)
        {
        	if (tank.getLiquid() == null)
        	{
        		renderLiquid = new LiquidStack(resource.itemID, 0, resource.itemMeta);
        	}
        	else
        	{
        		renderLiquid = tank.getLiquid();
        	}
        	counter = 24;
        	updateAmount = resource.amount / 24;
        	System.out.println("renderLiquid: "+renderLiquid.amount);			
        }*/
        //renderLiquid = tank.getLiquid();
        int amount = tank.fill(resource, doFill);
        if (amount > 0 && doFill)
        {
            renderOffset = resource.amount;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        //System.out.println("tankLiquid: "+tank.getLiquid().amount);	
        return amount;
    }

    @Override
    public LiquidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return drain(0, maxDrain, doDrain);
    }

    @Override
    public LiquidStack drain (int tankIndex, int maxDrain, boolean doDrain)
    {
        /*if (maxDrain > 20 && counter == 0)
        {
        	renderLiquid = tank.getLiquid();
        	counter = 24;
        	updateAmount = -(maxDrain / 24);
        }*/

        LiquidStack amount = tank.drain(maxDrain, doDrain);
        if (amount != null && doDrain)
        {
            renderOffset = -maxDrain;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        return amount;
    }

    @Override
    public ILiquidTank[] getTanks (ForgeDirection direction)
    {
        return new ILiquidTank[] { tank };
    }

    @Override
    public ILiquidTank getTank (ForgeDirection direction, LiquidStack type)
    {
        return tank;
    }

    public float getLiquidAmountScaled ()
    {
        return (float) (tank.getLiquid().amount - renderOffset) / (float) (tank.getCapacity() * 1.01F);
    }

    public boolean containsLiquid ()
    {
        return tank.getLiquid() != null;
    }

    public int getBrightness ()
    {
        if (containsLiquid())
        {
            int id = tank.getLiquid().itemID;
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
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        if (tags.getBoolean("hasLiquid"))
            tank.setLiquid(new LiquidStack(tags.getInteger("itemID"), tags.getInteger("amount"), tags.getInteger("itemMeta")));
        else
            tank.setLiquid(null);
        //renderLiquid = tank.getLiquid();
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        LiquidStack liquid = tank.getLiquid();
        tags.setBoolean("hasLiquid", liquid != null);
        if (liquid != null)
        {
            tags.setInteger("itemID", liquid.itemID);
            tags.setInteger("amount", liquid.amount);
            tags.setInteger("itemMeta", liquid.itemMeta);
        }
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.customParam1);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    /* Updating */
    public boolean canUpdate ()
    {
        return true;
    }

    @Override
    public void updateEntity ()
    {
        if (renderOffset > 0)
        {
            renderOffset -= 6;
            worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        }
    }
}
