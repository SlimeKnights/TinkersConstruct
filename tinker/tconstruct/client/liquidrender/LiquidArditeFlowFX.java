package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidArditeFlowFX extends TextureLiquidFlowingFX
{
	public LiquidArditeFlowFX()
	{
		super(10, 250, 10, 150, 10, 50, TContent.arditeFlowing.blockIndexInTexture+1, TContent.arditeFlowing.getTextureFile());
	}
}