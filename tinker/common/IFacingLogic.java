package tinker.common;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;

public interface IFacingLogic
{
	public boolean canFaceVertical();
	public byte getDirection();
	public void setDirection(byte direction);
}