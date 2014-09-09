package tconstruct.world.gen;

import cpw.mods.fml.common.IWorldGenerator;
import java.awt.geom.Ellipse2D;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.*;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.feature.WorldGenerator;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.*;
import tconstruct.world.TinkerWorld;

public class SlimeIslandGen extends WorldGenerator implements IWorldGenerator
{
    private Block liquidBlock;
    private int gelMeta;
    int randomness = 2;
    Random random = new Random();
    Block base = TinkerTools.craftedSoil;// Block.dirt.blockID;
    Block top = TinkerWorld.slimeGrass;
    SlimeTreeGen trees = new SlimeTreeGen(false, 5, 4, 1, 0);

    public SlimeIslandGen(Block slimePool, int meta)
    {
        this.liquidBlock = slimePool;
        this.gelMeta = meta;
    }

    @Override
    public void generate (Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) // IWorldGenerator
                                                                                                                                           // version
    {
        // dim 0 only?
        if ((chunkGenerator instanceof ChunkProviderFlat || world.provider.terrainType == WorldType.FLAT) && PHConstruct.genIslandsFlat)
        {
            return;
        }

        if (DimensionBlacklist.isDimInBlacklist(world.provider.dimensionId))
        {
            if (random.nextInt(PHConstruct.islandRarity) == 0)
                generateIsland(world, random, chunkX * 16, chunkZ * 16);
        }
    }

    // Island is biased towards one direction. Quadrants would be more useful
    public void generateIsland (World world, Random rand, int xChunk, int zChunk)
    {
        int xRange = random.nextInt(13) + 20;
        int yCenter = 50 + world.getHeightValue(xChunk, zChunk) + random.nextInt(50);
        int zRange = random.nextInt(13) + 20;
        int height = 12;
        int initialHeight = height;
        Ellipse2D.Double ellipse = new Ellipse2D.Double(0, 0, xRange, zRange);

        // Basic shape
        for (int x = 0; x <= xRange; x++)
        {
            for (int z = 0; z <= zRange; z++)
            {
                for (int y = 0; y <= height; y++)
                {
                    if (ellipse.contains(x, z))
                        world.setBlock(x + xChunk, y + yCenter, z + zChunk, base, 5, 0);
                }
            }
        }

        // Erode bottom
        height = 8;
        for (int x = 0; x <= xRange; x++)
        {
            for (int z = 0; z <= zRange; z++)
            {
                for (int y = 0; y >= -height; y--)
                {
                    int xPos = x + xChunk;
                    int yPos = y + yCenter + height;
                    int zPos = z + zChunk;
                    if (world.getBlock(xPos - 1, yPos + 1, zPos) == base && world.getBlock(xPos + 1, yPos + 1, zPos) == base && world.getBlock(xPos, yPos + 1, zPos - 1) == base && world.getBlock(xPos - 1, yPos + 1, zPos + 1) == base && random.nextInt(100) > randomness)
                    {
                        ;
                    }
                    else
                    {
                        world.setBlock(xPos, yPos, zPos, Blocks.air, 0, 0);

                    }
                }
            }
        }

        // Erode top
        height = 3;
        for (int x = 0; x <= xRange; x++)
        {
            for (int z = 0; z <= zRange; z++)
            {
                for (int y = 1; y <= height; y++)
                {
                    int xPos = x + xChunk;
                    int yPos = y + yCenter + initialHeight - height + 1;
                    int zPos = z + zChunk;
                    if (world.getBlock(xPos - 1, yPos - 1, zPos) == base && world.getBlock(xPos + 1, yPos - 1, zPos) == base && world.getBlock(xPos, yPos - 1, zPos - 1) == base && world.getBlock(xPos - 1, yPos - 1, zPos + 1) == base)
                    {
                        ;
                    }
                    else
                    {
                        world.setBlock(xPos, yPos, zPos, Blocks.air, 0, 0);
                    }
                }
            }
        }

        // Replace blocks
        for (int x = 0; x <= xRange; x++)
        {
            for (int z = 0; z <= zRange; z++)
            {
                for (int y = 0; y <= height; y++)
                {
                    int xPos = x + xChunk;
                    int yPos = y + yCenter + initialHeight - height;
                    int zPos = z + zChunk;
                    if (world.getBlock(xPos, yPos, zPos) == base)
                    {
                        Block block = world.getBlock(xPos, yPos + 1, zPos);
                        if (block == null || block == Blocks.air)
                            world.setBlock(xPos, yPos, zPos, top, 0, 0);
                    }
                }
            }
        }

        // Decorate
        if (!DimensionBlacklist.isDimNoPool(world.provider.dimensionId))
        {
            generateSlimePool(world, rand, xChunk + xRange / 2, yCenter + initialHeight, zChunk + zRange / 2);
        }
        PlantGen tallGrass = new PlantGen(TinkerWorld.slimeTallGrass, 0, 128, xRange, 1, zRange, false);
        tallGrass.generate(world, rand, xChunk, yCenter + initialHeight + 1, zChunk);
        for (int i = 0; i < 3; i++)
        {
            trees.generate(world, random, xChunk + random.nextInt(xRange), yCenter + initialHeight + 3, zChunk + random.nextInt(zRange));
        }
    }

    public void generateSlimePool (World world, Random rand, int x, int y, int z)
    {
        this.generate(world, rand, x, y, z);

    }

