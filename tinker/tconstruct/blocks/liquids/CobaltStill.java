package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class CobaltStill extends LiquidMetalStill
{

	public CobaltStill(int id)
	{
		super(id);
		blockIndexInTexture = 32;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.cobaltFlowing.blockID;
	}

}
