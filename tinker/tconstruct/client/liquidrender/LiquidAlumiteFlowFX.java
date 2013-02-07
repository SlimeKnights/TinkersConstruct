package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidAlumiteFlowFX extends TextureLiquidFlowingFX
{
	public LiquidAlumiteFlowFX()
	{
		super(0, 255, 0, 255, 0, 255, TContent.alumiteFlowing.blockIndexInTexture+1, TContent.alumiteFlowing.getTextureFile());
	}
}