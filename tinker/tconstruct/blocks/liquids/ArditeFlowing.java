package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class ArditeFlowing extends LiquidMetalFlowing
{

	public ArditeFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 35;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.arditeStill.blockID;
	}
}
