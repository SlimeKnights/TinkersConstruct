package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class ManyullynStill extends LiquidMetalStill
{

	public ManyullynStill(int id)
	{
		super(id);
		blockIndexInTexture = 44;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.manyullynFlowing.blockID;
	}

}
