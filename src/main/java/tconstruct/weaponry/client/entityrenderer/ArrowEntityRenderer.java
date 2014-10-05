package boni.tinkersweaponry.client.entityrenderer;

import boni.tinkersweaponry.entity.ArrowEntity;
import org.lwjgl.opengl.GL11;

public class ArrowEntityRenderer extends ProjectileBaseRenderer<ArrowEntity> {
    private int count = 0;
    private final float size;

    public ArrowEntityRenderer(float size) {
        this.size = size;
    }

    public ArrowEntityRenderer() {
        this(1.0f);
    }

    @Override
    public void doRender(ArrowEntity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
        count = 0;
        super.doRender(entity, x, y, z, p_76986_8_, p_76986_9_);
        count = 1;
        super.doRender(entity, x, y, z, p_76986_8_, p_76986_9_);
    }

    @Override
    public void customRendering(ArrowEntity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
        // flip it, flop it, pop it, pull it, push it, rotate it, translate it, TECHNOLOGY

        GL11.glScalef(size, size, size);

        // rotate it into the direction we threw it
        GL11.glRotatef(entity.rotationYaw, 0f, 1f, 0f);
        GL11.glRotatef(-entity.rotationPitch, 1f, 0f, 0f);

        // rotate it so it's "upright"
        if(count == 0)
            GL11.glRotatef(90, 0f, 0f, 1f);

        GL11.glRotatef(45, 0f, 0f, 1f);

        // rotate it so it faces forward
        GL11.glRotatef(90f, 1f, 0f, 0f);

        // rotate the projectile it so it faces upwards (because the graphic itself is rotated by 45°)
        GL11.glRotatef(-45, 0f, 0f, 1f);

        // thinner arrows
        toolCoreRenderer.setDepth(1/64f);
    }
}
