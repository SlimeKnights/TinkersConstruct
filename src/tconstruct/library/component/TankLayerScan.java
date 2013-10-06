package tconstruct.library.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;

public class TankLayerScan extends LogicComponent
{
    protected TileEntity master;
    protected IMasterLogic imaster;
    protected Block[] scanBlocks;

    protected boolean completeStructure;
    protected int lowestY;

    protected int bricks = 0;
    protected int airBlocks = 0;
    protected TreeSet<CoordTuple> layerBlockCoords = new TreeSet<CoordTuple>();
    protected TreeSet<CoordTuple> layerAirCoords = new TreeSet<CoordTuple>();

    public TreeSet<CoordTuple> blockCoords = new TreeSet<CoordTuple>();
    public TreeSet<CoordTuple> airCoords = new TreeSet<CoordTuple>();
    protected ArrayList<int[]> validAirCoords = new ArrayList<int[]>();
    protected CoordTuple returnStone;

    private boolean debug = false;

    public TankLayerScan(TileEntity te, Block... ids)
    {
        assert te instanceof IMasterLogic : "TileEntity must be an instance of IMasterLogic";
        master = te;
        imaster = (IMasterLogic) te;
        scanBlocks = ids;
        validAirCoords.add(new int[] { 1, 0 });
        validAirCoords.add(new int[] { -1, 0 });
        validAirCoords.add(new int[] { 0, 1 });
        validAirCoords.add(new int[] { 0, -1 });
    }

    public void checkValidStructure ()
    {
        bricks = 0;
        airBlocks = 0;
        blockCoords.clear();
        airCoords.clear();
        boolean validAir = false;
        //Check for air space in front of and behind the structure
        byte dir = getDirection();
        switch (getDirection())
        {
        case 2: // +z
        case 3: // -z
            if (checkAir(master.xCoord, master.yCoord, master.zCoord - 1) && checkAir(master.xCoord, master.yCoord, master.zCoord + 1))
                validAir = true;
            break;
        case 4: // +x
        case 5: // -x
            if (checkAir(master.xCoord - 1, master.yCoord, master.zCoord) && checkAir(master.xCoord + 1, master.yCoord, master.zCoord))
                validAir = true;
            break;
        }

        //Recurse the structure
        boolean validBlocks = false;

        int xPos = 0, zPos = 0;
        if (dir == 2)
            xPos = 1;
        if (dir == 3)
            xPos = -1;
        if (dir == 4)
            zPos = -1;
        if (dir == 5)
            zPos = 1;

        returnStone = new CoordTuple(master.xCoord - xPos, master.yCoord, master.zCoord - zPos);
        if (initialRecurseLayer(master.xCoord + xPos, master.yCoord, master.zCoord + zPos))
        {
            xPos = 0;
            zPos = 0;
            switch (dir)
            {
            case 2: // +z
                zPos = 1;
                break;
            case 3: // -z
                zPos = -1;
                break;
            case 4: // +x
                xPos = 1;
                break;
            case 5: // -x
                xPos = -1;
                break;
            }
            if (!world.isRemote && debug)
                System.out.println("Bricks in recursion: " + blockCoords.size());
            blockCoords.clear();
            bricks = 0;

            //Does the actual adding of blocks in the ring
            boolean sealed = floodTest(master.xCoord + xPos, master.yCoord, master.zCoord + zPos);
            if (!world.isRemote && debug)
            {
                System.out.println("Air in ring: " + airBlocks);
                System.out.println("Bricks in ring: " + bricks);
            }

            if (sealed)
            {
                layerAirCoords = new TreeSet<CoordTuple>(airCoords);
                layerBlockCoords = new TreeSet<CoordTuple>(blockCoords);

                int lowY = recurseStructureDown(master.yCoord - 1);
                if (lowY != -1)
                {
                    lowestY = lowY;
                    completeStructure = true;
                    recurseStructureUp(master.yCoord + 1);

                    if (!world.isRemote && debug)
                    {
                        System.out.println("Air in structure: " + airCoords.size());
                        System.out.println("Bricks in structure: " + blockCoords.size());
                    }
                }
            }
        }

        if (completeStructure)
        {

        }
        else
        {

        }
    }

