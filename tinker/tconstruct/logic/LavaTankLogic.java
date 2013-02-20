package tinker.tconstruct.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class LavaTankLogic extends TileEntity
	implements ILiquidTank
{
	public LiquidTank tank;
	public int max;
	public int pressure;
	public LiquidStack liquid;
	
	public LavaTankLogic()
	{
		tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
		max = LiquidContainerRegistry.BUCKET_VOLUME*4;
		pressure = 0;
	}
	
	public boolean canUpdate()
    {
        return false;
    }
	
	@Override
	public LiquidStack getLiquid ()
	{
		return liquid;
	}
	
	public int getLiquidAmount ()
	{
		if (liquid == null)
			return 0;
		else
			return liquid.amount;
	}

	@Override
	public int getCapacity ()
	{
		return max;
	}

	@Override
	public int fill (LiquidStack resource, boolean doFill)
	{
		if (resource == null)
			return 0;		
		
		if (liquid == null)
		{
			liquid = resource.copy();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return liquid.amount;
		}
		else if (resource.amount + liquid.amount > max)
		{
			if (doFill)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				liquid.amount = max;
			}
			return max - resource.amount;
		}
		else
		{
			if (doFill)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				liquid.amount += resource.amount;
			}
			return resource.amount;
		}
	}

	@Override
	public LiquidStack drain (int maxDrain, boolean doDrain)
	{
		if (liquid == null)
			return null;
		if (liquid.amount - maxDrain < 0)
		{
			LiquidStack liq = liquid.copy();
			if (doDrain)
			{
				liquid = null;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return liq;
		}
		else
		{
			if (doDrain)
			{
				liquid.amount -= maxDrain;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return new LiquidStack(liquid.itemID, maxDrain, liquid.itemMeta);
		}
	}

	@Override
	public int getTankPressure ()
	{
		return pressure;
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
		this.liquid = new LiquidStack(par1NBTTagCompound.getInteger("itemID"), par1NBTTagCompound.getInteger("amount"), par1NBTTagCompound.getInteger("itemMeta"));
		super.readFromNBT(par1NBTTagCompound);
    }
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
		if (liquid != null)
		{
			par1NBTTagCompound.setInteger("itemID", liquid.itemID);
			par1NBTTagCompound.setInteger("amount", liquid.amount);
			par1NBTTagCompound.setInteger("itemMeta", liquid.itemMeta);
		}
		else
		{
			par1NBTTagCompound.setInteger("itemID", 0);
			par1NBTTagCompound.setInteger("amount", 0);
			par1NBTTagCompound.setInteger("itemMeta", 0);
		}
		super.writeToNBT(par1NBTTagCompound);
    }
	
	public Packet getDescriptionPacket ()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}

	public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
	{
		worldObj.getBlockTileEntity(xCoord, yCoord, zCoord).readFromNBT(packet.customParam1);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}
