package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidCopperFlowFX extends TextureLiquidFlowingFX
{
	public LiquidCopperFlowFX()
	{
		super(220, 255, 130, 220, 0, 120, TContent.copperFlowing.blockIndexInTexture+1, TContent.copperFlowing.getTextureFile());
	}
}