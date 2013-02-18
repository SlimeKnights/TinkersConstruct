package tinker.tconstruct.logic;

import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidStack;

public class SmelteryDrainLogic extends MultiServantLogic 
	implements ILiquidTank
{
	boolean isDrain;
	
	public SmelteryDrainLogic()
	{
		isDrain = true;
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
		if (hasMaster && !isDrain) //Not sure if it should fill or not
		{
			return 0;
		}
		else
		{
			return 0;
		}
	}

	@Override
	public LiquidStack drain (int maxDrain, boolean doDrain)
	{
		if (hasMaster && isDrain)
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

}
