/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.api;

import net.minecraft.world.biome.BiomeGenBase;

import com.google.common.base.Optional;

import extrabiomes.api.events.GetBiomeIDEvent;

/**
 * Provides access to custom biomes. Reference implementation.
 * 
 * @author Scott
 * 
 */
public abstract class Biomes {

    /**
     * Retrieves a custom biome
     * 
     * @param targetBiome
     *            The string name of the targertBiome. See
     *            {@link GetBiomeIDEvent#targetBiome} for valid values.
     * @return The requested biome. If the biome does not exist, the
     *         <code>Optional</code> value will not be present.
     */
    public static Optional<BiomeGenBase> getBiome(String targetBiome) {
        final GetBiomeIDEvent event = new GetBiomeIDEvent(targetBiome);
        Api.getExtrabiomesXLEventBus().post(event);
        if (event.biomeID <= 0) return Optional.absent();
        return Optional.of(BiomeGenBase.biomeList[event.biomeID]);
    }

}
