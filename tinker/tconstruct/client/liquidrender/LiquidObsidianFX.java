package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidObsidianFX extends TextureLiquidStillFX
{
	public LiquidObsidianFX()
	{
		super(50, 200, 0, 40, 50, 200, TContent.obsidianStill.blockIndexInTexture, TContent.obsidianStill.getTextureFile());
	}
}