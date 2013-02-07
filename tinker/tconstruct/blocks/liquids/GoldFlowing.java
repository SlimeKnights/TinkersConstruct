package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class GoldFlowing extends LiquidMetalFlowing
{

	public GoldFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 3;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.goldStill.blockID;
	}
}
