package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class AluminumFlowing extends LiquidMetalFlowing
{

	public AluminumFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 12;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.aluminumStill.blockID;
	}
}
