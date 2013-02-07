package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class CopperFlowing extends LiquidMetalFlowing
{

	public CopperFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 6;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.copperStill.blockID;
	}
}
