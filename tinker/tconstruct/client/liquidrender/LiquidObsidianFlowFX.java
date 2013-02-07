package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidObsidianFlowFX extends TextureLiquidFlowingFX
{
	public LiquidObsidianFlowFX()
	{
		super(50, 200, 0, 40, 50, 200, TContent.obsidianFlowing.blockIndexInTexture+1, TContent.obsidianFlowing.getTextureFile());
	}
}