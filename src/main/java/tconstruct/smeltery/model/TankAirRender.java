package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.*;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.*;
import tconstruct.TConstruct;
import tconstruct.client.BlockSkinRenderHelper;
import tconstruct.smeltery.logic.TankAirLogic;

public class TankAirRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();
    private static final double capacity = TConstruct.ingotLiquidValue * 18;

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        //No inventory block
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            TankAirLogic logic = (TankAirLogic) world.getTileEntity(x, y, z);
            if (logic.hasItem())
            {
                ItemStack item = logic.getStackInSlot(0);
                if (item.getItem() instanceof ItemBlock)
                {
                    Block inv = Block.getBlockFromItem(item.getItem());
                    renderer.setOverrideBlockTexture(inv.getIcon(1, item.getItemDamage()));
                    renderer.renderBlockByRenderType(inv, x, y, z);
                    renderer.clearOverrideBlockTexture();
                }
            }
            else if (logic.hasFluids())
            {
                int base = 0;
                for (FluidStack fluidstack : logic.getFluids())
                {
                    Fluid fluid = fluidstack.getFluid();
                    //System.out.println("Base: "+getBaseAmount(base)+", Height: "+getHeightAmount(base, fluidstack.amount)+", fluid amount: "+fluidstack.amount);
                    renderer.setRenderBounds(0.0, getBaseAmount(base), 0.0, 1.0, getHeightAmount(base, fluidstack.amount), 1.0);
                    if (fluid.canBePlacedInWorld())
                        BlockSkinRenderHelper.renderMetadataBlock(fluid.getBlock(), 0, x, y, z, renderer, world);
                    else
                        BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);
                    base += fluidstack.amount;
                }
            }
        }
        return true;
    }

    private double getBaseAmount (int base)
    {
        return base / capacity;
    }

    private double getHeightAmount (int base, int amount)
    {
        return (base + amount) / capacity;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }
}