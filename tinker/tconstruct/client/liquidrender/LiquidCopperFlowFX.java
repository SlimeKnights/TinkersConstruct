package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidCopperFlowFX extends TextureLiquidFlowingFX
{
	public LiquidCopperFlowFX()
	{
		super(180, 255, 100, 255, 10, 40, TContent.copperFlowing.blockIndexInTexture+1, TContent.copperFlowing.getTextureFile());
	}
}