    @Override
    public boolean generate (World world, Random rand, int x, int y, int z) // WorldGenerator
                                                                            // version
    {
        x -= 8;
        z -= 8;
        /*
         * for (z -= 8; y > 5 && world.isAirBlock(x, y, z); --y) { ; }
         * 
         * if (y <= 4) { return false; } else {
         */
        y -= 4;
        boolean[] validLocations = new boolean[2048];
        int var7 = rand.nextInt(4) + 4;
        int xPos;

        for (xPos = 0; xPos < var7; ++xPos)
        {
            double noise1 = rand.nextDouble() * 6.0D + 3.0D;
            double noise2 = rand.nextDouble() * 4.0D + 2.0D;
            double noise3 = rand.nextDouble() * 6.0D + 3.0D;
            double noise4 = rand.nextDouble() * (16.0D - noise1 - 2.0D) + 1.0D + noise1 / 2.0D;
            double noise5 = rand.nextDouble() * (8.0D - noise2 - 4.0D) + 2.0D + noise2 / 2.0D;
            double noise6 = rand.nextDouble() * (16.0D - noise3 - 2.0D) + 1.0D + noise3 / 2.0D;

            for (int xIter = 1; xIter < 15; ++xIter)
            {
                for (int zIter = 1; zIter < 15; ++zIter)
                {
                    for (int yIter = 1; yIter < 7; ++yIter)
                    {
                        double var24 = ((double) xIter - noise4) / (noise1 / 2.0D);
                        double var26 = ((double) yIter - noise5) / (noise2 / 2.0D);
                        double var28 = ((double) zIter - noise6) / (noise3 / 2.0D);
                        double validSpot = var24 * var24 + var26 * var26 + var28 * var28;

                        if (validSpot < 1.0D)
                        {
                            validLocations[(xIter * 16 + zIter) * 8 + yIter] = true;
                        }
                    }
                }
            }
        }

        int yPos;
        int zPos;
        boolean var33;

        for (xPos = 0; xPos < 16; ++xPos)
        {
            for (zPos = 0; zPos < 16; ++zPos)
            {
                for (yPos = 0; yPos < 8; ++yPos)
                {
                    var33 = !validLocations[(xPos * 16 + zPos) * 8 + yPos] && (xPos < 15 && validLocations[((xPos + 1) * 16 + zPos) * 8 + yPos] || xPos > 0 && validLocations[((xPos - 1) * 16 + zPos) * 8 + yPos] || zPos < 15 && validLocations[(xPos * 16 + zPos + 1) * 8 + yPos] || zPos > 0 && validLocations[(xPos * 16 + (zPos - 1)) * 8 + yPos] || yPos < 7 && validLocations[(xPos * 16 + zPos) * 8 + yPos + 1] || yPos > 0 && validLocations[(xPos * 16 + zPos) * 8 + (yPos - 1)]);

                    if (var33)
                    {
                        Material var12 = world.getBlock(x + xPos, y + yPos, z + zPos).getMaterial();

                        if (yPos >= 4 && var12.isLiquid())
                        {
                            return false;
                        }

                        if (yPos < 4 && !var12.isSolid() && world.getBlock(x + xPos, y + yPos, z + zPos) != this.liquidBlock)
                        {
                            return false;
                        }
                    }
                }
            }
        }

        for (xPos = 0; xPos < 16; ++xPos)
        {
            for (zPos = 0; zPos < 16; ++zPos)
            {
                for (yPos = 0; yPos < 8; ++yPos)
                {
                    if (validLocations[(xPos * 16 + zPos) * 8 + yPos])
                    {
                        world.setBlock(x + xPos, y + yPos, z + zPos, yPos >= 4 ? Blocks.air : this.liquidBlock);
                    }
                }
            }
        }

        for (xPos = 0; xPos < 16; ++xPos)
        {
            for (zPos = 0; zPos < 16; ++zPos)
            {
                for (yPos = 4; yPos < 8; ++yPos)
                {
                    if (validLocations[(xPos * 16 + zPos) * 8 + yPos] && world.getBlock(x + xPos, y + yPos - 1, z + zPos) == base && world.getSavedLightValue(EnumSkyBlock.Sky, x + xPos, y + yPos, z + zPos) > 0)
                    {
                        world.setBlock(x + xPos, y + yPos - 1, z + zPos, top, 0, 0);
                    }
                }
            }
        }

        // Generate blocks around
        if (this.liquidBlock.getMaterial() == Material.water)
        {
            for (xPos = 0; xPos < 16; ++xPos)
            {
                for (zPos = 0; zPos < 16; ++zPos)
                {
                    for (yPos = 0; yPos < 8; ++yPos)
                    {
                        var33 = !validLocations[(xPos * 16 + zPos) * 8 + yPos] && (xPos < 15 && validLocations[((xPos + 1) * 16 + zPos) * 8 + yPos] || xPos > 0 && validLocations[((xPos - 1) * 16 + zPos) * 8 + yPos] || zPos < 15 && validLocations[(xPos * 16 + zPos + 1) * 8 + yPos] || zPos > 0 && validLocations[(xPos * 16 + (zPos - 1)) * 8 + yPos] || yPos < 7 && validLocations[(xPos * 16 + zPos) * 8 + yPos + 1] || yPos > 0 && validLocations[(xPos * 16 + zPos) * 8 + (yPos - 1)]);

                        if (var33 && (yPos < 4 || rand.nextInt(2) != 0) && world.getBlock(x + xPos, y + yPos, z + zPos).getMaterial().isSolid() && world.getBlock(x + xPos, y + yPos + 1, z + zPos).getMaterial() != Material.water)
                        {
                            world.setBlock(x + xPos, y + yPos, z + zPos, TinkerWorld.slimeGel, gelMeta, 2);
                        }
                    }
                }
            }
        }

        return true;
        // }
    }
}
