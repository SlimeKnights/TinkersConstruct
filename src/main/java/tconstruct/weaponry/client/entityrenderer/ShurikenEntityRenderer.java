package boni.tinkersweaponry.client.entityrenderer;

import boni.tinkersweaponry.entity.ShurikenEntity;
import org.lwjgl.opengl.GL11;

public class ShurikenEntityRenderer extends ProjectileBaseRenderer<ShurikenEntity> {

    @Override
    public void customRendering(ShurikenEntity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
        // make it smaller
        GL11.glScalef(0.6F, 0.6F, 0.6F);

        // rotate it into the direction we threw it
        GL11.glRotatef(entity.rotationYaw, 0f, 1f, 0f);
        GL11.glRotatef(-entity.rotationPitch, 1f, 0f, 0f);

        // add some diversity
        GL11.glRotatef(entity.rollAngle, 0f, 0f, 1f);

        // rotate it into a horizontal position
        GL11.glRotatef(90f, 1f, 0f, 0f);

        // shurikens spin around their center a lot. *spin*
        GL11.glRotatef(entity.spin, 0f, 0f, 1f);


        // also make it a bit thicker
        toolCoreRenderer.setDepth(1/20f);
    }
}
