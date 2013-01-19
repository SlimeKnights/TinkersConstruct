package tinker.tconstruct.logic;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import tinker.common.IActiveLogic;
import tinker.common.IFacingLogic;
import tinker.common.InventoryLogic;

/* Simple class for storing items in the block
 */

public class SmelteryLogic extends InventoryLogic
	implements IActiveLogic, IFacingLogic
{
	int temperature;
	int useTime;
	byte direction;
	
	public SmelteryLogic()
	{
		super(5);
	}

	@Override
	public String getInvName ()
	{
		return "crafters.Smeltery";
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public byte getDirection() 
	{
		return direction;
	}

	@Override
	public void setDirection(byte dir) 
	{
		direction = dir;
	}

	@Override
	public boolean getActive() 
	{
		return false;
	}

	@Override
	public void setActive(boolean flag) 
	{
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);		
	}

	@Override
	public boolean canFaceVertical() 
	{
		return false;
	}
	
	public void scanWorld()
	{
		switch(getDirection())
		{
		case 2: // +z
			//worldObj.setBlockWithNotify(xCoord, yCoord, zCoord+1, Block.dirt.blockID);
			placeBlockRing(xCoord, yCoord, zCoord+2);
			break;
		case 3: // -z
			//worldObj.setBlockWithNotify(xCoord, yCoord, zCoord-1, Block.dirt.blockID);
			placeBlockRing(xCoord, yCoord, zCoord-2);
			break;
		case 4: // +x
			//worldObj.setBlockWithNotify(xCoord+1, yCoord, zCoord, Block.dirt.blockID);
			placeBlockRing(xCoord+2, yCoord, zCoord);
			break;
		case 5: // -x
			//worldObj.setBlockWithNotify(xCoord-1, yCoord, zCoord, Block.dirt.blockID);
			placeBlockRing(xCoord-2, yCoord, zCoord);
			break;
		}
	}
	
	public void placeBlockRing(int x, int y, int z)
	{
		for (int xPos = x-1; xPos <= x+1; xPos++)
		{
			worldObj.setBlockWithNotify(xPos, y, z-2, Block.dirt.blockID);
			worldObj.setBlockWithNotify(xPos, y, z+2, Block.dirt.blockID);
		}
		for (int zPos = z-1; zPos <= z+1; zPos++)
		{
			worldObj.setBlockWithNotify(x-2, y, zPos, Block.dirt.blockID);
			worldObj.setBlockWithNotify(x+2, y, zPos, Block.dirt.blockID);
		}
	}
}
