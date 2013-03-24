package mods.tinker.tconstruct.worldgen;

import java.util.Random;

import mods.tinker.tconstruct.PHConstruct;
import mods.tinker.tconstruct.TContent;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class TBaseWorldGenerator 
	implements IWorldGenerator
{
	public TBaseWorldGenerator()
	{
		//copper = new ManhattanOreGenerator(TContent.oreSlag.blockID, 3, 5, 10, 100, true, true);
		//tin = new ManhattanOreGenerator(TContent.oreSlag.blockID, 4, 5, 10, 100, true, true);
		//aluminum = new ManhattanOreGenerator(TContent.oreSlag.blockID, 5, 8, 20, 100, false, true);
		
		//cobalt = new ManhattanOreGenerator(TContent.oreSlag.blockID, 1, 2, 4, 100, true, false, Block.netherrack.blockID);
		//ardite = new ManhattanOreGenerator(TContent.oreSlag.blockID, 2, 2, 4, 100, true, false, Block.netherrack.blockID);
		copper = new WorldGenMinable(TContent.oreSlag.blockID, 3, 9, Block.stone.blockID);
		tin = new WorldGenMinable(TContent.oreSlag.blockID, 4, 9, Block.stone.blockID);
		aluminum = new WorldGenMinable(TContent.oreSlag.blockID, 5, 16, Block.stone.blockID);
		
		cobalt = new WorldGenMinable(TContent.oreSlag.blockID, 1, 3, Block.netherrack.blockID);
		ardite = new WorldGenMinable(TContent.oreSlag.blockID, 2, 3, Block.netherrack.blockID);
	}
	
	@Override
	public void generate (Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if (world.provider.dimensionId == -1)
			generateNether(random, chunkX*16, chunkZ*16, world);
		else
			generateSurface(random, chunkX*16, chunkZ*16, world);
	}
	
	void generateSurface(Random random, int xChunk, int zChunk, World world)
	{
		int heightBand;
		int xPos, yPos, zPos;
		if (PHConstruct.generateCopper)
		{
			for (int q = 0; q < PHConstruct.copperDensity; q++)
			{
				xPos = xChunk + random.nextInt(16); yPos = PHConstruct.copperHeight + random.nextInt(PHConstruct.copperRange); zPos = zChunk + random.nextInt(16);
				copper.generate(world, random, xPos, yPos, zPos);
			}
		}
		if (PHConstruct.generateTin)
		{
			for (int q = 0; q < PHConstruct.tinDensity; q++)
			{
				xPos = xChunk + random.nextInt(16); yPos = PHConstruct.tinHeight + random.nextInt(PHConstruct.tinRange); zPos = zChunk + random.nextInt(16);
				tin.generate(world, random, xPos, yPos, zPos);
			}
		}
		if (PHConstruct.generateAluminum)
		{
			for (int q = 0; q < PHConstruct.aluminumDensity; q++)
			{
				xPos = xChunk + random.nextInt(16); yPos = PHConstruct.aluminumHeight + random.nextInt(PHConstruct.aluminumRange); zPos = zChunk + random.nextInt(16);
				aluminum.generate(world, random, xPos, yPos, zPos);
			}
		}
	}
	
	void generateNether(Random random, int xChunk, int zChunk, World world)
	{
		int xPos, yPos, zPos;
		for (int i = 0; i < PHConstruct.netherDensity; i++)
		{
			if (PHConstruct.generateCobalt)
			{
				xPos = xChunk + random.nextInt(16); yPos = random.nextInt(64); zPos = zChunk + random.nextInt(16);
				cobalt.generate(world, random, xPos, yPos, zPos);
			}
			if (PHConstruct.generateArdite)
			{
				xPos = xChunk + random.nextInt(16); yPos = random.nextInt(64)+32; zPos = zChunk + random.nextInt(16);
				ardite.generate(world, random, xPos, yPos, zPos);
			}
		}
		for (int i = 0; i < PHConstruct.netherDensity; i++)
		{
			if (PHConstruct.generateCobalt)
			{
				xPos = xChunk + random.nextInt(16); yPos = random.nextInt(128); zPos = zChunk + random.nextInt(16);
				cobalt.generate(world, random, xPos, yPos, zPos);
			}
			if (PHConstruct.generateArdite)
			{
				xPos = xChunk + random.nextInt(16); yPos = random.nextInt(128); zPos = zChunk + random.nextInt(16);
				ardite.generate(world, random, xPos, yPos, zPos);
			}
		}
	}
	
	WorldGenMinable copper;
	WorldGenMinable tin;
	WorldGenMinable aluminum;
	WorldGenMinable cobalt;
	WorldGenMinable ardite;
}
