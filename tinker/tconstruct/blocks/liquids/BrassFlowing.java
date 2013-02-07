package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class BrassFlowing extends LiquidMetalFlowing
{

	public BrassFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 41;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.brassStill.blockID;
	}
}
