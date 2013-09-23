package tconstruct.library.blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.common.TContent;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IFacingLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;

public abstract class AdaptiveInventoryLogic extends InventoryLogic implements IFacingLogic, IMasterLogic
{
    Random random = new Random();
    protected boolean validStructure;
    protected int inventorySize;
    protected boolean needsUpdate;
    protected int tick = 0;

    public AdaptiveInventoryLogic()
    {
        super(0);
        validAirCoords.add(new int[] { 1, 0 });
        validAirCoords.add(new int[] { -1, 0 });
        validAirCoords.add(new int[] { 0, 1 });
        validAirCoords.add(new int[] { 0, -1 });
    }

    protected void adjustInventory (int size, boolean forceAdjust)
    {
        if (size != inventorySize || forceAdjust)
        {
            needsUpdate = true;
            inventorySize = size;

            ItemStack[] tempInv = inventory;
            inventory = new ItemStack[size];
            int invLength = tempInv.length > inventory.length ? inventory.length : tempInv.length;
            System.arraycopy(tempInv, 0, inventory, 0, invLength);

            if (tempInv.length > inventory.length)
            {
                for (int i = inventory.length; i < tempInv.length; i++)
                {
                    ItemStack stack = tempInv[i];
                    if (stack != null)
                    {
                        float jumpX = random.nextFloat() * 0.8F + 0.1F;
                        float jumpY = random.nextFloat() * 0.8F + 0.1F;
                        float jumpZ = random.nextFloat() * 0.8F + 0.1F;

                        int offsetX = 0;
                        int offsetY = 0;
                        int offsetZ = 0;
                        switch (getTossDirection())
                        {
                        case 0: // -y
                            offsetY--;
                            break;
                        case 1: // +y
                            offsetY++;
                            break;
                        case 2: // +z
                            offsetZ--;
                            break;
                        case 3: // -z
                            offsetZ++;
                            break;
                        case 4: // +x
                            offsetX--;
                            break;
                        case 5: // -x
                            offsetX++;
                            break;
                        }

                        while (stack.stackSize > 0)
                        {
                            int itemSize = random.nextInt(21) + 10;

                            if (itemSize > stack.stackSize)
                            {
                                itemSize = stack.stackSize;
                            }

                            stack.stackSize -= itemSize;
                            EntityItem entityitem = new EntityItem(worldObj, (double) ((float) xCoord + jumpX + offsetX), (double) ((float) yCoord + jumpY),
                                    (double) ((float) zCoord + jumpZ + offsetZ), new ItemStack(stack.itemID, itemSize, stack.getItemDamage()));

                            if (stack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                            }

                            float offset = 0.05F;
                            entityitem.motionX = (double) ((float) random.nextGaussian() * offset);
                            entityitem.motionY = (double) ((float) random.nextGaussian() * offset + 0.2F);
                            entityitem.motionZ = (double) ((float) random.nextGaussian() * offset);
                            worldObj.spawnEntityInWorld(entityitem);
                        }
                    }
                }
            }
        }
    }

    public int getTossDirection ()
    {
        return getRenderDirection();
    }

    @Override
    public void updateEntity ()
    {
        tick++;

        if (tick % 20 == 0)
        {
            /*if (!validStructure)
                checkValidPlacement();*/

            if (needsUpdate)
            {
                needsUpdate = false;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
    }

    @Override
    public void onInventoryChanged ()
    {
        super.onInventoryChanged();
        needsUpdate = true;
    }

    int bricks = 0;
    int airBlocks = 0;
    HashSet<CoordTuple> blockCoords = new HashSet<CoordTuple>();
    HashSet<CoordTuple> airCoords = new HashSet<CoordTuple>();
    ArrayList<int[]> validAirCoords = new ArrayList<int[]>();
    CoordTuple returnStone;

    public void checkValidStructure ()
    {
        bricks = 0;
        airBlocks = 0;
        blockCoords.clear();
        airCoords.clear();
        boolean valid = false;
        //Check for air space in front of and behind the structure
        byte dir = getRenderDirection();
        switch (getRenderDirection())
        {
        case 2: // +z
        case 3: // -z
            if (checkAir(xCoord, yCoord, zCoord - 1) && checkAir(xCoord, yCoord, zCoord + 1))
                valid = true;
            break;
        case 4: // +x
        case 5: // -x
            if (checkAir(xCoord - 1, yCoord, zCoord) && checkAir(xCoord + 1, yCoord, zCoord))
                valid = true;
            break;
        }

        //Check for at least two connected blocks

        //Recurse the structure
        int xPos = 0, zPos = 0;
        if (dir == 2)
            xPos = 1;
        if (dir == 3)
            xPos = -1;
        if (dir == 4)
            zPos = -1;
        if (dir == 5)
            zPos = 1;

        returnStone = new CoordTuple(xCoord - xPos, yCoord, zCoord - zPos);
        if (recurseLayer(xCoord + xPos, yCoord, zCoord + zPos))
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
            boolean air = floodTest(xCoord + xPos, yCoord, zCoord + zPos);
            System.out.println("Air blocks: " + airBlocks);
        }

        System.out.println("Bricks: " + blockCoords.size());
    }

    protected boolean checkAir (int x, int y, int z)
    {
        Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
        if (block == null || block.isAirBlock(worldObj, x, y, z))
            return true;

        return false;
    }

    protected boolean checkServant (int x, int y, int z)
    {
        Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
        if (block == null || block.isAirBlock(worldObj, x, y, z))
            return false;

        if (!block.hasTileEntity(worldObj.getBlockMetadata(x, y, z)))
            return false;

        if (worldObj.getBlockTileEntity(x, y, z) instanceof IServantLogic)
            return true;

        return false;
    }

    protected boolean recurseLayer (int x, int y, int z)
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
                    int blockID = worldObj.getBlockId(x + xPos, y, z + zPos);
                    if (blockID == TContent.smeltery.blockID) //Needs different checking
                    {
                        bricks++;
                        blockCoords.add(coord);
                        return recurseLayer(x + xPos, y, z + zPos);
                    }
                }
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
                    airBlocks++;
                    airCoords.add(coord);
                    //worldObj.setBlock(x + offset[0], y, z + offset[1], Block.leaves.blockID);
                    floodTest(x + offset[0], y, z + offset[1]);
                }
            }
        }
        return true;
    }
    
    public void cleanup()
    {
        for (CoordTuple coord : airCoords)
        {
            worldObj.setBlockToAir(coord.x, coord.y, coord.z);            
        }
    }

    void testReplaceBlocks ()
    {

    }

    @Override
    public void notifyChange (int x, int y, int z) //The one method from IMasterLogic
    {

    }
}
