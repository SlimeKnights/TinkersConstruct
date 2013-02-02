package tinker.tconstruct;

import java.io.File;

/**
 * Common proxy class for InfiTools
 */

public class TProxyCommon
{
	/* Registers any rendering code. Does nothing server-side */
	public void registerRenderer() {}
	
	/* Ties an internal name to a visible one. Does nothing server-side */
	public void addNames() {}
	
	public void readManuals() {}
	
	public File getLocation()
	{
		return new File(".");
	}
}
