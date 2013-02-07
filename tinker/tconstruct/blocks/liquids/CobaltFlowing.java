package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class CobaltFlowing extends LiquidMetalFlowing
{

	public CobaltFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 32;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.cobaltStill.blockID;
	}
}
