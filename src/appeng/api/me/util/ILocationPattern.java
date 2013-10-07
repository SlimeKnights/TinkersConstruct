package appeng.api.me.util;

import appeng.api.WorldCoord;

/**
 * Lets you Access Internal Location Pattern data.
 */
public interface ILocationPattern {
	WorldCoord getLocation();

	void setLocation(int x, int y, int z);
}
