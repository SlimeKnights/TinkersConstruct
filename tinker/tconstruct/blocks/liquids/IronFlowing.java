package tinker.tconstruct.blocks.liquids;

import tinker.tconstruct.TContent;

public class IronFlowing extends LiquidMetalFlowing
{

	public IronFlowing(int id)
	{
		super(id);
	}

	@Override
	public int stillLiquidId ()
	{
		return TContent.ironStill.blockID;
	}
}
