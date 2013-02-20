package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidSteelFX extends TextureLiquidStillFX
{
	public LiquidSteelFX()
	{
		super(30, 150, 30, 150, 30, 150, TContent.steelStill.blockIndexInTexture, TContent.steelStill.getTextureFile());
	}
}