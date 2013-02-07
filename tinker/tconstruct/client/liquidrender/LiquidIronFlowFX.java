package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidIronFlowFX extends TextureLiquidFlowingFX
{
	public LiquidIronFlowFX()
	{
		super(10, 250, 10, 100, 10, 50, TContent.ironFlowing.blockIndexInTexture+1, TContent.ironFlowing.getTextureFile());
	}
}