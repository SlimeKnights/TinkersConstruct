
package extrabiomes.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import com.google.common.base.Optional;

/**
 * This class contains all of the custom items and blocks.
 * 
 * @author ScottKillen
 * 
 */
public enum Stuff {
	INSTANCE;

	public static Optional<? extends Item>	scarecrow			= Optional.absent();
	public static Optional<? extends Item>	paste				= Optional.absent();

	public static Optional<? extends Block>	planks				= Optional.absent();   
	public static Optional<? extends Block>	quickSand			= Optional.absent();
	public static Optional<? extends Block>	slabRedRock			= Optional.absent();
	public static Optional<? extends Block>	slabRedRockDouble	= Optional.absent();
	public static Optional<? extends Block>	slabWood			= Optional.absent();
	public static Optional<? extends Block>	slabWoodDouble		= Optional.absent();
	public static Optional<? extends Block>	stairsAcacia		= Optional.absent();
	public static Optional<? extends Block>	stairsFir			= Optional.absent();
	public static Optional<? extends Block>	stairsRedCobble		= Optional.absent();
	public static Optional<? extends Block>	stairsRedRockBrick	= Optional.absent();
	public static Optional<? extends Block>	stairsRedwood		= Optional.absent();
	public static Optional<? extends Block>	wall				= Optional.absent();

}
