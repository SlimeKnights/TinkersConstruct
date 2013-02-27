package tinker.tconstruct.logic;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;
import tinker.common.IFacingLogic;

public class SmelteryDrainLogic extends MultiServantLogic 
	implements ILiquidTank, IFacingLogic
{
	byte direction;

	public boolean canUpdate ()
	{
		return false;
	}

	@Override
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
	}

	@Override
	public int fill (LiquidStack resource, boolean doFill)
	{
		if (hasMaster) //Not sure if it should fill or not
		{
			if (doFill)
			{
			SmelteryLogic smeltery = (SmelteryLogic) worldObj.getBlockTileEntity(master.x, master.y, master.z);
			return smeltery.fill(resource);
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
	public LiquidStack drain (int maxDrain, boolean doDrain)
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
	public int getTankPressure ()
	{
		return 0;
	}

	@Override
	public byte getRenderDirection ()
	{
		return direction;
	}
	
	@Override
	public ForgeDirection getForgeDirection()
	{
		return ForgeDirection.VALID_DIRECTIONS[direction];
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
