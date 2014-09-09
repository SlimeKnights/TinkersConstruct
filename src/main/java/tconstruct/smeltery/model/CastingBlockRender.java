package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import tconstruct.TConstruct;
import tconstruct.client.BlockSkinRenderHelper;
import tconstruct.library.crafting.CastingRecipe;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.smeltery.logic.*;
import tconstruct.util.ItemHelper;

public class CastingBlockRender implements ISimpleBlockRenderingHandler
{
    public static int searedModel = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == searedModel)
        {
            if (metadata == 0)
            {
                // Top
                renderer.setRenderBounds(0.0F, 0.625F, 0.0F, 1.0F, 0.9375F, 1.0F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);

                // Lip
                renderer.setRenderBounds(0.0F, 0.9375, 0.0F, 0.0625, 1.0, 1.0F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0625, 0.9375, 0.9375, 0.9375, 1.0, 1.0F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.9375, 0.9375, 0.0F, 1.0F, 1.0, 1.0F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0625, 0.9375, 0.0F, 0.9375, 1.0, 0.0625);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);

                // Legs
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.625F, 0.3125F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.6875, 0.0F, 0.0F, 1.0F, 0.625F, 0.25F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0F, 0.0F, 0.6875, 0.3125F, 0.625F, 1.0F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.6875, 0.0F, 0.6875, 1.0F, 0.625F, 1.0F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
            }
            else if (metadata == 1)
            {
                renderer.setRenderBounds(0.25, 0.25, 0.625, 0.75, 0.375, 1);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.25, 0.25, 0.625, 0.375, 0.625, 1);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.625, 0.25, 0.625, 0.75, 0.625, 1);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.375, 0.375, 0.625, 0.625, 0.625, 1);
            }
            else if (metadata == 2)
            {
                renderer.setRenderBounds(0.125F, 0.125f, 0.125F, 0.875F, 0.25F, 0.875F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);

                // Details
                /*renderer.setRenderBounds(0.001f, 0.1245f, 0.001f, 0.1245f, 0.999f, 0.4375f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.001f, 0.1245f, 0.5625f, 0.1245f, 0.999f, 0.999f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.001f, 0.8755f, 0.4375f, 0.1245f, 0.999f, 0.5625f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.001f, 0.1245f, 0.4375f, 0.1245f, 0.25F, 0.5625f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);

                renderer.setRenderBounds(0.8755f, 0.1245f, 0f, 0.999f, 0.999f, 0.4375f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.8755f, 0.1245f, 0.5625f, 0.999f, 0.999f, 0.999f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.8755f, 0.8755f, 0.4375f, 0.999f, 0.999f, 0.5625f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.8755f, 0.1245f, 0.4375f, 0.999f, 0.25F, 0.5625f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);

                renderer.setRenderBounds(0.1245f, 0.1245f, 0.8755f, 0.4375f, 0.999f, 0.999f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.5625f, 0.1245f, 0.8755f, 0.8755f, 0.999f, 0.999f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.4375f, 0.8755f, 0.8755f, 0.5625f, 0.999f, 0.999f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.4375f, 0.1245f, 0.8755f, 0.5625f, 0.2495F, 0.999f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);

                renderer.setRenderBounds(0.1245f, 0.1245f, 0.001f, 0.4375f, 0.999f, 0.1245f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.5625f, 0.1245f, 0.001f, 0.8755f, 0.999f, 0.1245f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.4375f, 0.8755f, 0.001f, 0.5625f, 0.999f, 0.1245f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.4375f, 0.1245f, 0.001f, 0.5625f, 0.25F, 0.1245f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);*/

                // Legs
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.125, 0.3125F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.6875F, 0.0F, 0.0F, 1.0F, 0.125, 0.3125F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0F, 0.0F, 0.6875F, 0.3125F, 0.125, 1.0F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.6875F, 0.0F, 0.6875F, 1.0F, 0.125, 1.0F);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);

                // Outside
                renderer.setRenderBounds(0.0f, 0.125, 0f, 0.125, 1.0F, 1);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.875f, 0.125, 0f, 1, 1.0F, 1);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.125f, 0.125, 0f, 0.875f, 1.0F, 0.125f);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.125f, 0.125, 0.875f, 0.875f, 1.0F, 1);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
            }
            else
            {
                renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
                ItemHelper.renderStandardInvBlock(renderer, block, metadata);
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
                // Top
                renderer.setRenderBounds(0.0F, 0.625F, 0.0F, 1.0F, 0.9375F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);

                // Lip
                renderer.setRenderBounds(0.0F, 0.9375, 0.0F, 0.0625, 1.0, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0625, 0.9375, 0.9375, 0.9375, 1.0, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.9375, 0.9375, 0.0F, 1.0F, 1.0, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0625, 0.9375, 0.0F, 0.9375, 1.0, 0.0625);
                renderer.renderStandardBlock(block, x, y, z);

                // Legs
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.625F, 0.3125F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.6875, 0.0F, 0.0F, 1.0F, 0.625F, 0.25F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0F, 0.0F, 0.6875, 0.3125F, 0.625F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.6875, 0.0F, 0.6875, 1.0F, 0.625F, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);

                if (world.getTileEntity(x, y, z) instanceof CastingTableLogic)
                {
                    CastingTableLogic logic = (CastingTableLogic) world.getTileEntity(x, y, z);
                    if (logic.liquid != null)
                    {
                        float minHeight = 0.9375F;
                        float maxHeight = 1F;

                        float minX = 0.0625F;
                        float maxX = 0.9375F;
                        float minZ = 0.0625F;
                        float maxZ = 0.9375F;

                        ItemStack it = logic.getStackInSlot(0);
                        if (it != null)
                        {
                            CastingRecipe rec = TConstruct.tableCasting.getCastingRecipe(logic.liquid, it);
                            if (rec != null && rec.fluidRenderProperties != null)
                            {
                                minHeight = rec.fluidRenderProperties.minHeight;
                                maxHeight = rec.fluidRenderProperties.maxHeight;

                                minX = rec.fluidRenderProperties.minX;
                                maxX = rec.fluidRenderProperties.maxX;
                                minZ = rec.fluidRenderProperties.minZ;
                                maxZ = rec.fluidRenderProperties.maxZ;
                            }
                        }

                        float percent = (float) logic.getLiquidAmount() / (float) logic.getCapacity();
                        float height = percent * (maxHeight - minHeight);
                        // float height = logic.getLiquidAmount() /
                        // (logic.getCapacity() * 1.03F) / 16F;
                        renderer.setRenderBounds(minX, minHeight, minZ, maxX, minHeight + height, maxZ);

                        Fluid fluid = logic.liquid.getFluid();
                        BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);
                    }
                }
            }
            else if (metadata == 1)
            {
                if (world.getTileEntity(x, y, z) instanceof FaucetLogic)
                {
                    FaucetLogic logic = (FaucetLogic) world.getTileEntity(x, y, z);
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
                        // zMin = 0.625F;
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
                    Block uBlock = world.getBlock(x, y - 1, z);
                    int uMeta = world.getBlockMetadata(x, y - 1, z);
                    if (uBlock == TinkerSmeltery.searedBlock && uMeta == 0)
                    {
                        yMin = -0.125F;
                    }
                    else if (uBlock == TinkerSmeltery.searedBlock && uMeta == 2)
                    {
                        yMin = -0.75F;
                    }
                    else if (uBlock == TinkerSmeltery.lavaTank)
                    {
                        yMin = -1F;
                    }
                    else if (uBlock == TinkerSmeltery.castingChannel)
                    {
                        yMin = -0.5F;
                    }

                    if (logic.liquid != null)
                    {
                        Fluid fluid = logic.liquid.getFluid();
                        renderer.setRenderBounds(xMin, yMin, zMin, xMax, 0.625, zMax);
                        BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);

                        // float xMin = 0.375F, zMin = 0.375F, xMax = 0.625F,
                        // zMax = 0.625F;
                        switch (logic.getRenderDirection())
                        {
                        case 3:
                            zMin = 0.0F;
                            zMax = 0.375F;
                            break;
                        case 2:
                            zMin = 0.625F;
                            zMax = 1.0F;
                            break;
                        case 5:
                            xMin = 0.0F;
                            xMax = 0.375F;
                            break;
                        case 4:
                            xMin = 0.625F;
                            xMax = 1.0F;
                            break;
                        }
                        renderer.setRenderBounds(xMin, 0.5F, zMin, xMax, 0.625F, zMax);
                        BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);
                    }
                }
            }
            else if (metadata == 2)
            {
                renderer.setRenderBounds(0.125F, 0.125f, 0.125F, 0.875F, 0.25F, 0.875F);
                renderer.renderStandardBlock(block, x, y, z);

                // Outside
                renderer.setRenderBounds(0.0f, 0.125, 0f, 0.125, 1.0F, 1);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.875f, 0.125, 0f, 1, 1.0F, 1);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.125f, 0.125, 0f, 0.875f, 1.0F, 0.125f);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.125f, 0.125, 0.875f, 0.875f, 1.0F, 1);
                renderer.renderStandardBlock(block, x, y, z);

                // Details

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

                // Legs
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.3125F, 0.125, 0.3125F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.6875F, 0.0F, 0.0F, 1.0F, 0.125, 0.3125F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.0F, 0.0F, 0.6875F, 0.3125F, 0.125, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.6875F, 0.0F, 0.6875F, 1.0F, 0.125, 1.0F);
                renderer.renderStandardBlock(block, x, y, z);

                // Liquids
                if (world.getTileEntity(x, y, z) instanceof CastingBasinLogic)
                {
                    CastingBasinLogic logic = (CastingBasinLogic) world.getTileEntity(x, y, z);
                    if (logic.liquid != null)
                    {
                        float minHeight = 0.25F;
                        float maxHeight = 0.95F;

                        float minX = 0.0625F;
                        float maxX = 0.9375F;
                        float minZ = 0.0625F;
                        float maxZ = 0.9375F;

                        ItemStack it = logic.getStackInSlot(0);
                        if (it != null)
                        {
                            CastingRecipe rec = TConstruct.basinCasting.getCastingRecipe(logic.liquid, it);
                            if (rec != null && rec.fluidRenderProperties != null)
                            {
                                minHeight = rec.fluidRenderProperties.minHeight;
                                maxHeight = rec.fluidRenderProperties.maxHeight;

                                minX = rec.fluidRenderProperties.minX;
                                maxX = rec.fluidRenderProperties.maxX;
                                minZ = rec.fluidRenderProperties.minZ;
                                maxZ = rec.fluidRenderProperties.maxZ;
                            }
                        }
                        float percent = (float) logic.getLiquidAmount() / (float) logic.getCapacity();
                        float height = percent * (maxHeight - minHeight);

                        // float height = (logic.getLiquidAmount() /
                        // (logic.getCapacity() * 1.05F) * 0.6875F) / maxHeight;
                        renderer.setRenderBounds(minX, minHeight, minZ, maxX, minHeight + height, maxZ);

                        Fluid fluid = logic.liquid.getFluid();
                        BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);
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
    public boolean shouldRender3DInInventory (int modelid)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return searedModel;
    }
}
