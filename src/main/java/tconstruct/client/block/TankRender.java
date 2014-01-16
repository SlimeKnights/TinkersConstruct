package tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.LavaTankLogic;
import tconstruct.client.TProxyClient;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class TankRender implements ISimpleBlockRenderingHandler
{
    public static int tankModelID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == tankModelID)
        {
            TProxyClient.renderStandardInvBlock(renderer, block, metadata);
            if (metadata == 0)
            {
                renderer.func_147782_a(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125);
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
            LavaTankLogic logic = (LavaTankLogic) world.func_147438_o(x, y, z);
            if (logic.containsFluid())
            {
                FluidStack liquid = logic.tank.getFluid();
                renderer.func_147782_a(0.001, 0.001, 0.001, 0.999, logic.getFluidAmountScaled(), 0.999);
                Fluid fluid = liquid.getFluid();
                if (fluid.canBePlacedInWorld())
                    BlockSkinRenderHelper.renderMetadataBlock(fluid.getBlock(), 0, x, y, z, renderer, world);
                else
                    BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);

                renderer.func_147782_a(00, 0.001, 0.001, 0.999, logic.getFluidAmountScaled(), 0.999);
            }

            //Block
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 0 && world.func_147439_a(x, y + 1, z) == Blocks.air)
            {
                renderer.func_147782_a(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125);
                renderer.func_147784_q(block, x, y + 1, z);
            }
            renderer.func_147782_a(0, 0, 0, 1, 1, 1);
            renderer.func_147784_q(block, x, y, z);
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
        renderblocks.func_147768_a(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.func_147806_b(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.func_147761_c(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.func_147734_d(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.func_147798_e(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.func_147764_f(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
