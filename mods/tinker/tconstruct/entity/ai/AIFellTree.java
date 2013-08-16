package mods.tinker.tconstruct.entity.ai;

import mods.tinker.tconstruct.entity.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.block.StepSound;
import net.minecraft.util.MathHelper;

public class AIFellTree extends GolemAIBase
{
    private int counter;
    private int logID;
    private int blockStrength;
    private int cutX, cutY, cutZ;

    public static int cutRange = 4;
    public static double cutSpeed = 2.5;

    public AIFellTree(GolemBase golem)
    {
        super(golem);
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute ()
    {
        if (golem.paused)
        {
            return false;
        }

        int golemX = MathHelper.floor_double(golem.posX);
        int golemY = MathHelper.floor_double(golem.posY + (double) golem.getEyeHeight());
        int golemZ = MathHelper.floor_double(golem.posZ);
        int logCount = 0;
        int leavesCount = 0;
        int targetX = cutRange;
        int targetY = 0;
        int targetZ = cutRange;
        boolean isTarget = false;

        for (int iterX = -cutRange; iterX <= cutRange; ++iterX)
        {
            for (int iterZ = -cutRange; iterZ <= cutRange; ++iterZ)
            {
                int dy = 0;
                int distanceToBlock = Math.abs(iterX) + Math.abs(iterZ);
                if (distanceToBlock > cutRange)
                {
                    continue;
                }
                int bid = world.getBlockId(golemX + iterX, golemY + dy, golemZ + iterZ);
                Block log = Block.blocksList[bid];
                if (log == null)
                {
                    continue;
                }

                if (log.isWood(world, golemX + iterX, golemY + dy, golemZ + iterZ))
                {
                    if (Math.abs(targetX) + Math.abs(targetZ) > distanceToBlock)
                    {

                        while (golemY + dy > 0)
                        {
                            int cbid = world.getBlockId(golemX + iterX, golemY + dy - 1, golemZ + iterZ);
                            if (cbid != bid)
                            {
                                break;
                            }
                            --dy;
                        }
                        int lCount = 0;
                        int blockCount = 0;
                        while (golemY + dy < world.getHeight())
                        {
                            int cbid = world.getBlockId(golemX + iterX, golemY + dy, golemZ + iterZ);
                            if (cbid != bid)
                            {
                                break;
                            }

                            int[][] coords = new int[][] { { golemX + iterX + 1, golemY + dy, golemZ + iterZ }, { golemX + iterX - 1, golemY + dy, golemZ + iterZ },
                                    { golemX + iterX, golemY + dy, golemZ + iterZ + 1 }, { golemX + iterX, golemY + dy, golemZ + iterZ - 1 } };
                            for (int i = 0; i < 4; ++i)
                            {
                                Block leaves = Block.blocksList[world.getBlockId(coords[i][0], coords[i][1], coords[i][2])];
                                if (leaves != null && leaves.isLeaves(world, coords[i][0], coords[i][1], coords[i][2]))
                                {
                                    blockCount++;
                                }
                            }
                            ++lCount;
                            ++dy;
                        }

                        if (blockCount >= 4)
                        {
                            targetX = iterX;
                            targetY = dy - 1;
                            targetZ = iterZ;
                            isTarget = true;
                            logCount = blockCount;
                            leavesCount = lCount;
                            logID = bid;
                        }
                    }
                }
            }
        }
        golem.targetX = golemX + targetX;
        golem.targetY = golemY + targetY;
        golem.targetZ = golemZ + targetZ;
        golem.targetLock = isTarget;

        if (!isTarget)
        {
            return false;
        }

        golem.teleport((double) (golem.targetX) + 0.5D, (double) (golem.targetY + 1), (double) (golem.targetZ) + 0.5D);
        golem.getNavigator().clearPathEntity();

        return true;
    }

    public void startExecuting ()
    {
        counter = 0;
    }

    public boolean continueExecuting ()
    {
        if (golem.paused)
        {
            return false;
        }
        if (Math.abs(MathHelper.floor_double(golem.posX - golem.targetX)) + Math.abs(MathHelper.floor_double(golem.posY - golem.targetY))
                + Math.abs(MathHelper.floor_double(golem.posZ - golem.targetZ)) > cutRange)
        {
            System.out.println("Too far away");
            return false;
        }
        if (!golem.targetLock)
        {
            System.out.println("Target lost");
            return false;
        }
        return true;
    }

    @Override
    public void resetTask ()
    {
        golem.targetLock = false;
    }

    @Override
    public void updateTask ()
    {
        if (counter == 0)
        {
            boolean isCutting = false;
            for (int dx = -cutRange; dx <= cutRange; ++dx)
            {
                for (int dz = -cutRange; dz <= cutRange; ++dz)
                {
                    if (Math.abs(dx) + Math.abs(dz) > cutRange)
                    {
                        continue;
                    }
                    if (world.getBlockId(golem.targetX + dx, golem.targetY, golem.targetZ + dz) == logID)
                    {
                        cutX = golem.targetX + dx;
                        cutY = golem.targetY;
                        cutZ = golem.targetZ + dz;
                        isCutting = true;
                    }
                    if (isCutting)
                    {
                        break;
                    }
                }
                if (isCutting)
                {
                    break;
                }
            }
            if (!isCutting)
            {
                cutX = golem.targetX;
                cutY = golem.targetY;
                cutZ = golem.targetZ;
                golem.targetY--;
                if (world.getBlockId(golem.targetX, golem.targetY, golem.targetZ) == logID)
                {
                    isCutting = true;
                }
                else
                {
                    golem.targetLock = isCutting = false;
                    return;
                }
            }

            Block b = Block.blocksList[world.getBlockId(cutX, cutY, cutZ)];
            blockStrength = b == null ? 0 : MathHelper.floor_double(1.0 + 30.0 * b.getBlockHardness(world, cutX, cutY, cutZ) / cutSpeed);
            blockStrength = blockStrength < 0 ? 0 : blockStrength;
        }

        if (counter >= blockStrength)
        {
            Block b = Block.blocksList[world.getBlockId(cutX, cutY, cutZ)];
            if (b != null)
            {
                world.destroyBlock(cutX, cutY, cutZ, true);
            }
            counter = 0;
            return;
        }
        if (counter % 4 == 0)
        {
            Block b = Block.blocksList[world.getBlockId(golem.targetX, golem.targetY, golem.targetZ)];
            if (b != null)
            {
                StepSound stepsound = b.stepSound;
                world.playSoundEffect(golem.targetX + 0.5f, golem.targetY + 0.5f, golem.targetZ + 0.5f, stepsound.getBreakSound(), (stepsound.getVolume() + 1.0f) / 8f, stepsound.getPitch() * 0.5f);
            }
        }
        this.golem.getLookHelper().setLookPosition((double) (cutX) + 0.5D, (double) (cutY) + 0.5D, (double) (cutZ) + 0.5D, 20.0F, (float) this.golem.getVerticalFaceSpeed());
        counter++;
    }
}
