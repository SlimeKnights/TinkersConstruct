package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.*;
import org.lwjgl.opengl.GL11;
import tconstruct.client.BlockSkinRenderHelper;
import tconstruct.smeltery.logic.LavaTankLogic;
import tconstruct.util.ItemHelper;

public class TankRender implements ISimpleBlockRenderingHandler
{
    public static int tankModelID = RenderingRegistry.getNextAvailableRenderId();
    public static int renderPass = 0;

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == tankModelID)
        {
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
            if (metadata == 0)
            {
                renderer.setRenderBounds(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125);
                renderDoRe(renderer, block, metadata);
            }
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == tankModelID)
        {
            //Liquid
            if (renderPass == 0)
            {
                LavaTankLogic logic = (LavaTankLogic) world.getTileEntity(x, y, z);
                if (logic.containsFluid())
                {
                    FluidStack liquid = logic.tank.getFluid();
                    renderer.setRenderBounds(0.001, 0.001, 0.001, 0.999, logic.getFluidAmountScaled(), 0.999);
                    Fluid fluid = liquid.getFluid();
                    BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getStillIcon(), x, y, z, renderer, world, true);

                    renderer.setRenderBounds(00, 0.001, 0.001, 0.999, logic.getFluidAmountScaled(), 0.999);
                }
            }
            //Block
            else
            {
                int meta = world.getBlockMetadata(x, y, z);
                if (meta == 0 && world.getBlock(x, y + 1, z) == Blocks.air)
                {
                    renderer.setRenderBounds(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125);
                    renderer.renderStandardBlock(block, x, y + 1, z);
                }
                renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
                renderer.renderStandardBlock(block, x, y, z);
            }
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return tankModelID;
    }

    private void renderDoRe (RenderBlocks renderblocks, Block block, int meta)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, 0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}