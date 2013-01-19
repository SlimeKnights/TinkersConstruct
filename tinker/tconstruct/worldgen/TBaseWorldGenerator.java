package tinker.tconstruct.worldgen;

import java.util.Random;

import tinker.tconstruct.TConstructContent;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class TBaseWorldGenerator 
	implements IWorldGenerator
{
	public TBaseWorldGenerator()
	{
		cobalt = new ManhattanOreGenerator(TConstructContent.ores.blockID, 0, 2, 4, 100, Block.netherrack.blockID);
		ardite = new ManhattanOreGenerator(TConstructContent.ores.blockID, 1, 2, 4, 100, Block.netherrack.blockID);
	}
	
	@Override
	public void generate (Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if (world.provider.dimensionId == -1)
			generateNether(random, chunkX*16, chunkZ*16, world);
	}
	
	void generateNether(Random random, int xChunk, int zChunk, World world)
	{
		for (int i = 0; i < 6; i++)
		{
			int xPos = xChunk + random.nextInt(16), yPos = random.nextInt(64), zPos = zChunk + random.nextInt(16);
			cobalt.generate(world, random, xPos, yPos, zPos);
			xPos = xChunk + random.nextInt(16); yPos = random.nextInt(64)+32; zPos = zChunk + random.nextInt(16);
			ardite.generate(world, random, xPos, yPos, zPos);
		}
		for (int i = 0; i < 6; i++)
		{
			int xPos = xChunk + random.nextInt(16), yPos = random.nextInt(128), zPos = zChunk + random.nextInt(16);
			cobalt.generate(world, random, xPos, yPos, zPos);
			xPos = xChunk + random.nextInt(16); yPos = random.nextInt(128); zPos = zChunk + random.nextInt(16);
			ardite.generate(world, random, xPos, yPos, zPos);
		}
	}

	ManhattanOreGenerator cobalt;
	ManhattanOreGenerator ardite;
}
