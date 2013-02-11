package tinker.tconstruct.client.liquidrender;

import tinker.tconstruct.TContent;

public class LiquidObsidianFlowFX extends TextureLiquidFlowingFX
{
	public LiquidObsidianFlowFX()
	{
		super(0, 120, 0, 40, 0, 120, TContent.obsidianFlowing.blockIndexInTexture+1, TContent.obsidianFlowing.getTextureFile());
	}
}