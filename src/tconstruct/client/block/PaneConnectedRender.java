package tconstruct.client.block;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import tconstruct.blocks.GlassPaneConnected;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class PaneConnectedRender implements ISimpleBlockRenderingHandler {

	public static int model = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		GlassPaneConnected pane = (GlassPaneConnected)block;
		
		boolean flag = pane.canPaneConnectTo(world, x, y, z, EAST);
		boolean flag1 = pane.canPaneConnectTo(world, x, y, z, WEST);
		boolean flag2 = pane.canPaneConnectTo(world, x, y, z, SOUTH);
		boolean flag3 = pane.canPaneConnectTo(world, x, y, z, NORTH);
		
		Icon sideTexture = pane.getSideTextureIndex();
		
		if(!flag && !flag1 && !flag2 && !flag3){
			renderer.setRenderBounds(0D, 0D, 0.45D, 1D, 1D, 0.55D);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.setRenderBounds(0.45D, 0D, 0D, 0.55D, 1D, 1D);
			renderer.renderStandardBlock(block, x, y, z);
		}else{
//			renderer.setRenderBounds(0.45D, 0D, 0.45D, 0.55D, 1D, 0.55D);
//			renderer.renderStandardBlock(block, x, y, z);
		}
		
		if(flag){
			renderer.setRenderBounds(0.45D, 0D, 0.45D, 1D, 1D, 0.55D);
			renderer.renderStandardBlock(block, x, y, z);
		}
		
		if(flag1){
			renderer.setRenderBounds(0D, 0D, 0.45D, 0.45D, 1D, 0.55D);
			renderer.renderStandardBlock(block, x, y, z);
		}
		
		if(flag2){
			renderer.setRenderBounds(0.45D, 0D, 0.45D, 0.55D, 1D, 1D);
			renderer.renderStandardBlock(block, x, y, z);
		}
		
		if(flag3){
			renderer.setRenderBounds(0.45D, 0D, 0D, 0.55D, 1D, 0.45D);
			renderer.renderStandardBlock(block, x, y, z);
		}
		
		
		
		renderer.clearOverrideBlockTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return model;
	}

}
