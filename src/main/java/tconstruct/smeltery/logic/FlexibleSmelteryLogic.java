package tconstruct.smeltery.logic;

import mantle.blocks.abstracts.MultiServantLogic;
import mantle.blocks.iface.IServantLogic;
import mantle.world.CoordTuple;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.TinkerSmeltery;

public class FlexibleSmelteryLogic extends SmelteryLogic {
    private static final int MAX_SMELTERY_SIZE = 5;

    public CoordTuple minPos = new CoordTuple(0, 0, 0);
    public CoordTuple maxPos = new CoordTuple(0, 0, 0);

    /* Multiblock */
    @Override
    public void notifyChange (IServantLogic servant, int x, int y, int z)
    {
        checkValidPlacement();
    }

    public void checkValidPlacement ()
    {
        switch (getRenderDirection())
        {
            case 2: // +z
                alignInitialPlacement(xCoord, yCoord, zCoord + 1);
                break;
            case 3: // -z
                alignInitialPlacement(xCoord, yCoord, zCoord - 1);
                break;
            case 4: // +x
                alignInitialPlacement(xCoord + 1, yCoord, zCoord);
                break;
            case 5: // -x
                alignInitialPlacement(xCoord - 1, yCoord, zCoord);
                break;
        }
    }

    // aligns the position given (inside the smeltery) to be the center of the smeltery
    public void alignInitialPlacement (int x, int y, int z)
    {
        // x/y/z = the block behind the controller "inside the smeltery"

        // adjust the x-position of the block until the difference between the outer walls is at most 1
        // basically this means we center the block inside the smeltery on the x axis.
        int xd1 = 1, xd2 = 1; // x-difference
        for(int i = 1; i < MAX_SMELTERY_SIZE; i++) // don't check farther than needed
        {
            if(worldObj.getBlock(x - xd1, y, z) == null || worldObj.isAirBlock(x - xd1,y,z))
                xd1++;
            if(worldObj.getBlock(x + xd2, y, z) == null || worldObj.isAirBlock(x + xd2,y,z))
                xd2++;

            // if one side hit a wall and the other didn't we might have to center our x-position again
            if(xd1-xd2 > 1)
            {
                // move x and offsets to the -x
                xd1--;
                x--;
                xd2++;
            }
            // or the right
            if(xd2-xd1 > 1)
            {
                xd2--;
                x++;
                xd1++;
            }
        }
        // same for z-axis
        int zd1 = 1, zd2 = 1;
        for(int i = 1; i < MAX_SMELTERY_SIZE; i++) // don't check farther than needed
        {
            if(worldObj.getBlock(x, y, z - zd1) == null || worldObj.isAirBlock(x, y, z - zd1))
                zd1++;
            if(worldObj.getBlock(x, y, z + zd2) == null || worldObj.isAirBlock(x, y, z + zd2))
                zd2++;

            // if one side hit a wall and the other didn't we might have to center our x-position again
            if(zd1-zd2 > 1)
            {
                // move x and offsets to the -x
                zd1--;
                z--;
                zd2++;
            }
            // or the right
            if(zd2-zd1 > 1)
            {
                zd2--;
                z++;
                zd1++;
            }
        }

        // do the check
        int[] sides = new int[] {xd1, xd2, zd1, zd2};
        checkValidStructure(x, y, z, sides);
    }

    /**
     *
     * @param x x-center of the smeltery +-1
     * @param y y-position of the controller block
     * @param z z-center of the smeltery +-1
     * @param sides distance between the center point and the wall. [-x,+x,-z,+z]
     */
    public void checkValidStructure (int x, int y, int z, int[] sides)
    {
        int checkLayers = 0;
        //worldObj.setBlock(x,y,z, Blocks.redstone_block);
        //worldObj.setBlock(x+sides[1]-sides[0],y+1,z+sides[3]-sides[2], Blocks.lapis_block);

        tempValidStructure = false;
        // this piece of code here does the complete validity check.
        if (checkSameLevel(x, y, z, sides))
        {
            checkLayers++;
            checkLayers += recurseStructureUp(x, y + 1, z, sides, 0);
            checkLayers += recurseStructureDown(x, y - 1, z, sides, 0);
        }

        // maxLiquid = capacity * 20000;

        if (tempValidStructure != validStructure || checkLayers != this.layers)
        {
            if (tempValidStructure)
            {
                // try to derive temperature from fueltank
                activeLavaTank = null;
                for (CoordTuple tank : lavaTanks)
                {
                    TileEntity tankContainer = worldObj.getTileEntity(tank.x, tank.y, tank.z);
                    if (!(tankContainer instanceof IFluidHandler))
                        continue;

                    FluidStack liquid = ((IFluidHandler) tankContainer).getTankInfo(ForgeDirection.DOWN)[0].fluid;
                    if (liquid == null)
                        continue;
                    if (!Smeltery.isSmelteryFuel(liquid.getFluid()))
                        continue;

                    internalTemp = Smeltery.getFuelPower(liquid.getFluid());
                    activeLavaTank = tank;
                    break;
                }

                // no tank with fuel. we reserve the first found one
                if (activeLavaTank == null)
                    activeLavaTank = lavaTanks.get(0);

                // update other stuff
                adjustLayers(checkLayers, false);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                validStructure = true;
            }
            else
            {
                internalTemp = 20;
                validStructure = false;
            }
        }
    }

