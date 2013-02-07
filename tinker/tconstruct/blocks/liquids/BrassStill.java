package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class BrassStill extends LiquidMetalStill
{

	public BrassStill(int id)
	{
		super(id);
		blockIndexInTexture = 41;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.brassFlowing.blockID;
	}

}
