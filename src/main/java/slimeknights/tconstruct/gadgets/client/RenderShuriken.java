package slimeknights.tconstruct.gadgets.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.util.math.vector.Quaternion;
import slimeknights.tconstruct.gadgets.entity.shuriken.ShurikenEntityBase;

public class RenderShuriken extends SpriteRenderer<ShurikenEntityBase> {

  public RenderShuriken(EntityRendererManager renderManagerIn, ItemRenderer itemRendererIn) {
    super(renderManagerIn, itemRendererIn);
  }

  @Override
  public void render(ShurikenEntityBase entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
    matrixStackIn.push();
    matrixStackIn.scale(0.6F, 0.6F, 0.6F);

    // TODO Figure out rotation stuff if the current isn't good enough
    // original credits to Boni
    matrixStackIn.rotate(new Quaternion(1.0F, entity.rotationYaw, 0F, true));
    matrixStackIn.rotate(new Quaternion(-entity.rotationPitch, 1.0F, 0F, true));

    // add some diversity
    matrixStackIn.rotate(new Quaternion(entity.rotationPitch, 0F, 1.0F, true));

    // rotate it into a horizontal position
    matrixStackIn.rotate(new Quaternion(90.0F, 0.0F, 0.0F, true));
//    GL11.glRotatef(90f, 1f, 0f, 0f);

//    if(!entity.inGround) {
//      entity.spin += 20 * partialTicks;
//    }
//    float r = entity.spin;
//
//    // shurikens spin around their center a lot. *spin*
//    GL11.glRotatef(r, 0f, 0f, 1f);
    matrixStackIn.pop();
    super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
  }
}

