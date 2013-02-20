package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidAluminumFlowFX extends TextureLiquidFlowingFX
{
	public LiquidAluminumFlowFX()
	{
		super(50, 255, 0, 150, 0, 120, TContent.aluminumFlowing.blockIndexInTexture+1, TContent.aluminumFlowing.getTextureFile());
	}
}