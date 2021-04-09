package slimeknights.tconstruct.gadgets.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.gadgets.entity.shuriken.ShurikenEntityBase;

public class RenderShuriken extends EntityRenderer<ShurikenEntityBase> {
  private final ItemRenderer itemRenderer;
  public RenderShuriken(EntityRenderDispatcher manager, ItemRenderer itemRenderer) {
    super(manager);
    this.itemRenderer = itemRenderer;
  }

  @Override
  public void render(ShurikenEntityBase entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
    if (entity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25D)) {
      matrixStackIn.push();
      matrixStackIn.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
      matrixStackIn.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-(entity.age + partialTicks) * 30 % 360));
      matrixStackIn.translate(-0.03125, -0.09375, 0);
      this.itemRenderer.renderItem(entity.getStack(), ModelTransformation.Mode.GROUND, packedLightIn, OverlayTexture.DEFAULT_UV, matrixStackIn, bufferIn);
      matrixStackIn.pop();
      super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
  }

  @Override
  public Identifier getTexture(ShurikenEntityBase entity) {
    return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
  }
}
