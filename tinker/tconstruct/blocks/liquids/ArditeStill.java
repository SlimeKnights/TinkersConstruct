package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class ArditeStill extends LiquidMetalStill
{

	public ArditeStill(int id)
	{
		super(id);
		blockIndexInTexture = 35;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.arditeFlowing.blockID;
	}

}
