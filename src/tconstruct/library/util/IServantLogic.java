package tconstruct.library.util;

public interface IServantLogic
{
    public CoordTuple getMasterPosition ();

    public void notifyMasterOfChange ();
    
    /** Used to check whether a given master can own this servant
     */
    
    public boolean canBeMaster(IMasterLogic master, int x, int y, int z);
    
    /** Checks whether the given master and coords are valid
     */

    public boolean verifyMaster (IMasterLogic master, int x, int y, int z);

    public boolean setMaster (int x, int y, int z);
}
