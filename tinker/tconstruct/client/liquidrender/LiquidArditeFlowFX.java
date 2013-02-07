package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidArditeFlowFX extends TextureLiquidFlowingFX
{
	public LiquidArditeFlowFX()
	{
		super(0, 180, 0, 140, 0, 50, TContent.arditeFlowing.blockIndexInTexture+1, TContent.arditeFlowing.getTextureFile());
	}
}