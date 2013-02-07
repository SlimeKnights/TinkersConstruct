package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class BronzeStill extends LiquidMetalStill
{

	public BronzeStill(int id)
	{
		super(id);
		blockIndexInTexture = 38;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.bronzeFlowing.blockID;
	}

}
