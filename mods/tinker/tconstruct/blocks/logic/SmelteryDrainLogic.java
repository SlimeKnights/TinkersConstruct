package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.library.util.IFacingLogic;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;

public class SmelteryDrainLogic extends MultiServantLogic implements ITankContainer, IFacingLogic
{
    byte direction;

    public boolean canUpdate ()
    {
        return false;
    }

    /*@Override
    public LiquidStack getLiquid ()
    {
    	return null;
    }

    @Override
    public int getCapacity ()
    {
    	if (!hasMaster)
    		return 0;

    	SmelteryLogic smeltery = (SmelteryLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
    	return smeltery.getCapacity();
    }*/

    /*@Override
    public int fill (LiquidStack resource, boolean doFill)
    {
    	
    }*/

    /*@Override
    public LiquidStack drain (int maxDrain, boolean doDrain)
    {
    	
    }*/

    @Override
    public int fill (ForgeDirection from, LiquidStack resource, boolean doFill)
    {
        //if (from == ForgeDirection.OPPOSITES[getRenderDirection()])
        return fill(0, resource, doFill);
        //return 0;
    }

    @Override
    public int fill (int tankIndex, LiquidStack resource, boolean doFill)
    {
        if (hasMaster && resource != null)
        {
            if (doFill)
            {
                SmelteryLogic smeltery = (SmelteryLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
                return smeltery.fill(resource, doFill);
            }
            else
            {
                return resource.amount;
            }
        }
        else
        {
            return 0;
        }
    }

    @Override
    public LiquidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return drain(0, maxDrain, doDrain);
    }

    @Override
    public LiquidStack drain (int tankIndex, int maxDrain, boolean doDrain)
    {
        if (hasValidMaster())
        {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
            return smeltery.drain(maxDrain, doDrain);
        }
        else
        {
            return null;
        }
    }

    @Override
    public ILiquidTank[] getTanks (ForgeDirection direction)
    {
        if (hasValidMaster())
        {
            SmelteryLogic smeltery = (SmelteryLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
            return new ILiquidTank[] { smeltery };
        }
        return null;
    }

    @Override
    public ILiquidTank getTank (ForgeDirection direction, LiquidStack type)
    {
        if (hasValidMaster())
        {
            return (SmelteryLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
        }
        return null;
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
    public void setDirection (float yaw, float pitch, EntityLiving player)
    {
        if (pitch > 45)
            direction = 1;
        else if (pitch < -45)
            direction = 0;
        else
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
    }

    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        direction = tags.getByte("Direction");
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setByte("Direction", direction);
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
        readFromNBT(packet.customParam1);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }
}
