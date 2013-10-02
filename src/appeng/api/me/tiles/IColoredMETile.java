package appeng.api.me.tiles;

/**
 * Used to signify which color a particular IGridTileEntity or IGridMachine is, you must implement both, if you wish to have color bias.
 */
public interface IColoredMETile
{
	public static String[] Colors = {
		"Blue",
		"Black",
		"White",
		"Brown",
		"Red",
		"Yellow",
		"Green"
	};
	
	/**
	 * return true, if your block has a color, or false, if it dosn't.
	 * this allows you to have a colored block that can pretend to be colorless.
	 * @return true, if colored, false if not.
	 */
	boolean isColored();
	
	/**
	 * Change the color, AE dosn't call this except for its own blocks, its simply included for completeness.
	 * @param offset
	 */
	void setColor( int offset );
	
	/**
	 * which color is this tile?
	 * @return index into the above ColorsList.
	 */
	int getColor();
}
