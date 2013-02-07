package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class AlumiteFlowing extends LiquidMetalFlowing
{

	public AlumiteFlowing(int id)
	{
		super(id);
		blockIndexInTexture = 64;
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.alumiteStill.blockID;
	}
}
