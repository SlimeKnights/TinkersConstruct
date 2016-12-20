package slimeknights.tconstruct.tools.common.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import slimeknights.tconstruct.library.client.renderer.RenderProjectileBase;
import slimeknights.tconstruct.tools.common.entity.EntityShuriken;

public class RenderShuriken extends RenderProjectileBase<EntityShuriken> {

  public RenderShuriken(RenderManager renderManager) {
    super(renderManager);
  }

  @Override
  public void customRendering(EntityShuriken entity, double x, double y, double z, float entityYaw, float partialTicks) {
    // make it smaller
    GL11.glScalef(0.6F, 0.6F, 0.6F);

    // rotate it into the direction we threw it
    GL11.glRotatef(entity.rotationYaw, 0f, 1f, 0f);
    GL11.glRotatef(-entity.rotationPitch, 1f, 0f, 0f);

    // add some diversity
    GL11.glRotatef(entity.rollAngle, 0f, 0f, 1f);

    // rotate it into a horizontal position
    GL11.glRotatef(90f, 1f, 0f, 0f);

    if(!entity.inGround) {
      entity.spin += 20 * partialTicks;
    }
    float r = entity.spin;

    // shurikens spin around their center a lot. *spin*
    GL11.glRotatef(r, 0f, 0f, 1f);


    // also make it a bit thicker
    //toolCoreRenderer.setDepth(1/20f);
  }
}
