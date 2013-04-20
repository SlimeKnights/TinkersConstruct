package mods.tinker.tconstruct.worldgen;

import java.util.Random;

import mods.natura.common.PHNatura;
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

        ironSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 0, 12);
        goldSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 1, 12);
        copperSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 2, 12);
        tinSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 3, 12);
        aluminumSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 4, 12);
        cobaltSurface = new SurfaceOreGen(TContent.oreGravel.blockID, 4, 20);

        ironBush = new OreberryBushGen(TContent.oreBerry, 0);
        goldBush = new OreberryBushGen(TContent.oreBerry, 1);
        copperBush = new OreberryBushGen(TContent.oreBerry, 2);
        tinBush = new OreberryBushGen(TContent.oreBerry, 3);
        aluminumBush = new OreberryBushGen(TContent.oreBerrySecond, 0);
        silverBush = new OreberryBushGen(TContent.oreBerrySecond, 1);
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
    
    void generateUndergroundOres(Random random, int xChunk, int zChunk, World world)
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
    
    void generateSurfaceOres(Random random, int xChunk, int zChunk, World world)
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
    
    void generateOreBushes(Random random, int xChunk, int zChunk, World world)
    {
        int xPos, yPos, zPos;
        if (PHConstruct.generateIronBush && random.nextInt(PHConstruct.ironbRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            ironBush.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateGoldBush && random.nextInt(PHConstruct.goldbRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            goldBush.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateCopperBush && random.nextInt(PHConstruct.copperbRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            copperBush.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateTinBush && random.nextInt(PHConstruct.tinbRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            tinBush.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateAluminumBush && random.nextInt(PHConstruct.aluminumbRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            aluminumBush.generate(world, random, xPos, yPos, zPos);
        }
        if (PHConstruct.generateSilverBush && random.nextInt(PHConstruct.silverbRarity) == 0)
        {
            xPos = xChunk + random.nextInt(16);
            yPos = 64 + PHConstruct.seaLevel;
            zPos = zChunk + random.nextInt(16);
            silverBush.generate(world, random, xPos, yPos, zPos);
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
