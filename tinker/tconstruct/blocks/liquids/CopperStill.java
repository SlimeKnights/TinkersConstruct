package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class CopperStill extends LiquidMetalStill
{

	public CopperStill(int id)
	{
		super(id);
		blockIndexInTexture = 6;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.copperFlowing.blockID;
	}

}
