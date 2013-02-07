package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidTinFlowFX extends TextureLiquidFlowingFX
{
	public LiquidTinFlowFX()
	{
		super(150, 250, 150, 250, 150, 250, TContent.tinFlowing.blockIndexInTexture+1, TContent.tinFlowing.getTextureFile());
	}
}