package mods.tinker.tconstruct.worldgen;

import java.util.Random;

import mods.tinker.common.CoordTuple;
import mods.tinker.tconstruct.PHConstruct;
import mods.tinker.tconstruct.TContent;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class TBaseWorldGenerator implements IWorldGenerator
{
    public TBaseWorldGenerator()
    {
        copper = new WorldGenMinable(TContent.oreSlag.blockID, 3, 8, Block.stone.blockID);
        tin = new WorldGenMinable(TContent.oreSlag.blockID, 4, 8, Block.stone.blockID);
        aluminum = new WorldGenMinable(TContent.oreSlag.blockID, 5, 6, Block.stone.blockID);

        cobalt = new WorldGenMinable(TContent.oreSlag.blockID, 1, 3, Block.netherrack.blockID);
        ardite = new WorldGenMinable(TContent.oreSlag.blockID, 2, 3, Block.netherrack.blockID);

        ironSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 0, 12, true);
        goldSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 1, 20, true);
        copperSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 2, 12, true);
        tinSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 3, 12, true);
        aluminumSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 4, 12, true);
        cobaltSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 4, 30, true);


        ironBush = new OreberryBushGen(TContent.oreBerry.blockID, 12, 12);
        goldBush = new OreberryBushGen(TContent.oreBerry.blockID, 13, 6);
        copperBush = new OreberryBushGen(TContent.oreBerry.blockID, 14, 12);
        tinBush = new OreberryBushGen(TContent.oreBerry.blockID, 15, 12);
        aluminumBush = new OreberryBushGen(TContent.oreBerrySecond.blockID, 12, 14);
        silverBush = new OreberryBushGen(TContent.oreBerrySecond.blockID, 13, 8);
    }

    @Override
    public void generate (Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        if (world.provider.isHellWorld)
            generateNether(random, chunkX * 16, chunkZ * 16, world);
        else
            generateSurface(random, chunkX * 16, chunkZ * 16, world);
    }

    void generateSurface (Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        String biomeName = world.getWorldChunkManager().getBiomeGenAt(xChunk, zChunk).biomeName;

        generateUndergroundOres(random, xChunk, zChunk, world);
        generateSurfaceOres(random, xChunk, zChunk, world);
        generateOreBushes(random, xChunk, zChunk, world);

        if (biomeName == "Extreme Hills Edge" || biomeName == "Extreme Hills")
        {
            generateUndergroundOres(random, xChunk, zChunk, world);
            generateSurfaceOres(random, xChunk, zChunk, world);
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
                yPos = PHConstruct.copperuHeight + random.nextInt(PHConstruct.copperuRange - PHConstruct.copperuHeight);
                zPos = zChunk + random.nextInt(16);
                copper.generate(world, random, xPos, yPos, zPos);
            }
        }
        if (PHConstruct.generateTin)
        {
            for (int q = 0; q <= PHConstruct.tinuDensity; q++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.tinuHeight + random.nextInt(PHConstruct.tinuRange - PHConstruct.tinuHeight);
                zPos = zChunk + random.nextInt(16);
                tin.generate(world, random, xPos, yPos, zPos);
            }
        }
        if (PHConstruct.generateAluminum)
        {
            for (int q = 0; q <= PHConstruct.aluminumuDensity; q++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.aluminumuHeight + random.nextInt(PHConstruct.aluminumuRange - PHConstruct.aluminumuHeight);
                zPos = zChunk + random.nextInt(16);
                aluminum.generate(world, random, xPos, yPos, zPos);
            }
        }
    }

    void generateSurfaceOres (Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        if (PHConstruct.generateIronSurface && random.nextInt(PHConstruct.ironsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            ironSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateGoldSurface && random.nextInt(PHConstruct.goldsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            goldSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateCopperSurface && random.nextInt(PHConstruct.coppersRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            copperSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateTinSurface && random.nextInt(PHConstruct.tinsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            tinSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateAluminumSurface && random.nextInt(PHConstruct.aluminumsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            aluminumSurface.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateCobaltSurface && random.nextInt(PHConstruct.cobaltsRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            cobaltSurface.generate(world, random, xPos, yPos, zPos);
        }
    }

    void generateOreBushes (Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        if (PHConstruct.generateIronBush)
        {
            for (int i = 0; i < PHConstruct.ironbDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.seaLevel - 32;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, PHConstruct.seaLevel, 0);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    System.out.println("Iron: "+coord.toString());*/
                    ironBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateGoldBush)
        {
            for (int i = 0; i < PHConstruct.goldbDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = 16;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 32, 0);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    System.out.println("Gold: "+coord.toString());*/
                    goldBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateCopperBush)// && random.nextInt(PHConstruct.copperbRarity) == 0)
        {
            for (int i = 0; i < PHConstruct.copperbDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = (PHConstruct.copperuRange + PHConstruct.copperuHeight) / 2;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, PHConstruct.copperuHeight, PHConstruct.tinuRange);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    System.out.println("Copper: "+coord.toString());*/
                    copperBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateTinBush)// && random.nextInt(PHConstruct.tinbRarity) == 0)
        {
            for (int i = 0; i < PHConstruct.tinbDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = (PHConstruct.tinuRange + PHConstruct.tinuHeight) / 2;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, PHConstruct.tinuRange, PHConstruct.tinuHeight);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    System.out.println("Tin: "+coord.toString());*/
                    tinBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateAluminumBush)// && random.nextInt(PHConstruct.aluminumbRarity) == 0)
        {
            for (int i = 0; i < PHConstruct.aluminumbDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = (PHConstruct.aluminumuRange + PHConstruct.aluminumuHeight) / 2;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, PHConstruct.aluminumuRange, PHConstruct.aluminumuHeight);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    System.out.println("Aluminum: "+coord.toString());*/
                    aluminumBush.generate(world, random, xPos, yPos, zPos);
                }
            }
        }
        if (PHConstruct.generateSilverBush && random.nextInt(PHConstruct.silverbDensity) == 0)
        {
            for (int i = 0; i < PHConstruct.silverbDensity; i++)
            {
                xPos = xChunk + random.nextInt(16);
                yPos = PHConstruct.seaLevel - 16;
                zPos = zChunk + random.nextInt(16);
                yPos = findAdequateLocation(world, xPos, yPos, zPos, 32, 0);
                if (yPos != -1)
                {
                    /*CoordTuple coord = new CoordTuple(xPos, yPos, zPos);
                    System.out.println("Silver: "+coord.toString());*/
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

    WorldGenMinable copper;
    WorldGenMinable tin;
    WorldGenMinable aluminum;
    WorldGenMinable cobalt;
    WorldGenMinable ardite;

    SurfaceOreGen ironSurface;
    SurfaceOreGen goldSurface;
    SurfaceOreGen copperSurface;
    SurfaceOreGen tinSurface;
    SurfaceOreGen aluminumSurface;
    SurfaceOreGen cobaltSurface;

    OreberryBushGen ironBush;
    OreberryBushGen goldBush;
    OreberryBushGen copperBush;
    OreberryBushGen tinBush;
    OreberryBushGen aluminumBush;
    OreberryBushGen silverBush;
}
