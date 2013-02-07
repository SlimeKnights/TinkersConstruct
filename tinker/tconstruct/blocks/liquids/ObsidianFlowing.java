package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class ObsidianFlowing extends LiquidMetalFlowing
{

	public ObsidianFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 67;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.obsidianStill.blockID;
	}
}
