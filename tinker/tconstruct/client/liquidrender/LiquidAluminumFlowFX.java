package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidAluminumFlowFX extends TextureLiquidFlowingFX
{
	public LiquidAluminumFlowFX()
	{
		super(140, 255, 70, 150, 70, 150, TContent.aluminumFlowing.blockIndexInTexture+1, TContent.aluminumFlowing.getTextureFile());
	}
}