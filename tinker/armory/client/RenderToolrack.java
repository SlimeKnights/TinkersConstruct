package tinker.armory.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import tinker.armory.content.Toolrack;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderToolrack implements ISimpleBlockRenderingHandler
{
    public static int rackModel;
    private static final float[][] modelCubes = new float[][] {
    	{0.0F, 0.625F, 0.0F, 1.0F, 0.8125F, 0.125F}, {0.1875F, 0.3125F, 0.0F, 0.25F, 0.9375F, 0.125F}, {0.75f, 0.3125F, 0.0F, 0.8125F, 0.875F, 0.125F}, {0.0F, 0.25F, 0.0F, 1.0F, 0.3125F, 0.3125F}};

    public RenderToolrack()
    {
        rackModel = RenderingRegistry.getNextAvailableRenderId();
    }

    public int getRenderId()
    {
        return this.rackModel;
    }

    public boolean shouldRender3DInInventory()
    {
        return true;
    }

    public boolean renderWorldBlock(IBlockAccess var1, int x, int y, int z, Block block, int modelID, RenderBlocks var7)
    {
    	if (modelID == this.rackModel)
    		return  this.renderRackBlock(var7, var1, x, y, z, (Toolrack)block);
    	else
    		return false;
    }

    public void renderInventoryBlock(Block var1, int var2, int var3, RenderBlocks var4)
    {
        if (var3 == this.rackModel)
        {
            this.renderRackItem(var4, (Toolrack)var1, var2);
        }
    }

    private void renderRackItem(RenderBlocks var1, Toolrack block, int metadata)
    {
        Tessellator tessellator = Tessellator.instance;

        for (int iter = 0; iter < modelCubes.length; ++iter)
        {
            float[] var6 = modelCubes[iter];
            block.setBlockBounds(var6[0], var6[1], var6[2], var6[3], var6[4], var6[5]);
            GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            var1.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            var1.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, metadata));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            var1.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, metadata));
            tessellator.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.0F);
        }

        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    private boolean renderRackBlock(RenderBlocks render, IBlockAccess world, int x, int y, int z, Toolrack block)
    {
        int meta = world.getBlockMetadata(x, y, z) / 4;
        int iter;
        float[] size;

        if (meta == 0)
        {
            for (iter = 0; iter < modelCubes.length; ++iter)
            {
                size = modelCubes[iter];
                block.setBlockBounds(size[0], size[1], size[2], size[3], size[4], size[5]);
                render.renderStandardBlock(block, x, y, z);
            }
        }
        else if (meta == 1)
        {
            for (iter = 0; iter < modelCubes.length; ++iter)
            {
                size = modelCubes[iter];
                block.setBlockBounds(size[2], size[1], size[0], size[5], size[4], size[3]);
                render.renderStandardBlock(block, x, y, z);
            }
        }
        else if (meta == 2)
        {
            for (iter = 0; iter < modelCubes.length; ++iter)
            {
                size = modelCubes[iter];
                block.setBlockBounds(size[0], size[1], 1.0F - size[5], size[3], size[4], 1.0F - size[2]);
                render.renderStandardBlock(block, x, y, z);
            }
        }
        else if (meta == 3)
        {
            for (iter = 0; iter < modelCubes.length; ++iter)
            {
                size = modelCubes[iter];
                block.setBlockBounds(1.0F - size[5], size[1], size[0], 1.0F - size[2], size[4], size[3]);
                render.renderStandardBlock(block, x, y, z);
            }
        }

        block.setBounds(meta);
        return false;
    }
}
