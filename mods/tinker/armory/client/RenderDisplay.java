package mods.tinker.armory.client;

import mods.tinker.armory.content.DisplayLogic;
import mods.tinker.common.BlockSkinRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderDisplay implements ISimpleBlockRenderingHandler
{
    public static int displayModel;

    public RenderDisplay()
    {
        displayModel = RenderingRegistry.getNextAvailableRenderId();
    }

    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == displayModel)
        {
            if (metadata == 5)
            {
                renderer.setRenderBounds(0.0F, 0.0, 0.0F, 1.0F, 0.875F, 1.0F);
                renderDo(renderer, block, metadata);
            }
            else
            {
                renderer.setRenderBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                renderDo(renderer, block, metadata);
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
                renderDo(renderer, block, metadata);
                renderer.setRenderBounds(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
                renderDo(renderer, block, metadata);
                renderer.setRenderBounds(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
                renderDo(renderer, block, metadata);
                renderer.setRenderBounds(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
                renderDo(renderer, block, metadata);
            }
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == displayModel)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            TileEntity te = world.getBlockTileEntity(x, y, z);
            ItemStack topStack = ((DisplayLogic) te).getFrontDisplay();
            Block topSkin = Block.blocksList[topStack.itemID];
            int topMeta = topStack.getItemDamage();
            ItemStack bottomStack = ((DisplayLogic) te).getBackDisplay();
            Block bottomSkin = Block.blocksList[bottomStack.itemID];
            int bottomMeta = bottomStack.getItemDamage();

            renderer.setRenderBounds(0.125, 0.125F, 0.0F, 0.375, 0.875F, 0.0625);
            BlockSkinRenderHelper.renderMetadataBlock(bottomSkin, bottomMeta, x, y, z, renderer, world);
            renderer.setRenderBounds(0.6125, 0.125F, 0.0F, 0.875, 0.875F, 0.0625);
            BlockSkinRenderHelper.renderMetadataBlock(bottomSkin, bottomMeta, x, y, z, renderer, world);
            renderer.setRenderBounds(0.0F, 0.5F, 0.0F, 1.0F, 0.625F, 0.09375);
            BlockSkinRenderHelper.renderMetadataBlock(topSkin, topMeta, x, y, z, renderer, world);
            renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 0.125);
            BlockSkinRenderHelper.renderMetadataBlock(topSkin, topMeta, x, y, z, renderer, world);
        }
        return false;
    }

    @Override
    public boolean shouldRender3DInInventory ()
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return displayModel;
    }

    private void renderDo (RenderBlocks renderblocks, Block block, int meta)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
