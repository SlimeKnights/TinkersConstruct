package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidGoldFX extends TextureLiquidStillFX
{
	public LiquidGoldFX()
	{
		super(180, 255, 180, 255, 10, 40, TContent.goldStill.blockIndexInTexture, TContent.goldStill.getTextureFile());
	}
}