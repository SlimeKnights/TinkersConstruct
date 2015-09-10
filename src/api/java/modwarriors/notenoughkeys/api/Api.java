package modwarriors.notenoughkeys.api;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Center of the API. Main api methods can be found in this class.
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
public class Api {

	/**
	 * Checks if NotEnoughKeys is loaded in the current environment
	 *
	 * @return 'true' if loaded
	 */
	public static boolean isLoaded() {
		return Loader.isModLoaded("notenoughkeys");
	}

	/**
	 * Registers a mod's keys with NEK
	 *
	 * @param modname        The NAME of the mod registering the key
	 * @param keyDescriptions A String[] (Array[String]) of the key descriptions
	 *                           as an inherit array. i.e. ("modName", "key.hotbar1", "key.hotbar2")
	 */
	public static void registerMod(String modname, String... keyDescriptions) {
		try {
			Class.forName("modwarriors.notenoughkeys.keys.KeyHelper").getMethod(
					"registerMod", String.class, String[].class
			).invoke(null, modname, keyDescriptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
