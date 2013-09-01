package tconstruct.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class PlantGen extends WorldGenerator
{
    /** The ID of the plant block used in this plant generator. */
    public final int plantID;
    public final int metadata;
    public int chances = 64;
    int xSize = 8;
    int ySize = 4;
    int zSize = 8;
    boolean clumped = true;

    public PlantGen(int id, int meta)
    {
        this.plantID = id;
        this.metadata = meta;
    }

    public PlantGen(int id, int meta, int count, int range, int height, int width, boolean clumped)
    {
        this.plantID = id;
        this.metadata = meta;
        this.chances = count;
        this.xSize = range;
        this.ySize = height;
        this.zSize = width;
        this.clumped = clumped;
    }

    public boolean generate (World world, Random random, int x, int y, int z)
    {
        for (int iter = 0; iter < chances; ++iter)
        {
            int posX = clumped ? x + random.nextInt(xSize) - random.nextInt(xSize) : x + random.nextInt(xSize);
            int posY = clumped ? y + random.nextInt(ySize) - random.nextInt(ySize) : y + random.nextInt(ySize);
            int posZ = clumped ? z + random.nextInt(zSize) - random.nextInt(zSize) : z + random.nextInt(zSize);

            if (world.isAirBlock(posX, posY, posZ) && (!world.provider.hasNoSky || posY < 127) && Block.blocksList[this.plantID].canBlockStay(world, posX, posY, posZ))
            {
                world.setBlock(posX, posY, posZ, this.plantID, this.metadata, 2);
            }
        }

        return true;
    }
}