/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.google.common.base.Optional;

/**
 * Allows direct access to Extrabiome's biomes. This class' members will
 * be populated during the @Init event. If a biome is absent after the
 * Init event, then the Extrabiomes mod is not active or not installed.
 * <p>
 * <b>NOTE:</b> Make sure to only reference members of this class in the
 * PostInit event or later.
 * 
 * @author ScottKillen
 * 
 */

// The BiomeManager will be removed following release 3.6.0. Use extrabiomes.api.Biomes instead

@Deprecated
public abstract class BiomeManager {

	protected enum GenType {
		TREE, GRASS;
	}

	public static Optional<? extends BiomeGenBase> alpine				= Optional.absent();
	public static Optional<? extends BiomeGenBase> autumnwoods			= Optional.absent();
	public static Optional<? extends BiomeGenBase> birchforest			= Optional.absent();
	public static Optional<? extends BiomeGenBase> extremejungle		= Optional.absent();
	public static Optional<? extends BiomeGenBase> forestedisland		= Optional.absent();
	public static Optional<? extends BiomeGenBase> forestedhills		= Optional.absent();
	public static Optional<? extends BiomeGenBase> glacier				= Optional.absent();
	public static Optional<? extends BiomeGenBase> greenhills			= Optional.absent();
	public static Optional<? extends BiomeGenBase> icewasteland			= Optional.absent();
	public static Optional<? extends BiomeGenBase> greenswamp			= Optional.absent();
	public static Optional<? extends BiomeGenBase> marsh				= Optional.absent();
	public static Optional<? extends BiomeGenBase> meadow				= Optional.absent();
	public static Optional<? extends BiomeGenBase> minijungle			= Optional.absent();
	public static Optional<? extends BiomeGenBase> mountaindesert		= Optional.absent();
	public static Optional<? extends BiomeGenBase> mountainridge		= Optional.absent();
	public static Optional<? extends BiomeGenBase> mountaintaiga		= Optional.absent();
	public static Optional<? extends BiomeGenBase> pineforest			= Optional.absent();
	public static Optional<? extends BiomeGenBase> rainforest			= Optional.absent();
	public static Optional<? extends BiomeGenBase> redwoodforest		= Optional.absent();
	public static Optional<? extends BiomeGenBase> redwoodlush			= Optional.absent();
	public static Optional<? extends BiomeGenBase> savanna				= Optional.absent();
	public static Optional<? extends BiomeGenBase> shrubland			= Optional.absent();
	public static Optional<? extends BiomeGenBase> snowforest			= Optional.absent();
	public static Optional<? extends BiomeGenBase> snowyrainforest		= Optional.absent();
	public static Optional<? extends BiomeGenBase> temperaterainforest	= Optional.absent();
	public static Optional<? extends BiomeGenBase> tundra				= Optional.absent();
	public static Optional<? extends BiomeGenBase> wasteland			= Optional.absent();
	public static Optional<? extends BiomeGenBase> woodlands			= Optional.absent();

	protected static Optional<? extends BiomeManager>	instance		= Optional.absent();

	/**
	 * This method allows the addition of grasses to custom biomes by
	 * weight.
	 * 
	 * @param biome
	 *            the biomes to add the tree to.
	 * @param grassGen
	 *            the grass generator
	 * @param weight
	 *            the relative probabilty of picking this grass to
	 *            generate. To establish a relative priority, some
	 *            function should be applied to the current total weight
	 *            for a biome.
	 */
	public static void addWeightedGrassGenForBiome(BiomeGenBase biome,
			WorldGenerator grassGen, int weight)
	{
		checkArgument(instance.isPresent(),
				"Cannot add weighted grass gens until after API is initialized.");
		checkNotNull(biome, "Biome is required.");
		checkNotNull(grassGen, "Grass generator is required.");
		checkArgument(weight > 0, "Weight must be greater than zero.");
		instance.get().addBiomeGen(GenType.GRASS, biome, grassGen,
				weight);
	}

	/**
	 * This method allows the addition of trees to custom biomes by
	 * weight.
	 * 
	 * @param biome
	 *            the biomes to add the tree to.
	 * @param treeGen
	 *            the tree generator
	 * @param weight
	 *            the relative probabilty of picking this tree to
	 *            generate. To establish a relative priority, some
	 *            function should be applied to the current total weight
	 *            for a biome.
	 */
	public static void addWeightedTreeGenForBiome(BiomeGenBase biome,
			WorldGenerator treeGen, int weight)
	{
		checkArgument(instance.isPresent(),
				"Cannot add weighted tree gens until after API is initialized.");
		checkNotNull(biome, "Biome is required.");
		checkNotNull(treeGen, "Tree Generator is required.");
		checkArgument(weight > 0, "Weight must be greater than zero.");
		instance.get()
				.addBiomeGen(GenType.TREE, biome, treeGen, weight);
	}

	/**
	 * Returns a random choice from the weighted list of grass
	 * generators
	 * 
	 * @param biome
	 *            The biome for which to select a grass gen
	 * @return the selected grass generator.
	 */
	public static Optional<? extends WorldGenerator> chooseRandomGrassGenforBiome(
			Random rand, BiomeGenBase biome)
	{
		return instance.get().chooseBiomeRandomGen(GenType.GRASS, rand,
				biome);
	}

	/**
	 * Returns a random choice from the weighted list of tree generators
	 * 
	 * @param biome
	 *            The biome for which to select a tree gen
	 * @return the selected tree generator.
	 */
	public static Optional<? extends WorldGenerator> chooseRandomTreeGenforBiome(
			Random rand, BiomeGenBase biome)
	{
		return instance.get().chooseBiomeRandomGen(GenType.TREE, rand,
				biome);
	}

	/**
	 * @return An immutable collection of this mod's biomes.
	 */
	public static Collection<BiomeGenBase> getBiomes() {
		checkArgument(instance.isPresent(),
				"Biome list not available until after API is initialized.");
		return instance.get().getBiomeCollection();
	}

	/**
	 * @param biome
	 *            The biome for which to calculate the total weight.
	 * @return the total weight of all grassGen choices for a given
	 *         biome
	 */
	public static int getTotalGrassWeightForBiome(BiomeGenBase biome) {
		checkNotNull(biome, "Biome is required.");
		return instance.get().getBiomeTotalWeight(GenType.GRASS, biome);
	}

	/**
	 * @param biome
	 *            The biome for which to calculate the total weight.
	 * @return the total weight of all treeGen choices for a given biome
	 */
	public static int getTotalTreeWeightForBiome(BiomeGenBase biome) {
		checkNotNull(biome, "Biome is required.");
		return instance.get().getBiomeTotalWeight(GenType.TREE, biome);
	}

	static boolean isActive() {
		return instance.isPresent();
	}

	protected abstract void addBiomeGen(GenType genType,
			BiomeGenBase biome, WorldGenerator treeGen, int weight);

	protected abstract Optional<? extends WorldGenerator> chooseBiomeRandomGen(
			GenType genType, Random rand, BiomeGenBase biome);

	protected abstract Collection<BiomeGenBase> getBiomeCollection();

	protected abstract int getBiomeTotalWeight(GenType genType,
			BiomeGenBase biome);
}
