package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidGoldFlowFX extends TextureLiquidFlowingFX
{
	public LiquidGoldFlowFX()
	{
		super(180, 255, 180, 255, 10, 40, TContent.goldFlowing.blockIndexInTexture+1, TContent.goldFlowing.getTextureFile());
	}
}