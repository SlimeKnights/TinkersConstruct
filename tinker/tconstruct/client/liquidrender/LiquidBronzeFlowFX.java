package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidBronzeFlowFX extends TextureLiquidFlowingFX
{
	public LiquidBronzeFlowFX()
	{
		super(140, 220, 70, 220, 10, 40, TContent.bronzeFlowing.blockIndexInTexture+1, TContent.arditeFlowing.getTextureFile());
	}
}