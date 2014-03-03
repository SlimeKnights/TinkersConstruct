package tconstruct.library.util;

public interface IMasterLogic
{
    /** Called when servants change their state
     * 
     * @param x Servant X
     * @param y Servant Y
     * @param z Servant Z
     */
    public void notifyChange (IServantLogic servant, int x, int y, int z);
}
