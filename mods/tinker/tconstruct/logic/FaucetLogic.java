package mods.tinker.tconstruct.logic;

import mods.tinker.common.IActiveLogic;
import mods.tinker.common.IFacingLogic;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;

public class FaucetLogic extends TileEntity implements IFacingLogic, IActiveLogic
{
	byte direction;
	boolean active;
	public LiquidStack liquid;
	ITankContainer drain;
	ITankContainer tank;
	int drainAmount = 5;

	public void activateFaucet ()
	{
		if (liquid == null)
		{
			int x = xCoord, z = zCoord;
			switch (getRenderDirection())
			{
			case 2:
				z++;
				break;
			case 3:
				z--;
				break;
			case 4:
				x++;
				break;
			case 5:
				x--;
				break;
			}

			TileEntity drainte = worldObj.getBlockTileEntity(x, yCoord, z);
			TileEntity tankte = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
			if (drainte != null && drainte instanceof ITankContainer && tankte != null && tankte instanceof ITankContainer)
			{
				//System.out.println("Activating Faucet");
				liquid = ((ITankContainer) drainte).drain(getForgeDirection(), drainAmount, true);
				drain = (ITankContainer) drainte;
				tank = (ITankContainer) tankte;
				active = true;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				//System.out.println("Drained");
				//return true;
			}
		}
		//return true;
	}

	public void deactivateFaucet ()
	{
		//System.out.println("Deactivating Faucet");
		drain.fill(getForgeDirection(), liquid, true);
		liquid = null;
		drain = null;
		tank = null;
		active = false;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void updateEntity ()
	{
		if (active && liquid != null)
		{
			int drained = tank.fill(ForgeDirection.UP, liquid, true);
			//System.out.println("Drained: "+drained);
			if (drained < drainAmount)
			{
				liquid.amount -= drained;
				deactivateFaucet();
			}
			else
			{
				liquid = drain.drain(getForgeDirection(), drainAmount, true);
			}
			/*TileEntity te = worldObj.getBlockTileEntity(xCoord, yCoord-1, zCoord);
			if (te != null && te instanceof ILiquidTank)
			{
				int amount = ((ILiquidTank) te).fill(new LiquidStack(liquid.itemID, 2, liquid.itemMeta), true);
				liquid.amount -= amount;
				if (liquid.amount <= 0)
				{
					liquid = null;
				}
			}*/
		}
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
	public void setDirection (float yaw, float pitch, EntityLiving player)
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

	public void readFromNBT (NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		direction = tags.getByte("Direction");
		if (tags.getBoolean("hasLiquid"))
			this.liquid = new LiquidStack(tags.getInteger("itemID"), tags.getInteger("amount"), tags.getInteger("itemMeta"));
		else
			this.liquid = null;
	}

	@Override
	public void writeToNBT (NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		tags.setByte("Direction", direction);
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
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
	{
		readFromNBT(packet.customParam1);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public boolean getActive ()
	{
		return active;
	}

	@Override
	public void setActive (boolean flag)
	{
		//if (!worldObj.isRemote)
		//{
			if (flag)
				activateFaucet();
			else
				deactivateFaucet();
		//}
	}

}
