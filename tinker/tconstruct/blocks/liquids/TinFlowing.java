package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class TinFlowing extends LiquidMetalFlowing
{

	public TinFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 9;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.tinStill.blockID;
	}
}
