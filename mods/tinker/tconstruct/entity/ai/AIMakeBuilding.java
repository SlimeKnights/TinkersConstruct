package mods.tinker.tconstruct.entity.ai;

import mods.tinker.tconstruct.entity.Gardeslime;
import mods.tinker.tconstruct.library.util.CoordTuple;

public class AIMakeBuilding extends GolemAIBase
{
    int blockIndex = 0;
    int delay = 0;
    Gardeslime slime;
    public AIMakeBuilding(Gardeslime entity)
    {
        super(entity);
        this.slime = entity;
        this.setMutexBits(4);
    }

    @Override
    public boolean shouldExecute ()
    {
        if (golem.paused)
        {
            return false;
        }
        if (slime.completedHome)
        {
            return false;
        }
        if (!slime.hasMaterials())
        {
            return false;
        }
        return true;
    }

    @Override
    public void updateTask ()
    {
        delay++;
        if (delay == 5)
        {
            delay = 0;
            int posX = blockIndex % 3;
            int posY = blockIndex / 9;
            int posZ = (blockIndex / 3) % 3;
            int blockID = slime.buildingArray[posX][posY][posZ];
            int blockMeta = slime.metaArray[posX][posY][posZ];
            if (slime.home == null)
            {
                slime.setHome((int) Math.floor(slime.posX), (int) Math.floor(slime.posY), (int) Math.floor(slime.posZ));
            }
            slime.worldObj.setBlock(slime.home.x + posX, slime.home.y + posY-1, slime.home.z + posZ, blockID, blockMeta, 3);
            world.playAuxSFX(2001, slime.home.x + posX, slime.home.y + posY-1, slime.home.z + posZ, blockID + (blockMeta << 12));
            blockIndex++;
            if (blockIndex == 18)
                slime.completedHome = true;
        }
    }
}
