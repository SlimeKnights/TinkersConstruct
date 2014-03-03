package powercrystals.minefactoryreloaded.api;

import java.util.Random;

import net.minecraft.world.World;

/**
 * @author PowerCrystals
 *
 * Defines a fertilizable block, and the process to fertilize it. You can assume that you will never have to check that block ID matches the one returned by
 * getFertilizableBlockId().
 */
public interface IFactoryFertilizable
{
	/**
	 * @return The block ID this instance is managing.
	 */
	public int getFertilizableBlockId();
	
	/**
	 * @param world The world this block belongs to.
	 * @param x The X coordinate of this block.
	 * @param y The Y coordinate of this block.
	 * @param z The Z coordinate of this block.
	 * @param fertilizerType The kind of fertilizer being used.
	 * @return True if the block at (x,y,z) can be fertilized with the given type of fertilizer.
	 */
	public boolean canFertilizeBlock(World world, int x, int y, int z, FertilizerType fertilizerType);
	
	/**
	 * @param world The world this block belongs to.
	 * @param rand A Random instance to use when fertilizing, if necessary.
	 * @param x The X coordinate of this block.
	 * @param y The Y coordinate of this block.
	 * @param z The Z coordinate of this block.
	 * @param fertilizerType The kind of fertilizer being used.
	 * @return True if fertilization was successful. If false, the Fertilizer will not consume a fertilizer item and will not drain power.
	 */
	public boolean fertilize(World world, Random rand, int x, int y, int z, FertilizerType fertilizerType);
}
