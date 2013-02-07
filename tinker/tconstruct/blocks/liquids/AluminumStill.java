package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class AluminumStill extends LiquidMetalStill
{

	public AluminumStill(int id)
	{
		super(id);
		blockIndexInTexture = 12;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.aluminumFlowing.blockID;
	}

}
