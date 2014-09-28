package tconstruct.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class PlantGen extends WorldGenerator
{
    /** The ID of the plant block used in this plant generator. */
    public final Block plant;
    public final int metadata;
    public int chances = 64;
    int xSize = 8;
    int ySize = 4;
    int zSize = 8;
    boolean clumped = true;

    public PlantGen(Block b, int meta)
    {
        this.plant = b;
        this.metadata = meta;
    }

    public PlantGen(Block b, int meta, int count, int range, int height, int width, boolean clumped)
    {
        this.plant = b;
        this.metadata = meta;
        this.chances = count;
        this.xSize = range;
        this.ySize = height;
        this.zSize = width;
        this.clumped = clumped;
    }

    @Override
    public boolean generate (World world, Random random, int x, int y, int z)
    {
        for (int iter = 0; iter < chances; ++iter)
        {
            int posX = clumped ? x + random.nextInt(xSize) - random.nextInt(xSize) : x + random.nextInt(xSize);
            int posY = clumped ? y + random.nextInt(ySize) - random.nextInt(ySize) : y + random.nextInt(ySize);
            int posZ = clumped ? z + random.nextInt(zSize) - random.nextInt(zSize) : z + random.nextInt(zSize);

            if (world.getBlock(posX, posY, posZ) == Blocks.air && (!world.provider.hasNoSky || posY < 127) && this.plant.canBlockStay(world, posX, posY, posZ))
            {
                world.setBlock(posX, posY, posZ, this.plant, this.metadata, 2);
            }
        }

        return true;
    }
}