package tinker.tconstruct.worldgen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import tinker.tconstruct.util.SortedList;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

/* Metallurgy ore generator
 * Specially given by RebelKeithy!
 */

public class ManhattanOreGenerator extends WorldGenerator
{
	private List<Integer> replaceableBlocks;
	private int numberOfBlocks;
	private int sizeVariance;
	private int metadata;
	private int minableBlockId;
	private int density;
	private boolean generateLines;
	private boolean checkGenMinable;
	
	public ManhattanOreGenerator(int id, int meta, int minSize, int maxSize, int dense, boolean lines, boolean replaceGenMinable, Object... replacableIDs)
	{
		minableBlockId = id;
		metadata = meta;
		numberOfBlocks = minSize;
		sizeVariance = maxSize - minSize + 1;
		density = dense;
		replaceableBlocks = new ArrayList<Integer>();
		generateLines = lines;
		checkGenMinable = replaceGenMinable;
		for(Object i : replacableIDs)
		{
			replaceableBlocks.add((Integer) i);
		}
	}
	
	public boolean spawnOre(World world, Integer[] coords)
	{
		return spawnOre(world, coords[0], coords[1], coords[2]);
	}
	
	public boolean spawnOre(World world, int x, int y, int z)
	{
		int currentID = world.getBlockId(x, y, z);
		if (checkGenMinable)
		{
			Block block = Block.blocksList[currentID];
			if (block != null && block.isGenMineableReplaceable(world, x, y, z))
			{
				world.setBlock(x, y, z, this.minableBlockId);
	        	world.setBlockMetadata(x, y, z, this.metadata);
			}
		}
		else if(replaceableBlocks.contains(currentID))
        {
        	world.setBlock(x, y, z, this.minableBlockId);
        	world.setBlockMetadata(x, y, z, this.metadata);
        	return true;
        }
        
        return false;
	}
	
	private class CompareCoordinates implements Comparator<Integer[]>
	{
		@Override
		public int compare(Integer[] arg0, Integer[] arg1) {
			return (arg0[0] - arg1[0]) + (arg0[1] - arg1[1]) + (arg0[2] - arg1[2]);
		}
		
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
    {
		//List<Integer[]> spawnedCoords = new ArrayList<Integer[]>();
		SortedList<Integer[]> sortedList = new SortedList<Integer[]>(new CompareCoordinates());
		
		spawnOre(world, x, y, z);
		sortedList.add(new Integer[] {x, y, z});
		
		List<Integer[]> possibleMoves = new ArrayList<Integer[]>();
		possibleMoves.add(new Integer[] {1, 0, 0});
		possibleMoves.add(new Integer[] {0, 1, 0});
		possibleMoves.add(new Integer[] {0, 0, 1});
		possibleMoves.add(new Integer[] {-1, 0, 0});
		possibleMoves.add(new Integer[] {0, -1, 0});
		possibleMoves.add(new Integer[] {0, 0, -1});
		possibleMoves.add(new Integer[] {1, 1, 0});
		possibleMoves.add(new Integer[] {-1, 1, 0});
		possibleMoves.add(new Integer[] {1, -1, 0});
		possibleMoves.add(new Integer[] {-1, -1, 0});
		possibleMoves.add(new Integer[] {1, 0, 1});
		possibleMoves.add(new Integer[] {-1, 0, 1});
		possibleMoves.add(new Integer[] {1, 0, -1});
		possibleMoves.add(new Integer[] {-1, 0, -1});
		possibleMoves.add(new Integer[] {0, 1, 1});
		possibleMoves.add(new Integer[] {0, -1, 1});
		possibleMoves.add(new Integer[] {0, 1, -1});
		possibleMoves.add(new Integer[] {0, -1, -1});
		
		int trueSize = 1;
		int randomSize = this.numberOfBlocks + random.nextInt(this.sizeVariance);
		for(int n = 1; n < randomSize; n++)
		{
			List<Integer[]> cpm = new ArrayList<Integer[]>();
			for(Integer[] i : possibleMoves)
				cpm.add(i);
			
			int pickedOre = (int) (((random.nextFloat()*random.nextFloat())) * sortedList.size());
			if (!generateLines)
				pickedOre = sortedList.size()-1; //Comment this line to create groups instead of lines
			Integer[] coords = sortedList.get(pickedOre);
			
			Integer[] finalCoords = {coords[0], coords[1], coords[2]};
			do
			{
				if(cpm.size() == 0)
				{
					n--;
					sortedList.remove(pickedOre);
					break;
				}
				int pick = (int) (random.nextFloat() * cpm.size());
				Integer[] offset = cpm.get(pick);
				finalCoords = new Integer[] { coords[0] + offset[0], coords[1] + offset[1], coords[2] + offset[2] };
				cpm.remove(pick);
			} while(sortedList.contains(finalCoords));
			
			if(random.nextInt(100) < density)
			{
				spawnOre(world, finalCoords);
				trueSize++;
			}
			sortedList.add(finalCoords);
		}
		
		//System.out.println("Ore vein size = " + trueSize);
        return true;
    }
}