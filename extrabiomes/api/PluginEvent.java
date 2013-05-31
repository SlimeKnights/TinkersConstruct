/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.api;

import net.minecraftforge.event.Event;

/**
 * These events are fired during FML @PostInit to manage plugins
 */
public class PluginEvent extends Event {

	/**
	 * Fired before any ExtrabiomesXL plugin is initialized
	 */
	public static class Pre extends PluginEvent {}

	/**
	 * Fired to initialize ExtrabiomesXL plugins
	 */
	public static class Init extends PluginEvent {}

	/**
	 * Fired after every ExtrabiomesXL plugin is initialized
	 */
	public static class Post extends PluginEvent {}

}
