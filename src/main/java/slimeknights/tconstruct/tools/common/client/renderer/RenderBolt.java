package slimeknights.tconstruct.tools.common.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import slimeknights.tconstruct.library.client.renderer.RenderProjectileBase;
import slimeknights.tconstruct.tools.common.entity.EntityArrow;
import slimeknights.tconstruct.tools.common.entity.EntityBolt;

public class RenderBolt extends RenderProjectileBase<EntityBolt> {

  public RenderBolt(RenderManager renderManager) {
    super(renderManager);
  }

  @Override
  protected void customCustomRendering(EntityBolt entity, double x, double y, double z, float entityYaw, float partialTicks) {
    /*if(!entity.inGround) {
      entity.roll += entity.rollSpeed * partialTicks;
    }
    float r = entity.roll;

    GL11.glRotatef(r, 0f, 0f, 1f);*/
  }
}
