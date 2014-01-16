package tconstruct.blocks.component;

import java.util.ArrayList;
import java.util.Iterator;

import mantle.blocks.iface.IServantLogic;
import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import tconstruct.common.TContent;
import tconstruct.common.TRepo;
import tconstruct.library.component.TankLayerScan;

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

    protected boolean checkAir (int x, int y, int z)
    {
        Block block = world.func_147439_a(x, y, z);
        if (block == null || block == Blocks.air || block == TRepo.tankAir)
            return true;

        return false;
    }

    @Override
    protected boolean checkServant (int x, int y, int z)
    {
        Block block = world.func_147439_a(x, y, z);
        if (block == null || world.func_147439_a( x, y, z)  == Blocks.air || !isValidBlock(x, y, z))
            return false;

        if (!block.hasTileEntity(world.getBlockMetadata(x, y, z)))
            return false;

        TileEntity be = world.func_147438_o(x, y, z);
        if (be instanceof IServantLogic)
        {
            boolean ret = ((IServantLogic) be).setPotentialMaster(this.imaster, this.world, x, y, z);
            if (ret && block == TRepo.lavaTank)
                lavaTanks.add(new CoordTuple(x, y, z));
            return ret;
        }

        return false;
    }

    @Override
    protected void finalizeStructure ()
    {
        super.finalizeStructure();
        if (lavaTanks.size() < 1)
            completeStructure = false;
        else
        {
            for (CoordTuple coord : airCoords)
            {
                if (world.func_147439_a(coord.x, coord.y, coord.z) != TRepo.tankAir)
                {
                    world.func_147449_b(coord.x, coord.y, coord.z, TRepo.tankAir);
                    IServantLogic servant = (IServantLogic) world.func_147438_o(coord.x, coord.y, coord.z);
                    servant.verifyMaster(imaster, world, master.field_145851_c, master.field_145848_d, master.field_145849_e);
                }
            }
        }
    }

    @Override
    protected void invalidateStructure ()
    {
        super.invalidateStructure();
        for (CoordTuple coord : airCoords)
        {
            TileEntity servant = world.func_147438_o(coord.x, coord.y, coord.z);
            if (servant instanceof IServantLogic)
                ((IServantLogic) servant).invalidateMaster(imaster, world, master.field_145851_c, master.field_145848_d, master.field_145849_e);
        }
    }

    @Override
    protected void invalidateBlocksAbove (int height)
    {
        for (CoordTuple coord : airCoords)
        {
            if (coord.y < height)
                continue;

            TileEntity servant = world.func_147438_o(coord.x, coord.y, coord.z);
            if (servant instanceof IServantLogic)
                ((IServantLogic) servant).invalidateMaster(imaster, world, master.field_145851_c, master.field_145848_d, master.field_145849_e);
        }
    }

    @Override
    public void cleanup ()
    {
        super.cleanup();
        Iterator i = airCoords.iterator();
        while (i.hasNext())
        {
            CoordTuple coord = (CoordTuple) i.next();
            TileEntity te = world.func_147438_o(coord.x, coord.y, coord.z);
            if (te != null && te instanceof IServantLogic)
            {
                ((IServantLogic) te).invalidateMaster(imaster, world, master.field_145851_c, master.field_145848_d, master.field_145849_e);
            }
        }
    }

    //Sync lava tanks for fuel values
    @Override
    public void readNetworkNBT (NBTTagCompound tags)
    {
        super.readNetworkNBT(tags);

        NBTTagList tanks = tags.getTagList("Tanks");
        if (tanks != null)
        {
            lavaTanks.clear();

            for (int i = 0; i < tanks.tagCount(); ++i)
            {
                NBTTagIntArray tag = (NBTTagIntArray) tanks.tagAt(i);
                int[] coord = tag.intArray;
                layerAirCoords.add(new CoordTuple(coord[0], coord[1], coord[2]));
            }
        }
    }

    @Override
    public void writeNetworkNBT (NBTTagCompound tags)
    {
        super.writeNetworkNBT(tags);

        NBTTagList tanks = new NBTTagList();
        for (CoordTuple coord : lavaTanks)
        {
            tanks.appendTag(new NBTTagIntArray("coord", new int[] { coord.x, coord.y, coord.z }));
        }
        tags.setTag("Tanks", tanks);
    }
}
