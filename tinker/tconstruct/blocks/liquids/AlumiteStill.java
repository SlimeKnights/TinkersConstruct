package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class AlumiteStill extends LiquidMetalStill
{

	public AlumiteStill(int id)
	{
		super(id);
		blockIndexInTexture = 64;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.alumiteFlowing.blockID;
	}

}
