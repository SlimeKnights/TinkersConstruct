package tconstruct.library.multiblock;

import net.minecraft.world.World;
import tconstruct.library.util.CoordTuple;

public interface IMultiblockMember
{

    public boolean isCompatible (Object other);

    public boolean isConnected ();

    public MultiblockMasterBaseLogic getMultiblockMaster ();

    public CoordTuple getCoordInWorld ();

    public void onAttached (MultiblockMasterBaseLogic newMaster);

    public void onDetached (MultiblockMasterBaseLogic oldMaster);

    public void createNewMultiblock ();

    public MultiblockMasterBaseLogic getNewMultiblockMasterObject ();

    public void onMasterMerged (MultiblockMasterBaseLogic newMaster);

    public void setVisited ();

    public void setUnivisted ();

    public boolean isVisited ();

    public void becomeMultiblockSaveDelegate ();

    public void forfeitMultiblockSaveDelegate ();

    public boolean isMultiblockSaveDelegate ();

    public IMultiblockMember[] getNeighboringMembers ();

    public void onBlockAdded (World world, int x, int y, int z);

    public void onChunkLoad ();

    public void onOrphaned ();

    public void onChunkUnloaded ();

    public World getWorldObj ();

    public boolean willConnect (CoordTuple coord);
}
