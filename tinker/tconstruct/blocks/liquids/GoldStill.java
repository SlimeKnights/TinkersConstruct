package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class GoldStill extends LiquidMetalStill
{

	public GoldStill(int id)
	{
		super(id);
		blockIndexInTexture = 3;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.goldFlowing.blockID;
	}

}
