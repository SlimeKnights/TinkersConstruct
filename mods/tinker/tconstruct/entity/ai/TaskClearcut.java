package mods.tinker.tconstruct.entity.ai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

import mods.tinker.tconstruct.entity.GolemBase;
import mods.tinker.tconstruct.library.util.CoordTuple;

public class TaskClearcut extends TaskBase
{
	boolean moving;
	boolean searching;
	List<CoordTuple> blocks = new ArrayList<CoordTuple>();
	public TaskClearcut(GolemBase golem)
	{
		super(golem);
	}

	public boolean update ()
	{
		if (searching)
		{
			searchForBlocks();
		}
		return true;
	}
	
	void searchForBlocks()
	{
		for (int x = -7; x <= 7; x++)
		{
			for (int z = -7; z <= 7; z++)
			{
				for (int y = -1; y <= 1; y++)
				{
					Block block = Block.blocksList[owner.worldObj.getBlockId((int)owner.posX+x, (int)owner.posY+y, (int)owner.posZ+z)];
					if (block != null && block.isWood(owner.worldObj, (int)owner.posX+x, (int)owner.posY+y, (int)owner.posZ+z))
					{
						blocks.add(new CoordTuple((int)owner.posX+x, (int)owner.posY+y, (int)owner.posZ+z));
					}
				}
			}
		}
	}
	
	public void finishTask()
	{
		moving = false;
	}
}
