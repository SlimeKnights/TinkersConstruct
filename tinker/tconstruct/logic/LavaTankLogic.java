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

public class LavaTankLogic extends MultiServantLogic
	implements ILiquidTank
{
	public int maxLiquid;
	public int pressure;
	public LiquidStack liquid;
	
	public LavaTankLogic()
	{
		maxLiquid = LiquidContainerRegistry.BUCKET_VOLUME*4;
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
		return maxLiquid;
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
		else if (resource.itemID != liquid.itemID || resource.itemMeta != liquid.itemMeta)
		{
			return 0;
		}
		else if (resource.amount + liquid.amount >= getCapacity())
		{
			int total = getCapacity();
			int cap = total - liquid.amount;
			if (doFill && cap != total)
			{
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				liquid.amount = getCapacity();
			}
			return cap;
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
	
	public void readFromNBT(NBTTagCompound tags)
    {
		if (tags.getBoolean("hasLiquid"))
			this.liquid = new LiquidStack(tags.getInteger("itemID"), tags.getInteger("amount"), tags.getInteger("itemMeta"));
		super.readFromNBT(tags);
    }
	
	public void writeToNBT(NBTTagCompound tags)
    {
		tags.setBoolean("hasLiquid", liquid != null);
		if (liquid != null)
		{
			tags.setInteger("itemID", liquid.itemID);
			tags.setInteger("amount", liquid.amount);
			tags.setInteger("itemMeta", liquid.itemMeta);
		}
		super.writeToNBT(tags);
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
