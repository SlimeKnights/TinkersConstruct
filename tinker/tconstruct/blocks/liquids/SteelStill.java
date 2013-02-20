package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class SteelStill extends LiquidMetalStill
{

	public SteelStill(int id)
	{
		super(id);
		blockIndexInTexture = 70;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.steelFlowing.blockID;
	}

}