    public boolean checkBricksOnLevel(int x, int y, int z, int[] sides)
    {
        int numBricks = 0;
        Block block;
        int xMin = x - sides[0];
        int xMax = x + sides[1];
        int zMin = z - sides[2];
        int zMax = z + sides[3];

        // Check inside
        for (int xPos = xMin + 1; xPos <= xMax - 1; xPos++)
        {
            for (int zPos = zMin + 1; zPos <= zMax - 1; zPos++)
            {
                block = worldObj.getBlock(xPos, y, zPos);
                if (block != null && !worldObj.isAirBlock(xPos, y, zPos))
                    return false;
            }
        }

        // Check outer layer
        for (int xPos = xMin + 1; xPos <= xMax - 1; xPos++)
        {
            numBricks += checkBricks(xPos, y, zMin);
            numBricks += checkBricks(xPos, y, zMax);
        }

        for (int zPos = zMin + 1; zPos <= zMax - 1; zPos++)
        {
            numBricks += checkBricks(xMin, y, zPos);
            numBricks += checkBricks(xMax, y, zPos);
        }

        int neededBricks = (xMax-xMin)*2 + (zMax-zMin)*2 - 4; // -4 because corners are not needed

        return numBricks == neededBricks;
    }

    public boolean checkSameLevel(int x, int y, int z, int[] sides)
    {
        lavaTanks.clear();

        boolean check = checkBricksOnLevel(x,y,z,sides);

        if (check && lavaTanks.size() > 0)
            return true;
        else
            return false;
    }

    public int recurseStructureUp (int x, int y, int z, int[] sides, int count)
    {
        boolean check = checkBricksOnLevel(x,y,z,sides);

        if(!check)
            return count;

        count++;
        return recurseStructureUp(x, y + 1, z, sides, count);
    }

    public int recurseStructureDown (int x, int y, int z, int[] sides, int count)
    {
        boolean check = checkBricksOnLevel(x,y,z,sides);

        if(!check) {
            // regular check failed, maybe it's the bottom?
            Block block = worldObj.getBlock(x,y,z);
            if (block != null && !worldObj.isAirBlock(x, y, z))
                if (validBlockID(block))
                    return validateBottom(x, y, z, sides, count);

            return count;
        }

        count++;
        return recurseStructureDown(x, y - 1, z, sides, count);
    }

    public int validateBottom (int x, int y, int z, int[] sides, int count)
    {
        int bottomBricks = 0;
        int xMin = x - sides[0] + 1;
        int xMax = x + sides[1] - 1;
        int zMin = z - sides[2] + 1;
        int zMax = z + sides[3] - 1;

        // Check inside
        for (int xPos = xMin; xPos <= xMax; xPos++)
        {
            for (int zPos = zMin; zPos <= zMax; zPos++)
            {
                if (validBlockID(worldObj.getBlock(xPos, y, zPos)) && (worldObj.getBlockMetadata(xPos, y, zPos) >= 2))
                    bottomBricks++;
            }
        }

        int neededBricks = (xMax+1-xMin) * (zMax+1-zMin); // +1 because we want inclusive the upper border

        if (bottomBricks == neededBricks)
        {
            tempValidStructure = true;
            minPos = new CoordTuple(xMin, y+1, zMin);
            maxPos = new CoordTuple(xMax, y+1, zMax);
        }
        return count;
    }

    /*
     * Returns whether the brick is a lava tank or not. Increments bricks, sets
     * them as part of the structure, and adds tanks to the list.
     */
    int checkBricks (int x, int y, int z)
    {
        int tempBricks = 0;
        Block blockID = worldObj.getBlock(x, y, z);
        if (validBlockID(blockID) || validTankID(blockID))
        {
            TileEntity te = worldObj.getTileEntity(x, y, z);
            if (te == this)
            {
                tempBricks++;
            }
            else if (te instanceof MultiServantLogic)
            {
                MultiServantLogic servant = (MultiServantLogic) te;
                if (servant.hasValidMaster())
                {
                    if (servant.verifyMaster(this, worldObj, this.xCoord, this.yCoord, this.zCoord))
                        tempBricks++;
                }
                else
                {
                    servant.overrideMaster(this.xCoord, this.yCoord, this.zCoord);
                    tempBricks++;
                }

                if (te instanceof LavaTankLogic)
                {
                    lavaTanks.add(new CoordTuple(x, y, z));
                }
            }
        }
        return tempBricks;
    }

    boolean validBlockID (Block blockID)
    {
        return blockID == TinkerSmeltery.smeltery || blockID == TinkerSmeltery.smelteryNether;
    }

    boolean validTankID (Block blockID)
    {
        return blockID == TinkerSmeltery.lavaTank || blockID == TinkerSmeltery.lavaTankNether;
    }
}
