package tinker.tconstruct.client.liquidrender;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderLiquidMetal implements ISimpleBlockRenderingHandler
{
	public static int liquidModel = RenderingRegistry.getNextAvailableRenderId();
	
	@Override
	public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		//You shouldn't have this
	}

	@Override
	public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		renderer.renderBlockFluids(block, x, y, z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory ()
	{
		return false;
	}

	@Override
	public int getRenderId ()
	{
		return liquidModel;
	}
}
