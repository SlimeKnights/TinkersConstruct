package mods.tinker.tconstruct.client.block;

import mods.tinker.tconstruct.blocks.logic.CastingBasinLogic;
import mods.tinker.tconstruct.blocks.logic.CastingTableLogic;
import mods.tinker.tconstruct.blocks.logic.FaucetLogic;
import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SearedRender implements ISimpleBlockRenderingHandler
{
    public static int searedModel = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == searedModel)
        {
            if (metadata == 0)
            {
                //Top
                renderer.setRenderBounds(0.0F, 0.625F, 0.0F, 1.0F, 0.9375F, 1.0F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);

                //Lip
                renderer.setRenderBounds(0.0F, 0.9375, 0.0F, 0.0625, 1.0, 1.0F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0625, 0.9375, 0.9375, 0.9375, 1.0, 1.0F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.9375, 0.9375, 0.0F, 1.0F, 1.0, 1.0F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0625, 0.9375, 0.0F, 0.9375, 1.0, 0.0625);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);

                //Legs
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.625F, 0.3125F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.6875, 0.0F, 0.0F, 1.0F, 0.625F, 0.25F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0F, 0.0F, 0.6875, 0.3125F, 0.625F, 1.0F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.6875, 0.0F, 0.6875, 1.0F, 0.625F, 1.0F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
            }
            else if (metadata == 1)
            {
                renderer.setRenderBounds(0.25, 0.25, 0.625, 0.75, 0.375, 1);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.25, 0.25, 0.625, 0.375, 0.625, 1);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.625, 0.25, 0.625, 0.75, 0.625, 1);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.375, 0.375, 0.625, 0.625, 0.625, 1);
            }
            else if (metadata == 2)
            {
                renderer.setRenderBounds(0.125F, 0.125f, 0.125F, 0.875F, 0.25F, 0.875F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);

                //Details                
                renderer.setRenderBounds(0.001f, 0.1245f, 0.001f, 0.1245f, 0.999f, 0.4375f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.001f, 0.1245f, 0.5625f, 0.1245f, 0.999f, 0.999f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.001f, 0.8755f, 0.4375f, 0.1245f, 0.999f, 0.5625f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.001f, 0.1245f, 0.4375f, 0.1245f, 0.25F, 0.5625f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);

                renderer.setRenderBounds(0.8755f, 0.1245f, 0f, 0.999f, 0.999f, 0.4375f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.8755f, 0.1245f, 0.5625f, 0.999f, 0.999f, 0.999f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.8755f, 0.8755f, 0.4375f, 0.999f, 0.999f, 0.5625f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.8755f, 0.1245f, 0.4375f, 0.999f, 0.25F, 0.5625f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);

                renderer.setRenderBounds(0.1245f, 0.1245f, 0.8755f, 0.4375f, 0.999f, 0.999f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.5625f, 0.1245f, 0.8755f, 0.8755f, 0.999f, 0.999f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.4375f, 0.8755f, 0.8755f, 0.5625f, 0.999f, 0.999f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.4375f, 0.1245f, 0.8755f, 0.5625f, 0.2495F, 0.999f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);

                renderer.setRenderBounds(0.1245f, 0.1245f, 0.001f, 0.4375f, 0.999f, 0.1245f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.5625f, 0.1245f, 0.001f, 0.8755f, 0.999f, 0.1245f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.4375f, 0.8755f, 0.001f, 0.5625f, 0.999f, 0.1245f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.4375f, 0.1245f, 0.001f, 0.5625f, 0.25F, 0.1245f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);

                //Legs
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.125, 0.3125F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.6875F, 0.0F, 0.0F, 1.0F, 0.125, 0.3125F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0F, 0.0F, 0.6875F, 0.3125F, 0.125, 1.0F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.6875F, 0.0F, 0.6875F, 1.0F, 0.125, 1.0F);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);

                //Outside
                renderer.setRenderBounds(0.0f, 0.125, 0f, 0.125, 1.0F, 1);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.875f, 0.125, 0f, 1, 1.0F, 1);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.125f, 0.125, 0f, 0.875f, 1.0F, 0.125f);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.125f, 0.125, 0.875f, 0.875f, 1.0F, 1);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
            }
            else
            {
                renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
                TProxyClient.renderStandardInvBlock(renderer, block, metadata);
            }
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == searedModel)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            if (metadata == 0)
            {
                //Top
                renderer.setRenderBounds(0.0F, 0.625F, 0.0F, 1.0F, 0.9375F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);

                //Lip
                renderer.setRenderBounds(0.0F, 0.9375, 0.0F, 0.0625, 1.0, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0625, 0.9375, 0.9375, 0.9375, 1.0, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.9375, 0.9375, 0.0F, 1.0F, 1.0, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0625, 0.9375, 0.0F, 0.9375, 1.0, 0.0625);
                renderer.renderStandardBlock(block, x, y, z);

                //Legs
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.625F, 0.3125F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.6875, 0.0F, 0.0F, 1.0F, 0.625F, 0.25F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0F, 0.0F, 0.6875, 0.3125F, 0.625F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.6875, 0.0F, 0.6875, 1.0F, 0.625F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);

                CastingTableLogic logic = (CastingTableLogic) world.getBlockTileEntity(x, y, z);
                if (logic.liquid != null)
                {
                    float height = logic.getLiquidAmount() / (logic.getCapacity() * 1.03F) / 16F;
                    renderer.setRenderBounds(0.0625F, 0.9375F, 0.0625F, 0.9375F, 0.9375F + height, 0.9375F);
                    if (logic.liquid.itemID < 4096) //Block
                    {
                        Block liquidBlock = Block.blocksList[logic.liquid.itemID];
                        if (liquidBlock != null)
                        {
                            //ForgeHooksClient.bindTexture(liquidBlock.getTextureFile(), 0);
                            BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, logic.liquid.itemMeta, x, y, z, renderer, world);
                        }
                    }
                    else
                    //Item
                    {
                        Item liquidItem = Item.itemsList[logic.liquid.itemID];
                        if (liquidItem != null)
                        {
                            //ForgeHooksClient.bindTexture(liquidItem.getTextureFile(), 0);
                            BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(logic.liquid.itemMeta), x, y, z, renderer, world);
                        }
                    }
                }
            }
            else if (metadata == 1)
            {
                FaucetLogic logic = (FaucetLogic) world.getBlockTileEntity(x, y, z);
                float xMin = 0.375F, zMin = 0.375F, xMax = 0.625F, zMax = 0.625F;
                switch (logic.getRenderDirection())
                {
                case 2:
                    renderer.setRenderBounds(0.25, 0.25, 0.625, 0.75, 0.375, 1);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.25, 0.375, 0.625, 0.375, 0.625, 1);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.625, 0.375, 0.625, 0.75, 0.625, 1);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.375, 0.375, 0.625, 0.625, 0.625, 1);
                    zMin = 0.5F;
                    //zMin = 0.625F;
                    break;
                case 3:
                    renderer.setRenderBounds(0.25, 0.25, 0, 0.75, 0.375, 0.375);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.25, 0.375, 0, 0.375, 0.625, 0.375);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.625, 0.375, 0, 0.75, 0.625, 0.375);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.375, 0.375, 0, 0.625, 0.625, 0.375);
                    zMax = 0.5F;
                    break;
                case 4:
                    renderer.setRenderBounds(0.625, 0.25, 0.25, 1, 0.375, 0.75);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.625, 0.375, 0.25, 1, 0.625, 0.375);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.625, 0.375, 0.625, 1, 0.625, 0.75);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0.625, 0.375, 0.375, 1, 0.625, 0.625);
                    xMin = 0.5F;
                    break;
                case 5:
                    renderer.setRenderBounds(0, 0.25, 0.25, 0.375, 0.375, 0.75);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0, 0.375, 0.25, 0.375, 0.625, 0.375);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0, 0.375, 0.625, 0.375, 0.625, 0.75);
                    renderer.renderStandardBlock(block, x, y, z);
                    renderer.setRenderBounds(0, 0.375, 0.375, 0.375, 0.625, 0.625);
                    xMax = 0.5F;
                    break;
                }

                float yMin = 0F;
                int uID = world.getBlockId(x, y - 1, z);
                int uMeta = world.getBlockMetadata(x, y - 1, z);
                if (uID == TContent.searedBlock.blockID && uMeta == 0)
                {
                    yMin = -0.125F;
                }
                else if (uID == TContent.searedBlock.blockID && uMeta == 2)
                {
                    yMin = -0.75F;
                }
                else if (uID == TContent.lavaTank.blockID)
                {
                    yMin = -1F;
                }
                else if (uID == TContent.castingChannel.blockID)
                {
                    yMin = -0.5F;
                }

                if (logic.liquid != null)
                {
                    ItemStack blockToRender = new ItemStack(logic.liquid.itemID, 1, logic.liquid.itemMeta);
                    if (blockToRender.itemID < 4096) //Block
                    {
                        Block liquidBlock = Block.blocksList[blockToRender.itemID];
                        //ForgeHooksClient.bindTexture(liquidBlock.getTextureFile(), 0);
                        BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, blockToRender.getItemDamage(), x, y, z, renderer, world);
                        renderer.setRenderBounds(xMin, yMin, zMin, xMax, 0.625, zMax);
                        BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, blockToRender.getItemDamage(), x, y, z, renderer, world);
                    }
                    else
                    //Item
                    {
                        Item liquidItem = Item.itemsList[blockToRender.itemID];
                        //ForgeHooksClient.bindTexture(liquidItem.getTextureFile(), 0);
                        int meta = blockToRender.getItemDamage();
                        BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(meta), x, y, z, renderer, world);
                        renderer.setRenderBounds(xMin, yMin, zMin, xMax, 0.625, zMax);
                        BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(meta), x, y, z, renderer, world);
                    }
                    //renderer.renderStandardBlock(block, x, y, z);
                }

            }
            else if (metadata == 2)
            {
                renderer.setRenderBounds(0.125F, 0.125f, 0.125F, 0.875F, 0.25F, 0.875F);
                renderer.renderStandardBlock(block, x, y, z);

                //Outside
                renderer.setRenderBounds(0.0f, 0.125, 0f, 0.125, 1.0F, 1);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.875f, 0.125, 0f, 1, 1.0F, 1);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.125f, 0.125, 0f, 0.875f, 1.0F, 0.125f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.125f, 0.125, 0.875f, 0.875f, 1.0F, 1);
                renderer.renderStandardBlock(block, x, y, z);

                //Details

                renderer.setRenderBounds(0.001f, 0.1245f, 0.001f, 0.1245f, 0.999f, 0.4375f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.001f, 0.1245f, 0.5625f, 0.1245f, 0.999f, 0.999f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.001f, 0.8755f, 0.4375f, 0.1245f, 0.999f, 0.5625f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.001f, 0.1245f, 0.4375f, 0.1245f, 0.25F, 0.5625f);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(0.8755f, 0.1245f, 0f, 0.999f, 0.999f, 0.4375f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.8755f, 0.1245f, 0.5625f, 0.999f, 0.999f, 0.999f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.8755f, 0.8755f, 0.4375f, 0.999f, 0.999f, 0.5625f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.8755f, 0.1245f, 0.4375f, 0.999f, 0.25F, 0.5625f);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(0.1245f, 0.1245f, 0.8755f, 0.4375f, 0.999f, 0.999f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.5625f, 0.1245f, 0.8755f, 0.8755f, 0.999f, 0.999f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.4375f, 0.8755f, 0.8755f, 0.5625f, 0.999f, 0.999f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.4375f, 0.1245f, 0.8755f, 0.5625f, 0.2495F, 0.999f);
                renderer.renderStandardBlock(block, x, y, z);

                renderer.setRenderBounds(0.1245f, 0.1245f, 0.001f, 0.4375f, 0.999f, 0.1245f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.5625f, 0.1245f, 0.001f, 0.8755f, 0.999f, 0.1245f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.4375f, 0.8755f, 0.001f, 0.5625f, 0.999f, 0.1245f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.4375f, 0.1245f, 0.001f, 0.5625f, 0.25F, 0.1245f);
                renderer.renderStandardBlock(block, x, y, z);

                //Legs
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.125, 0.3125F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.6875F, 0.0F, 0.0F, 1.0F, 0.125, 0.3125F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0F, 0.0F, 0.6875F, 0.3125F, 0.125, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.6875F, 0.0F, 0.6875F, 1.0F, 0.125, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);

                //Liquids
                CastingBasinLogic logic = (CastingBasinLogic) world.getBlockTileEntity(x, y, z);
                if (logic.liquid != null)
                {
                    float height = logic.getLiquidAmount() / (logic.getCapacity() * 1.05F) * 0.6875F;
                    renderer.setRenderBounds(0.0625F, 0.25f, 0.0625F, 0.9375F, 0.25f + height, 0.9375F);
                    if (logic.liquid.itemID < 4096) //Block
                    {
                        Block liquidBlock = Block.blocksList[logic.liquid.itemID];
                        if (liquidBlock != null)
                        {
                            //ForgeHooksClient.bindTexture(liquidBlock.getTextureFile(), 0);
                            BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, logic.liquid.itemMeta, x, y, z, renderer, world);
                        }
                    }
                    else
                    //Item
                    {
                        Item liquidItem = Item.itemsList[logic.liquid.itemID];
                        if (liquidItem != null)
                        {
                            //ForgeHooksClient.bindTexture(liquidItem.getTextureFile(), 0);
                            BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(logic.liquid.itemMeta), x, y, z, renderer, world);
                        }
                    }
                }
            }
            else
            {
                renderer.setRenderBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
            }
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
        return searedModel;
    }
}
