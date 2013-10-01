package tconstruct.library.util;

public interface IServantLogic
{
    public CoordTuple getMasterPosition ();

    /** The block should already have a valid master */
    public void notifyMasterOfChange ();
    
    /** Checks if this block can be tied to this master
     * 
     * @param master
     * @param x xCoord of master
     * @param y yCoord of master
     * @param z zCoord of master
     * @return whether the servant can be tied to this master
     */
    
    public boolean setPotentialMaster(IMasterLogic master, int x, int y, int z);
    
    /** Used to set and verify that this is the block's master
     * 
     * @param master
     * @param x xCoord of master
     * @param y yCoord of master
     * @param z zCoord of master
     * @return Is this block tied to this master?
     */

    public boolean verifyMaster (IMasterLogic master, int x, int y, int z);

    /* Deprecated in favor of setPotentialMaster and verifyMaster */
    @Deprecated
    public boolean setMaster (int x, int y, int z);
}
