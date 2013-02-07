package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class ObsidianStill extends LiquidMetalStill
{

	public ObsidianStill(int id)
	{
		super(id);
		blockIndexInTexture = 67;
	}

	@Override
	public int flowingLiquidID ()
	{
		return TContent.obsidianFlowing.blockID;
	}

}
