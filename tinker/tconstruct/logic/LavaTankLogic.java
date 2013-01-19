package tinker.tconstruct.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

public class LavaTankLogic extends TileEntity
	implements ITankContainer
{
	public LiquidTank tank;
	public int amount;
	public int max;
	public LiquidStack liquid;
	
	public LavaTankLogic()
	{
		tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME);
		max = 1;
		amount = 0;
	}

	@Override
	public int fill (ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		return fill(0, resource, doFill);
	}

	@Override
	public int fill (int tankIndex, LiquidStack resource, boolean doFill)
	{
		if (resource == null)
			return 0;
		
		
		if (liquid == null)
		{
			liquid = resource;
			amount = resource.amount;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			return amount;
		}
		else if (resource.amount + amount > max*1000)
		{
			if (doFill)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				amount = 4000;
			}
			return resource.amount + amount - 4000;
		}
		else
		{
			if (doFill)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				amount += resource.amount;
			}
			return amount;
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
		if (amount - maxDrain < 0)
		{
			LiquidStack liq =  new LiquidStack(liquid.itemID, 0, liquid.itemMeta);
			if (doDrain)
			{
				amount = 0;
				liquid = null;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return liq;
		}
		else
		{
			if (doDrain)
			{
				amount -= maxDrain;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			return new LiquidStack(liquid.itemID, amount - maxDrain, liquid.itemMeta);
		}
	}

	@Override
	public ILiquidTank[] getTanks (ForgeDirection direction)
	{
		return new ILiquidTank[] { (ILiquidTank) this };
	}

	@Override
	public ILiquidTank getTank (ForgeDirection direction, LiquidStack type)
	{
		return (ILiquidTank) this;
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
		this.amount = par1NBTTagCompound.getInteger("amount");
		this.liquid = new LiquidStack(par1NBTTagCompound.getInteger("itemID"), amount, par1NBTTagCompound.getInteger("itemMeta"));
		super.readFromNBT(par1NBTTagCompound);
    }
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
		if (liquid != null)
		{
			par1NBTTagCompound.setInteger("itemID", liquid.itemID);
			par1NBTTagCompound.setInteger("amount", this.amount);
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
