package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidManyullynFlowFX extends TextureLiquidFlowingFX
{
	public LiquidManyullynFlowFX()
	{
		super(50, 200, 0, 40, 50, 200, TContent.manyullynFlowing.blockIndexInTexture+1, TContent.manyullynFlowing.getTextureFile());
	}
}