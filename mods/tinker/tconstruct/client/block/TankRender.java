package mods.tinker.tconstruct.client.block;

import mods.tinker.tconstruct.blocks.logic.LavaTankLogic;
import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

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
            LavaTankLogic logic = (LavaTankLogic) world.getBlockTileEntity(x, y, z);
            if (logic.containsLiquid())
            {
                LiquidStack liquid = logic.tank.getLiquid();
                renderer.setRenderBounds(0.001, 0.001, 0.001, 0.999, logic.getLiquidAmountScaled(), 0.999);
                if (liquid.itemID < 4096) //Block
                {
                    Block liquidBlock = Block.blocksList[liquid.itemID];
                    BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, liquid.itemMeta, x, y, z, renderer, world);
                }
                else
                //Item
                {
                    Item liquidItem = Item.itemsList[liquid.itemID];
                    BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(liquid.itemMeta), x, y, z, renderer, world);
                }
            }

            //Block
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 0 && world.getBlockId(x, y + 1, z) == 0)
            {
                renderer.setRenderBounds(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125);
                renderer.renderStandardBlock(block, x, y + 1, z);
            }
            renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
            renderer.renderStandardBlock(block, x, y, z);
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory ()
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
