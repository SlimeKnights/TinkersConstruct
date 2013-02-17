package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class AlBrassFlowing extends LiquidMetalFlowing
{

	public AlBrassFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 41;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.alBrassStill.blockID;
	}
}
