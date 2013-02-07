package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class TinStill extends LiquidMetalStill
{

	public TinStill(int id)
	{
		super(id);
		blockIndexInTexture = 9;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.tinFlowing.blockID;
	}

}
