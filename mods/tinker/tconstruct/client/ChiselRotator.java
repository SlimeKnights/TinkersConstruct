package mods.tinker.tconstruct.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ChiselRotator implements IItemRenderer
{
    static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type)
    {
        return type == ItemRenderType.EQUIPPED;
    }

    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem (ItemRenderType type, ItemStack item, Object... data)
    {
        renderEquippedItem(item, (RenderBlocks) data[0], (EntityLiving) data[1]);
    }

    void renderEquippedItem (ItemStack stack, RenderBlocks renderer, EntityLiving living)
    {
        float f4 = 0.0F;
        float f5 = 0.3F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(-f4, -f5, 0.0F);
        float f6 = 1.5F;
        GL11.glScalef(f6, f6, f6);
        GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.875F, -1.5F, 0.25F);
        this.renderItem(stack, living, 0);
        float f3 = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(living.posX), MathHelper.floor_double(living.posY), MathHelper.floor_double(living.posZ));
        for (int x = 1; x < stack.getItem().getRenderPasses(stack.getItemDamage()); x++)
        {
            int i1 = Item.itemsList[stack.itemID].getColorFromItemStack(stack, x);
            float f10 = (float) (i1 >> 16 & 255) / 255.0F;
            float f11 = (float) (i1 >> 8 & 255) / 255.0F;
            float f12 = (float) (i1 & 255) / 255.0F;
            GL11.glColor4f(f3 * f10, f3 * f11, f3 * f12, 1.0F);
            this.renderItem(stack, living, x);
        }
    }

    void renderItem (ItemStack stack, EntityLiving living, int pass)
    {
        GL11.glPushMatrix();

        Icon icon = living.getItemIcon(stack, pass);

        if (icon == null)
        {
            GL11.glPopMatrix();
            return;
        }

        if (stack.getItemSpriteNumber() == 0)
        {
            this.mc.renderEngine.func_110577_a(new ResourceLocation("tinkers:terrain.png"));
        }
        else
        {
            this.mc.renderEngine.func_110577_a(new ResourceLocation("tinkers:gui/items.png"));
        }

        Tessellator tessellator = Tessellator.instance;
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(-f4, -f5, 0.0F);
        float f6 = 1.5F;
        GL11.glScalef(f6, f6, f6);
        GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
        ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getSheetWidth(), icon.getSheetHeight(), 0.0625F);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }
}
