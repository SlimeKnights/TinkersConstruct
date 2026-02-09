package slimeknights.tconstruct.world.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;

/** Recreation of {@link net.minecraft.client.model.dragon.DragonHeadModel} but adjusted for slimeskulls. */
public class DragonSkullModel extends SkullModelBase {
  private final ModelPart root;
  private final ModelPart head;
  private final ModelPart jaw;

  public DragonSkullModel(ModelPart root) {
    this.root = root;
    this.head = root.getChild("head");
    this.jaw = this.head.getChild("jaw");
  }

  @Override
  public void setupAnim(float pMouthAnimation, float yRot, float xRot) {
    this.jaw.xRot = (float)(Math.sin(pMouthAnimation * Math.PI * 0.2f) + 1) * 0.2f;
    this.head.yRot = yRot * ((float)Math.PI / 180);
    this.head.xRot = xRot * ((float)Math.PI / 180);
  }

  @Override
  public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
    poseStack.pushPose();
    poseStack.translate(0, -0.25f, 0.075f);
    poseStack.scale(0.5f, 0.5f, 0.49f);
    this.root.render(poseStack, buffer, light, overlay, red, green, blue, alpha);
    poseStack.popPose();
  }
}
