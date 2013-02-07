package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class ManyullynFlowing extends LiquidMetalFlowing
{

	public ManyullynFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 44;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.manyullynStill.blockID;
	}
}
