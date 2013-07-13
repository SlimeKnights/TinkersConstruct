package mods.tinker.tconstruct.client.block;

import mods.tinker.tconstruct.blocks.logic.SmelteryLogic;
import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.library.crafting.Smeltery;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SmelteryRender implements ISimpleBlockRenderingHandler
{
    public static int smelteryModel = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == smelteryModel)
        {
            TProxyClient.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == smelteryModel)
        {
            if (world.getBlockMetadata(x, y, z) == 0)
                return renderSmeltery(world, x, y, z, block, modelID, renderer);
            else
                renderer.renderStandardBlock(block, x, y, z);
        }
        return true;
    }

    public boolean renderSmeltery (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        renderer.renderStandardBlock(block, x, y, z);
        SmelteryLogic logic = (SmelteryLogic) world.getBlockTileEntity(x, y, z);
        if (logic.validStructure)
        {
            int posX = logic.centerPos.x - 1, posY = logic.centerPos.y, posZ = logic.centerPos.z - 1;
            //Melting
            if (logic.getSizeInventory() > 0)
            {
                for (int i = 0; i < logic.layers; i++)
                {
                    renderLayer(logic, i * 9, posX, posY + i, posZ, renderer, world);
                }
            }

            //Liquids
            float base = 0F;
            int yBase = 0;
            int liquidBase = 0;
            for (LiquidStack liquid : logic.moltenMetal)
            {
                int liquidSize = liquid.amount;
                while (liquidSize > 0)
                {
                    int room = 20000 - liquidBase;
                    int countSize = liquidSize > room ? room : liquidSize;
                    liquidSize -= countSize;

                    float height = countSize > 20000 ? 1.0F : countSize / 20000F;
                    //renderer.setRenderBounds(0, base, 0, 1, height + base, 1);
                    float renderBase = base;
                    float renderHeight = height + base;
                    base += height;
                    liquidBase += countSize;

                    if (liquid.itemID < 4096) //Block
                    {
                        Block liquidBlock = Block.blocksList[liquid.itemID];
                        for (int i = 0; i < 9; i++)
                        {
                            float minX = i % 3 == 0 ? -0.001F : 0F;
                            float minZ = i / 3 == 0 ? -0.001F : 0F;
                            float maxX = i % 3 == 2 ? 1.001F : 1F;
                            float maxZ = i / 3 == 2 ? 1.001F : 1F;
                            renderer.setRenderBounds(minX, renderBase, minZ, maxX, renderHeight, maxZ);
                            BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, liquid.itemMeta, posX + i % 3, posY + yBase, posZ + i / 3, renderer, world);
                        }
                    }
                    else
                    //Item
                    {
                        Item liquidItem = Item.itemsList[liquid.itemID];
                        for (int i = 0; i < 9; i++)
                        {
                            float minX = i % 3 == 0 ? -0.001F : 0F;
                            float minZ = i / 3 == 0 ? -0.001F : 0F;
                            float maxX = i % 3 == 2 ? 1.001F : 1F;
                            float maxZ = i / 3 == 2 ? 1.001F : 1F;
                            renderer.setRenderBounds(minX, renderBase, minZ, maxX, renderHeight, maxZ);
                            BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(liquid.itemMeta), posX, posY + yBase, posZ, renderer, world);
                        }
                    }

                    if (countSize == room)
                    {
                        base = 0F;
                        yBase++;
                        liquidBase = 0;
                    }
                }
            }
        }
        return true;
    }

    void renderLayer (SmelteryLogic logic, int start, int posX, int posY, int posZ, RenderBlocks renderer, IBlockAccess world)
    {
        renderer.setRenderBounds(-0.001F, -0.001F, -0.001F, 1.001F, 1.001F, 1.001F);
        for (int i = 0; i < 9; i++)
        {
            ItemStack input = logic.getStackInSlot(i + start);
            if (input != null && logic.getTempForSlot(i + start) > 20)
            {
                ItemStack blockToRender = Smeltery.getRenderIndex(input);
                if (blockToRender != null)
                {
                    float blockHeight = input.stackSize / (float) blockToRender.stackSize;
                    renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, MathHelper.clamp_float(blockHeight, 0.01F, 1.0F), 1.0F);

                    if (blockToRender.itemID < 4096) //Block
                    {
                        Block liquidBlock = Block.blocksList[blockToRender.itemID];
                        BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, blockToRender.getItemDamage(), posX + i % 3, posY, posZ + i / 3, renderer, world);
                    }
                    else
                    //Item                        
                    {
                        Item liquidItem = Item.itemsList[blockToRender.itemID];
                        int metadata = blockToRender.getItemDamage();
                        BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(metadata), posX + i % 3, posY, posZ + i / 3, renderer, world);
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldRender3DInInventory ()
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return smelteryModel;
    }
}
