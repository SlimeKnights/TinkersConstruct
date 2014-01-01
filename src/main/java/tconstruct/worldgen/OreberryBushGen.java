package tconstruct.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class OreberryBushGen extends WorldGenerator
{
    private int blockID;
    private int metadata;
    int chance;
    private int[] replaceBlocks;

    public OreberryBushGen(Block block, int meta, int chance)
    {
        this(meta, chance, Blocks.stone, Blocks.grass, Blocks.dirt, Blocks.water, Blocks.sand, Blocks.gravel, Blocks.snow);
    }

    public OreberryBushGen(int blockID, int meta, int chance, int... target)
    {
        this.blockID = blockID;
        metadata = meta;
        this.chance = chance;
        this.replaceBlocks = target;
    }

    public boolean generate (World world, Random random, int x, int y, int z)
    {
        int type = random.nextInt(chance);
        if (type == 11)
            generateMediumNode(world, random, x, y, z);
        else if (type >= 5)
            generateSmallNode(world, random, x, y, z);
        else
            generateTinyNode(world, random, x, y, z);

        return true;
    }

    public void generateMediumNode (World world, Random random, int x, int y, int z)
    {
        for (int xPos = -1; xPos <= 1; xPos++)
            for (int yPos = -1; yPos <= 1; yPos++)
                for (int zPos = -1; zPos <= 1; zPos++)
                    if (random.nextInt(4) == 0)
                        generateBerryBlock(world, x + xPos, y + yPos, z + zPos, random);

        generateSmallNode(world, random, x, y, z);
    }

    public void generateSmallNode (World world, Random random, int x, int y, int z)
    {
        generateBerryBlock(world, x, y, z, random);
        if (random.nextBoolean())
            generateBerryBlock(world, x + 1, y, z, random);
        if (random.nextBoolean())
            generateBerryBlock(world, x - 1, y, z, random);
        if (random.nextBoolean())
            generateBerryBlock(world, x, y, z + 1, random);
        if (random.nextBoolean())
            generateBerryBlock(world, x, y, z - 1, random);
        if (random.nextBoolean())
            generateBerryBlock(world, x, y + 1, z, random);
        if (random.nextBoolean())
            generateBerryBlock(world, x, y + 1, z, random);
    }

    public void generateTinyNode (World world, Random random, int x, int y, int z)
    {
        generateBerryBlock(world, x, y, z, random);
        if (random.nextInt(4) == 0)
            generateBerryBlock(world, x + 1, y, z, random);
        if (random.nextInt(4) == 0)
            generateBerryBlock(world, x - 1, y, z, random);
        if (random.nextInt(4) == 0)
            generateBerryBlock(world, x, y, z + 1, random);
        if (random.nextInt(4) == 0)
            generateBerryBlock(world, x, y, z - 1, random);
        if (random.nextInt(4) == 0)
            generateBerryBlock(world, x, y + 1, z, random);
        if (random.nextInt(4) == 0)
            generateBerryBlock(world, x, y + 1, z, random);
    }

    void generateBerryBlock (World world, int x, int y, int z, Random random)
    {
        /*if (!Block.opaqueCubeLookup[world.getBlockId(x, y, z)])
        {
            setBlockAndMetadata(world, x, y, z, blockID, metadata);
        }*/

        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block == null || (block != Block.endPortalFrame && !Block.opaqueCubeLookup[world.getBlockId(x, y, z)]))
            world.setBlock(x, y, z, this, metadata, 2);
        else
        {
            for (int iter = 0; iter < replaceBlocks.length; iter++)
            {
                if (block.isGenMineableReplaceable(world, x, y, z, replaceBlocks[iter]))
                {
                    world.setBlock(x, y, z, this, metadata, 2);
                    break;
                }
            }
        }

    }
}
