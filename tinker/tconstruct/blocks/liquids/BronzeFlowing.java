package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class BronzeFlowing extends LiquidMetalFlowing
{

	public BronzeFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 38;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.bronzeStill.blockID;
	}
}
