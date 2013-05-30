package powercrystals.minefactoryreloaded.api;

/**
 * @author PowerCrystals
 *
 * Determines what algorithm the Harvester uses when it encounters this IFactoryHarvestable in the world.
 */
public enum HarvestType
{
	/**
	 * Just break the single block - no special action needed. Carrots, flowers, etc.
	 */
	Normal,
	/**
	 * Search for identical blocks above.
	 */
	Column,
	/**
	 * Search for identical blocks above but leave this bottom one for the future. Cactus and sugarcane.
	 */
	LeaveBottom,
	/**
	 * This block is the base of a tree and the harvester should enter tree-cutting mode.
	 */
	Tree,
	/**
	 * This block is the base of the tree and the harvester should enter tree-cutting mode, but the tree grows upside-down.
	 */
	TreeFlipped,
	/**
	 * This block is part of a tree as above, but leaves are cut before tree logs so that leaves do not decay more than necessary.
	 */
	TreeLeaf
}
