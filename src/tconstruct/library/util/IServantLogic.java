package tconstruct.library.util;

import net.minecraft.world.World;

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
    
    public boolean setPotentialMaster(IMasterLogic master, World world, int xMaster, int yMaster, int zMaster);
    
    /** Used to set and verify that this is the block's master
     * 
     * @param master
     * @param x xCoord of master
     * @param y yCoord of master
     * @param z zCoord of master
     * @return Is this block tied to this master?
     */

    public boolean verifyMaster (IMasterLogic master, World world, int xMaster, int yMaster, int zMaster);
    
    /** Exactly what it says on the tin
     * 
     * @param master
     * @param x xCoord of master
     * @param y yCoord of master
     * @param z zCoord of master
     */
    
    public void invalidateMaster(IMasterLogic master, World world, int xMaster, int yMaster, int zMaster);
}
