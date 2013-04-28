package mods.tinker.tconstruct.library.util;

public interface IServantLogic
{
	public CoordTuple getMasterPosition ();
	public void notifyMasterOfChange();
	public boolean verifyMaster(int x, int y, int z);
	public boolean setMaster(int x, int y, int z);
}
