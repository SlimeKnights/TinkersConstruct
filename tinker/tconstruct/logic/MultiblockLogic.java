package tinker.tconstruct.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import tinker.common.CoordTuple;

public class MultiblockLogic extends TileEntity
{
	boolean hasMaster;
	CoordTuple master;

	public boolean hasValidMaster ()
	{
		return hasMaster;
	}

	public CoordTuple getMaster ()
	{
		return master;
	}
	
	public void setMaster(int x, int y, int z)
	{
		master = new CoordTuple(x, y, z);
	}

	public void readFromNBT (NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		hasMaster = tags.getBoolean("HasMaster");
		if (hasMaster)
		{
			int xCenter = tags.getInteger("xCenter");
			int yCenter = tags.getInteger("yCenter");
			int zCenter = tags.getInteger("zCenter");
			master = new CoordTuple(xCenter, yCenter, zCenter);
		}
	}

	public void writeToNBT (NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		tags.setBoolean("HasMaster", hasMaster);
		if (hasMaster)
		{
			tags.setInteger("xCenter", master.x);
			tags.setInteger("yCenter", master.y);
			tags.setInteger("zCenter", master.z);
		}
	}
}
