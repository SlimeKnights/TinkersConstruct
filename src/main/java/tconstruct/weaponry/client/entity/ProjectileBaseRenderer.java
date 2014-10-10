package tconstruct.weaponry.client.entity;

import tconstruct.client.FlexibleToolRenderer;
import tconstruct.library.entity.ProjectileBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ProjectileBaseRenderer<T extends ProjectileBase> extends Render {
    protected static FlexibleToolRenderer toolCoreRenderer = new FlexibleToolRenderer();

    @SuppressWarnings("unchecked")
    @Override
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        doRender((T)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    public void doRender(T entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
        // preface: Remember that the rotations are applied in reverse order.
        // the rendering call does not apply any transformations.
        // That'd screw things up, since it'd be applied before our transformations
        // So remember to read this from the rendering call up to this line

        // can be overwritten in customRendering
        toolCoreRenderer.setDepth(1/32f);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        // last step: translate from 0/0/0 to correct position in world
        GL11.glTranslated(x, y, z);
        // mkae it smaller
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        customRendering(entity, x, y, z, p_76986_8_, p_76986_9_);

        // arrow shake
        float f11 = (float)entity.arrowShake - p_76986_9_;
        if (f11 > 0.0F)
        {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }

        // draw correct texture. not some weird block fragments.
        renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);
        // rendering code has been optimized to be exactly at the center and without translation
        GL11.glTranslatef(0.0f, -0.25f, 0);
        toolCoreRenderer.renderItem(IItemRenderer.ItemRenderType.ENTITY, entity.getEntityItem());
        GL11.glTranslatef(0.0f, 0.25f, 0);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    public void customRendering(T entity, double x, double y, double z, float p_76986_8_, float p_76986_9_)
    {
        // flip it, flop it, pop it, pull it, push it, rotate it, translate it, TECHNOLOGY

        // rotate it into the direction we threw it
        GL11.glRotatef(entity.rotationYaw, 0f, 1f, 0f);
        GL11.glRotatef(-entity.rotationPitch, 1f, 0f, 0f);

        // rotate it so it's "upright"
        GL11.glRotatef(90, 0f, 0f, 1f);

        // rotate it so it faces forward
        GL11.glRotatef(90f, 1f, 0f, 0f);

        // rotate the projectile it so it faces upwards
        GL11.glRotatef(-45, 0f, 0f, 1f);
    }

    @Override
    protected ResourceLocation getEntityTexture (Entity par1Entity)
    {
        return this.func_110796_a((ProjectileBase) par1Entity);
    }

    protected ResourceLocation func_110796_a (ProjectileBase par1ArrowEntity)
    {
        return this.renderManager.renderEngine.getResourceLocation(par1ArrowEntity.getEntityItem().getItemSpriteNumber());
    }
}
