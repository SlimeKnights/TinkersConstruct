package tconstruct.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.common.TRepo;

public class SlimeTreeGen extends WorldGenerator
{
    public final int minTreeHeight;
    public final int treeHeightRange;
    public final int metaWood;
    public final int metaLeaves;
    public final boolean seekHeight;
    public final boolean notify;

    public SlimeTreeGen(boolean notify, int treeHeight, int treeRange, int woodMeta, int leavesMeta)
    {
        super(notify);
        this.notify = notify;
        this.minTreeHeight = treeHeight;
        this.treeHeightRange = treeRange;
        this.metaWood = woodMeta;
        this.metaLeaves = leavesMeta;
        this.seekHeight = !notify;
    }

    public boolean generate (World world, Random random, int xPos, int yPos, int zPos)
    {
        int height = random.nextInt(this.treeHeightRange) + this.minTreeHeight;
        boolean flag = true;
        if (seekHeight)
        {
            yPos = findGround(world, xPos, yPos, zPos);
            if (yPos == -1)
                return false;
        }

        if (yPos >= 1 && yPos + height + 1 <= 256)
        {
            Block soil = world.getBlock(xPos, yPos - 1, zPos);
            boolean isSoil = (soil != null && soil.canSustainPlant(world, xPos, yPos - 1, zPos, ForgeDirection.UP, TRepo.slimeSapling));

            if (isSoil)
            {
                if (!checkClear(world, xPos, yPos, zPos, height))
                    return false;

                soil.onPlantGrow(world, xPos, yPos - 1, zPos, xPos, yPos, zPos);
                placeCanopy(world, random, xPos, yPos, zPos, height);
                placeTrunk(world, xPos, yPos, zPos, height);
                return true;
            }
        }
        return false;
    }

    boolean checkClear (World world, int x, int y, int z, int treeHeight)
    {
        for (int yPos = 0; yPos < treeHeight + 1; yPos++)
        {
            int range = 1;

            if (yPos == 0)
                range = 0;
            else if (yPos >= treeHeight - 1)
                range = 2;

            for (int xPos = range; xPos <= range; xPos++)
            {
                for (int zPos = range; zPos <= range; zPos++)
                {
                    Block block = world.getBlock(x + xPos, y + yPos, z + zPos);
                    if (block != null && block != TRepo.slimeSapling && !block.isLeaves(world, x + xPos, y + yPos, z + zPos))
                        return false;
                }
            }
        }
        return true;
    }

    int findGround (World world, int x, int y, int z)
    {
        int ret = -1;
        int height = y;
        do
        {
            Block heightBL = world.getBlock(x, height, z);
            if ((heightBL == TRepo.craftedSoil || heightBL == TRepo.slimeGrass) && !world.getBlock(x, height + 1, z).isOpaqueCube())
            {
                ret = height + 1;
                break;
            }
            height--;
        } while (height > 0);
        return ret;
    }

    void placeCanopy (World world, Random random, int xPos, int yPos, int zPos, int height)
    {
        for (int i = 0; i < 4; i++)
        {
            placeDiamondLayer(world, xPos, yPos + height - i, zPos, i + 1);
        }

        this.setBlockAndMetadata(world, xPos + 4, yPos + height - 3, zPos, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos - 4, yPos + height - 3, zPos, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos, yPos + height - 3, zPos + 4, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos, yPos + height - 3, zPos - 4, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos + 1, yPos + height - 3, zPos + 1, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos + 1, yPos + height - 3, zPos - 1, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos - 1, yPos + height - 3, zPos + 1, Blocks.air, 0);
        this.setBlockAndMetadata(world, xPos - 1, yPos + height - 3, zPos - 1, Blocks.air, 0);

        // Drippers
        this.setBlockAndMetadata(world, xPos + 3, yPos + height - 4, zPos, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos - 3, yPos + height - 4, zPos, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos, yPos + height - 4, zPos - 3, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos, yPos + height - 4, zPos + 3, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos + 2, yPos + height - 4, zPos + 2, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos + 2, yPos + height - 4, zPos - 2, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos - 2, yPos + height - 4, zPos + 2, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos - 2, yPos + height - 4, zPos - 2, TRepo.slimeLeaves, this.metaLeaves);

        this.setBlockAndMetadata(world, xPos + 3, yPos + height - 5, zPos, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos - 3, yPos + height - 5, zPos, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos, yPos + height - 5, zPos - 3, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos, yPos + height - 5, zPos + 3, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos + 2, yPos + height - 5, zPos + 2, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos + 2, yPos + height - 5, zPos - 2, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos - 2, yPos + height - 5, zPos + 2, TRepo.slimeLeaves, this.metaLeaves);
        this.setBlockAndMetadata(world, xPos - 2, yPos + height - 5, zPos - 2, TRepo.slimeLeaves, this.metaLeaves);
    }

    void placeDiamondLayer (World world, int xPos, int yPos, int zPos, int range)
    {
        for (int x = -range; x <= range; x++)
        {
            for (int z = -range; z <= range; z++)
            {
                if (Math.abs(x) + Math.abs(z) <= range)
                {
                    this.setBlockAndMetadata(world, x + xPos, yPos, z + zPos, TRepo.slimeLeaves, this.metaLeaves);
                }
            }
        }
    }

    void placeTrunk (World world, int xPos, int yPos, int zPos, int height)
    {
        for (int localHeight = 0; localHeight < height; ++localHeight)
        {
            Block block = world.getBlock(xPos, yPos + localHeight, zPos);

            if (block == null || block == Blocks.air || block.isLeaves(world, xPos, yPos + localHeight, zPos))
            {
                this.setBlockAndMetadata(world, xPos, yPos + localHeight, zPos, TRepo.slimeGel, this.metaWood);
            }
        }
    }

    protected void setBlockAndMetadata (World world, int x, int y, int z, Block block, int blockMeta)
    {
        Block blockWorld = world.getBlock(x, y, z);
        if (blockWorld == null || blockWorld == Blocks.air || blockWorld.canPlaceBlockAt(world, x, y, z))
        {
            if (this.notify)
            {
                world.setBlock(x, y, z, block, blockMeta, 3);
            }
            else
            {
                world.setBlock(x, y, z, block, blockMeta, 2);
            }
        }
    }
}
