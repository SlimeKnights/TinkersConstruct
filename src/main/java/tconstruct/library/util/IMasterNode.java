package tconstruct.library.util;

public interface IMasterNode extends IMasterLogic, IServantLogic
{
    public boolean isCurrentlyMaster ();

    public boolean isEquivalentMaster (IMasterLogic master);
}
