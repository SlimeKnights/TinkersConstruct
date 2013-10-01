package tconstruct.library.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;

public class TankLayerScan extends LogicComponent
{
    TileEntity master;
    IMasterLogic imaster;
    World world;
    Block[] scanBlocks;

    public boolean completeStructure;

    int bricks = 0;
    int airBlocks = 0;
    HashSet<CoordTuple> tempBlockCoords = new HashSet<CoordTuple>();
    HashSet<CoordTuple> tempAirCoords = new HashSet<CoordTuple>();

    public HashSet<CoordTuple> blockCoords = new HashSet<CoordTuple>();
    public HashSet<CoordTuple> airCoords = new HashSet<CoordTuple>();
    ArrayList<int[]> validAirCoords = new ArrayList<int[]>();
    CoordTuple returnStone;

    public TankLayerScan(TileEntity te, Block... id)
    {
        assert te instanceof IMasterLogic : "TileEntity must be an instance of IMasterLogic";
        master = te;
        imaster = (IMasterLogic) te;
        scanBlocks = id;
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
            if (!world.isRemote)
                System.out.println("Bricks in recursion: " + blockCoords.size());
            blockCoords.clear();
            bricks = 0;

            //Does the actual adding of blocks in the ring
            boolean sealed = floodTest(master.xCoord + xPos, master.yCoord, master.zCoord + zPos);
            if (!world.isRemote)
            {
                System.out.println("Air in ring: " + airBlocks);
                System.out.println("Bricks in ring: " + bricks);
            }

            if (sealed)
            {
                tempAirCoords = new HashSet<CoordTuple>(airCoords);
                tempBlockCoords = new HashSet<CoordTuple>(blockCoords);

                if (recurseStructureDown(master.yCoord - 1))
                {
                    completeStructure = true;
                    recurseStructureUp(master.yCoord + 1);

                    if (!world.isRemote)
                    {
                        System.out.println("Air in structure: " + airCoords.size());
                        System.out.println("Bricks in structure: " + blockCoords.size());
                    }
                }
            }
        }

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
        if (block == null || block.isAirBlock(world, x, y, z))
            return false;

        if (!block.hasTileEntity(world.getBlockMetadata(x, y, z)))
            return false;

        TileEntity be = world.getBlockTileEntity(x, y, z);
        if (be instanceof IServantLogic)
            return ((IServantLogic) be).setPotentialMaster(this.imaster, x, y, z);

        return false;
    }

    protected boolean initialRecurseLayer (int x, int y, int z)
    {
        if (bricks >= 4095)
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
        if (airBlocks >= 4095)
            return false;

        for (int[] offset : validAirCoords)
        {
            CoordTuple coord = new CoordTuple(x + offset[0], y, z + offset[1]);
            if (!airCoords.contains(coord))
            {
                if (checkAir(x + offset[0], y, z + offset[1]))
                {
                    airCoords.add(coord);
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

    public boolean recurseStructureDown (int y)
    {
        Iterator i = tempAirCoords.iterator();
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
                    airCoords.add(new CoordTuple(coord.x, y, coord.z));
                    addAirBlock(coord.x, y, coord.z);
                }
                else
                {
                    valid = false;
                    break;
                }
            }

            //Bricks
            i = tempBlockCoords.iterator();
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
            return valid;
        }

        return false;
    }

    public void recurseStructureUp (int y)
    {
        Iterator i = tempBlockCoords.iterator();
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
                i = tempAirCoords.iterator();
                while (i.hasNext())
                {
                    coord = (CoordTuple) i.next();
                    if (checkAir(coord.x, y, coord.z))
                    {
                        airCoords.add(new CoordTuple(coord.x, y, coord.z));
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

    protected void addAirBlock (int x, int y, int z)
    {
        airBlocks++;
        //world.setBlock(x + offset[0], y, z + offset[1], Block.leaves.blockID);
    }
}
