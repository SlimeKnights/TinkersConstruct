package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidCopperFX extends TextureLiquidStillFX
{
	public LiquidCopperFX()
	{
		super(180, 255, 100, 255, 10, 40, TContent.copperStill.blockIndexInTexture, TContent.copperStill.getTextureFile());
	}
}