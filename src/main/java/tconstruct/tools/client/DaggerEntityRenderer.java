package tconstruct.tools.client;

import org.lwjgl.opengl.GL11;
import tconstruct.tools.entity.DaggerEntity;
import tconstruct.weaponry.client.entity.ProjectileBaseRenderer;

public class DaggerEntityRenderer extends ProjectileBaseRenderer<DaggerEntity> {
    @Override
    public void customRendering(DaggerEntity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {

        // rotate it into the direction we threw it
        GL11.glRotatef(entity.rotationYaw, 0f, 1f, 0f);
        GL11.glRotatef(-entity.rotationPitch, 1f, 0f, 0f);

        // rotate it into a vertical position
        GL11.glRotatef(90f, 0f, 1f, 0f);

        // it rotates!
        GL11.glRotatef(entity.roll, 0f, 0f, 1f);


        // also make it a bit thicker
        toolCoreRenderer.setDepth(1/20f);
    }
}
