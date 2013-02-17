package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidBrassFlowFX extends TextureLiquidFlowingFX
{
	public LiquidBrassFlowFX()
	{
		super(200, 255, 200, 255, 40, 80, TContent.alBrassFlowing.blockIndexInTexture+1, TContent.alBrassFlowing.getTextureFile());
	}
}