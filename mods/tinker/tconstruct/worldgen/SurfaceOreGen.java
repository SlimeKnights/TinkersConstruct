package mods.tinker.tconstruct.worldgen;

import java.util.Random;

import mods.tinker.tconstruct.PHConstruct;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class SurfaceOreGen extends WorldGenerator
{
    /** The block ID of the ore to be placed using this generator. */
    private int minableBlockId;
    private int minableBlockMeta = 0;

    /** The number of blocks to generate. */
    private int numberOfBlocks;
    private int[] replaceBlocks;
    private boolean alterSize;

    public SurfaceOreGen(int id, int meta, int number, boolean changeSize)
    {
        this(id, meta, number, changeSize, Block.stone.blockID, Block.grass.blockID, Block.dirt.blockID, Block.waterStill.blockID, Block.sand.blockID, Block.gravel.blockID, Block.snow.blockID);
    }

    public SurfaceOreGen(int id, int meta, int number, boolean changeSize, int... target)
    {
        this.minableBlockId = id;
        this.numberOfBlocks = number;
        this.replaceBlocks = target;
        this.alterSize = changeSize;
        minableBlockMeta = meta;
    }

    int findGround (World world, int x, int y, int z)
    {
        int returnHeight = -1;
        int blockID = world.getBlockId(x, y - 1, z);
        if (!Block.opaqueCubeLookup[world.getBlockId(x, y, z)] && (blockID == Block.dirt.blockID || blockID == Block.grass.blockID))
        {
            return y;
        }
        int height = PHConstruct.seaLevel + 64;
        do
        {
            if (height < PHConstruct.seaLevel - 30)
            {
                break;
            }
            int bID = world.getBlockId(x, height, z);
            if (bID == Block.dirt.blockID || bID == Block.grass.blockID)
            {
                if (!Block.opaqueCubeLookup[world.getBlockId(x, height + 1, z)])
                {
                    returnHeight = height + 1;
                }
                break;
            }
            height--;
        } while (height > 0);
        return returnHeight;
    }

    public boolean generate (World world, Random random, int startX, int startY, int startZ)
    {
        if (alterSize)
        {
            startY = findGround(world, startX, startY, startZ);
            if (startY == -1)
                return false;
        }

        float f = random.nextFloat() * (float) Math.PI;
        int blockNumber = numberOfBlocks;
        if (alterSize)
            blockNumber = numberOfBlocks * 2 / 5 + random.nextInt(numberOfBlocks * 3 / 5);
        double d0 = (double) ((float) (startX + 8) + MathHelper.sin(f) * (float) blockNumber / 8.0F);
        double d1 = (double) ((float) (startX + 8) - MathHelper.sin(f) * (float) blockNumber / 8.0F);
        double d2 = (double) ((float) (startZ + 8) + MathHelper.cos(f) * (float) blockNumber / 8.0F);
        double d3 = (double) ((float) (startZ + 8) - MathHelper.cos(f) * (float) blockNumber / 8.0F);
        double d4 = (double) (startY + random.nextInt(3) - 2);
        double d5 = (double) (startY + random.nextInt(3) - 2);

        for (int l = 0; l <= blockNumber; ++l)
        {
            double d6 = d0 + (d1 - d0) * (double) l / (double) blockNumber;
            double d7 = d4 + (d5 - d4) * (double) l / (double) blockNumber;
            double d8 = d2 + (d3 - d2) * (double) l / (double) blockNumber;
            double d9 = random.nextDouble() * (double) blockNumber / 16.0D;
            double d10 = (double) (MathHelper.sin((float) l * (float) Math.PI / (float) blockNumber) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin((float) l * (float) Math.PI / (float) blockNumber) + 1.0F) * d9 + 1.0D;
            int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
            int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
            int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
            int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
            int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
            int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

            for (int k2 = i1; k2 <= l1; ++k2)
            {
                double d12 = ((double) k2 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D)
                {
                    for (int l2 = j1; l2 <= i2; ++l2)
                    {
                        double d13 = ((double) l2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D)
                        {
                            for (int i3 = k1; i3 <= j2; ++i3)
                            {
                                double d14 = ((double) i3 + 0.5D - d8) / (d10 / 2.0D);

                                Block block = Block.blocksList[world.getBlockId(k2, l2, i3)];
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D)
                                {
                                    if (block == null || !Block.opaqueCubeLookup[world.getBlockId(k2, l2, i3)])
                                        world.setBlock(k2, l2, i3, this.minableBlockId, minableBlockMeta, 2);
                                    else
                                    {
                                        for (int iter = 0; iter < replaceBlocks.length; iter++)
                                        {
                                            if (block.isGenMineableReplaceable(world, k2, l2, i3, replaceBlocks[iter]))
                                            {
                                                world.setBlock(k2, l2, i3, this.minableBlockId, minableBlockMeta, 2);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
