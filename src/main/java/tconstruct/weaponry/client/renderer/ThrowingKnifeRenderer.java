package tconstruct.weaponry.client.renderer;

import tconstruct.weaponry.library.weaponry.IWindup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ThrowingKnifeRenderer extends AmmoItemRenderer {
    private static Minecraft mc = Minecraft.getMinecraft();

    @Override
    protected void specialAnimation(ItemRenderType type, ItemStack item) {
        float progress = ((IWindup)item.getItem()).getWindupProgress(item, mc.thePlayer);

        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glPushMatrix();
            GL11.glScalef(2.5f, 2.5f, 2.5f);
            // rotate it, otherwise it's drawn over our item :(
            GL11.glTranslatef(0.5f, -0.6f, 0f);
            //GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            //GL11.glRotatef(-85.0F, 0.0F, 0.0F, 1.0F);

            GL11.glRotatef(-25.0F, 0.0F, 0.0F, 1.0F);

            GL11.glRotatef(progress * 35.0F, 0.0F, 0.0F, 1.0F);

            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            mc.getTextureManager().bindTexture(mc.thePlayer.getLocationSkin());
            RenderPlayer plrRender = (RenderPlayer) RenderManager.instance.getEntityRenderObject(mc.thePlayer);
            plrRender.renderFirstPersonArm(mc.thePlayer);

            GL11.glPopMatrix();
            mc.getTextureManager().bindTexture(TextureMap.locationItemsTexture);

            // rotate it aroooound
            //GL11.glRotatef(-180, 0, 0, 1);
            //GL11.glTranslatef(0.5f, -0.6f, 0f);
            //GL11.glRotatef(-25.0F, 0.0F, 0.0F, 1.0F);

            GL11.glTranslatef(1.3f, 0.4f, -0.2f);

            //GL11.glTranslatef(-0.5f, 0.6f, 0f);
            //GL11.glTranslatef(0.f, 1f, 0f);
            //GL11.glTranslatef(progress * -1.5f, 0.0f, 0f);
            GL11.glTranslatef(0.0f, -1.85f, 0f);
            GL11.glRotatef(progress * 35.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(0.0f, 1.85f, 0f);
            GL11.glRotatef(progress * -15.0F, 0.0F, 0.0F, 1.0F);
            //GL11.glTranslatef(-0.f, -1f, 0f);
            //GL11.glTranslatef(0.5f, -0.6f, 0f);


            GL11.glScalef(1.5f, 1.5f, 1.5f);

            GL11.glRotatef(-180, 0, 0, 1);
            GL11.glTranslatef(0.5f, -0.6f, 0f);
            GL11.glRotatef(-25.0F, 0.0F, 0.0F, 1.0F);
        }

        if(type == ItemRenderType.EQUIPPED)
            GL11.glRotatef(-180, 0, 0, 1);

    }
}
