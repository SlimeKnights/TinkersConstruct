package tconstruct.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import tconstruct.common.TRepo;
import tconstruct.util.config.PHConstruct;
import cpw.mods.fml.common.IWorldGenerator;

public class TBaseWorldGenerator implements IWorldGenerator
{
    public TBaseWorldGenerator()
    {
        copper = new WorldGenMinable(TRepo.oreSlag, 3, 8, Block.stone);
        tin = new WorldGenMinable(TRepo.oreSlag, 4, 8, Block.stone);
        aluminum = new WorldGenMinable(TRepo.oreSlag, 5, 6, Block.stone);

        cobalt = new WorldGenMinable(TRepo.oreSlag, 1, 3, Block.netherrack);
        ardite = new WorldGenMinable(TRepo.oreSlag, 2, 3, Block.netherrack);

        ironBush = new OreberryBushGen(TRepo.oreBerry, 12, 12);
        goldBush = new OreberryBushGen(TRepo.oreBerry, 13, 6);
        copperBush = new OreberryBushGen(TRepo.oreBerry, 14, 12);
        tinBush = new OreberryBushGen(TRepo.oreBerry, 15, 12);
        aluminumBush = new OreberryBushGen(TRepo.oreBerrySecond, 12, 14);
        silverBush = new OreberryBushGen(TRepo.oreBerrySecond, 13, 8);
    }

    @Override
    public void generate (Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        if (world.provider.isHellWorld)
        {
            generateNether(random, chunkX * 16, chunkZ * 16, world);
        }
        else if (world.provider.terrainType != WorldType.FLAT)
        {
            generateSurface(random, chunkX * 16, chunkZ * 16, world);
            if (world.provider.dimensionId == 0)
                generateOreBushes(random, chunkX * 16, chunkZ * 16, world);
        }

        if (PHConstruct.superfunWorld && world.provider.dimensionId == 0)
        {
            superfunGenerate(random, chunkX * 16, chunkZ * 16, world);
        }

        if (PHConstruct.worldBorder)
        {
            generateChunkBorder(random, chunkX * 16, chunkZ * 16, world);
        }
    }

    void generateSurface (Random random, int xChunk, int zChunk, World world)
    {
        String biomeName = world.getWorldChunkManager().getBiomeGenAt(xChunk, zChunk).biomeName;

        generateUndergroundOres(random, xChunk, zChunk, world);

        if (biomeName == "Extreme Hills Edge" || biomeName == "Extreme Hills")
        {
            generateUndergroundOres(random, xChunk, zChunk, world);
        }
    }

    void generateUndergroundOres (Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        if (PHConstruct.generateCopper)
        {
            for (int q = 0; q <= PHConstruct.copperuDensity; q++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.copperuMinY + random.nextInt(PHConstruct.copperuMaxY - PHConstruct.copperuMinY);
                zPos = zChunk + random.nextInt(16);
                copper.generate(world, random, xPos, yPos, zPos);
            }
        }
        if (PHConstruct.generateTin)
        {
            for (int q = 0; q <= PHConstruct.tinuDensity; q++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.tinuMinY + random.nextInt(PHConstruct.tinuMaxY - PHConstruct.tinuMinY);
                zPos = zChunk + random.nextInt(16);
                tin.generate(world, random, xPos, yPos, zPos);
            }
        }
        if (PHConstruct.generateAluminum)
        {
            for (int q = 0; q <= PHConstruct.aluminumuDensity; q++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.aluminumuMinY + random.nextInt(PHConstruct.aluminumuMaxY - PHConstruct.aluminumuMinY);
                zPos = zChunk + random.nextInt(16);
                aluminum.generate(world, random, xPos, yPos, zPos);
            }
        }
    }

