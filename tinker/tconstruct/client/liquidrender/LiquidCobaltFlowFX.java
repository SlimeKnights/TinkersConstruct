package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidCobaltFlowFX extends TextureLiquidFlowingFX
{
	public LiquidCobaltFlowFX()
	{
		super(0, 30, 30, 100, 100, 250, TContent.cobaltFlowing.blockIndexInTexture+1, TContent.cobaltFlowing.getTextureFile());
	}
}