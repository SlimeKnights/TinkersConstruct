/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.api;

import net.minecraftforge.event.EventBus;

import com.google.common.base.Optional;

/*
 * @author ScottKillen
 */
public class Api {

	private static final EventBus		eventBus	= new EventBus();
	protected static Optional<EventBus>	pluginBus	= Optional.of(new EventBus());

	public static EventBus getExtrabiomesXLEventBus() {
		return eventBus;
	}

	/**
	 * @return true if ExtrtabiomesXL is installed and active
	 * @deprecated Use {@link #isExtrabiomesXLActive()} instead
	 */
	@Deprecated
	public static boolean isActive() {
		return isExtrabiomesXLActive();
	}

	/**
	 * @return true if ExtrtabiomesXL is installed and active
	 */
	@SuppressWarnings("deprecation")
    public static boolean isExtrabiomesXLActive() {
		return BiomeManager.isActive();
	}

	public static void registerPlugin(Object plugin) {
		if (pluginBus.isPresent()) pluginBus.get().register(plugin);
	}

}
