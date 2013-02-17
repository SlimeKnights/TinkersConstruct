package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class AlBrassStill extends LiquidMetalStill
{

	public AlBrassStill(int id)
	{
		super(id);
		blockIndexInTexture = 41;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.alBrassFlowing.blockID;
	}

}
