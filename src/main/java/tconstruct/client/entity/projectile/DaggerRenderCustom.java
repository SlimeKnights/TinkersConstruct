package tconstruct.client.entity.projectile;

import cpw.mods.fml.relauncher.*;
import java.util.Random;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.*;
import tconstruct.client.ToolCoreRenderer;
import tconstruct.tools.entity.DaggerEntity;

@SideOnly(Side.CLIENT)
public class DaggerRenderCustom extends Render
{
    private static RenderItem renderer = new RenderItem();
    private static ToolCoreRenderer toolCoreRenderer = new ToolCoreRenderer(true, true);
    private Random random = new Random();

    public DaggerRenderCustom()
    {
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    /**
     * Renders the item
     */
    public void doRenderItem (DaggerEntity dagger, double par2, double par4, double par6, float par8, float par9)
    {
        random.setSeed(187L);
        ItemStack item = dagger.getEntityItem();
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par2, (float) par4, (float) par6);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(dagger.prevRotationYaw + (dagger.rotationYaw - dagger.prevRotationYaw) * par9 - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(dagger.prevRotationPitch + (dagger.rotationPitch - dagger.prevRotationPitch) * par9 - 45.0F, 0.0F, 0.0F, 1.0F);

        float rotation = dagger.prevRotationPitch + (dagger.rotationPitch - dagger.prevRotationPitch) * par9;
        GL11.glRotatef(dagger.rotationYaw + 90, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(rotation * 15, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.25f, -0.25f, 0f); // translate to the middle. This makes it so that the dagger rotates around its center
        float shake = dagger.arrowShake - par9;
        if (shake > 0.0F)
            GL11.glRotatef(-MathHelper.sin(shake * 3) * shake, 0, 0, 1);
        //GL11.glTranslatef(-7 / 16f, -8 / 16f, -1 / 32f);
        float scale = 1.35f;
        GL11.glScalef(scale, scale, scale);

        /* begin hardcoded regular item rendering */
        // see ForgeHooksClient.renderEntityItem
        renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        toolCoreRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, item);
        /* end hardcoded regular item rendering */

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    /**
     * Renders the item's icon or block into the UI at the specified position.
     */
    public void renderItemIntoGUI (FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5)
    {
        renderer.renderItemIntoGUI(par1FontRenderer, par2TextureManager, par3ItemStack, par4, par5);
    }

    /**
     * Render the item's icon or block into the GUI, including the glint effect.
     */
    public void renderItemAndEffectIntoGUI (FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5)
    {
        renderer.renderItemIntoGUI(par1FontRenderer, par2TextureManager, par3ItemStack, par4, par5);
    }

    /**
     * Renders the item's overlay information. Examples being stack count or
     * damage on top of the item's image at the specified position.
     */
    public void renderItemOverlayIntoGUI (FontRenderer par1FontRenderer, TextureManager par2TextureManager, ItemStack par3ItemStack, int par4, int par5)
    {
        renderer.renderItemOverlayIntoGUI(par1FontRenderer, par2TextureManager, par3ItemStack, par4, par5);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then handing it off to a worker
     * function which does the actual work. In all probabilty, the class Render
     * is generic (Render<T extends Entity) and this method has signature public
     * void doRender(T entity, double d, double d1, double d2, float f, float
     * f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender (Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderItem((DaggerEntity) par1Entity, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture (Entity par1Entity)
    {
        return this.func_110796_a((DaggerEntity) par1Entity);
    }

    protected ResourceLocation func_110796_a (DaggerEntity par1ArrowEntity)
    {
        return this.renderManager.renderEngine.getResourceLocation(par1ArrowEntity.getEntityItem().getItemSpriteNumber());
    }
}