    public boolean isComplete ()
    {
        return completeStructure;
    }

    public int getAirSize ()
    {
        return airBlocks;
    }

    private byte getDirection ()
    {
        if (master instanceof IFacingLogic)
            return ((IFacingLogic) master).getRenderDirection();
        return 0;
    }

    protected boolean checkAir (int x, int y, int z)
    {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block == null || block.isAirBlock(world, x, y, z))
            return true;

        return false;
    }

    protected boolean checkServant (int x, int y, int z)
    {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block == null || block.isAirBlock(world, x, y, z) || !isValidBlock(x, y, z))
            return false;

        if (!block.hasTileEntity(world.getBlockMetadata(x, y, z)))
            return false;

        TileEntity be = world.getBlockTileEntity(x, y, z);
        if (be instanceof IServantLogic)
            return ((IServantLogic) be).setPotentialMaster(this.imaster, this.world, x, y, z);

        return false;
    }

    protected boolean initialRecurseLayer (int x, int y, int z)
    {
        if (bricks > 4000)
            return false;

        CoordTuple keystone = new CoordTuple(x, y, z);
        if (keystone.equals(returnStone))
            return true;

        for (int xPos = -1; xPos <= 1; xPos++)
        {
            for (int zPos = -1; zPos <= 1; zPos++)
            {
                CoordTuple coord = new CoordTuple(x + xPos, y, z + zPos);
                if (!blockCoords.contains(coord))
                {
                    if (isValidBlock(x + xPos, y, z + zPos))
                    {
                        bricks++;
                        blockCoords.add(coord);
                        return initialRecurseLayer(x + xPos, y, z + zPos);
                    }
                }
            }
        }

        return false;
    }

    protected boolean isValidBlock (int x, int y, int z)
    {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block != null)
        {
            for (int i = 0; i < scanBlocks.length; i++)
            {
                if (block == scanBlocks[i])
                    return true;
            }
        }
        return false;
    }

    protected boolean floodTest (int x, int y, int z)
    {
        if (airBlocks > 4000)
            return false;

        for (int[] offset : validAirCoords)
        {
            CoordTuple coord = new CoordTuple(x + offset[0], y, z + offset[1]);
            if (!airCoords.contains(coord))
            {
                if (checkAir(x + offset[0], y, z + offset[1]))
                {
                    addAirBlock(coord.x, y, coord.z);
                    floodTest(x + offset[0], y, z + offset[1]);
                }
                else if (!blockCoords.contains(coord) && checkServant(x + offset[0], y, z + offset[1]))
                {
                    bricks++;
                    blockCoords.add(coord);
                }
            }
        }
        return true;
    }

    public int recurseStructureDown (int y)
    {
        Iterator i = layerAirCoords.iterator();
        if (i.hasNext())
        {
            CoordTuple coord = (CoordTuple) i.next();
            if (checkAir(coord.x, y, coord.z))
            {
                boolean valid = true;

                //Air blocks
                while (i.hasNext())
                {
                    coord = (CoordTuple) i.next();
                    if (checkAir(coord.x, y, coord.z))
                    {
                        addAirBlock(coord.x, y, coord.z);
                    }
                    else
                    {
                        valid = false;
                        break;
                    }
                }

                //Bricks
                i = layerBlockCoords.iterator();
                while (i.hasNext())
                {
                    coord = (CoordTuple) i.next();
                    if (checkServant(coord.x, y, coord.z))
                        blockCoords.add(new CoordTuple(coord.x, y, coord.z));

                    else
                    {
                        valid = false;
                        break;
                    }
                }

                if (valid)
                    return recurseStructureDown(y - 1);
            }
            else if (checkServant(coord.x, y, coord.z))
            {
                //Bottom floor. All blocks, please vacate the elevator
                boolean valid = true;
                while (i.hasNext())
                {
                    coord = (CoordTuple) i.next();

                    if (checkServant(coord.x, y, coord.z))
                        blockCoords.add(new CoordTuple(coord.x, y, coord.z));

                    else
                    {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                return y+1;
            }
        }

        return -1;
    }

    public void recurseStructureUp (int y)
    {
        Iterator i = layerBlockCoords.iterator();
        if (i.hasNext())
        {
            CoordTuple coord = (CoordTuple) i.next();
            if (checkServant(coord.x, y, coord.z))
            {
                boolean valid = true;

                //Bricks
                while (i.hasNext())
                {
                    coord = (CoordTuple) i.next();
                    if (checkServant(coord.x, y, coord.z))
                        blockCoords.add(new CoordTuple(coord.x, y, coord.z));

                    else
                    {
                        valid = false;
                        break;
                    }
                }

                //Air blocks
                if (valid)
                {
                    i = layerAirCoords.iterator();
                    while (i.hasNext())
                    {
                        coord = (CoordTuple) i.next();
                        if (checkAir(coord.x, y, coord.z))
                        {
                            addAirBlock(coord.x, y, coord.z);
                        }
                        else
                        {
                            valid = false;
                            break;
                        }
                    }
                }

                if (valid)
                    recurseStructureUp(y + 1);
            }
        }
    }

    protected void addAirBlock (int x, int y, int z)
    {
        airBlocks++;
        airCoords.add(new CoordTuple(x, y, z));
    }

    /** Do any necessary cleanup here. Remove air blocks, invalidate servants, etc */
    public void cleanup ()
    {
        Iterator i = blockCoords.iterator();
        while (i.hasNext())
        {
            CoordTuple coord = (CoordTuple) i.next();
            IServantLogic servant = (IServantLogic) world.getBlockTileEntity(coord.x, coord.y, coord.z);
            if (servant != null)
                servant.invalidateMaster(imaster, world, master.xCoord, master.yCoord, master.zCoord);
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        NBTTagList layerAir = tags.getTagList("AirLayer");
        if (layerAir != null)
        {
            layerAirCoords.clear();

            for (int i = 0; i < layerAir.tagCount(); ++i)
            {
                NBTTagIntArray tag = (NBTTagIntArray) layerAir.tagAt(i);
                int[] coord = tag.intArray;
                layerAirCoords.add(new CoordTuple(coord[0], coord[1], coord[2]));
            }
        }
        
        NBTTagList blocks = tags.getTagList("AirLayer");
        if (blocks != null)
        {
            blockCoords.clear();

            for (int i = 0; i < blocks.tagCount(); ++i)
            {
                NBTTagIntArray tag = (NBTTagIntArray) blocks.tagAt(i);
                int[] coord = tag.intArray;
                blockCoords.add(new CoordTuple(coord[0], coord[1], coord[2]));
            }
        }
        
        NBTTagList air = tags.getTagList("AirLayer");
        if (air != null)
        {
            airCoords.clear();

            for (int i = 0; i < air.tagCount(); ++i)
            {
                NBTTagIntArray tag = (NBTTagIntArray) air.tagAt(i);
                int[] coord = tag.intArray;
                airCoords.add(new CoordTuple(coord[0], coord[1], coord[2]));
            }
        }
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        NBTTagList layerAir = new NBTTagList();
        for (CoordTuple coord : layerAirCoords)
        {
            layerAir.appendTag(new NBTTagIntArray("coord", new int[] { coord.x, coord.y, coord.z }));
        }
        tags.setTag("AirLayer", layerAir);
        
        NBTTagList blocks = new NBTTagList();
        for (CoordTuple coord : blockCoords)
        {
            blocks.appendTag(new NBTTagIntArray("coord", new int[] { coord.x, coord.y, coord.z }));
        }
        tags.setTag("Blocks", blocks);
        
        NBTTagList air = new NBTTagList();
        for (CoordTuple coord : airCoords)
        {
            air.appendTag(new NBTTagIntArray("coord", new int[] { coord.x, coord.y, coord.z }));
        }
        tags.setTag("Air", air);
    }
}
