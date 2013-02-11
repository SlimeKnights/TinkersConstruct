package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidBronzeFlowFX extends TextureLiquidFlowingFX
{
	public LiquidBronzeFlowFX()
	{
		super(0, 180, 0, 140, 0, 50, TContent.bronzeFlowing.blockIndexInTexture+1, TContent.arditeFlowing.getTextureFile());
	}
}