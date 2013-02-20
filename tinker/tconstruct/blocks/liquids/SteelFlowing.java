package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class SteelFlowing extends LiquidMetalFlowing
{

	public SteelFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 70;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.steelStill.blockID;
	}
}
