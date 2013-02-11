package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class IronStill extends LiquidMetalStill
{

	public IronStill(int id)
	{
		super(id);
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.ironFlowing.blockID;
	}

}
