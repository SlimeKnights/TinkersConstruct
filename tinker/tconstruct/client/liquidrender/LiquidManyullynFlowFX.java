package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidManyullynFlowFX extends TextureLiquidFlowingFX
{
	public LiquidManyullynFlowFX()
	{
		super(150, 250, 50, 120, 150, 250, TContent.manyullynFlowing.blockIndexInTexture+1, TContent.manyullynFlowing.getTextureFile());
	}
}