    void generateOreBushes (Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        if (PHConstruct.generateIronBush && random.nextInt(PHConstruct.ironBushRarity + 1) == 0)
        {
            for (int i = 0; i < PHConstruct.ironBushDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.seaLevel - 32;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, PHConstruct.seaLevel, 0);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    TConstruct.logger.info("Iron: "+coord.toString());*/
                    ironBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateGoldBush && random.nextInt(PHConstruct.goldBushRarity + 1) == 0)
        {
            for (int i = 0; i < PHConstruct.goldBushDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = 16;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 32, 0);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    TConstruct.logger.info("Gold: "+coord.toString());*/
                    goldBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateCopperBush && random.nextInt(PHConstruct.copperBushRarity + 1) == 0)// && random.nextInt(PHConstruct.copperbRarity) == 0)
        {
            for (int i = 0; i < PHConstruct.copperBushDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = (PHConstruct.copperBushMaxY + PHConstruct.copperBushMinY) / 2;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, PHConstruct.copperBushMaxY, PHConstruct.copperBushMinY);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    TConstruct.logger.info("Copper: "+coord.toString());*/
                    copperBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateTinBush && random.nextInt(PHConstruct.tinBushRarity + 1) == 0)// && random.nextInt(PHConstruct.tinbRarity) == 0)
        {
            for (int i = 0; i < PHConstruct.tinBushDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = (PHConstruct.tinBushMaxY + PHConstruct.tinBushMinY) / 2;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, PHConstruct.tinBushMaxY, PHConstruct.tinBushMinY);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    TConstruct.logger.info("Tin: "+coord.toString());*/
                    tinBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateAluminumBush && random.nextInt(PHConstruct.aluminumBushRarity + 1) == 0)// && random.nextInt(PHConstruct.aluminumbRarity) == 0)
        {
            for (int i = 0; i < PHConstruct.aluminumBushDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = (PHConstruct.aluminumBushMaxY + PHConstruct.aluminumBushMinY) / 2;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, PHConstruct.aluminumBushMaxY, PHConstruct.aluminumBushMinY);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    TConstruct.logger.info("Aluminum: "+coord.toString());*/
                    aluminumBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateEssenceBush && random.nextInt(PHConstruct.essenceBushRarity) == 0)
        {
            for (int i = 0; i < PHConstruct.silverBushDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.seaLevel - 16;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 32, 0);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    TConstruct.logger.info("Silver: "+coord.toString());*/
                    silverBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
    }

    void generateNether (Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        if (PHConstruct.generateNetherOres)
        {
            for (int i = 0; i < PHConstruct.netherDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = random.nextInt(64) + 32;
                zPos = zChunk + random.nextInt(16);
                cobalt.generate(world, random, xPos, yPos, zPos);

                xPos = xChunk + random.nextInt(16);
                yPos = random.nextInt(64) + 32;
                zPos = zChunk + random.nextInt(16);
                ardite.generate(world, random, xPos, yPos, zPos);

            }
            for (int i = 0; i < PHConstruct.netherDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = random.nextInt(128);
                zPos = zChunk + random.nextInt(16);
                cobalt.generate(world, random, xPos, yPos, zPos);

                xPos = xChunk + random.nextInt(16);
                yPos = random.nextInt(128);
                zPos = zChunk + random.nextInt(16);
                ardite.generate(world, random, xPos, yPos, zPos);
            }
        }
    }

    int findAdequateLocation (World world, int x, int y, int z, int heightLimit, int depthLimit)
    {
        int height = y;
        do
        {
            if (world.getBlockId(x, height, z) == 0 && world.getBlockId(x, height + 1, z) != 0)
                return height + 1;
            height++;
        } while (height < heightLimit);

        height = y;
        do
        {
            if (world.getBlockId(x, height, z) == 0 && world.getBlockId(x, height - 1, z) != 0)
                return height - 1;
            height--;
        } while (height > depthLimit);

        return -1;
    }

    void superfunGenerate (Random random, int chunkX, int chunkZ, World world)
    {
        /*for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++)
                world.setBlock(x+chunkX, 192, z+chunkZ, Block.glowStone);*/

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                for (int y = 0; y < 128; y++)
                {
                    int blockID = world.getBlockId(x + chunkX, y, z + chunkZ);
                    Block block = Block.blocksList[blockID];
                    if (block != null)
                    {
                        if (block.func_149688_o() == Material.leaves)
                            world.setBlock(x + chunkX, y, z + chunkZ, Blocks.lava, 0, 0);
                        if (block.func_149688_o() == Material.wood)
                            world.setBlock(x + chunkX, y, z + chunkZ, Blocks.netherrack, 0, 0);
                        if (block == Blocks.stone)
                            world.setBlock(x + chunkX, y, z + chunkZ, Blocks.end_stone, 0, 0);
                        if (y > 40 && (block.func_149688_o() == Material.ground || block.func_149688_o() == Material.grass))
                            world.setBlock(x + chunkX, y, z + chunkZ, Blocks.soul_sand, 0, 0);
                        if (block.func_149688_o() == Material.sand)
                            world.setBlock(x + chunkX, y, z + chunkZ, Block.silverfish, 0, 0);
                    }
                }
            }
        }
    }

    void generateChunkBorder (Random random, int chunkX, int chunkZ, World world)
    {
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                if (x + chunkX == PHConstruct.worldBorderSize || x + chunkX == -PHConstruct.worldBorderSize || z + chunkZ == PHConstruct.worldBorderSize || z + chunkZ == -PHConstruct.worldBorderSize)
                {
                    for (int y = 0; y < 256; y++)
                    {
                        world.setBlock(x + chunkX, y, z + chunkZ, Blocks.bedrock, 0, 0);
                    }
                }
            }
        }
    }

    WorldGenMinable copper;
    WorldGenMinable tin;
    WorldGenMinable aluminum;
    WorldGenMinable cobalt;
    WorldGenMinable ardite;

    OreberryBushGen ironBush;
    OreberryBushGen goldBush;
    OreberryBushGen copperBush;
    OreberryBushGen tinBush;
    OreberryBushGen aluminumBush;
    OreberryBushGen silverBush;
}
