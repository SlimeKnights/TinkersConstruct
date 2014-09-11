package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.*;
import tconstruct.client.BlockSkinRenderHelper;
import tconstruct.library.crafting.Smeltery;
import tconstruct.smeltery.logic.SmelteryLogicOld;
import tconstruct.util.ItemHelper;

public class SmelteryRenderOld implements ISimpleBlockRenderingHandler
{
    public static int smelteryModel = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == smelteryModel)
        {
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
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
        SmelteryLogicOld logic = (SmelteryLogicOld) world.getTileEntity(x, y, z);
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
            for (FluidStack liquid : logic.moltenMetal)
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

                    Fluid fluid = liquid.getFluid();
                    for (int i = 0; i < 9; i++)
                    {
                        float minX = i % 3 == 0 ? -0.001F : 0F;
                        float minZ = i / 3 == 0 ? -0.001F : 0F;
                        float maxX = i % 3 == 2 ? 1.001F : 1F;
                        float maxZ = i / 3 == 2 ? 1.001F : 1F;
                        renderer.setRenderBounds(minX, renderBase, minZ, maxX, renderHeight, maxZ);
                        if (fluid.canBePlacedInWorld())
                            BlockSkinRenderHelper.renderMetadataBlock(fluid.getBlock(), 0, posX + i % 3, posY + yBase, posZ + i / 3, renderer, world);
                        else
                            BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), posX + i % 3, posY + yBase, posZ + i / 3, renderer, world);
                    }
                    /*if (liquid.itemID < 4096) //Block
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
                    }*/

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

    void renderLayer (SmelteryLogicOld logic, int start, int posX, int posY, int posZ, RenderBlocks renderer, IBlockAccess world)
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

                    Block liquidBlock = Block.getBlockFromItem(blockToRender.getItem());
                    BlockSkinRenderHelper.renderMetadataBlock(liquidBlock, blockToRender.getItemDamage(), posX + i % 3, posY, posZ + i / 3, renderer, world);

                    /*else //No items, only blocks
                    //Item                        
                    {
                        Item liquidItem = Item.itemsList[blockToRender.itemID];
                        int metadata = blockToRender.getItemDamage();
                        BlockSkinRenderHelper.renderFakeBlock(liquidItem.getIconFromDamage(metadata), posX + i % 3, posY, posZ + i / 3, renderer, world);
                    }*/
                }
            }
        }
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return smelteryModel;
    }
}