package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import tconstruct.util.ItemHelper;

public class TankItemRenderer implements IItemRenderer {
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        // should be impossible to happen, but rather be safe

        RenderBlocks renderblocks = (RenderBlocks) data[0];
        Block block = Block.getBlockFromItem(item.getItem());
        int meta = item.getItemDamage();

        if(item.hasTagCompound() && item.getTagCompound().hasKey("Fluid"))
        {
            FluidStack liquid = FluidStack.loadFluidStackFromNBT(item.getTagCompound().getCompoundTag("Fluid"));
            if(liquid != null && liquid.getFluid().getBlock() != null)
            {
                GL11.glEnable(GL11.GL_BLEND);
                float height = (float)liquid.amount / 4000f - 0.01f;
                renderblocks.setRenderBounds(0.01, 0.01, 0.01, 0.99, height, 0.99);
                ItemHelper.renderStandardInvBlock(renderblocks, liquid.getFluid().getBlock(), 0);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        renderblocks.setRenderBounds(0, 0, 0, 1, 1, 1);
        //ItemHelper.renderStandardInvBlock(renderblocks, block, meta);
        RenderingRegistry.instance().renderInventoryBlock(renderblocks, block, meta, TankRender.tankModelID);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }
}
