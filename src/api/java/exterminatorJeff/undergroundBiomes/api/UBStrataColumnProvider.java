package exterminatorJeff.undergroundBiomes.api;

import net.minecraft.world.chunk.IChunkProvider;

/**
 *
 * @author Zeno410
 */
public interface UBStrataColumnProvider {
    public UBStrataColumn strataColumn(int x, int z);

    public IChunkProvider UBChunkProvider(IChunkProvider currentProvider);
    /* this wraps the passed chunk provider with a class that will convert stone to the appropriate
     * UB stones in chunks fetched with the provide() method.
     *
     * If the user has directed UB not to convert that dimension, or not to use in-chunk generation,
     * or to use in-chunk generation but not in that dimension, UB will hand your current provider back to you.
     *
     * Only use it for IChunkProviders that only provide new chunks. If you wrap a provider
     * that gets involved in loading chunks or passing around chunks it gets so slow the world will
     * appear to lock up and never load. It is actually grinding forward, but I've never waited long
     * enough for a world to come up with the wrong provider wrapped.
     */

    public boolean inChunkGenerationAllowed();
    // so you can check and not have to wonder what's going on
}
