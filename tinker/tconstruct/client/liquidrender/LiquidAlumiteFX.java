package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidAlumiteFX extends TextureLiquidStillFX
{
	public LiquidAlumiteFX()
	{
		super(0, 255, 0, 200, 0, 255, TContent.alumiteStill.blockIndexInTexture, TContent.alumiteStill.getTextureFile());
	}
}