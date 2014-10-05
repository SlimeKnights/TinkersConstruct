package tconstruct.weaponry.client.entity;

import tconstruct.weaponry.entity.JavelinEntity;
import org.lwjgl.opengl.GL11;

public class JavelinEntityRenderer extends ProjectileBaseRenderer<JavelinEntity> {
    @Override
    public void customRendering(JavelinEntity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {

        // rotate it into the direction we threw it
        GL11.glRotatef(entity.rotationYaw, 0f, 1f, 0f);
        GL11.glRotatef(-entity.rotationPitch, 1f, 0f, 0f);

        // rotate it so it faces forward
        GL11.glRotatef(90f, 1f, 0f, 0f);

        // roll it all aroun
        GL11.glRotated(entity.roll, 0, 1, 0);

        // make it looooong
        GL11.glScalef(1.5F, 3.0F, 1.5F);
        GL11.glTranslatef(0f, -0.5f, 0f);

        // rotate the projectile it so it faces upwards (because the graphic itself is rotated by 45°)
        GL11.glRotatef(45, 0f, 0f, 1f);

        toolCoreRenderer.setDepth(1/16f);
    }
}
