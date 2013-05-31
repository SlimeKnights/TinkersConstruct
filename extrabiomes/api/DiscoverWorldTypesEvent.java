/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.api;

import java.util.Collection;

import net.minecraft.world.WorldType;
import net.minecraftforge.event.Event;

/**
 * This event fires on the ExtrabiomesXL event bus when biomes are added
 * to WorldTypes.
 */
public class DiscoverWorldTypesEvent extends Event {

	private final Collection<WorldType>	worldTypes;

	public DiscoverWorldTypesEvent(Collection<WorldType> worldTypes) {
		this.worldTypes = worldTypes;
	}

	/**
	 * Adds a WorldType to the list of WorldTypes to which ExtrabiomesXL
	 * will add its enabled biomes.
	 * 
	 * @param worldType
	 *            The WorldType to Add
	 * @return true if worldType was successfully added
	 */
	public boolean addWorldType(WorldType worldType) {
		if (worldTypes.contains(worldType)) return false;
		return worldTypes.add(worldType);
	}
}
