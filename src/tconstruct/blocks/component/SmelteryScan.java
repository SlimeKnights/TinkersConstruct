package tconstruct.blocks.component;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import tconstruct.common.TContent;
import tconstruct.library.component.TankLayerScan;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IServantLogic;

public class SmelteryScan extends TankLayerScan
{
    public ArrayList<CoordTuple> lavaTanks = new ArrayList<CoordTuple>();

    public SmelteryScan(TileEntity te, Block... id)
    {
        super(te, id);
    }

    @Override
    public void checkValidStructure ()
    {
        lavaTanks.clear();
        super.checkValidStructure();
    }
    
    @Override
    protected boolean checkServant (int x, int y, int z)
    {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block == null || block.isAirBlock(world, x, y, z) || !isValidBlock(x, y, z))
            return false;

        if (!block.hasTileEntity(world.getBlockMetadata(x, y, z)))
            return false;

        TileEntity be = world.getBlockTileEntity(x, y, z);
        if (be instanceof IServantLogic)
        {
            boolean ret = ((IServantLogic) be).setPotentialMaster(this.imaster, this.world, x, y, z);
            if (ret && block == TContent.lavaTank)
                lavaTanks.add(new CoordTuple(x, y, z));
            return ret;
        }

        return false;
    }
    
    protected void addAirBlock (int x, int y, int z)
    {
        super.addAirBlock(x, y, z);
        world.setBlock(x, y, z, Block.leaves.blockID);
    }
    
    public void cleanup()
    {
        System.out.println("Structure cleanup activated. Air blocks: "+airCoords.size());
        super.cleanup();
        Iterator i = airCoords.iterator();
        while (i.hasNext())
        {
            CoordTuple coord = (CoordTuple) i.next();
            world.setBlockToAir(coord.x, coord.y, coord.z);
        }
    }
}
