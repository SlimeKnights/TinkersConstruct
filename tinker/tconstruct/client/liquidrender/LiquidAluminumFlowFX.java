package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidAluminumFlowFX extends TextureLiquidFlowingFX
{
	public LiquidAluminumFlowFX()
	{
		super(140, 255, 30, 190, 30, 190, TContent.aluminumFlowing.blockIndexInTexture+1, TContent.aluminumFlowing.getTextureFile());
	}
}